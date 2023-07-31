package com.datong.radiodab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.logging.Logger;

public class DabMediaService extends MediaBrowserService{
    private static final String TAG = "DAB.MEDIA";
    private static final String MEDIA_TAG= "NCMR_DabDrmService";
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        setServiceForeground();
        initMediaSession();
    }

    private void initMediaSession() {
        Log.i(TAG, "initMediaSession");
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
        mMediaSession.setActive(true);
        updateMediaSession();
    }

    // mediasession 的使用
    private void updateMediaSession() {
        Log.i(TAG, "updateMediaSession");
        MediaMetadata.Builder builder = new MediaMetadata.Builder();
        builder.putString(MediaMetadata.METADATA_KEY_TITLE, "DAB -1");
        mMediaSession.setMetadata(builder.build());

        PlaybackState.Builder playbackStateBuilder = new PlaybackState.Builder();
        int playbackState = PlaybackState.STATE_PLAYING;
        playbackStateBuilder.setState(playbackState, 0, 0, SystemClock.uptimeMillis());
        mMediaSession.setPlaybackState(playbackStateBuilder.build());
    }
/*
    // mediasession 的使用
    private void updateMediaSession() {
        Log.i(TAG, "updateMediaSession");
        MediaMetadata.Builder builder = new MediaMetadata.Builder();
        builder.putString(MediaMetadata.METADATA_KEY_TITLE, mAMOperator.obtainNow().getName());
        mMediaSession.setMetadata(builder.build());
        RadioController radioController = ProviderFactory.get().providerFMRadio();
        PlaybackState.Builder playbackStateBuilder = new PlaybackState.Builder();
        int playbackState = radioController.isMute() ? PlaybackState.STATE_PAUSED : PlaybackState.STATE_PLAYING;
        playbackStateBuilder.setState(playbackState, 0, 0, SystemClock.uptimeMillis());
        mMediaSession.setPlaybackState(playbackStateBuilder.build());
    }
*/

    // 这里是你收到intent处理的地方
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            handleCommand(intent);
        }
        return START_STICKY;
    }



    private void handleCommand(Intent intent) {

    }

    private void setServiceForeground() {
        Log.i(TAG, "setServiceForeground...");
        startForeground(android.os.Process.myPid(), notification());
    }

    private Notification notification() {
        if (notification == null) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(
                    new NotificationChannel(getPackageName(), getPackageName(),
                            NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName());
            notification = builder.build();
        }
        return notification;
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
        }

        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onStop() {
            super.onStop();
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        super.onDestroy();
    }
}