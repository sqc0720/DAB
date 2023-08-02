package com.datong.radiodab;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DabStationContent {
    public static final int FAVORITE_MAX_ITEM = 18;
    private static final String TAG = "DAB.stationcontent";
    private static DabStationContent mDabStationContent;

    private Context mContext;
    private List<DabStation> mDabListAll = new ArrayList<>();
    private List<DabStation> mDabListCategory = new ArrayList<>();
    private List<DabStation> mDabListFavorite = new ArrayList<>();
    private DabStationDao mDaoAll;
    private DabStationDBEngine mEngineAll;
    private DabStationFavoriteDao mDaoFavorite;
    private DabStationFavoriteDBEngine mEngineFavorite;

    //for server pass data to fragment
    private int mPlayStatus;   // -1 no data to pass ;0 play; 1 pause;

    public static DabStationContent getInstance(Context context) {
        if(mDabStationContent == null) {
            mDabStationContent = new DabStationContent(context);
            Log.i(TAG,"new DabStationContent:toString= " + mDabStationContent.toString());
        }
        Log.i(TAG,"DabStationContent:toString= " + mDabStationContent.toString());
        return mDabStationContent;
    }

    private DabStationContent(Context context) {
        mContext= context;
        mEngineAll=DabStationDBEngine.newInstance(mContext);
        mDaoAll= mEngineAll.getDabStationDao();
        mEngineFavorite= DabStationFavoriteDBEngine.newInstance(mContext);
        mDaoFavorite= mEngineFavorite.getDabStationFavoriteDao();
        mPlayStatus= -1;
    }
    // -1 no data to pass ;0 play; 1 pause;
    public void setStatusPlay(){
        Log.i(TAG , "setStatusPlay= 0");
        mPlayStatus= 0;
    }
    public void setStatusPause(){
        Log.i(TAG , "setStatusPause= 1");
        mPlayStatus= 1;
    }
    public void setStatusReset(){
        Log.i(TAG , "setStatusReset= -1");
        mPlayStatus= -1;
    }

    public int getStatusPauseOrPlay(){
        Log.i(TAG , "getStatusPauseOrPlay= "+ mPlayStatus);
        return mPlayStatus;
    }

    // >>only api for scan
    public void updateDabStationAll(List<DabStation> list) {
        mEngineAll.deleteAllDabStations();
        mEngineAll.addDabStationsList(list);
        mDabListCategory.clear();
        setCurrentStationList(0);
        setCurrentStation(0, 0);
    }   //<<
    //for all
    public void getDabStationAll(){
        mDabListAll = mDaoAll.getDabStationsAll();
        Log.i(TAG , "Query [all] dabListAll size ="+ mDabListAll.size() );
    }
    public int getDabStationListAllSize() {
        return mDabListAll.size();
    }
    public DabStation getDabStationListAllItem(int index){
        if((mDabListAll.size()-1)< index) {
            Log.i(TAG , "Query [Favorite] index error="+ index );
            return mDabListAll.get(mDabListAll.size()-1);
        }
        return mDabListAll.get(index);
    }

    //for favorite
    public DabStationFavoriteDBEngine getEngineFavorite(){
        return mEngineFavorite;
    }
    public boolean checkDabStationListFavoriteSizeUpper(){
        if(mDabListFavorite.size()>=FAVORITE_MAX_ITEM)
            return true;  //already max value
        else
            return false;
    }

    public DabStation getDabStationListFavoriteItem(int index){
        if((mDabListFavorite.size()-1)< index) {
            Log.i(TAG , "Query [Favorite] index error="+ index );
            return mDabListFavorite.get(mDabListFavorite.size()-1);
        }
        return mDabListFavorite.get(index);
    }

    public void addDabStationListFavoriteItem(DabStation station){
        if(mDabListFavorite.size()>=FAVORITE_MAX_ITEM)
            return;  //already max value
        mDabListFavorite.add(station);
    }

    public void removeDabStationListFavoriteItem(DabStation station){
        mDabListFavorite.remove(station);
    }

    public void removeDabStationListFavoriteItem(int index){
        if( ( index > (mDabListFavorite.size()-1) )||(index<0))
            return;  //index invalid
        mDabListFavorite.remove(index);
    }

    public int getDabStationListFavoriteSize(){
        return mDabListFavorite.size();
    }
    public List<DabStation> getDabStationListFavorite(){
        return mDabListFavorite;
    }
    public void getDabStationFavorite(){
        mDabListFavorite = mDaoFavorite.getDabStationsAll();
        Log.i(TAG , "Query [Favorite] dabListFavorite size ="+ mDabListFavorite.size() );
    }
    //for category
    public int getDabStationListCategorySize(){
        return mDabListCategory.size();
    }

    public DabStation getDabStationListCategoryItem(int index){
        if((mDabListCategory.size()-1)< index) {
            Log.i(TAG , "Query [Category] index error="+ index );
            return mDabListCategory.get(mDabListCategory.size()-1);
        }
        return mDabListCategory.get(index);
    }
    public List<DabStation> getDabStationListCategory(){
        return mDabListCategory;
    }

    private final long BAND_L_UPPER = 1490624;
    private final long BAND_L_LOWER = 1452960;
    private final long BAND_III_UPPER = 239200;
    private final long BAND_III_LOWER = 174928;
    private void filterListIII(){
        DabStation station;
        Log.i(TAG, "ALL  mDabListAll.size="+mDabListAll.size());
        for(int i=0; i< mDabListAll.size(); i++){
            station = mDabListAll.get(i);
            Log.i(TAG, "index="+i+"; current station frequency="+station.getFrequency());
            if((station.getFrequency()>=BAND_III_LOWER)&&(station.getFrequency()<=BAND_III_UPPER))
                mDabListCategory.add(station);
        }
        Log.i(TAG, "III mDabListCategory.size="+mDabListCategory.size());
    }
    private void filterListL(){
        DabStation station;
        Log.i(TAG, "ALL  mDabListAll.size="+mDabListAll.size());
        for(int i=0; i< mDabListAll.size(); i++){
            station = mDabListAll.get(i);
            Log.i(TAG, "index="+i+"current station frequency="+station.getFrequency());
            if((station.getFrequency()>=BAND_L_LOWER)&&(station.getFrequency()<=BAND_L_UPPER))
                mDabListCategory.add(station);
        }
        Log.i(TAG, "L  mDabListCategory.size="+mDabListCategory.size());
    }

    private void filterListcategory(int category){
        DabStation station;
        int pty=0;
        List<DabStation> tmp = new ArrayList<>();
        if( (mDabListCategory.size()<=0) || (category==0) )   //"All ";
            return ;

        for(int i=0; i< mDabListCategory.size(); i++){
            station = mDabListCategory.get(i);
            //Log.i(TAG, "category: index="+i+" category="+category+" ;current station pty="+station.getPty());
            pty= station.getPty();
            switch(category){
                case 1:     // "News";
                    if((pty==1) ||(pty==3)||(pty==16) ||(pty==17)||(pty==22))
                        tmp.add(station);
                    break;
                case 2:     //"Talk";
                    if((pty==2) ||(pty==5)||(pty==6) ||(pty==7)||(pty==8)||(pty==9)
                            ||(pty==19)||(pty==20)||(pty==21)||(pty==23)||(pty==29))
                        tmp.add(station);
                    break;
                case 3:     //"Sports";
                    if(pty==4)
                        tmp.add(station);
                    break;
                case 4:     //"Popular";
                    if((pty==10) ||(pty==11)||(pty==12) ||(pty==13)||(pty==15)||(pty==18)
                            ||(pty==24)||(pty==25)||(pty==26)||(pty==27)||(pty==28))
                        tmp.add(station);
                    break;
                case 5:     //"Classic";
                    if(pty==14)
                        tmp.add(station);
                    break;
                case 6:     //"No PTY";
                    if((pty>=30)||(pty== 0))
                        tmp.add(station);
                    break;
                default:    // error;
                    Log.i(TAG, "error input parameter category ="+category);;
            }
        }
        mDabListCategory.clear();
        if(tmp.size()>0)
            mDabListCategory.addAll(tmp);
        Log.i(TAG, "category mDabListCategory.size="+mDabListCategory.size());
    }

    public void getDabStationCategory(){
        if(mDabListAll.size()<=0)
            return ;
        mDabListCategory.clear();
        int band= DabSharePreference.getDabBand(mContext);
        switch (band){  //0 all; 1 III; 2 L;
            case 0:
                mDabListCategory.addAll(mDabListAll);
                break;
            case 1:
                filterListIII();
                break;
            case 2:
                filterListL();
                break;
            default:
                mDabListCategory.addAll(mDabListAll);
        }
        int category= DabSharePreference.getDabCategory(mContext);
        filterListcategory(category);
    }
    // check mDabListFavorite and mDabListCategory ,去除冗余项after get list
    // 将 categorylist的 favorite项设置
    public void checkStationList(){
        DabStation station = null;
        DabStation stationfavorite = null;
        if(mDabListFavorite.size()==0)
            return ;
        else {
            //dabFavoriteList remove the same station
            for (int i = 0; i < (mDabListFavorite.size() - 1); i++) {
                stationfavorite = mDabListFavorite.get(i);
                for (int j = i + 1; j < mDabListFavorite.size(); ) {
                    station = mDabListFavorite.get(j);
                    if ((stationfavorite.getService_id() == station.getService_id())
                            && (stationfavorite.getFrequency() == station.getFrequency())) {
                        mDabListFavorite.remove(j);
                        mEngineFavorite.deleteDabStations(station);
                    } else {
                        j++;
                    }
                }
            }
        }
        while(mDabListFavorite.size()>FAVORITE_MAX_ITEM){
            mDabListFavorite.remove(mDabListFavorite.size()-1);
        }
        //Category set favorite flag
        if(mDabListCategory.size()==0)
            return ;
        else{
            station= null;
            stationfavorite= null;
            for (int i = 0; i < mDabListFavorite.size(); i++) {
                stationfavorite = mDabListFavorite.get(i);
                for (int j = 0; j < mDabListCategory.size(); j++) {
                    station = mDabListCategory.get(j);
                    if (station.getName().equals(stationfavorite.getName())
                            && (station.getFrequency() == stationfavorite.getFrequency())
                            && (station.getService_id() == stationfavorite.getService_id())
                            && (station.getSec() == stationfavorite.getSec())
                    ) {
                        Log.i(TAG, "==================match one favorite==============");
                        station.setFavorite(1);
                        break;
                    }
                }
            }
        }
    }


    // check mDabListCategory ,have one  station have the same id and frequency
    public int  checkStationCategoryList(long id, long frequency){
        DabStation station = null;
        int index= -1;

        if(mDabListCategory.size()>0){
            for (int i = 0; i < mDabListCategory.size(); i++) {
                station = mDabListCategory.get(i);
                if ( (station.getFrequency() == frequency)
                        && (station.getService_id() ==id)) {
                    Log.i(TAG, "==================match one station==============");
                    index=i;
                    break;
                }
            }
        }
        return index;
    }

    //0= all  ;1 = favorite
    public void  setCurrentStationList(int list){
        if(list==1)
            DabSharePreference.setDabCurrentList(mContext, 1);
        else
            DabSharePreference.setDabCurrentList(mContext, 0);
    }

    public void setCurrentStation(long id, long frequency){
        Log.i(TAG, "setCurrentStation::  id="+id+" frequency="+frequency);
        DabSharePreference.setDabCurrentStationID(mContext, id);
        DabSharePreference.setDabCurrentStationFrequency(mContext, frequency);
    }

    public int  getCurrentStationListType(){
        return DabSharePreference.getDabCurrentList(mContext);
    }
    //default index=0;
    public int getCurrentStation(){
        int indexlist=0;
        int index= 0;
        long id= DabSharePreference.getDabCurrentStationID(mContext);
        long frequency= DabSharePreference.getDabCurrentStationFrequency(mContext);
        DabStation station;
        if((id!=0)&&(frequency!=0)){
            indexlist= getCurrentStationListType(); //0=all; 1= favorite
            if(indexlist==0){
                if(mDabListCategory.size()<=0)
                    return index;
                for(int i=0; i<mDabListCategory.size(); i++ ){
                    station = mDabListCategory.get(i);
                    if ((station.getFrequency() == frequency)&&(station.getService_id() == id)){
                        index= i;
                        return index;
                    }
                }
            }else if(indexlist==1){
                if(mDabListFavorite.size()<=0)
                    return index;
                for(int i=0; i<mDabListFavorite.size(); i++ ){
                    station = mDabListFavorite.get(i);
                    if ((station.getFrequency() == frequency)&&(station.getService_id() == id)){
                        index= i;
                        return index;
                    }
                }
            }
        }
        return index;
    }

    public long getCurrentStationID() {
        return DabSharePreference.getDabCurrentStationID(mContext);
    }
/*
    private final long BAND_L_UPPER = 1490624;
    private final long BAND_L_LOWER = 1452960;
    private final long BAND_III_UPPER = 239200;
    private final long BAND_III_LOWER = 174928;
*/
    public long getCurrentStationFrequency() {
        long frequency= DabSharePreference.getDabCurrentStationFrequency(mContext);
        Log.i(TAG, "getCurrentStationFrequency:: frequency="+frequency);
        if((frequency>= BAND_III_LOWER)&&(frequency<= BAND_III_UPPER))
            return frequency;
        if((frequency>= BAND_L_LOWER)&&(frequency<= BAND_L_UPPER))
            return frequency;
        Log.i(TAG, "getCurrentStationFrequency  error:: frequency="+frequency);
        return 0L;
    }

    public int findFavoriteListSelectPosition() {
        int position = -1;
        long id = DabSharePreference.getDabCurrentStationID(mContext);
        long frequency = DabSharePreference.getDabCurrentStationFrequency(mContext);
        if (mDabListFavorite.size() <= 0)
            return position;
        for (int i = 0; i < mDabListFavorite.size(); i++) {
            DabStation station = mDabListFavorite.get(i);
            if ((station.getFrequency() == frequency) && (station.getService_id() == id)) {
                position = i;
                return position;
            }
        }
        return position;
    }

    public int findStationListSelectPosition() {
        int position = -1;
        long id = DabSharePreference.getDabCurrentStationID(mContext);
        long frequency = DabSharePreference.getDabCurrentStationFrequency(mContext);
        if (mDabListCategory.size() == 0)
            return position;
        for (int i = 0; i < mDabListCategory.size(); i++) {
            DabStation station = mDabListCategory.get(i);
            if ((station.getFrequency() == frequency) && (station.getService_id() == id)) {
                position = i;
                return position;
            }
        }
        return position;
    }












/*
    void addFavoriteStation(DabStation station){
        DabStation favorite;
        if(dabFavoriteList.size()>0) {
            for (int i = 0; i < dabFavoriteList.size(); i++) {
                favorite = dabFavoriteList.get(i);
                if ((favorite.getService_id() == station.getService_id()) &&
                        (favorite.getFrequency() == station.getFrequency()))
                    return;
            }
        }
        dabFavoriteList.add(station);
        mFavoriteEngine.addDabStations(station);
    }
 */
    /*
    private void testProgramList(){
        DabStation tmp=new DabStation("1_AAA09",
                0, 0, 23343, 4366264,0);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("2_542309",
                0, 1, 23563, 45624268,1);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("3_A74356",
                0, 0, 47834, 232577,2);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("4_64573y9",
                0, 0, 43626245, 84535,3);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("5_tyje45yrh",
                0, 1, 34532, 3454,4);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("6_45375457",
                0, 1, 374562, 8456,5);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("7_ttjtu5467rt",
                0, 0, 734572, 2452362,6);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("8_3432446trhtru",
                0, 1,7577, 1231235,6);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("9_A555555555hhhhh",
                0, 0, 89564, 5745634,10);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("10_dyirtvwre",
                0, 0, 234, 23476573,9);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("11_dyirtvwre",
                0, 1, 9532433, 8433,23);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("12_dyirtvwre",
                0, 1, 3463623, 243572,32);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("13_dyirtvwre",
                0, 0, 343467, 462344,10);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("14_dyirtvwre",
                0, 0, 5734572, 24652,20);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("15_dyirtvwre",
                0, 1, 347234, 35673,29);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("16_dyirtvwre",
                0, 1, 1233577, 246627,7);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("17_dyirtvwre",
                0, 0, 731325, 24527,16);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("18_dyirtvwre",
                0, 1, 2346673, 245467,15);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("19_dyirtvwre",
                0, 0, 437312, 245235,27);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("20_dyirtvwre",
                0, 0, 657368, 23452,25);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("21_dyirtvwre",
                0, 0, 34624, 324672,24);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("22_dyirtvwre",
                0, 1, 345272, 243234,22);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("23_dyirtvwre",
                0, 0, 243534, 246753,10);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("24_dyirtvwre",
                0, 1, 341464, 34277,18);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("25_dyirtvwre",
                0, 0, 45676, 563458,15);  //silas temper
        mEngineAll.addDabStations(tmp);
        tmp=new DabStation("26_dyirtvwre",
                0, 0, 83673, 35638,30);  //silas temper
        mEngineAll.addDabStations(tmp);
    }*/
}
