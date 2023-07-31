package com.datong.radiodab;

import android.os.SystemProperties;
import android.util.Log;

public class VendorIpkProperty {
    private static final String TAG = "VENDOR_IPK_PROPERTY";
    private static final String IPK_KEY="vendor.vehicle.ipk.config1";
    private static final int IPK_VALUE_LENGTH= 64;

    private String mIpkValue;
    private boolean mIpkCheck;

    public VendorIpkProperty(){
        updateIpkValue();
    }

    public void updateIpkValue(){
        mIpkValue= SystemProperties.get(IPK_KEY);
        Log.i(TAG, "ipk.config1="+mIpkValue);
        mIpkCheck= checkIpkValue();
    }

    private boolean checkIpkValue(){
        if(mIpkValue.length()!=IPK_VALUE_LENGTH)
            return false;
        for (int i = 0; i < mIpkValue.length(); i++) {
            char c = mIpkValue.charAt(i);
            if (!( (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') ) )
                return false;
        }
        return true;
    }

    private int getValue(String s){
        return Integer.parseInt(s,16);
    }

    //VEHICLE_CONFIG_110
    public int getCFGLanguage(){
        if( !mIpkCheck )
            return -1;
        String str1= mIpkValue.substring(2,4);
        int byte1=getValue(str1);
        return byte1&0x0f;
    }

    public int getCFGRegion(){
        if( !mIpkCheck )
            return -1;
        String str1= mIpkValue.substring(2,4);
        int byte1=getValue(str1);
        return (byte1&0x10)>>4;
    }

    public int getCFGALCA(){
        if( !mIpkCheck )
            return -1;
        String str1= mIpkValue.substring(2,4);
        int byte1=getValue(str1);
        return (byte1&0x80)>>7;
    }

    public int getCFGSLIF(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return byte2&0x01;
    }

    public int getCFGISA(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x02)>>1;
    }

    public int getCFGFCTA(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x04)>>2;
    }

    public int getCFGRCW(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x08)>>3;
    }

