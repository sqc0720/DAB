package com.datong.radiodab;

import static android.hardware.radio.ProgramSelector.IDENTIFIER_TYPE_DAB_FREQUENCY;
import static android.hardware.radio.ProgramSelector.IDENTIFIER_TYPE_DAB_SID_EXT;
import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.webkit.WebViewZygote.getPackageName;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioTuner;
import android.hardware.radio.RadioManager.ProgramInfo;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.util.Log;

import android.hardware.radio.RadioManager;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.saicmotor.hardkey.KeyPolicyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DabService extends MediaBrowserService {
    private static final String TAG = "DAB.SERVICE";
    public static String ACTION_DAB_SERVICE = "com.datong.radiodab.ACTION_DAB_SERVICE";
    private static final int HARDCODED_DAB_INDEX = 1;

    private int mErrorStatus;

    private final HandlerThread mCallbackHandlerThread = new HandlerThread("dab.cbhandler");
    private RadioTuner mTuner;
    private RadioManager mRadioManager;
    private ProgramList mProgramList;
    private List<RadioManager.ModuleProperties> mModules;
    private final Object mLock = new Object();
    private boolean mAudioFocus;


    private  IDabCallback mIDabCallback ;

    //for media session
    private static final String MEDIA_TAG= "NCMR_DabDrmService";
    private static final String EXTRACMD_PLAYSTATUS= "PLAYSTATUS";
    private Notification mNotification;
    private MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private DabStationContent mDabStationContent;

    public DabService() {
        mIDabCallback= null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"Service -> onCreate, Thread: " + Thread.currentThread().getName()
                + "  pid=" + android.os.Process.myPid());
        mCallbackHandlerThread.start();
        mErrorStatus= 0;
        mAudioFocus=false;
        getSystemService();
        getModules();
        openTuner( mHardwareCallback,null);
        super.onCreate();
        setServiceForeground();
        initMediaSession();
    }

    @Override
    //not used
    public int onStartCommand(Intent intent, int flags, int startId) {//not used
        Log.i(TAG, "Service -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
        setServiceForeground();
        //for other component call
        if(intent != null) {
            if (intent.getStringExtra(EXTRACMD_PLAYSTATUS).equals("DAB_PLAY")) {
                servicePlay();
                Log.i(TAG, "Service -> onStartCommand, servicePlay " );
            }
        }
        return START_NOT_STICKY;
    }

    private void setServiceForeground() {
        Log.i(TAG, "setServiceForeground...");
        startForeground(android.os.Process.myPid(), notification());
    }

    private Notification notification() {
        if (mNotification == null) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(
                    new NotificationChannel(getPackageName(), getPackageName(),
                            NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName());
            mNotification = builder.build();
        }
        return mNotification;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service -> onBind, Thread: " + Thread.currentThread().getName()
                + "  pid=" + android.os.Process.myPid());
        if (ACTION_DAB_SERVICE.equals(intent.getAction())) {
            return mBinder;
        }
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service -> onUnbind, from:" + intent.getStringExtra("from"));
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service -> onDestroy, Thread: " + Thread.currentThread().getName());
        close();
        stopForeground(true);
        super.onDestroy();
    }
