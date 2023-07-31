package com.datong.radiodab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;


/**
 * {@link IDabService} wrapper to abstract out some nuances of interactions
 * with remote services.
 */
public class DabServiceWrapper {
    private static final String TAG = "DAB.SERVICEWRAPPER";
    private Context mContext;
    private static DabServiceWrapper mServiceWrapper;
    //0=init; 1= already binded;
    private int mServiceWrapperStatus;
    private IDabService mDabService;
    private boolean mScanFinish;
    private final MutableLiveData<Integer> mScanFinishState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mTuneImageID = new MutableLiveData<>();
    private final MutableLiveData<String> mTuneLabel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mAudioFocusStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> mTunerStatus = new MutableLiveData<>();


    public static DabServiceWrapper newInstance(Context context) {
        if (mServiceWrapper == null) {
            mServiceWrapper = new DabServiceWrapper(context);
        }
        return mServiceWrapper;
    }

    private DabServiceWrapper(@NonNull Context context) {
        mContext= Objects.requireNonNull(context);
        mServiceWrapperStatus= 0;
        mScanFinish= false;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            DabServiceWrapper.this.onServiceConnected(binder,
                    Objects.requireNonNull(IDabService.Stub.asInterface(binder)));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            //onServiceFailure();
            mDabService= null;
            Log.i(TAG, "  onServiceDisconnected():: a connection to the Service has been lost."+
                    className.toString());
        }
    };

    public void bind() {
        Log.d(TAG, "RadioAppServiceWrapper bind");
        Intent intent = new Intent(DabService.ACTION_DAB_SERVICE, null,
                mContext, DabService.class);
        mContext.startForegroundService(intent);
        if (!mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
            throw new RuntimeException("Failed to bind to RadioAppService");
        }
        /*
        Log.d(TAG, "DabMediaService >>>");
        Intent it=new Intent(mContext, DabMediaService.class);
        mContext.startService(it);
        Log.d(TAG, "DabMediaService <<<");
         */
    }
    public void unbind() {
        if (mContext == null) {
            throw new IllegalStateException("This is not a remote service wrapper, you can't unbind it");
        }
        mContext.unbindService(mServiceConnection);
        Intent intent = new Intent(DabService.ACTION_DAB_SERVICE, null,
                mContext, DabService.class);
        mContext.stopService(intent);
    }
    public int getDabServiceWrapperStatus(){
        return mServiceWrapperStatus;
    }
    //===========================callback===========================

    private final IDabCallback mCallback = new IDabCallback.Stub() {
        public void ICallbackProgramListChanged(List<DabStation> list){
        }

        public void ICallbackScanFinishStateChanged(int state, List<DabStation> list){
            Log.d(TAG, "ICallbackScanFinishStateChanged   ==>scan update list ="+list.size());

            DabStationContent content= DabStationContent.getInstance(mContext);
            content.updateDabStationAll(list);
            mScanFinish=true;
            mScanFinishState.postValue(state);
        }

        public void ICallbackTuneImage(int id){
            mTuneImageID.postValue(id);
        }

        public void ICallbackTuneLabel(String msg){
            mTuneLabel.postValue(msg);
        }

        public void ICallbackAudioFocusFailed(){
            mAudioFocusStatus.postValue(0);  //always failed;
        }

        public void ICallbackTunerPlay(){
            mTunerStatus.postValue(1);
        }

        public void ICallbackTunerStop(){
            mTunerStatus.postValue(0);
        }
    };

    //===========================status throught==================================
    @NonNull
    public LiveData<Integer> getScanFinishState() {
        return mScanFinishState;
    }
    public LiveData<Integer> getAudioFocusStatus() {
        return mAudioFocusStatus;
    }
    public LiveData<Integer> getTunerStatus() {
        return mTunerStatus;
    }
    public boolean getScanFinish() {
        return mScanFinish;
    }
    public void setScanFinishFalse() {
        mScanFinish= false;
    }
    @NonNull
    public LiveData<Integer> getTuneImageID() {
        return mTuneImageID;
    }
    @NonNull
    public LiveData<String> getTuneLabel() {
        return mTuneLabel;
    }

    //===========================operation=========================
    private void onServiceConnected(IBinder binder, @NonNull IDabService service) {
        Log.d(TAG, "RadioAppService connected");
        mDabService= service;
        try {
            int error= mDabService.IServiceGetStatus();
            if(error != 0)
                throw  new IllegalStateException(" [aero] service is error ");
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IServiceAddCallback() error");
        }
        try {
            mDabService.IServiceAddCallback(mCallback);
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IServiceAddCallback() error");
        }
        mServiceWrapperStatus= 1;
    }

    public void onServiceScan(int index){
        mScanFinish=false;
        if(mDabService== null)
            return ;
        try {
            mDabService.IServiceStationProgramList(index);
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IDabStationDbEngine() error");
        }
    }

    public void onServiceTune(long severId, long frequency, int index){
        if(mDabService== null)
            return ;
        try {
            mDabService.IServiceTune(severId, frequency,index);
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IServiceTune() error");
        }
    }

    public void onServiceCancel(){
        if(mDabService== null)
            return ;
        try {
            mDabService.IServiceCancel();
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IServiceTune() error");
        }
    }






    /*
    public void onServiceDataBase(){
        try {
            mDabService.IServiceStationDbEngine();
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IServiceStationDbEngine() error");
        }
    }*/
/*
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_NOT_SUPPORTED = 3;
    public static final int STATE_ERROR = 4;

    /**
     * Application state.
     *
     /
    @IntDef(value = {
            STATE_CONNECTING,
            STATE_CONNECTED,
            STATE_NOT_SUPPORTED,
            STATE_ERROR,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectionState {}

    @Nullable
    private final AtomicReference<IDabService> mService = new AtomicReference<>();

    //================================================================================

    private void onServiceFailure() {
        if (mService.getAndSet(null) == null) return;
        Log.e(TAG, "RadioAppService failed " + (mContext == null ? "(local)" : "(remote)"));
        //mConnectionState.postValue(STATE_ERROR);
    }
    private interface ServiceVoidOperation {
        void execute(@NonNull IDabService service) throws RemoteException;
    }

    private void callService(@NonNull ServiceVoidOperation op) {
        IDabService service = mService.get();
        if (service == null) {
            throw new IllegalStateException("Service is not connected");
        }
        try {
            op.execute(service);
        } catch (RemoteException e) {
            Log.e(TAG, "Remote call failed", e);
            onServiceFailure();
        }
    }

    @NonNull
    public LiveData<ProgramInfo> getCurrentProgram() {
        return mCurrentProgram;
    }

    @NonNull
    public LiveData<List<ProgramInfo>> getProgramList() {
        return mProgramList;
    }
    */
}
