package com.datong.radiodab;

import android.content.Context;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioTuner;

import android.util.Log;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import java.util.Objects;
import java.util.stream.Stream;


public class DabTunerExt {
    private static final String TAG = "DAB.TUNEREXT";

    private final RadioTuner mTuner;
    private final Object mLock = new Object();
    private final Object mTuneLock = new Object();


   // private HwAudioSource mHwAudioSource;

    @Nullable private ProgramSelector mOperationSelector;  // null for seek operations
    @Nullable private TuneCallback mOperationResultCb;

    /**
     * A callback handling tune/seek operation result.
     */
    public interface TuneCallback {
        /**
         * Called when tune operation finished.
         *
         * @param succeeded States whether the operation succeeded or not.
         */
        void onFinished(boolean succeeded);

        /**
         * Chains other result callbacks.
         */
        default TuneCallback alsoCall(@NonNull TuneCallback other) {
            return succeeded -> {
                onFinished(succeeded);
                other.onFinished(succeeded);
            };
        }
    }

    DabTunerExt(@NonNull Context context, @NonNull RadioTuner tuner,
                  @NonNull DabTunerCallbackAdapterExt cbExt) {
        mTuner = Objects.requireNonNull(tuner);
        cbExt.setTuneFailedCallback(this::onTuneFailed);
        cbExt.setProgramInfoCallback(this::onProgramInfoChanged);


    }



    /**
     * See {@link RadioTuner#scan}.
     */
    public void seek(boolean forward, @Nullable TuneCallback resultCb) {
        synchronized (mTuneLock) {
            synchronized (mLock) {
                markOperationFinishedLocked(false);
                mOperationResultCb = resultCb;
            }

            mTuner.cancel();
            int res = mTuner.scan(
                    forward ? RadioTuner.DIRECTION_UP : RadioTuner.DIRECTION_DOWN, false);
            if (res != RadioManager.STATUS_OK) {
                throw new RuntimeException("Seek failed with result of " + res);
            }
        }
    }

    /**
     * See {@link RadioTuner#step}.
     */
    public void step(boolean forward, @Nullable TuneCallback resultCb) {
        synchronized (mTuneLock) {
            markOperationFinishedLocked(false);
            mOperationResultCb = resultCb;
        }
        mTuner.cancel();
        int res =
                mTuner.step(forward ? RadioTuner.DIRECTION_UP : RadioTuner.DIRECTION_DOWN, false);
        if (res != RadioManager.STATUS_OK) {
            throw new RuntimeException("Step failed with result of " + res);
        }
    }

    /**
     * See {@link RadioTuner#tune}.
     */
    public void tune(@NonNull ProgramSelector selector, @Nullable TuneCallback resultCb) {
        synchronized (mTuneLock) {
            synchronized (mLock) {
                markOperationFinishedLocked(false);
                mOperationSelector = selector;
                mOperationResultCb = resultCb;
            }

            mTuner.cancel();
            mTuner.tune(selector);
        }
    }



    private void markOperationFinishedLocked(boolean succeeded) {
        if (mOperationResultCb == null) return;

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Tune operation for " + mOperationSelector
                    + (succeeded ? " succeeded" : " failed"));
        }

        TuneCallback cb = mOperationResultCb;
        mOperationSelector = null;
        mOperationResultCb = null;

        cb.onFinished(succeeded);

        if (mOperationSelector != null) {
            throw new IllegalStateException("Can't tune in callback's failed branch. It might "
                    + "interfere with tune operation that requested current one cancellation");
        }
    }

    private boolean isMatching(@NonNull ProgramSelector currentOperation,
                               @NonNull ProgramSelector event) {
        ProgramSelector.Identifier pri = currentOperation.getPrimaryId();
        return Stream.of(event.getAllIds(pri.getType())).anyMatch(id -> pri.equals(id));
    }

    private void onProgramInfoChanged(RadioManager.ProgramInfo info) {
        synchronized (mLock) {
            if (mOperationResultCb == null) return;
            // if we're seeking, all program info chanes does match
            if (mOperationSelector != null) {
                if (!isMatching(mOperationSelector, info.getSelector())) return;
            }
            markOperationFinishedLocked(true);
        }
    }

    private void onTuneFailed(int result, @Nullable ProgramSelector selector) {
        synchronized (mLock) {
            if (mOperationResultCb == null) return;
            // if we're seeking and got a failed tune (or vice versa), that's a mismatch
            if ((mOperationSelector == null) != (selector == null)) return;
            if (mOperationSelector != null) {
                if (!isMatching(mOperationSelector, selector)) return;
            }
            markOperationFinishedLocked(false);
        }
    }

    /**
     * See {@link RadioTuner#cancel}.
     */
    public void cancel() {
        synchronized (mTuneLock) {
            synchronized (mLock) {
                markOperationFinishedLocked(false);
            }

            int res = mTuner.cancel();
            if (res != RadioManager.STATUS_OK) {
                Log.e(TAG, "Cancel failed with result of " + res);
            }
        }
    }

    /**
     * See {@link RadioTuner#getDynamicProgramList}.
     */
    public @Nullable ProgramList getDynamicProgramList(@Nullable ProgramList.Filter filter) {
        return mTuner.getDynamicProgramList(filter);
    }

    public void close() {
        synchronized (mLock) {
            markOperationFinishedLocked(false);
        }

        mTuner.close();
    }
}

