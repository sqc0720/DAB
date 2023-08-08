package com.datong.radiodab;
import android.car.Car;
import android.car.media.CarAudioManager;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class VoiceSettingsController {
    private static final String TAG = VoiceSettingsController.class.getSimpleName();
    private CarAudioManager mCarAudioManager;
    private AudioManager mAudioManager;
    private Context mContext;
    private Car mCar;

    public VoiceSettingsController(Context context) {
        mContext = context;
        mAudioManager = mContext.getSystemService(AudioManager.class);
        mCar = Car.createCar(mContext);
        if (mCar != null) {
            mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
        } else {
            Log.d(TAG, "mCar is null!");
        }
    }

    public void registerCarVolumeCallback(CarAudioManager.CarVolumeCallback carVolumeCallback) {
        if (mCarAudioManager != null) {
            mCarAudioManager.registerCarVolumeCallback(carVolumeCallback);
        }
    }

    /**
     * 获取音量组音量（媒体、蓝牙通话、导航、铃声、语音),默认值为12，范围为0-31
     *
     * @param type:0-多媒体、1-导航、3-蓝牙通话、4-铃声、5-语音
     */
    public int getVolumeGroupIndex(int type) {
        int volumeGroupDefault = 6;
        if (mCarAudioManager == null) {
            Log.d(TAG, "getVolumeGroupIndex mCarAudioManager is null");
            return volumeGroupDefault;
        }
        try {
            int temp = mCarAudioManager.getGroupVolume(type);
            Log.d(TAG, "getVolumeGroupIndex#type=" + type + ", value=" + temp);
            if (-100 == temp) {
                Log.d(TAG, "音量值返回为-100");
            } else {
                int volumeGroupMax = 32;
                int volumeGroupMin = 0;
                if (temp >= volumeGroupMin && temp <= volumeGroupMax) {
                    Log.d(TAG, "getVolumeGroupIndex#type=" + type + ", value=" + temp);
                    return temp;
                }
                Log.d(TAG, "获取的音量组音量越界");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volumeGroupDefault;
    }

    /**
     * 设置音量组音量
     *
     * @param type:0-多媒体、1-导航、3-蓝牙通话、4-铃声、5-语音
     * @param index：音量值[0,31]
     */
    public void setVolumeGroupIndex(int type, int index) {
        try {
            Log.d(TAG, "setVolumeGroupIndex#type=" + type + " value=" + index);
            mCarAudioManager.setGroupVolume(type, index, AudioManager.FLAG_PLAY_SOUND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    *//**
     * 多媒体音量是否静音
     *
     * @return
     *//*
    public boolean isMute() {
        try {
            boolean isMute = mAudioManager.isMasterMute();
            Log.d(TAG, "isMasterMute:" + isMute);
            return isMute *//*&& getGroupID() == 0*//*;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    *//**
     * 多媒体音量解除静音
     *
     * @return
     *//*
    public void closeMasterMute() {
        try {
            boolean isMute = mAudioManager.isMasterMute();
            if (isMute) {
                mAudioManager.setMasterMute(false, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}