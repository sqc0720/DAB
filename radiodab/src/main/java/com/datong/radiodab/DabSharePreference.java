package com.datong.radiodab;

import android.content.Context;
import android.content.SharedPreferences;

public class DabSharePreference {
    private static final String FILE_NAME = "dab_setting";

    private static void setParam(Context context, String key, Object object) {
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }

    private static Object getParam(Context context, String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static int getDabFirst(Context context){
        return (int)getParam(context, "first_show", 1);
    }
    public static void setDabFirst(Context context, int value){
        setParam(context, "first_show", value);
    }

    //0= all  ;1 = favorite
    public static int getDabCurrentList(Context context){
        return (int)getParam(context, "dab_list", 0);
    }
    public static void setDabCurrentList(Context context, int value){
        setParam(context, "dab_list", value);
    }

    //
    public static long getDabCurrentStationID(Context context){
        return (long)getParam(context, "dab_station_id", 0L);
    }
    public static void setDabCurrentStationID(Context context, long id){
        setParam(context, "dab_station_id", id);
    }

    public static long getDabCurrentStationFrequency(Context context){
        return (long)getParam(context, "dab_station_frequency", 0L);
    }
    public static void setDabCurrentStationFrequency(Context context, long frequency){
        setParam(context, "dab_station_frequency", frequency);
    }

    public static boolean getDabIsPlaying(Context context){
        return (boolean)getParam(context, "is_playing", false);
    }
    public static void setDabIsPlaying(Context context, boolean value){
        setParam(context, "is_playing", value);
    }


    //0 all; 1 III; 2 L;
    public static int getDabBand(Context context){
        return (int)getParam(context, "dab_band", 0);
    }
    public static void setDabBand(Context context, int value){
        setParam(context, "dab_band", value);
    }

    public static boolean getDabAnnounmentAlarm(Context context){
        return (boolean)getParam(context, "dab_announment_alarm", true);
    }
    public static void setDabAnnounmentAlarm(Context context, boolean value){
        setParam(context, "dab_announment_alarm", value);
    }

    public static boolean getDabAnnounmentNewFlash(Context context){
        return (boolean)getParam(context, "dab_announment_newflash", false);
    }
    public static void setDabAnnounmentNewFlash(Context context, boolean value){
        setParam(context, "dab_announment_newflash", value);
    }
    public static boolean getDabAnnounmentSport(Context context){
        return (boolean)getParam(context, "dab_announment_sport", false);
    }
    public static void setDabAnnounmentSport(Context context, boolean value){
        setParam(context, "dab_announment_sport", value);
    }

    public static boolean getDabAnnounmentRoadTrafficFlash(Context context){
        return (boolean)getParam(context, "dab_announment_roadtrafficflash", false);
    }
    public static void setDabAnnounmentRoadTrafficFlash(Context context, boolean value){
        setParam(context, "dab_announment_roadtrafficflash", value);
    }



    public static boolean getDabAnnounmentAreaWeatherFlash(Context context){
        return (boolean)getParam(context, "dab_announment_areaweatherflash", false);
    }
    public static void setDabAnnounmentAreaWeatherFlash(Context context, boolean value){
        setParam(context, "dab_announment_areaweatherflash", value);
    }

    public static boolean getDabAnnounmentFinancialReport(Context context){
        return (boolean)getParam(context, "dab_announment_financialreport", false);
    }
    public static void setDabAnnounmentFinancialReport(Context context, boolean value){
        setParam(context, "dab_announment_financialreport", value);
    }

    public static boolean getDabAnnounmentTransportFlash(Context context){
        return (boolean)getParam(context, "dab_announment_transportflash", false);
    }
    public static void setDabAnnounmentTransportFlash(Context context, boolean value){
        setParam(context, "dab_announment_transportflash", value);
    }

    public static boolean getDabAnnounmentEvent(Context context){
        return (boolean)getParam(context, "dab_announment_event", false);
    }
    public static void setDabAnnounmentEvent(Context context, boolean value){
        setParam(context, "dab_announment_event", value);
    }

    public static boolean getDabAnnounmentProgramInformation(Context context){
        return (boolean)getParam(context, "dab_announment_programinformation", false);
    }
    public static void setDabAnnounmentProgramInformation(Context context, boolean value){
        setParam(context, "dab_announment_programinformation", value);
    }

    public static boolean getDabAnnounmentWarningService(Context context){
        return (boolean)getParam(context, "dab_announment_warningservice", false);
    }
    public static void setDabAnnounmentWarningService(Context context, boolean value){
        setParam(context, "dab_announment_warningservice", value);
    }

    public static boolean getDabDLS(Context context){
        return (boolean)getParam(context, "dab_announment_dls", true);
    }
    public static void setDabDLS(Context context, boolean value){
        setParam(context, "dab_announment_dls", value);
    }

    public static int getDabCategory(Context context){
        return (int)getParam(context, "dab_category", 0);
    }
    public static void setDabCategory(Context context, int value){
        setParam(context, "dab_category", value);
    }


}
