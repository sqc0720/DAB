package com.datong.radiodab;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO代表数据访问对象
 * Query  查询
 * Insert  插入
 * Update  更新
 * Delete 删除
 * 传入多种不同的参数
 * 下面的User...users  可变对象，可以多个
 */
@Dao
public interface DabStationDao {
    //add
    @Insert()
    void addDabStations(DabStation...items);
    @Insert
    void addDabStationsList(List<DabStation> userLists);

    //delete
    @Delete
    void deleteDabStations(DabStation...items);
    //delete all
    @Query("DELETE FROM DabStation")
    void deleteDabStationsAll();
    //change
    @Update
    void updateDabStations(DabStation...items);

    //@Query("select * from DabStation ORDER BY 'index' DESC")
    @Query("select * from DabStation")
    List<DabStation> getDabStationsAll();

    //根据用户id查询数据,数据表的字段id对应方法的id值，查询语句里可以通过冒号方法变量名方式使用
    @Query("select * from DabStation where favorite = :favorite")
    List<DabStation> getDabStationsFavorite(int favorite);
}

