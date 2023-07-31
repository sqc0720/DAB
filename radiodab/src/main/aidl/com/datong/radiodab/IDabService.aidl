// IDabService.aidl
package com.datong.radiodab;

import com.datong.radiodab.IDabCallback;
/**
 * An interface to the backend Radio app's service.
 */
interface IDabService {
   //int add(int arg1, int arg2);
   //void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

   void IServiceAddCallback(in IDabCallback callback);
   int  IServiceGetStatus();
   void IServiceStationProgramList(int index);
   void IServiceTune(long severId, long frequency, int index);
   void IServiceCancel();

}
