package com.datong.radiodab;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DabStationFavoriteDBEngine {
    private DabStationFavoriteDao mDao;
    private static final String TAG = "DAB.FAVORITE_DBENGINE";
    private static DabStationFavoriteDBEngine mEngine;

    public static DabStationFavoriteDBEngine newInstance(Context context) {
        if (mEngine == null) {
            Log.d(TAG, "new DabStationFavoriteDBEngine");
            mEngine = new DabStationFavoriteDBEngine( context );
        }
        return mEngine;
    }

    private DabStationFavoriteDBEngine(Context context) {
        DabStationFavoriteDatabase database = DabStationFavoriteDatabase.getInstance(context);
        mDao = database.getDabStationFavoriteDao();
    }

    public DabStationFavoriteDao getDabStationFavoriteDao(){
        return mDao;
    }
    //插入
    public void addDabStations(DabStation...items) {
        new AddAsyncTask(mDao).execute(items);
    }

    public void addDabStationsList(List<DabStation> list) {
        new AddListAsyncTask(mDao).execute(list);
    }

    //删除个
    public void deleteDabStations(DabStation...items) {
        Log.d(TAG, "enter deleteDabStations");
        new DeleteAsyncTask(mDao).execute(items);
    }
    //删除所有
    public void deleteAllDabStations(){
        new DeleteAllAsyncTask(mDao).execute();
    }
    //更新
    public void updateDabStations(DabStation...items) {
        new UpdateAsyncTask(mDao).execute(items);
    }

    //
    public void queryAllDabStations() {
        Log.d(TAG, "enter queryAllDabStations");
        new QueryAllAsyncTask(mDao).execute();
    }

    public void queryFavoriteDabStations() {
        new QueryFavoriteAsyncTask(mDao).execute();
    }
    //=======task
    //插入task
    static class AddAsyncTask extends AsyncTask<DabStation, Void, Void> {
        private DabStationFavoriteDao mDao;
        public AddAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(DabStation... items) {
            mDao.addDabStations(items);
            return null;
        }
    }

    static class AddListAsyncTask extends AsyncTask< List<DabStation>, Void, Void> {
        private DabStationFavoriteDao mDao;
        public AddListAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(List<DabStation>... list) {
            mDao.addDabStationsList(list[0]);
            return null;
        }
    }

    //更新task
    static class UpdateAsyncTask extends AsyncTask<DabStation, Void, Void> {
        private DabStationFavoriteDao mDao;
        public UpdateAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(DabStation... items) {
            mDao.updateDabStations(items);
            return null;
        }
    }

    //删除
    static class DeleteAsyncTask extends AsyncTask<DabStation, Void, Void> {
        private DabStationFavoriteDao mDao;
        public DeleteAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
            Log.d(TAG, "enter DeleteAsyncTask");
        }

        @Override
        protected Void doInBackground(DabStation... items) {
            mDao.deleteDabStations(items);
            Log.d("DBEngine", "enter DeleteAsyncTask, doInBackground");

            return null;
        }
    }

    //删除全部
    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private DabStationFavoriteDao mDao;
        public DeleteAllAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteDabStationsAll();
            return null;
        }
    }

    static class QueryAllAsyncTask extends AsyncTask<List<DabStation>, Void, Void> {
        private DabStationFavoriteDao mDao;
        public QueryAllAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(List<DabStation>... list) {
            Log.d(TAG , "QueryAsyncTask doInBackground");
            list[0] = mDao.getDabStationsAll();
            /*
            for (DabStation item : all) {
                Log.d(TAG, "item： " + item.toString());

            }
            Log.d(TAG , "Query  [ all ]  AsyncTask doInBackground, size = " + all.size());*/
            return null;
        }
    }

    static class QueryFavoriteAsyncTask extends AsyncTask<Void, Void, Void> {

        private DabStationFavoriteDao mDao;
        public QueryFavoriteAsyncTask(DabStationFavoriteDao dao) {
            this.mDao = dao;
            Log.d(TAG, "enter QueryFavoriteAsyncTask");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG , "QueryFavoriteAsyncTask doInBackground");
            List<DabStation> favorites = mDao.getDabStationsFavorite(1);

            for (DabStation item : favorites) {
                Log.d(TAG , "favorite： " + item.toString());

            }
            Log.d(TAG , "Query [favorite] AsyncTask doInBackground, size = " + favorites.size());
            return null;
        }
    }
}
