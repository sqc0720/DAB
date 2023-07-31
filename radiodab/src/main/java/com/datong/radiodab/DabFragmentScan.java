package com.datong.radiodab;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DabFragmentScan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DabFragmentScan extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "DAB.SCAN";
    private Button mButtonCancel;
    private TextView mTextViewHint;
    private ImageView mImageView;
    private AnimationDrawable mAnim;
    private DabTabFragment mTab;
    private int mBand;
    private DabServiceWrapper mDabServiceWrapper;
    private Timer mTimerStart;
    private Timer mTimerDestroy;
    private TimerTask mTaskStart ;
    private TimerTask mTaskDestroy ;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private int  mFirst ;

    public DabFragmentScan() {
        // Required empty public constructor
        super();
        mFirst= 0;
        mBand=0;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DabFragmentScan.
     */
    // TODO: Rename and change types and number of parameters
    public static DabFragmentScan newInstance(Context context, DabTabFragment tab) {
        DabFragmentScan fragment = new DabFragmentScan();
        fragment.mTab= tab;
        return fragment;
    }

    public static DabFragmentScan newInstance(Context context, DabTabFragment tab, boolean isFirst) {
        DabFragmentScan fragment = new DabFragmentScan();
        fragment.mTab= tab;
        fragment.mFirst= 1;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"DabFragmentScan:: onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(TAG);
        }
        clearPngFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG,"DabFragmentScan:: onCreateView");
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dab_scan, container, false);
        mTextViewHint= view.findViewById(R.id.dab_textview_scan_hint);
        mButtonCancel= view.findViewById(R.id.dab_button_scan_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirst == 0) {
                    mTab.popDabFragmentScan();
                } else {
                    Log.i(TAG, "DabFragmentScan:: new DabFragment");
                    mTab.replaceDabFragment();
                }
            }
        });
        mImageView= view.findViewById(R.id.dab_imageView_scan_animation);
        return view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mButtonCancel.setBackground(getResources().getDrawable(R.drawable.dab_scan_cancel_shape));
        mButtonCancel.setTextColor(getResources().getColor(R.color.dab_scan_cancel_text));
        mTextViewHint.setTextColor(getResources().getColor(R.color.dab_scan_hint));
    }

    @Override
    public void onStart() {
        Log.i(TAG,"DabFragmentScan:: onStart");
        mAnim= (AnimationDrawable) mImageView.getBackground();
        mAnim.start();
        mTimerDestroy = new Timer();
        mTaskDestroy = new TimerTask() {
            @Override
            public void run() {
            try {
                Log.i(TAG, "DabFragmentScan:: Destroy myself because of timeout ");
                scanOnDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        };
        //15000ms执行一次
        mTimerDestroy.schedule(mTaskDestroy, 15000);

        startBindService();
        scanProgramlist();
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG,"DabFragmentScan:: onStop");
        mAnim.stop();
        if(mTimerDestroy!=null)
            mTimerDestroy.cancel();
        mTimerDestroy=null;
        if(mTimerStart!=null)
            mTimerStart.cancel();
        mTimerStart=null;//据说这样不会自动跳出软件
        mDabServiceWrapper.onServiceCancel();

        super.onStop();
    }
    @Override
    public void onDestroyView() {
        Log.i(TAG,"DabFragmentScan:: onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"DabFragmentScan:: onDestroy");
        super.onDestroy();
    }

    private void startBindService(){
        Log.i(TAG, "fragment ->Thread: " + Thread.currentThread().getName()
                + "  pid=" + android.os.Process.myPid());
        mDabServiceWrapper= DabServiceWrapper.newInstance(getActivity().getApplicationContext());
        if(mDabServiceWrapper.getDabServiceWrapperStatus()==0) {
            mDabServiceWrapper.bind();
        }else {
            Log.d(TAG, "RadioAppServiceWrapper already  run ,no bind");
        }
        mDabServiceWrapper.getScanFinishState().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "  maybe Backflow data  mScanFinishState = "
                        +mDabServiceWrapper.getScanFinishState().getValue());
                if(mDabServiceWrapper.getScanFinish()) {
                    scanOnDestroy();
                }
                mDabServiceWrapper.setScanFinishFalse();
            }
        });
    }

    private void scanProgramlist(){
        if (mFirst == 0) {
            mBand= DabSharePreference.getDabBand(getContext());
            Log.i(TAG, "scan -> [Band] index= "+ mBand);
            mDabServiceWrapper.onServiceScan(mBand);
        }else{
            firstOpenDelayScan();
            Log.i(TAG, "first start  -> open scan fragment ");
        }
    }

    private void scanOnDestroy() {
        if (mFirst == 0) {
            mTab.popDabFragmentScan();
        } else {
            Log.i(TAG, "scanOnDestroy:: destroy self , new DabFragment");
            mTab.replaceDabFragment();
        }
        mTab.setAfterScan(true);
    }

    private void firstOpenDelayScan(){
        mTimerStart = new Timer();
        mTaskStart = new TimerTask() {
            @Override
            public void run() {
                try {
                    mBand= DabSharePreference.getDabBand(getContext());
                    Log.i(TAG, "scan -> [Band] index= "+ mBand);
                    mDabServiceWrapper.onServiceScan(mBand);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mTimerStart.schedule(mTaskStart, 2000);
    }

    private void clearPngFile(){
        File dir= getContext().getDir("dab",Context.MODE_PRIVATE);
        if (!dir.exists())  return;
        if (!dir.isDirectory())   return;

        String[] fileList = dir.list();
        File png = null;

        for (int i = 0; i < fileList.length; i++) {
            if (dir.getAbsolutePath().endsWith(File.separator)) {
                png = new File(dir.getAbsolutePath() + fileList[i]);
            } else {
                png = new File(dir.getAbsolutePath() + File.separator + fileList[i]);
            }
            if (png.isFile()) {
                png.delete();
            }
        }
    }
}