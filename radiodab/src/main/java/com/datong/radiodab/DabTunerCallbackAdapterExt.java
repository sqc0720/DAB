package com.datong.radiodab;

import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioMetadata;
import android.hardware.radio.RadioTuner;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.android.car.radio.util.Log;
//import com.android.internal.annotations.GuardedBy;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class DabTunerCallbackAdapterExt extends RadioTuner.Callback {
        private static final String TAG = "DAB.TUNER.CALLBACKEXT";
        private static final int INIT_TIMEOUT_MS = 10000;  // 10s

        private final Object mInitLock = new Object();
        private boolean mIsInitialized = false;

        private final RadioTuner.Callback mCallback;
        private final Handler mHandler;

        private final AtomicReference<TuneFailedCallback> mTuneFailedCallback = new AtomicReference<>();
        private final Object mProgramInfoLock = new Object();
        @GuardedBy("mProgramInfoLock")
        private ProgramInfoCallback mProgramInfoCallback;
        @GuardedBy("mProgramInfoLock")
        private RadioManager.ProgramInfo mCachedProgramInfo;

        interface TuneFailedCallback {
            void onTuneFailed(int result, @Nullable ProgramSelector selector);
        }

        interface ProgramInfoCallback {
            void onProgramInfoChanged(RadioManager.ProgramInfo info);
        }

        DabTunerCallbackAdapterExt(@NonNull RadioTuner.Callback callback, @Nullable Handler handler) {
            mCallback = Objects.requireNonNull(callback);
            if (handler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            } else {
                mHandler = handler;
            }
        }

        public boolean waitForInitialization() {
            synchronized (mInitLock) {
                if (mIsInitialized) return true;
                try {
                    mInitLock.wait(INIT_TIMEOUT_MS);
                } catch (InterruptedException ex) {
                    // ignore the exception, as we check mIsInitialized anyway
                }
                return mIsInitialized;
            }
        }

        void setTuneFailedCallback(TuneFailedCallback cb) {
            mTuneFailedCallback.set(cb);
        }

        void setProgramInfoCallback(ProgramInfoCallback cb) {
            synchronized (mProgramInfoLock) {
                mProgramInfoCallback = cb;
                if (mProgramInfoCallback != null && mCachedProgramInfo != null) {
                    Log.d(TAG, "Invoking callback with cached ProgramInfo");
                    mProgramInfoCallback.onProgramInfoChanged(mCachedProgramInfo);
                    mCachedProgramInfo = null;
                }
            }
        }

        @Override
        public void onError(int status) {
            mHandler.post(() -> mCallback.onError(status));
        }

        @Override
        public void onTuneFailed(int result, @Nullable ProgramSelector selector) {
            TuneFailedCallback cb = mTuneFailedCallback.get();
            if (cb != null) {
                cb.onTuneFailed(result, selector);
            }
            mHandler.post(() -> mCallback.onTuneFailed(result, selector));
        }

        @Override
        public void onConfigurationChanged(RadioManager.BandConfig config) {
            mHandler.post(() -> mCallback.onConfigurationChanged(config));
            if (mIsInitialized) return;
            synchronized (mInitLock) {
                mIsInitialized = true;
                mInitLock.notifyAll();
            }
        }

        public void onProgramInfoChanged(RadioManager.ProgramInfo info) {
            synchronized (mProgramInfoLock) {
                if (mProgramInfoCallback == null) {
                    // Cache the ProgramInfo until the callback is set. This workaround is needed
                    // because a TunerCallbackAdapterExt needed to call RadioManager.openTuner(), but
                    // the return of that function is needed to create a RadioManagerExt, which calls
                    // sets the callback through setProgramInfoCallback().
                    Log.d(TAG, "ProgramInfo callback is not set yet; caching ProgramInfo  silas");
                    mCachedProgramInfo = info;
                } else {
                    mProgramInfoCallback.onProgramInfoChanged(info);
                }
            }
            mHandler.post(() -> mCallback.onProgramInfoChanged(info));
        }

        public void onMetadataChanged(RadioMetadata metadata) {
            mHandler.post(() -> mCallback.onMetadataChanged(metadata));
        }

        public void onTrafficAnnouncement(boolean active) {
            mHandler.post(() -> mCallback.onTrafficAnnouncement(active));
        }

        public void onEmergencyAnnouncement(boolean active) {
            mHandler.post(() -> mCallback.onEmergencyAnnouncement(active));
        }

        public void onAntennaState(boolean connected) {
            mHandler.post(() -> mCallback.onAntennaState(connected));
        }

        public void onControlChanged(boolean control) {
            mHandler.post(() -> mCallback.onControlChanged(control));
        }

        public void onBackgroundScanAvailabilityChange(boolean isAvailable) {
            mHandler.post(() -> mCallback.onBackgroundScanAvailabilityChange(isAvailable));
        }

        public void onBackgroundScanComplete() {
            mHandler.post(() -> mCallback.onBackgroundScanComplete());
        }

        public void onProgramListChanged() {
            mHandler.post(() -> mCallback.onProgramListChanged());
        }

        public void onParametersUpdated(@NonNull Map<String, String> parameters) {
            mHandler.post(() -> mCallback.onParametersUpdated(parameters));
        }
    }
