package com.datong.radiodab;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


/**
 * 使用@Database注解该类并添加了表名、数据库版本（每当我们改变数据库中的内容时它都会增加），所以这里使用exportSchema = false
 * 除了添加表映射的类以及和数据库版本外，还要添加exportSchema = false否则会报警告。
 */
@Database(entities = {DabStation.class},version = 2,exportSchema = false)
public abstract class DabStationDatabase extends RoomDatabase {
    //数据库名字
    private static final String DB_NAME = "DabStationDatabase.db";
    private static DabStationDatabase INSTANCE;

    public static synchronized DabStationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }

    private static DabStationDatabase create( Context context) {
        return Room.databaseBuilder(
                context,
                DabStationDatabase.class,
                DB_NAME)
                //添加一个迁移策略
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    public abstract DabStationDao getDabStationDao();

    //添加字段 具体的版本迁移策略
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //添加字段
            database.execSQL("ALTER TABLE DabStation ADD COLUMN pty INTEGER NOT NULL DEFAULT 0");
        }
    };
    //删除原有表中的字段 具体的版本迁移策略
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //重新创建一个表
            database.execSQL("CREATE TABLE word_temp(id INTEGER PRIMARY KEY NOT NULL,english_word TEXT,chinese_meaning TEXT)");
            //将word表中的数据 赋值到新表中
            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaning)" + "SELECT id,english_word,chinese_meaning FROM word");
            //删除旧表word
            database.execSQL("DROP TABLE word");
            //将新表名该为原来的word表名
            database.execSQL("ALTER TABLE word_temp RENAME to word");
        }
    };




}

