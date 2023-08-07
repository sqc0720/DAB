package com.datong.radiodab;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.view.View.INVISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.textclassifier.SelectionSessionLogger;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.saicmotor.hardkey.KeyPolicyManager;
import com.saicmotor.power.SaicPowerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DabFragment extends Fragment implements View.OnClickListener,
                                        CategoryDialog.CategoryDialogListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String TAG = "DAB";

    private static DabFragment mFragment;
    // TODO: Rename and change types of parameters
    private String name;
    private DabTabFragment mTab;
    private int mIsFavorite;

    private ImageButton mImageButtonFavorite;
    private ImageButton mImageButtonPretrack;
    private ImageButton mImageButtonNexttrack;
    private ImageButton mImageButtonVoice;
    private boolean mIsPlay;
    private ImageButton mImageButtonPlay;
    private ImageView mImageViewCover;

    private DabButton mDabButtonCategory;
    private DabButton mDabButtonScan;
    private RadioGroup mListGroup;
    private RadioButton mRadioList;
    private RadioButton mRadioFavorite;
    private RecyclerView mRecyclerViewList;
    private TextView mTextViewName;
    private TextView mTextViewSec;
    private TextView mTextViewLabel;
    private TextView mTextViewHint;

    private DabStationContent mDabStationContent;

    private int mSelectedList;  // 0 =category list ; 1=favorite list
    private int mSelectedIndex;   //for select item
    private int mSelectedFavoriteIndex;   //for favorite select item

    private DabStationFavoriteDBEngine mFavoriteEngine;

    private long mFrequency;
    private long mServerId;
    //service
    private DabServiceWrapper mDabServiceWrapper;

    public DabFragment(DabTabFragment tab) {
        // Required empty public constructor
        super();
        mTab= tab;
        mDabServiceWrapper=null;
        mServerId =0L;
        mFrequency =0L;
    }

    public DabFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DabFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(Context context, DabTabFragment tab) {
        if (mFragment == null) {
            mFragment = new DabFragment(tab);
        }
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(TAG);
        }
        Log.d(TAG, "DadFragment  onCreate()");
        mSelectedList = 0;
        mSelectedFavoriteIndex= 0;
        //for data
        mDabStationContent= DabStationContent.getInstance(getActivity().getApplicationContext());
        registerKeyCallback();
        EventBus.getDefault().register(this);
        powerManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dab, container, false);
        mTextViewName= view.findViewById(R.id.dab_textview_name);
        mTextViewSec= view.findViewById(R.id.dab_textview_sec);
        mImageViewCover= view.findViewById(R.id.dab_imageview_cover);
        mTextViewLabel= view.findViewById(R.id.dab_textview_label);
        mImageButtonFavorite= view.findViewById(R.id.dab_imagebutton_favorite);
        mImageButtonPretrack= view.findViewById(R.id.dab_imagebutton_pretrack);
        mImageButtonNexttrack= view.findViewById(R.id.dab_imagebutton_nexttrack);
        mImageButtonVoice= view.findViewById(R.id.dab_imagebutton_voice);
        mImageButtonPlay= view.findViewById(R.id.dab_imagebutton_play);
        mDabButtonCategory= view.findViewById(R.id.dab_dabbutton_category);
        getDialogCategorylabel();
        mDabButtonScan= view.findViewById(R.id.dab_dabbutton_scan);
        mDabButtonScan.setOnDabButtonClickListener(new DabButton.OnDabButtonClickListener(){
            public void onClick(View v){
                Log.i(TAG,"<========   OnDabButtonClickListener on click ");
                //Toast.makeText(getActivity(), "Scan", Toast.LENGTH_SHORT).show();
                mTab.replaceDabFragmentScan();
            }
        });
        mListGroup = (RadioGroup) view.findViewById(R.id.dab_radiogroup_list);
        mRadioFavorite= (RadioButton) view.findViewById(R.id.dab_radiobutton_favorite);
        mRadioList= (RadioButton) view.findViewById(R.id.dab_radiobutton_list);
        //list
        mRecyclerViewList= (RecyclerView)view.findViewById(R.id.dab_recyclerview_list);
        mRecyclerViewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.d(TAG, "onScrolled dx="+dx+" dy="+dy);
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled ===dx="+dx+" dy="+dy);
            }
        });
        mTextViewHint= (TextView)view.findViewById(R.id.dab_textview_hint);
        initView();
        initDabStationContent();

        return view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResourceForLightDarkMode();

    }
    @SuppressLint("ResourceAsColor")
    private void updateResourceForLightDarkMode(){
        Log.d(TAG, "DadFragment ========== updateResourceForLightDarkMode()");

        mTextViewName.setTextColor(getResources().getColor(R.color.dab_name));
        mTextViewSec.setTextColor(getResources().getColor(R.color.dab_sec));
        mTextViewSec.setBackgroundResource(R.drawable.sec_frame);
        if(0==mIsFavorite)
            mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
        else
            mImageButtonFavorite.setImageResource(R.drawable.dab_icon_favorites);
        mImageButtonPretrack.setImageResource(R.drawable.dab_icon_pretrack);
        if(!mIsPlay)
            mImageButtonPlay.setImageResource(R.drawable.dab_icon_play);
        else
            mImageButtonPlay.setImageResource(R.drawable.dab_icon_stop);
        mImageButtonNexttrack.setImageResource(R.drawable.dab_icon_nexttrack);
        mImageButtonVoice.setImageResource(R.drawable.dab_icon_voice);
        mTextViewLabel.setTextColor(getResources().getColor(R.color.dab_label));

        mRadioFavorite.setTextColor(
                getResources().getColorStateList(R.color.dab_radiobutton_text_color,null));
        mRadioList.setTextColor(
                getResources().getColorStateList(R.color.dab_radiobutton_text_color,null));
        mDabButtonScan.redraw(R.drawable.dab_icon_scan);
        mDabButtonCategory.redraw(R.drawable.dab_icon_type);
        mTextViewHint.setTextColor(
                getResources().getColor(R.color.dab_text_hint));
        initRecyclerViewList();
    }

    //4_23
    private void initRecyclerViewList() {
        //实现水平滑动效果
        int size= 0;
        mSelectedIndex= 0;
        mSelectedFavoriteIndex=0;
        mSelectedList= mDabStationContent.getCurrentStationListType(); //0=category; 1=favorite
        if(mSelectedList==1) {
            size = mDabStationContent.getDabStationListFavoriteSize();
            mSelectedFavoriteIndex = mDabStationContent.getCurrentStation();
            mRadioFavorite.setChecked(true);
        }else {
            size = mDabStationContent.getDabStationListCategorySize();
            mSelectedIndex= mDabStationContent.getCurrentStation();
            mRadioList.setChecked(true);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewList.setLayoutManager(layoutManager);
        if(size==0){
            mRecyclerViewList.setVisibility(View.GONE);
            mTextViewHint.setVisibility(View.VISIBLE);
            if(mSelectedList==1)
                mTextViewHint.setText(R.string.dab_string_favorite_null);  //for favorite
            else
                mTextViewHint.setText(R.string.dab_string_station_null);
        }else {
            mRecyclerViewList.setVisibility(View.VISIBLE);
            mTextViewHint.setVisibility(View.GONE);
            DabProgramListAdapter adapter;
            if(mSelectedList==1) {  //favorite
                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListFavorite(),
                        mSelectedFavoriteIndex, true,
                        mDabStationContent.getDabStationListCategory(), mFavoriteEngine);
            }else{
                adapter = new DabProgramListAdapter(
                        mDabStationContent.getDabStationListCategory(),
                        mSelectedIndex, false,
                        mDabStationContent.getDabStationListFavorite(),
                        mDabStationContent.getEngineFavorite());
            }
            mRecyclerViewList.setAdapter(adapter);
            //play section
            DabStation item;
            if(mSelectedList==1)
                item= mDabStationContent.getDabStationListFavorite().get(mSelectedFavoriteIndex);
            else
                item= mDabStationContent.getDabStationListCategory().get(mSelectedIndex);
            mTextViewName.setText(item.getName());
            if(0==item.getSec())
                mTextViewSec.setVisibility(INVISIBLE);
            if(0==item.getFavorite())
                mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
            else
                mImageButtonFavorite.setImageResource(R.drawable.dab_icon_favorites);
            if(DabSharePreference.getDabIsPlaying(getContext()) == true) {//run flag
                mIsPlay = true;//stop flag
                mImageButtonPlay.setImageResource(R.drawable.dab_icon_stop);
                DabSharePreference.setDabIsPlaying(getContext(),false);
            }

            mServerId= item.getService_id();
            mFrequency= item.getFrequency();
            mIsFavorite= item.getFavorite();
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerViewList.getLayoutManager();
            if (mSelectedList == 0) {
                mRecyclerViewList.scrollToPosition(mDabStationContent.getDabStationListCategory().indexOf(item));
                mLayoutManager.scrollToPositionWithOffset(mDabStationContent.getDabStationListCategory().indexOf(item), 0);
            }else if (mSelectedList == 1) {
                mRecyclerViewList.scrollToPosition(mDabStationContent.getDabStationListFavorite().indexOf(item));
                mLayoutManager.scrollToPositionWithOffset(mDabStationContent.getDabStationListFavorite().indexOf(item), 0);
            }

            //for run after scan
            if(mTab!= null) {  //null point
                if (mTab.getAfterScan()) {
                    mTab.setAfterScan(false);
                    mIsPlay = false; //stop status
                    setPlayPauseButton(mImageButtonPlay); //stop-->run
                }
            }
        }
    }

    private void initView(){
        mIsFavorite= 0;
        mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
        mImageButtonFavorite.setOnClickListener(this);
        mIsPlay= false;
        mImageButtonPlay.setImageResource(R.drawable.dab_icon_play);
        mImageButtonPlay.setOnClickListener(this);
        mImageButtonPretrack.setOnClickListener(this);
        mImageButtonNexttrack.setOnClickListener(this);

        mDabButtonCategory.setOnDabButtonClickListener(new DabButton.OnDabButtonClickListener(){
            public void onClick(View v){
                Log.i(TAG,"<========   OnDabButtonClickListener on click ");
                DialogFragment category = new CategoryDialog(DabFragment.this, mDabButtonCategory.getTitle());
                category.showNow(getChildFragmentManager(), "category");
                category.setCancelable(false);
            }
        });
        mListGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(mRadioList.isChecked()) {
                    creatCategoryList(mDabStationContent.getDabStationListCategorySize());
                }
                if(mRadioFavorite.isChecked()) {
                    creatFavoriteList(mDabStationContent.getDabStationListFavoriteSize());
                }
            }
        });
    }

    private boolean isServiceLive(){
        if(mDabServiceWrapper== null)
            return false;
        if(mDabServiceWrapper.getDabServiceWrapperStatus()==0)
            return false;
        return true;
    }

    private void setPreNextStation(int direction) {  //1 pre  ;2=next
        //check
        if ((direction != 1) && (direction != 2))
            return;
        if ((mSelectedList != 0) && (mSelectedList != 1))
            return;
        //list is null
        if ((mSelectedList == 0) && (mDabStationContent.getDabStationListCategorySize() == 0))
            return;
        if ((mSelectedList == 1) && (mDabStationContent.getDabStationListFavoriteSize() == 0))
            return;
        if (!isServiceLive())
            return;
        // category list  left  header
        int id = mListGroup.getCheckedRadioButtonId();
        if (id == R.id.dab_radiobutton_list) {
            if (direction == 1) {
                if (mSelectedIndex <= 0) {
                    mSelectedIndex = mDabStationContent.getDabStationListCategorySize() - 1;
                } else {
                    mSelectedIndex = mSelectedIndex - 1;
                }
            } else if (direction == 2) {
                if (mSelectedIndex >= (mDabStationContent.getDabStationListCategorySize() - 1)) {
                    mSelectedIndex = 0;
                } else {
                    mSelectedIndex = mSelectedIndex + 1;
                }
            }
        } else if (id == R.id.dab_radiobutton_favorite) {
            if (direction == 1) {
                if ((mSelectedFavoriteIndex <= 0)) {
                    mSelectedFavoriteIndex = mDabStationContent.getDabStationListFavoriteSize() - 1;
                } else {
                    mSelectedFavoriteIndex = mSelectedFavoriteIndex - 1;
                }
            } else if (direction == 2) {
                if (mSelectedFavoriteIndex >= (mDabStationContent.getDabStationListFavoriteSize() - 1)) {
                    mSelectedFavoriteIndex = 0;
                } else {
                    mSelectedFavoriteIndex = mSelectedFavoriteIndex + 1;
                }
            }
        }
        DabStation station=null;
        DabProgramListAdapter adapter;
        if (id == R.id.dab_radiobutton_list) { // for category
            station = mDabStationContent.getDabStationListCategoryItem(mSelectedIndex);
            adapter = (DabProgramListAdapter) mRecyclerViewList.getAdapter();
            if (!adapter.getFavorite()) {
                adapter.setmSelectedIndex(mSelectedIndex);
            }
        } else if (id == R.id.dab_radiobutton_favorite) { // for favorite
            station = mDabStationContent.getDabStationListFavoriteItem(mSelectedFavoriteIndex);
            adapter = (DabProgramListAdapter) mRecyclerViewList.getAdapter();
            if (adapter.getFavorite()) {
                adapter.setmSelectedIndex(mSelectedFavoriteIndex);
            }
        } else {
            return;
        }
        if (station != null) {
            mIsPlay = true;
            mServerId = station.getService_id();
            mFrequency = station.getFrequency();
            mIsFavorite = station.getFavorite();
            mImageButtonPlay.setImageResource(R.drawable.dab_icon_stop);
            mTextViewName.setText(station.getName());
            if (station.getFavorite() >= 1)
                mImageButtonFavorite.setImageResource(R.drawable.dab_icon_favorites);
            else
                mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);

            if (station.getSec() >= 1)
                mTextViewSec.setVisibility(View.VISIBLE);
            else
                mTextViewSec.setVisibility(INVISIBLE);
            relieveMuteState(getContext());

            mDabStationContent.setCurrentStation(mServerId,mFrequency);
            mDabServiceWrapper.onServiceTune(mServerId, mFrequency, DabSharePreference.getDabBand(getContext()));

            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerViewList.getLayoutManager();
            if (mSelectedList == 0) {
               // mRecyclerViewList.scrollToPositionWithOffset(mDabStationContent.getDabStationListCategory().indexOf(station),-50);
                mLayoutManager.scrollToPosition(mDabStationContent.getDabStationListCategory().indexOf(station));
              //  mLayoutManager.scrollToPosition(100);
           //  mLayoutManager.scrollToPositionWithOffset(mDabStationContent.getDabStationListCategory().indexOf(station),+300);


            }else if (mSelectedList == 1) {
                mRecyclerViewList.scrollToPosition(mDabStationContent.getDabStationListFavorite().indexOf(station));
                mLayoutManager.scrollToPositionWithOffset(mDabStationContent.getDabStationListFavorite().indexOf(station), 0);
            }
        }
    }

    private boolean isStationListNull(){
        int size=0;
        if(mSelectedList==1) {  //0=category; 1=favorite
            size = mDabStationContent.getDabStationListFavoriteSize();
        }else {
            size = mDabStationContent.getDabStationListCategorySize();
        }
        if((mFrequency==0) &&(mServerId==0)&&( size==0))
            return true;
        else
            return false;
    }

    private void setPlayPauseButton(ImageButton v){
        if(isStationListNull()){  //list is null and station text is null
            v.setImageResource(R.drawable.dab_icon_play);
            mIsPlay = false;
            return;
        }
        if(isServiceLive()) {
            if (mIsPlay) {
                v.setImageResource(R.drawable.dab_icon_play);
                mDabServiceWrapper.onServiceCancel();
                mIsPlay = !mIsPlay;
                //run--> stop operation
            } else {
                //stop-->run
                v.setImageResource(R.drawable.dab_icon_stop);
                mDabServiceWrapper.onServiceTune(mServerId, mFrequency, DabSharePreference.getDabBand(getContext()));
                mIsPlay = true;

            }
        }
        Log.i(TAG,"<========   play/pause radio  =========>");
    }

    private void setFavoriteButton(){
        //upper limition
        if((mIsFavorite<=0)&& mDabStationContent.checkDabStationListFavoriteSizeUpper()) {
            showFavoriteUpperToast();
            return;
        }
        //check
        if( ((mSelectedList==0)&&(mDabStationContent.getDabStationListCategorySize()<=0))
            ||((mSelectedList==0)&&(mDabStationContent.getDabStationListCategorySize()<=mSelectedIndex)) )
            return;
        if( ((mSelectedList==1)&&(mDabStationContent.getDabStationListFavoriteSize() <=0))
            ||((mSelectedList==1)&&(mDabStationContent.getDabStationListFavoriteSize() <=mSelectedFavoriteIndex)) )
            return;

        //value
        if (mIsFavorite>=1) { //favorite==> none
            mIsFavorite =0;
            mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
        } else {  //none==>favorite
            mIsFavorite =1;
            mImageButtonFavorite.setImageResource(R.drawable.dab_icon_favorites);
        }

        DabProgramListAdapter adapter= (DabProgramListAdapter) mRecyclerViewList.getAdapter();
        if(mSelectedList==0){ //list all
            DabStation station=mDabStationContent.getDabStationListCategoryItem(mSelectedIndex);
            station.setFavorite(mIsFavorite);
            DabStation stationfavorite= null;
            if(mIsFavorite==0){ //favorite-->none
                for(int i=0; i<mDabStationContent.getDabStationListFavoriteSize(); i++){
                    stationfavorite=mDabStationContent.getDabStationListFavoriteItem(i);
                    if( station.getName().equals(stationfavorite.getName())
                            &&(station.getFrequency()==stationfavorite.getFrequency())
                            &&(station.getService_id()==stationfavorite.getService_id())
                            &&(station.getSec()==stationfavorite.getSec()) ){
                        mDabStationContent.removeDabStationListFavoriteItem(stationfavorite);
                        mFavoriteEngine.deleteDabStations(stationfavorite);
                        if (adapter!= null) //update ui list
                            adapter.setFavoriteOperation();
                        break;
                    }
                }
            }else {  //none-->favorite
                int i=0;
                for(; i<mDabStationContent.getDabStationListFavoriteSize() ; i++) {
                    stationfavorite = mDabStationContent.getDabStationListFavoriteItem(i);
                    if (station.getName().equals(stationfavorite.getName())
                            && (station.getFrequency() == stationfavorite.getFrequency())
                            && (station.getService_id() == stationfavorite.getService_id())
                            && (station.getSec() == stationfavorite.getSec())) {
                        break;
                    }
                }
                if(i==mDabStationContent.getDabStationListFavoriteSize() ){
                    mDabStationContent.addDabStationListFavoriteItem(station);
                    mFavoriteEngine.addDabStations(station);
                    if (adapter!= null)
                        adapter.setFavoriteOperation();
                }
            }
        }else{ // play list= favorite
            DabStation stationfavorite;
            DabStation station;
            if(mIsFavorite==0) { //favorit-->none
                stationfavorite = mDabStationContent.getDabStationListFavoriteItem(mSelectedFavoriteIndex);
                mFavoriteEngine.deleteDabStations(stationfavorite);
                for (int i = 0; i < mDabStationContent.getDabStationListCategorySize(); i++) { //update list
                    station = mDabStationContent.getDabStationListCategoryItem(i);
                    if (station.getName().equals(stationfavorite.getName())
                            && (station.getFrequency() == stationfavorite.getFrequency())
                            && (station.getService_id() == stationfavorite.getService_id())
                            && (station.getSec() == stationfavorite.getSec())) {
                        station.setFavorite(0);
                        break;
                    }
                }
                mDabStationContent.removeDabStationListFavoriteItem(mSelectedFavoriteIndex);
                if(mDabStationContent.getDabStationListFavoriteSize() <=0){
                    mSelectedFavoriteIndex=-1;
                }else{
                    if(mSelectedFavoriteIndex>0)
                        mSelectedFavoriteIndex=mSelectedFavoriteIndex-1;
                    else
                        mSelectedFavoriteIndex= 0;
                }
                //for delete stations
                //mFavoriteEngine.deleteDabStations(stationfavorite);
                Log.i(TAG,"<========   on favorite =====remove = "+ mSelectedFavoriteIndex);
            }else {//none-->favorite
                DabStation tmp=null;
                DabStation tmp1=null;
                for (int i = 0; i < mDabStationContent.getDabStationListCategorySize(); i++) { //update list
                    station = mDabStationContent.getDabStationListCategoryItem(i);
                    if (station.getName().equals(mTextViewName.getText().toString())
                            && (station.getFrequency() == mFrequency)
                            && (station.getService_id() == mServerId)) {
                        station.setFavorite(1);
                        tmp= station;
                        break;
                    }
                }
                for (int i = 0; i < mDabStationContent.getDabStationListFavoriteSize(); i++) {
                    stationfavorite = mDabStationContent.getDabStationListFavoriteItem(i);
                    if (mTextViewName.getText().toString().equals(stationfavorite.getName())
                            && (mFrequency == stationfavorite.getFrequency())
                            && (mServerId == stationfavorite.getService_id())) {
                        stationfavorite.setFavorite(1);
                        tmp1 = stationfavorite;
                        adapter.setmSelectedIndex(i);
                        mSelectedFavoriteIndex= i;
                        Log.i(TAG,"<========   on favorite ===== except= "+i);
                        break;
                    }
                }
                Log.i(TAG,"<========   on favorite =====0 ");
                if(tmp1==null){
                    if(tmp==null){
                        int sec=0;
                        if( mTextViewSec.getVisibility()==View.VISIBLE)
                            sec= 1;
                        tmp=new DabStation(mTextViewName.getText().toString(),
                                mIsFavorite, sec, mServerId, mFrequency,0);  //silas temper
                        Log.i(TAG,"<========   on favorite =====1 ");
                    }
                    mDabStationContent.addDabStationListFavoriteItem(tmp);
                    mFavoriteEngine.addDabStations(tmp);
                    adapter.setmSelectedIndex(mDabStationContent.getDabStationListFavoriteSize() -1);
                    mSelectedFavoriteIndex=mDabStationContent.getDabStationListFavoriteSize() -1;
                    Log.i(TAG,"<========   on favorite =====2 ");
                }
            }
            if (adapter != null) {
                adapter.setFavoriteOperation();
                Log.i(TAG,"<========   on favorite =====3 ");
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i=v.getId();
        Log.i(TAG,"<========   on click ");
        //favorite
        if(i== R.id.dab_imagebutton_favorite) {
            setFavoriteButton();
        }else if(i== R.id.dab_imagebutton_pretrack) {  //previouse have bug
            setPreNextStation(1);
            //mute()
            Log.i(TAG,"<========   previouse radio  =========>");
        }else if(i== R.id.dab_imagebutton_play) {
            setPlayPauseButton((ImageButton)v);
        }else if(i== R.id.dab_imagebutton_nexttrack) {   //have bug
            setPreNextStation(2);
            Log.i(TAG,"<========   next radio  =========>");
        }
        Log.i(TAG,"              on click  =========>");
    }

    public void onDialogCategoryClick(CategoryDialog dialog){
        mDabButtonCategory.setTitle(dialog.getCategory());
    }

    private void getDialogCategorylabel(){
        int index= DabSharePreference.getDabCategory(this.getContext());
        switch(index){
            case 0:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_all));         break;
            case 1:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_news));        break;
            case 2:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_talk));        break;
            case 3:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_sports));      break;
            case 4:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_popular));     break;
            case 5:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_classic));     break;
            case 6:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_no_pty));      break;
            default:
                mDabButtonCategory.setTitle(getString(R.string.dab_string_all));
        }
    }

    private void creatFavoriteList(int num){
        int selected= 0;
        DabStation item;
        if(num==0){
            mRecyclerViewList.setVisibility(View.GONE);
            mTextViewHint.setVisibility(View.VISIBLE);
            mTextViewHint.setText(R.string.dab_string_favorite_null);
        }else {
            mRecyclerViewList.setVisibility(View.VISIBLE);
            mTextViewHint.setVisibility(View.GONE);
            DabProgramListAdapter adapter;
//            if (mSelectedList == 0) { // for category
//                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListFavorite(), mDabStationContent.findFavoriteListSelectPosition(), true, mDabStationContent.getDabStationListCategory(), mFavoriteEngine);
//            } else if (mSelectedList == 1) {
//                if (mSelectedFavoriteIndex < 0) mSelectedFavoriteIndex = 0;
//                else if (mSelectedFavoriteIndex >= mDabStationContent.getDabStationListFavoriteSize())
//                    mSelectedFavoriteIndex = mDabStationContent.getDabStationListFavoriteSize() - 1;
//                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListFavorite(), mDabStationContent.findFavoriteListSelectPosition(), true, mDabStationContent.getDabStationListCategory(), mFavoriteEngine);
//            } else {
//                mSelectedList = 0;
//                mSelectedIndex = 0;
//                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListFavorite(), 1, true, mDabStationContent.getDabStationListCategory(), mFavoriteEngine);
//            }
            mSelectedFavoriteIndex = mDabStationContent.findFavoriteListSelectPosition();
            adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListFavorite(), mDabStationContent.findFavoriteListSelectPosition(), true, mDabStationContent.getDabStationListCategory(), mFavoriteEngine);
            mRecyclerViewList.setAdapter(adapter);
        }
    }
    private void creatCategoryList(int num) {
        if(num==0){
            mRecyclerViewList.setVisibility(View.GONE);
            mTextViewHint.setVisibility(View.VISIBLE);
            mTextViewHint.setText(R.string.dab_string_station_null);
        }else {
            mRecyclerViewList.setVisibility(View.VISIBLE);
            mTextViewHint.setVisibility(View.GONE);
            DabProgramListAdapter adapter;
//            if (mSelectedList == 0) { // for category
//                if (mSelectedIndex < 0) mSelectedIndex = 0;
//                else if (mSelectedIndex >= mDabStationContent.getDabStationListCategorySize())
//                    mSelectedIndex = mDabStationContent.getDabStationListCategorySize() - 1;
//                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListCategory(), mDabStationContent.findStationListSelectPosition(), false, mDabStationContent.getDabStationListFavorite(), mFavoriteEngine);
//            } else if (mSelectedList == 1) { // for favorite
//                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListCategory(), mDabStationContent.findStationListSelectPosition(), false, mDabStationContent.getDabStationListFavorite(), mFavoriteEngine);
//            } else {
//                mSelectedList = 0;
//                mSelectedIndex = 0;
//                adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListCategory(), -1, false, mDabStationContent.getDabStationListFavorite(), mFavoriteEngine);
//            }
            mSelectedIndex = mDabStationContent.findStationListSelectPosition();
            adapter = new DabProgramListAdapter(mDabStationContent.getDabStationListCategory(), mSelectedIndex, false, mDabStationContent.getDabStationListFavorite(), mFavoriteEngine);

            mRecyclerViewList.setAdapter(adapter);
        }
    }

    private void showFavoriteUpperToast(){
        View toastRoot = getLayoutInflater().inflate(R.layout.dab_toast_layout, null);
        Toast toast = new Toast(getActivity());
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {

        startBindService();
        Log.d(TAG, "DadFragment  onStart()");
        updateUIForMediaCard();
        super.onStart();
    }

    private void startBindService(){
        Log.i(TAG, "fragment ->Thread: " + Thread.currentThread().getName()
                + "  pid=" + android.os.Process.myPid());
        mDabServiceWrapper= DabServiceWrapper.newInstance(getActivity().getApplicationContext());
        if(mDabServiceWrapper.getDabServiceWrapperStatus()==0)
            mDabServiceWrapper.bind();
        else
            Log.d(TAG, "RadioAppServiceWrapper already  run ,no bind");

        mDabServiceWrapper.getTuneImageID().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                int id=mDabServiceWrapper.getTuneImageID().getValue();
                File dir= getContext().getDir("dab",Context.MODE_PRIVATE);
                String file=dir.getAbsolutePath()+"/"+id+".png";
                Log.d(TAG, " image file "+file);
                Bitmap bm= BitmapFactory.decodeFile(file);
                mImageViewCover.setImageBitmap(bm);
            }
        });
        mDabServiceWrapper.getTuneLabel().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                String label=mDabServiceWrapper.getTuneLabel().getValue();
                mTextViewLabel.setText(label);
                Log.d(TAG, "label = "+label);
            }
        });

        mDabServiceWrapper.getAudioFocusStatus().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                // current only support when tune, get audio focus failed;
                mImageButtonPlay.setImageResource(R.drawable.dab_icon_play);
                mIsPlay = false;
            }
        });

        mDabServiceWrapper.getTunerStatus().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                int value= mDabServiceWrapper.getTunerStatus().getValue();
                if(value==0) {
                    mImageButtonPlay.setImageResource(R.drawable.dab_icon_play);
                    mIsPlay = false;
                }else{
                    mImageButtonPlay.setImageResource(R.drawable.dab_icon_stop);
                    mIsPlay = true;
                }
            }
        });


    }

    @Override
    public void onStop() {
        Log.d(TAG, "DadFragment  onStop()");
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(Integer cmd) {
        Log.i(TAG, "eventbus  ===>>onReceiveMsg: " + cmd);
        switch(cmd.intValue()){
            case 0:     //click one station item
                {
                DabStation station;
                if(((DabProgramListAdapter) mRecyclerViewList.getAdapter()).getFavorite()) {
                    mSelectedList = 1;
                    mDabStationContent.setCurrentStationList(1);
                }else {
                    mSelectedList = 0;
                    mDabStationContent.setCurrentStationList(0);
                }
                Log.i(TAG,"current mSelectedList ===>>"+mSelectedList);
                int index = ((DabProgramListAdapter) mRecyclerViewList.getAdapter()).getSelectedIndex();
                if (mSelectedList == 0) {
                    mSelectedIndex = index;
                    station = mDabStationContent.getDabStationListCategoryItem(mSelectedIndex);
                } else if (mSelectedList == 1) {
                    mSelectedFavoriteIndex = index;
                    station = mDabStationContent.getDabStationListFavoriteItem(mSelectedFavoriteIndex);
                } else {
                    return;
                }

                mTextViewName.setText(station.getName());
                if (station.getFavorite() >= 1)
                    mImageButtonFavorite.setImageResource(R.drawable.dab_icon_favorites);
                else
                    mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
                if (station.getSec() >= 1)
                    mTextViewSec.setVisibility(View.VISIBLE);
                else
                    mTextViewSec.setVisibility(INVISIBLE);
                mServerId = station.getService_id();
                mFrequency = station.getFrequency();
                mIsFavorite=station.getFavorite();
                if (isServiceLive()) {
                    if (!mIsPlay) {
                        mImageButtonPlay.setImageResource(R.drawable.dab_icon_stop);
                        mIsPlay = !mIsPlay;
                    }
                    mDabStationContent.setCurrentStation(mServerId,mFrequency);
                    mDabServiceWrapper.onServiceTune(mServerId, mFrequency, DabSharePreference.getDabBand(getContext()));
                }
                Log.i(TAG, "<========list   play  radio  =========>");
                }
                break;
            case 1:     //click icon favorite
                if(mSelectedList==0) {  //play list=all
                    DabStation station = mDabStationContent.getDabStationListCategoryItem(mSelectedIndex);
                    if (station.getFavorite() >= 1) {
                        mImageButtonFavorite.setImageResource(R.drawable.dab_icon_favorites);
                        mIsFavorite=1;
                    }else {
                        mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
                        mIsFavorite=0;
                    }
                }else { //play list=favorite
                    DabStation station;
                    DabProgramListAdapter adapter = (DabProgramListAdapter) mRecyclerViewList.getAdapter();
                    if (adapter.getFavorite() == false) {  //ui list= all
                        for (int i = 0; i < mDabStationContent.getDabStationListCategorySize(); i++) {
                            station = mDabStationContent.getDabStationListCategoryItem(i);
                            if ((station.getFrequency() == mFrequency) && (station.getService_id() == mServerId)) {
                                if (mIsFavorite != station.getFavorite()) {
                                    mIsFavorite = station.getFavorite();
                                    mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
                                    mIsFavorite=0;
                                }
                            }
                        }
                    } else {//ui list= favorite
                        if (mDabStationContent.getDabStationListFavoriteSize() == 0) {
                            creatFavoriteList(0);
                            mSelectedFavoriteIndex = -1;
                            mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
                            mIsFavorite=0;
                        } else {
                            mSelectedFavoriteIndex = adapter.getSelectedIndex();
                            int i = 0;
                            for (; i < mDabStationContent.getDabStationListFavoriteSize() ; i++) {
                                station = mDabStationContent.getDabStationListFavoriteItem(i);
                                if ((station.getFrequency() == mFrequency) && (station.getService_id() == mServerId)) {
                                    break;
                                }
                            }
                            if(i==mDabStationContent.getDabStationListFavoriteSize() ){
                                mImageButtonFavorite.setImageResource(R.drawable.dab_icon_none_favorites);
                                mIsFavorite=0;
                            }
                        }
                    }
                }
                break;
            case 2:
                showFavoriteUpperToast();
                break;
            case 3:  //update station list for category
                mDabStationContent.getDabStationCategory();
                mDabStationContent.checkStationList();
                updateRecyclerViewList();
                break;
            case 4: // recreate DialogFragment  for light/dark color
                DialogFragment category = new CategoryDialog(DabFragment.this, mDabButtonCategory.getTitle());
                category.showNow(getChildFragmentManager(), "category");
                category.setCancelable(false);
                break;
            default:
        }
    }
    //mDabStationContent.getDabStationListCategorySize()
    private void updateRecyclerViewList() {
        mSelectedIndex= mDabStationContent.checkStationCategoryList(mServerId,mFrequency);
        if(mSelectedList==0) { //0=category; 1=favorite
            if (mDabStationContent.getDabStationListCategorySize() == 0) {
                mRecyclerViewList.setVisibility(View.GONE);
                mTextViewHint.setVisibility(View.VISIBLE);
                mTextViewHint.setText(R.string.dab_string_station_null);
            } else {
                mRecyclerViewList.setVisibility(View.VISIBLE);
                mTextViewHint.setVisibility(View.GONE);
                DabProgramListAdapter adapter = new DabProgramListAdapter(
                        mDabStationContent.getDabStationListCategory(), mSelectedIndex, false,
                        mDabStationContent.getDabStationListFavorite(),
                        mDabStationContent.getEngineFavorite());
                mRecyclerViewList.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DadFragment  onDestroy()");
        EventBus.getDefault().unregister(this);
        unRegisterKeyCallback();
        super.onDestroy();
    }

    //for database  =====>>
    private void initDabStationContent(){
        mFavoriteEngine= DabStationFavoriteDBEngine.newInstance(getContext());
        new DabStationAsyncTask().execute();
    }

    class DabStationAsyncTask extends AsyncTask<Void, Void, Void> {
        public DabStationAsyncTask() {}
        @Override
        protected Void doInBackground(Void... voids) {
            mDabStationContent.getDabStationAll();
            mDabStationContent.getDabStationFavorite();
            mDabStationContent.getDabStationCategory();
            mDabStationContent.checkStationList();
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            initRecyclerViewList();
        }
    }
    //for physics key click
    private KeyPolicyManager.OnKeyCallBackListener mKeyCallback= new KeyPolicyManager.OnKeyCallBackListener(){
        @Override
        public void onKeyEventCallBack(KeyEvent keyEvent) {
            if(keyEvent.getAction()==KeyEvent.ACTION_UP) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        Log.d(TAG, "key previouse");
                        setPreNextStation(1);  //1 pre  ;2=next
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        Log.d(TAG, "key next");
                        setPreNextStation(2);  //1 pre  ;2=next
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        Log.d(TAG, "key play-pause");
                        if (mImageButtonPlay != null)
                            setPlayPauseButton(mImageButtonPlay);
                        break;
                    default:
                        Log.d(TAG, "receive other key ");
                        break;
                }
            }
        }
    };
    private void registerKeyCallback(){
        Log.d(TAG, "registerKeyEventCallback");
        KeyPolicyManager keyPolicyManager = KeyPolicyManager.getInstance(getActivity().getApplicationContext());
        keyPolicyManager.registerKeyCallBack(mKeyCallback,"DAB",getActivity().getPackageName());
    }

    private void unRegisterKeyCallback(){
        Log.d(TAG, "registerKeyEventCallback");
        KeyPolicyManager keyPolicyManager = KeyPolicyManager.getInstance(getActivity().getApplicationContext());
        keyPolicyManager.unRegisterKeyCallBack(mKeyCallback,"DAB",getActivity().getPackageName());
    }
    //for database  <<=====
    // audio controll >>
    private  void relieveMuteState(Context context){
        Log.d("TAG", "relieveMuteState");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DAB_PREV_NEXT_UNMUTE");
        context.sendBroadcast(intent);
    }
    // audio controll <<
    private  void powerManager() {
        try {
            SaicPowerManager.getInstance().registerListener(1, new SaicPowerManager.OnPowerStateListener() {
                @Override
                public void onCallback(int opeCode, int powerState) {
                    Log.i(TAG, "SaicPowerManager::powerState:"+powerState);
                    if (SaicPowerManager.POWER_TEMP_RUN == powerState) {
                        Log.i(TAG, "send POWER_TEMP_RUN......");
                    } else if (SaicPowerManager.POWER_RUN == powerState) {//silas wakeup
                        Log.i(TAG, "send POWER_ON......");
                        if(DabSharePreference.getDabIsPlaying(getContext()) == true) {//run flag
                            mIsPlay = false;//stop flag
                            setPlayPauseButton(mImageButtonPlay); //stop-->run
                            DabSharePreference.setDabIsPlaying(getContext(),false);
                        }
                    } else if (SaicPowerManager.POWER_ABNORMAL == powerState) {
                        Log.i(TAG, "send VOLTAGE_ABNORMAL......");
                    } else if (SaicPowerManager.POWER_STAND_BY == powerState) { //silas standby
                        if(mIsPlay == true) {//run flag
                            setPlayPauseButton(mImageButtonPlay); //run -->stop
                            DabSharePreference.setDabIsPlaying(getContext(),true);
                        }
                    }
                }
            });
        }catch (RemoteException e){
            Log.i(TAG, "SaicPowerManager  error !!!");
        };
    }

    private void updateUIForMediaCard(){
        //for desktop media card
        int status= mDabStationContent.getStatusPauseOrPlay();
        Log.d(TAG, "DadFragment  onStart::updateUIForMediaCard()::status="+status);
        switch(status){
            case 0:
                mImageButtonPlay.setImageResource(R.drawable.dab_icon_stop);
                mIsPlay =true;
                break;
            case 1:
                mImageButtonPlay.setImageResource(R.drawable.dab_icon_play);
                mIsPlay =false;
                break;
            default:
                break;
        }
        mDabStationContent.setStatusReset();
        long id= mDabStationContent.getCurrentStationID();
        long frequency= mDabStationContent.getCurrentStationFrequency();
        Log.d(TAG, "DadFragment  onStart::updateUIForMediaCard()::id= "+id+" frequency="+frequency);
        if((id != mServerId)||(frequency!=mFrequency)) {
            Log.d(TAG, "DadFragment  onStart::updateUIForMediaCard()");
            initRecyclerViewList();
        }
    }
}