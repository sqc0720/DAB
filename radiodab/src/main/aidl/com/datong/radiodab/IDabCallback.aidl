// IDabCallback.aidl
package com.datong.radiodab;

parcelable  DabStation;


/**
 * Watches current program changes.
 */
interface IDabCallback {
   // int  ICallbackCurrentBand();
    void ICallbackProgramListChanged(in List<DabStation> list);
    void ICallbackScanFinishStateChanged(int state,in List<DabStation> list);

    void ICallbackTuneImage(int id);
    void ICallbackTuneLabel(String msg);
    void ICallbackAudioFocusFailed();
    void ICallbackTunerPlay();
    void ICallbackTunerStop();
}