    public int getCFGTJA(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x10)>>4;
    }
    public int getCFGHWA(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x20)>>5;
    }

    public int getCFGBSD(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x40)>>6;
    }

    public int getCFGRCTA(){
        if( !mIpkCheck )
            return -1;
        String str2= mIpkValue.substring(4,6);
        int byte2=getValue(str2);
        return (byte2&0x80)>>7;
    }

    public int getCFGAPA(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return byte3&0x01;
    }

    public int getCFGECALL(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x02)>>1;
    }

    public int getCFGVDR(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x04)>>2;
    }

    public int getCFGELK(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x08)>>3;
    }

    public int getCFGFrontObject(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x10)>>4;
    }

    public int getCFGBlockObject(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x20)>>5;
    }

    public int getCFGRAEB(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x40)>>6;
    }

    public int getCFGAES(){
        if( !mIpkCheck )
            return -1;
        String str3= mIpkValue.substring(6,8);
        int byte3=getValue(str3);
        return (byte3&0x80)>>7;
    }

    public int getCFGRVC(){
        if( !mIpkCheck )
            return -1;
        String str4= mIpkValue.substring(8,10);
        int byte4=getValue(str4);
        return byte4&0x0f;
    }
    public int getCFGTrailer(){
        if( !mIpkCheck )
            return -1;
        String str4= mIpkValue.substring(8,10);
        int byte4=getValue(str4);
        return (byte4&0x10)>>4;
    }

    public int getCFGHLC(){
        if( !mIpkCheck )
            return -1;
        String str4= mIpkValue.substring(8,10);
        int byte4=getValue(str4);
        return (byte4&0x20)>>5;
    }
    //VEHICLE_CONFIG_111
    public int getCFGTransmission(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0=getValue(str0);
        return byte0&0x03;
    }
    public int getCFGEPB(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0=getValue(str0);
        return (byte0&0x04)>>2;
    }

    public int getCFGTPMS(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0=getValue(str0);
        return (byte0&0x08)>>3;
    }

    public int getCFGEngine(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0=getValue(str0);
        return (byte0&0x30)>>4;
    }

    public int getCFGPEPS(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0=getValue(str0);
        return (byte0&0x40)>>6;
    }

    public int getCFGESP(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0=getValue(str0);
        return (byte0&0x80)>>7;
    }

    public int getCFGDriverPosition(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1=getValue(str1);
        return byte1&0x01;
    }

    public int getCFGCruise(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1=getValue(str1);
        return (byte1&0x06)>>1;
    }

    public int getCFGVoltageDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1=getValue(str1);
        return (byte1&0x08)>>3;
    }

    public int getCFGMultiMedia(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1=getValue(str1);
        return (byte1&0x30)>>4;
    }

    public int getCFGGlow(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1=getValue(str1);
        return (byte1&0x40)>>6;
    }

    public int getCFGRearFog(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1=getValue(str1);
        return (byte1&0x80)>>7;
    }

    public int getCFGBootAnimation(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2=getValue(str2);
        return byte2&0x03;
    }

    public int getCFGWaterInfuel(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2=getValue(str2);
        return (byte2&0x04)>>2;
    }

    public int getCFGOverspeedEn(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2=getValue(str2);
        return (byte2&0x18)>>3;
    }

    public int getCFGSpeedLimit(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2=getValue(str2);
        return (byte2&0x60)>>5;
    }

    public int getCFGAVH(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2=getValue(str2);
        return (byte2&0x80)>>7;
    }

    public int getCFGAutoLight(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return byte3&0x01;
    }

    public int getCFGMaintenance(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return (byte3&0x02)>>1;
    }

    public int getCFGEPS(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return (byte3&0x04)>>2;
    }

    public int getCFGFuelTank(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return (byte3&0x18)>>3;
    }

    public int getCFGEVP(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return (byte3&0x20)>>5;
    }

    public int getCFGBrake(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return (byte3&0x40)>>6;
    }

    public int getCFGAGS(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3=getValue(str3);
        return (byte3&0x80)>>7;
    }

    public int getCFGAirbag(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return byte4&0x01;
    }

    public int getCFGDMS(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x02)>>1;
    }

    public int getCFGWheelDeviation(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x04)>>2;
    }

    public int getCFGOverspeedSwitch(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x08)>>3;
    }

    public int getCFGAfterResetInfoDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x10)>>4;
    }

    public int getCFGAfterStartInfoDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x20)>>5;
    }

    public int getCFGSCR(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x40)>>6;
    }

    public int getCFGMileUnit(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4=getValue(str4);
        return (byte4&0x80)>>7;
    }

    public int getCFGOverspeedSet(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str5= mIpkValue.substring(offset+10,offset+12);
        int byte5=getValue(str5);
        return byte5;
    }

    public int getCFGPDC(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return byte6&0x03;
    }

    public int getCFGEngineGaugeDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return (byte6&0x04)>>2;
    }

    public int getCFGLDW(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return (byte6&0x08)>>3;
    }

    public int getCFGLKA(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return (byte6&0x10)>>4;
    }

    public int getCFGFCW(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return (byte6&0x20)>>5;
    }

    public int getCFGAEB(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return (byte6&0x40)>>6;
    }

    public int getCFGTSR(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6=getValue(str6);
        return (byte6&0x80)>>7;
    }

    public int getCFGSeatbelt(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str7= mIpkValue.substring(offset+14,offset+16);
        int byte7=getValue(str7);
        return byte7&0x07;
    }

    public int getCFGSeatLayout(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str7= mIpkValue.substring(offset+14,offset+16);
        int byte7=getValue(str7);
        return (byte7&0x18)>>3;
    }

    public int getCFGAWD(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str7= mIpkValue.substring(offset+14,offset+16);
        int byte7=getValue(str7);
        return (byte7&0x60)>>5;
    }

    public int getCFGCruiseSpeedDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str7= mIpkValue.substring(offset+14,offset+16);
        int byte7=getValue(str7);
        return (byte7&0x80)>>7;
    }

    public int getCFGNaviDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return byte8&0x01;
    }

    public int getCFGMediaDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x02)>>1;
    }

    public int getCFGCallDisplay(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x04)>>2;
    }

    public int getCFGOutsideTemp(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x08)>>3;
    }

    public int getCFGDPF(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x10)>>4;
    }

    public int getCFGGPF(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x20)>>5;
    }

    public int getCFGIHC(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x40)>>6;
    }

    public int getCFGEbooster(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str8= mIpkValue.substring(offset+16,offset+18);
        int byte8=getValue(str8);
        return (byte8&0x80)>>7;
    }

    public int getCFGESCL(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str9= mIpkValue.substring(offset+18,offset+20);
        int byte9=getValue(str9);
        return byte9&0x01;
    }

    public int getCFGESCU(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str9= mIpkValue.substring(offset+18,offset+20);
        int byte9=getValue(str9);
        return (byte9&0x02)>>1;
    }

    public int getCFGExteriorColor(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str9= mIpkValue.substring(offset+18,offset+20);
        int byte9=getValue(str9);
        return (byte9&0x3c)>>2;
    }

    public int getCFGHVAC(){
        if( !mIpkCheck )
            return -1;
        int offset= 16;
        String str9= mIpkValue.substring(offset+18,offset+20);
        int byte9=getValue(str9);
        return (byte9&0xc0)>>6;
    }

    //VEHICLE_CONFIG_112
    public int getCFGIECRange1(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;
        String str0= mIpkValue.substring(offset+0,offset+2);
        int byte0= getValue(str0);
        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1= getValue(str1);
        byte1=(byte1&0xc0)>>6;
        return byte1*256+byte0;
    }

    public int getCFGIECRange2(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str1= mIpkValue.substring(offset+2,offset+4);
        int byte1= getValue(str1);
        byte1=byte1&0x3f;
        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2= getValue(str2);
        byte2=(byte2&0xf0)>>4;
        return byte2*64+byte1;
    }

    public int getCFGAFEFuelConsumptionIdle(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2= getValue(str2);
        return (byte2&0x02)>>1;
    }

    public int getCFGDTEDisplayDirection(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2= getValue(str2);
        return (byte2&0x04)>>2;
    }

    public int getCFGDTEFuelConsumptionIdle(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str2= mIpkValue.substring(offset+4,offset+6);
        int byte2= getValue(str2);
        return (byte2&0x08)>>3;
    }

    public int getCFGAFEInit(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str3= mIpkValue.substring(offset+6,offset+8);
        int byte3= getValue(str3);
        return byte3;
    }

    public int getCFGAECRange(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str4= mIpkValue.substring(offset+8,offset+10);
        int byte4= getValue(str4);
        String str5= mIpkValue.substring(offset+10,offset+12);
        int byte5= getValue(str5);
        byte5=(byte5&0xc0)>>6;
        return byte5*256+byte4;
    }

    public int getCFGDTEFuelcalculate(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str5= mIpkValue.substring(offset+10,offset+12);
        int byte5= getValue(str5);
        return byte5&0x3f;
    }

    public int getCFGCurrentFuelConsumptionDistance(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str6= mIpkValue.substring(offset+12,offset+14);
        int byte6= getValue(str6);
        return byte6;
    }

    public int getCFGMaxFuelConsumption(){
        if( !mIpkCheck )
            return -1;
        int offset= 36;

        String str7= mIpkValue.substring(offset+14,offset+16);
        int byte7= getValue(str7);
        return byte7;
    }

    public int getCFGMainteanceDistance(){
        if( !mIpkCheck )
            return -1;
        int offset= 52;

        String str= mIpkValue.substring(offset+0,offset+4);
        int value= getValue(str);
        return value;
    }




}
