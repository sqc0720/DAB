// ITunerCallback.aidl
package com.datong.radiodab;
/**
 * Asynchronous result for tune/seek operation.
 */
interface ITuneCallback {
    /**
     * Called when tune operation has finished.
     *
     * @param succeeded States whether operation has succeeded or not.
     */
    void onFinished(boolean succeeded);
}