//=======================call back=======================
    private RadioTuner.Callback mHardwareCallback = new RadioTuner.Callback() {
        @Override
        public void onProgramInfoChanged(RadioManager.ProgramInfo info){
            int type= 0;  // 1= image  2= label;
            int id=0;
            String msg= null;
            Objects.requireNonNull(info);
            Log.d(TAG, "Program info changed(): "+info.toString());
            if(info.getMetadata().containsKey("android.hardware.radio.metadata.ICON")) {  //image
                type = 1;
                id= info.getMetadata().getInt("android.hardware.radio.metadata.ICON");
            }
            if(info.getMetadata().containsKey("android.hardware.radio.metadata.ARTIST")) { //label
                type = 2;
                msg= info.getMetadata().getString("android.hardware.radio.metadata.ARTIST");
            }
            try {
                if(mIDabCallback!= null) {
                    switch(type) {
                        case 1:
                            Log.i(TAG, "Service ->Dab Callback  Image");
                            saveImageID(id);
                            mIDabCallback.ICallbackTuneImage(id);
                            break;
                        case 2:
                            mIDabCallback.ICallbackTuneLabel(msg);
                            Log.i(TAG, "Service ->Dab Callback  Label");
                            break;
                        default:
                            break;
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, "  call service IServiceStationDbEngine() error");
            }

        }
        @Override
        public void onTuneFailed(int result, @Nullable ProgramSelector selector) {
            Objects.requireNonNull(selector);
            Log.i(TAG, "onTuneFailed() result: "+ selector.toString()); //DAB
        }
        @Override
        public void onTrafficAnnouncement(boolean active) {
            Log.i(TAG, "onTrafficAnnouncement() : "+ active);
        }
        @Override
        public void onEmergencyAnnouncement(boolean active) {
            Log.i(TAG, "onEmergencyAnnouncement() : "+ active);
        }
        @Override
        public void onAntennaState(boolean connected) {
            Log.i(TAG, "onAntennaState() connected: "+ connected);
        }
        @Override
        public void onControlChanged(boolean control) {
            Log.i(TAG, "onControlChanged() control: "+ control);
            if (!control) onHardwareError();
        }
        @Override
        public void onBackgroundScanAvailabilityChange(boolean isAvailable) {
            Log.i(TAG, "onBackgroundScanAvailabilityChange() isAvailable: "+ isAvailable);
        }
        @Override
        public void onBackgroundScanComplete() {
            Log.i(TAG, "onBackgroundScanComplete()  ");
        }
        @Override
        public void onProgramListChanged() {
            Log.i(TAG, "onProgramListChanged()  ");
        }

        @Override
        public void onParametersUpdated(@NonNull Map<String, String> parameters) {
            for (String key : parameters.keySet()) {
                Log.i(TAG, "onParametersUpdated::key= "+ key + " and value= " + parameters.get(key));
            }
        }
    };

    IDabService.Stub mBinder = new IDabService.Stub() {
        @Override
        public void IServiceAddCallback(IDabCallback callback) {
            mIDabCallback= callback;
        }
        @Override
        public int IServiceGetStatus() {
            return mErrorStatus;
        }

        @Override
        public void IServiceStationProgramList(int index){
            if(setAudioFoucs()) {
                getProgramList(index);
            }
        }

        @Override
        public void IServiceTune(long severId, long frequency, int index){
            tune(severId, frequency,index);
            Log.i(TAG, "Service -> IServiceTune");
        }
        public void IServiceCancel(){
            tuneCancel();
        }
    };

    private void onTuneFailed(int result, @Nullable ProgramSelector selector) {
        synchronized (mLock) {
            Objects.requireNonNull(selector);
            Log.i(TAG, "Service -> onTuneFailed,result="+result+":" +selector.toString());
        }
    }

    private void onProgramInfoChanged(RadioManager.ProgramInfo info) {
        synchronized (mLock) {
            Log.i(TAG, "Service -> onProgramInfoChanged::"+info.toString());
        }
    }

//===============operation===========================================
    private void getSystemService() {
        mRadioManager = (RadioManager)this.getSystemService("broadcastradio");
        Objects.requireNonNull(mRadioManager, "RadioManager could not be loaded");
        if(mRadioManager==null){
            Log.e(TAG, "Service -> getSystemService,[RadioManager could not be loaded]");
            mErrorStatus= 1;
        }else {
            Log.i(TAG, "Service -> getSystemService,ok ");
        }

    }
    private void getModules() {
        synchronized (mLock) {
            if (mModules != null) return;
            mModules = new ArrayList<>();
            int status = mRadioManager.listModules(mModules);
            if (status != RadioManager.STATUS_OK) {
                Log.e(TAG, "Couldn't get radio module list: " + status);
                mErrorStatus= 2;
                return;
            }
            if (mModules.size() == 0) {
                Log.e(TAG, "No radio modules on this device");
                mErrorStatus= 3;
                return;
            }else{
                Log.i(TAG, "radio module num="+mModules.size());
            }

            RadioManager.ModuleProperties module = mModules.get(HARDCODED_DAB_INDEX);
            Log.i(TAG, "1:"+ module.toString()); //DAB
            RadioManager.BandDescriptor des[] =module.getBands();
            Log.i(TAG, "bands" +module.getDabFrequencyTable().toString());
            //module = mModules.get(0);
            //Log.i(TAG, "0:"+ module.toString()); //DAB
            //Log.i(TAG, "bands" +module.getDabFrequencyTable().toString());
        }
    }

    private void openTuner(RadioTuner.Callback callback, Handler handler) {
        Log.i(TAG, "Opening broadcast dab session...");
        Handler hwHandler = new Handler(mCallbackHandlerThread.getLooper());
        RadioManager.ModuleProperties module = mModules.get(HARDCODED_DAB_INDEX);
        DabTunerCallbackAdapterExt cbExt = new DabTunerCallbackAdapterExt(callback, handler);

        mTuner = mRadioManager.openTuner(
                module.getId(),
                null,  // BandConfig - let the service automatically select one.
                true,  // withAudio
                cbExt, hwHandler);

        if (mTuner == null) {
            Log.e(TAG, "Service -> openTuner,failed !!!! ");
            mErrorStatus= 4;
            return;
        }

        Log.i(TAG, "Service -> openTuner,ok ");
        cbExt.setTuneFailedCallback(this::onTuneFailed);
        //cbExt.setProgramInfoCallback(this::onProgramInfoChanged);

    }
    private void close() {
        synchronized (mLock) {
            if (mProgramList != null) {
                mProgramList.close();
                mProgramList = null;
            }
            if (mTuner != null) {
                mTuner.close();
                mTuner = null;
            }
        }
    }

    private void getProgramList(int index){
        Map<String, String> m = new HashMap<String, String>();
        switch (index){
            case 0:
                m.put("DAB", "3");break;
            case 1:
                m.put("3_DAB", "4");break;
            case 2:
                m.put("L_DAB", "5");break;
            default:
                Log.e(TAG, "Service -> [getDabBand] error ");
                return;
        }
        mTuner.setParameters(m);

        Log.i(TAG, "Service -> [getDabBand] index= "+ index);
        Log.i(TAG, "Service -> [getDynamicProgramList] ,start ");
        mProgramList = mTuner.getDynamicProgramList(null);
        if (mProgramList != null) {
            mProgramList.registerListCallback(new ProgramList.ListCallback() {
                @Override
                public void onItemChanged(@NonNull ProgramSelector.Identifier id) {
                    Log.i(TAG, "Service -> change[ProgramList]:: "+id.toString());//silas
                }
                public void onItemRemoved(@NonNull ProgramSelector.Identifier id) {
                    Log.i(TAG, "Service -> Removed[ProgramList]:: "+id.toString()); //silas
                }
            });
            mProgramList.addOnCompleteListener(this::pushProgramListUpdate);
        }
    }

    private void pushProgramListUpdate() {
        List<ProgramInfo> plist = mProgramList.toList();
        Log.i(TAG, "Service ->getDynamicProgramList   [finish]  size "+plist.size());
        for(int i=0;i<plist.size();i++) {
            Log.i(TAG, "Service -> "+plist.get(i).toString());
            Log.i(TAG, "Service -> vendor::" +plist.get(i).getVendorInfo().toString());
        }
        try {
            if(mIDabCallback!= null) {
                mIDabCallback.ICallbackScanFinishStateChanged(1,ProgramInfo2DabStation(plist));
                Log.i(TAG, "Service ->Dab Callback  finish");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "  call service IServiceStationDbEngine() error");
        }
    }

    private void tune(long severId,long frequency, int index){
        Map<String, String> m = new HashMap<String, String>();
        switch (index){
            case 0:
                m.put("DAB", "3");break;
            case 1:
                m.put("3_DAB", "4");break;
            case 2:
                m.put("L_DAB", "5");break;
            default:
                Log.e(TAG, "Service -> [getDabBand] error ");
                return;
        }
        mTuner.setParameters(m);
        Log.i(TAG, "Service -> [getDabBand] index= "+ index);
        ProgramSelector.Identifier pId=
                new ProgramSelector.Identifier(IDENTIFIER_TYPE_DAB_SID_EXT,severId  );
        ProgramSelector.Identifier sId=
                new ProgramSelector.Identifier(IDENTIFIER_TYPE_DAB_FREQUENCY ,frequency);
        ProgramSelector.Identifier[] sIds={sId};

        ProgramSelector selector=new ProgramSelector(
                ProgramSelector.PROGRAM_TYPE_DAB, pId, sIds, null);

        //audio focus
        if(setAudioFoucs()){   //get focus
            mTuner.tune(selector);
            mTuner.setMute(false);
            updateMediaSession(PlaybackState.STATE_PLAYING);
        }else{
            CallbackAudioFocusFailed();
        }
    }

    private void CallbackAudioFocusFailed(){
        try {
            if(mIDabCallback!= null) {
                mIDabCallback.ICallbackAudioFocusFailed();
                Log.i(TAG, "Service ->Dab Callback audio foucs Failed");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "service call CallbackAudioFocusFailed() error");
        }
    }

    private void CallbackTunerStop(){
        try {
            if(mIDabCallback!= null) {
                mIDabCallback.ICallbackTunerStop();
                Log.i(TAG, "Service ->Dab Callback Tuner stop ");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "service call CallbackTunerStop() error");
        }
    }

    private void CallbackTunerPlay(){
        try {
            if(mIDabCallback!= null) {
                mIDabCallback.ICallbackTunerPlay();
                Log.i(TAG, "Service ->Dab Callback Tuner Play ");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "service call CallbackTunerPlay() error");
        }
    }

    private void tuneCancel(){
        mTuner.setMute(true);
        mTuner.cancel();
        updateMediaSession(PlaybackState.STATE_PAUSED);
    }

    private List<DabStation> ProgramInfo2DabStation(List<ProgramInfo> programs){
        List<DabStation> dabList = new ArrayList<>();
        String name;
        int favorite=0;
        String sec;
        int pty;
        long service_id;
        long frequency;
        Log.i(TAG, " program size= "+ programs.size());
        for(int i= 0; i<programs.size(); i++){
            ProgramInfo pro= programs.get(i);
            service_id= pro.getSelector().getPrimaryId().getValue();
            frequency= pro.getSelector().getSecondaryIds()[0].getValue();
            pty= pro.getMetadata().getInt("android.hardware.radio.metadata.RDS_PTY");
            name= pro.getMetadata().getString("android.hardware.radio.metadata.DAB_SERVICE_NAME");
            sec= pro.getVendorInfo().get("com.aero.sec");
            Log.i(TAG, " program : "+ name +" : "+ frequency  +"  :"+service_id
                    +"  :"+sec+"  :"+pty);
            if(sec.equals("false"))   //silas
                dabList.add(new DabStation( name,0,0,service_id,frequency,pty));
            else
                dabList.add(new DabStation( name,0,1,service_id,frequency,pty));
        }
        return dabList;
    }

    private void onHardwareError() {
        close();
        stopSelf();
    }

    private void saveImageID(int id){
        Bitmap bm= mTuner.getMetadataImage(id);
        if(bm== null) {
            Log.d(TAG, "bitmap is null ");
            return ;
        }
        File dir= getDir("dab",Context.MODE_PRIVATE);

        String name= id+".png";
        File image= new File(dir,name);
        Log.d(TAG, "Save dir=>>"+ dir+ "  name= " + name);
        if(image== null)
            Log.d(TAG, "image is null ");
        try {
            FileOutputStream out = new FileOutputStream(image); //保存到本地，格式为JPEG
            if (bm.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException e.toString: "+e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException e.toString: "+e.toString());
            e.printStackTrace();
        }
    }
    //======================================================
    private boolean setAudioFoucs(){
        if(mAudioFocus == true)
            return true;
        AudioManager manager= (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if(manager==null)
            return false;
        //1、创建声音焦点 requestBuilder
        AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
        attributesBuilder.setUsage(26) //AudioAttributes.USAGE_DAB) // 表明是那种音源类型：这里是语音识别类型
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);//上下文，一般用不上
        //2、这个参数 AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)下面会进行详细说明
        AudioFocusRequest request;
        AudioFocusRequest.Builder requestBuilder = new AudioFocusRequest.Builder(AUDIOFOCUS_GAIN);//申请声音焦点类型
        requestBuilder.setAudioAttributes(attributesBuilder.build())
                .setAcceptsDelayedFocusGain(false) //是否接受延迟获取声音焦点
                //设置对声音焦点监听
                .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        //3、对声音焦点状态变化进行处理
                        Log.d(TAG, "AudioFocus change : " + focusChange);
                        switch(focusChange) {
                            case AUDIOFOCUS_GAIN:
                                Log.d(TAG, "AudioFocus change : AUDIOFOCUS_GAIN" );
                                mAudioFocus = true;
                                break;
                            case AUDIOFOCUS_LOSS:
                            case AUDIOFOCUS_LOSS_TRANSIENT:
                            case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                mAudioFocus = false;
                                tuneCancel();
                                CallbackAudioFocusFailed();
                                break;
                            default:
                                break;

                        }
                    }
                });
        request = requestBuilder.build();
        //4、向AudioManager发起声音焦点申请。
        if( manager.requestAudioFocus(request)== AUDIOFOCUS_REQUEST_GRANTED) {
            Log.i(TAG , "AudioFoucs success" );
            mAudioFocus = true;
        }else{
            Log.i(TAG , "AudioFoucs failed" );
            mAudioFocus = false;
        }
        return mAudioFocus;
    }
    //=============================list station============================
    private void servicePlay() {
        if(setAudioFoucs()) {
            long id = mDabStationContent.getCurrentStationID();
            long frequency = mDabStationContent.getCurrentStationFrequency();
            if ((id <= 0L) || (frequency <= 0L))
                return;
            CallbackTunerPlay();
            tune(id, frequency, 0);
            mDabStationContent.setStatusPlay();

        }
    }


    //=====================================================
    //for media session
    private void initMediaSession() {
        Log.i(TAG, "initMediaSession");
        mDabStationContent= DabStationContent.getInstance(getApplicationContext());
        // 初始化，第一个参数为context，第二个参数为String类型tag
        mMediaSession = new MediaSession(this, MEDIA_TAG); //MEDIA_TAG= "NCMR_DabDrmService";
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSession.setCallback(mCallback);
        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());
    }

    private void getPlayList(){
       // mMediaSession.setQueue();
    }

    // mediasession 的使用
    private void updateMediaSession(int playbackState) { //PlaybackState.STATE_PLAYING;
        Log.i(TAG, "updateMediaSession");
        mMediaSession.setActive(true);
        MediaMetadata.Builder builder = new MediaMetadata.Builder();
        String title="DAB "+getMediaCardName();
        Log.i(TAG, "updateMediaSession:: name="+title);
        builder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        mMediaSession.setMetadata(builder.build());
        PlaybackState.Builder playbackStateBuilder = new PlaybackState.Builder();
        playbackStateBuilder.setState(playbackState, 0, 0,
                                                        SystemClock.uptimeMillis());
        mMediaSession.setPlaybackState(playbackStateBuilder.build());
    }


    @Nullable
    @Override
    public MediaBrowserService.BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        //根据包名对每个访问端做一些访问权限判断等
        Log.i(TAG, "MediaBrowserService.BrowserRoot onGetRoot...");
        return new MediaBrowserService.BrowserRoot("_Root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result) {
        // 根据parentMediaId返回播放列表相关信息
        //根据parentId来返回第三放App所需要获得媒体数据
        Log.i(TAG, "MediaBrowserService. onLoadChildren ...");
    }

    // callback
    private final MediaSession.Callback mCallback = new MediaSession.Callback() {
        @Override
        public void onCommand(@NonNull String command, @Nullable Bundle args, @Nullable ResultReceiver cb) {
            super.onCommand(command, args, cb);
            Log.i(TAG, "MediaBrowserService:: onCommand ...");
        }

        @Override
        public void onPlay() {
            super.onPlay();
            Log.i(TAG, "MediaBrowserService:: onPlay ..."); //status::pause-->play
            long id= mDabStationContent.getCurrentStationID();
            long frequency= mDabStationContent.getCurrentStationFrequency();
            if((id<=0L)||(frequency<=0L))
                return;
            tune(id,frequency,0);
            mDabStationContent.setStatusPlay();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            Log.i(TAG, "MediaBrowserService:: onPlayFromMediaId ...");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.i(TAG, "MediaBrowserService:: onPause ..."); //status::play-->pause
            tuneCancel();
            mDabStationContent.setStatusPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            Log.i(TAG, "MediaBrowserService:: onSkipToNext ..");
            playMediaCardNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            Log.i(TAG, "MediaBrowserService:: onSkipToPrevious ..");
            playMediaCardPrevious();
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.i(TAG, "MediaBrowserService:: onStop ..");
        }
    };

    // return list size
    private int checkMediaCardProgramList(){
        int type= mDabStationContent.getCurrentStationListType();
        //for service run    and    UI not
        if((mDabStationContent.getDabStationListAllSize()==0)&&
                (mDabStationContent.getDabStationListFavoriteSize()==0)){
            if(type == 0){//0=all; 1= favorite
                mDabStationContent.getDabStationAll();
                if(mDabStationContent.getDabStationListAllSize()>0)
                    mDabStationContent.getDabStationCategory();
            }else if(type == 1){
                mDabStationContent.getDabStationFavorite();
            }
        }
        if(type == 0){//0=all; 1= favorite
            return mDabStationContent.getDabStationListAllSize();
        }else if(type == 1){
            return mDabStationContent.getDabStationListFavoriteSize();
        }
        return 0;
    }

    public String getMediaCardName(){
        if(checkMediaCardProgramList()==0) {
            Log.i(TAG, "getMediaCardName ..1");
            return null;
        }

        int type= mDabStationContent.getCurrentStationListType();

        long id= mDabStationContent.getCurrentStationID();
        long frequency= mDabStationContent.getCurrentStationFrequency();
        Log.i(TAG, "getMediaCardName ..type ="+type+" id ="+id+"frequency ="+frequency);
        DabStation station;
        if(type == 0){//0=all; 1= favorite
            for(int i=0; i< mDabStationContent.getDabStationListAllSize(); i++){
                station = mDabStationContent.getDabStationListAllItem(i);
                Log.i(TAG, "All .. id ="+station.getService_id()+"frequency ="+station.getFrequency());
                if((station.getFrequency()==frequency)&&(station.getService_id()==id))
                    return station.getName();
            };
        }else if(type == 1){
            for(int i=0; i< mDabStationContent.getDabStationListFavoriteSize(); i++){
                station = mDabStationContent.getDabStationListFavoriteItem(i);
                Log.i(TAG, "Favorite .. id ="+station.getService_id()+"frequency ="+station.getFrequency());
                if((station.getFrequency()==frequency)&&(station.getService_id()==id))
                    return station.getName();
            };
        }
        return null;
    }

    public void playMediaCardPrevious(){
        if(checkMediaCardProgramList()==0)
            return ;
        Log.i(TAG, "MediaBrowserService:: onSkipToPrevious ..2");
        int type= mDabStationContent.getCurrentStationListType();
        long id= mDabStationContent.getCurrentStationID();
        long frequency= mDabStationContent.getCurrentStationFrequency();
        DabStation station;
        if(type == 0){//0=all; 1= favorite
            for(int i=0; i< mDabStationContent.getDabStationListCategorySize(); i++){
                station = mDabStationContent.getDabStationListCategoryItem(i);
                if((station.getFrequency()==frequency)&&(station.getService_id()==id)) {
                    if(i>=1) {
                        station = mDabStationContent.getDabStationListCategoryItem(i-1);
                        mDabStationContent.setCurrentStation(station.getService_id(),
                                station.getFrequency());
                        tune(station.getService_id(), station.getFrequency(),0);
                        mDabStationContent.setStatusPlay();
                        Log.i(TAG, "MediaBrowserService:: onSkipToPrevious ..3");
                        return ;
                    }
                }
            };
        }else if(type == 1){
            for(int i=0; i< mDabStationContent.getDabStationListFavoriteSize(); i++){
                station = mDabStationContent.getDabStationListFavoriteItem(i);
                if((station.getFrequency()==frequency)&&(station.getService_id()==id)) {
                    if(i>=1) {
                        station = mDabStationContent.getDabStationListFavoriteItem(i-1);
                        mDabStationContent.setCurrentStation(station.getService_id(),
                                station.getFrequency());
                        tune(station.getService_id(), station.getFrequency(),0);
                        mDabStationContent.setStatusPlay();
                        Log.i(TAG, "MediaBrowserService:: onSkipToPrevious ..4");
                        return ;
                    }
                }
            };
        }
        Log.i(TAG, "MediaBrowserService:: onSkipToPrevious ..5");
        return ;
    }

    public void playMediaCardNext(){
        if(checkMediaCardProgramList()==0)
            return ;
        int type= mDabStationContent.getCurrentStationListType();
        long id= mDabStationContent.getCurrentStationID();
        long frequency= mDabStationContent.getCurrentStationFrequency();
        DabStation station;
        if(type == 0){//0=all; 1= favorite
            for(int i=0; i< mDabStationContent.getDabStationListCategorySize(); i++){
                station = mDabStationContent.getDabStationListCategoryItem(i);
                if((station.getFrequency()==frequency)&&(station.getService_id()==id)) {
                    if(i<(mDabStationContent.getDabStationListCategorySize()-1)) {
                        station = mDabStationContent.getDabStationListCategoryItem(i+1);
                        mDabStationContent.setCurrentStation(station.getService_id(),
                                station.getFrequency());
                        tune(station.getService_id(), station.getFrequency(),0);
                        mDabStationContent.setStatusPlay();
                        return ;
                    }
                }
            };
        }else if(type == 1){
            for(int i=0; i< mDabStationContent.getDabStationListFavoriteSize(); i++){
                station = mDabStationContent.getDabStationListFavoriteItem(i);
                if((station.getFrequency()==frequency)&&(station.getService_id()==id)) {
                    if(i<(mDabStationContent.getDabStationListFavoriteSize()-1)) {
                        station = mDabStationContent.getDabStationListFavoriteItem(i+1);
                        mDabStationContent.setCurrentStation(station.getService_id(),
                                station.getFrequency());
                        tune(station.getService_id(), station.getFrequency(),0);
                        mDabStationContent.setStatusPlay();
                        return ;
                    }
                }
            };
        }
        return ;
    }


}