package com.datong.radiodab;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * 使用@Database注解该类并添加了表名、数据库版本（每当我们改变数据库中的内容时它都会增加），所以这里使用exportSchema = false
 * 除了添加表映射的类以及和数据库版本外，还要添加exportSchema = false否则会报警告。
 */
@Database(entities = {DabStation.class},version = 2,exportSchema = false)
public abstract class DabStationFavoriteDatabase extends RoomDatabase {
    //数据库名字
    private static final String DB_NAME = "DabStationFavoriteDatabase.db";
    private static DabStationFavoriteDatabase INSTANCE;

    public static synchronized DabStationFavoriteDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }

    private static DabStationFavoriteDatabase create( Context context) {
        return Room.databaseBuilder(
                context,
                DabStationFavoriteDatabase.class,
                DB_NAME)
                //添加一个迁移策略
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    public abstract DabStationFavoriteDao getDabStationFavoriteDao();

    //添加字段 具体的版本迁移策略
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //添加字段
            database.execSQL("ALTER TABLE DabStation ADD COLUMN pty INTEGER NOT NULL DEFAULT 0");
        }
    };

}

