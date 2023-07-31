package com.datong.radiodab;

import android.app.Activity;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager.ProgramInfo;
import android.hardware.radio.RadioMetadata;
import android.media.session.PlaybackState;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;


public class DabController {
    private static final String TAG = "DAB.CONTROLLER";

    private final Object mLock = new Object();
//    private final Activity mActivity;
//
//    private final DabServiceWrapper mAppService = new DabServiceWrapper();
  //  private final DabStorage mRadioStorage;

    @Nullable private ProgramInfo mCurrentProgram;
/*
    public DabController(@NonNull Activity activity) {
        mActivity = Objects.requireNonNull(activity);

       // mRadioStorage = DabStorage.getInstance(activity);
       // mRadioStorage.getFavorites().observe(activity, this::onFavoritesChanged);

     //   mAppService.getCurrentProgram().observe(activity, this::onCurrentProgramChanged);
      //  mAppService.getConnectionState().observe(activity, this::onConnectionStateChanged);
    }*/


}



