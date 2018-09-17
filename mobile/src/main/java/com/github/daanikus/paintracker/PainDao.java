package com.github.daanikus.paintracker;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Database Access Object (DAO) class. This object allows abstract access to the DB.
 * The majority of this class is boilerplate code retrieved from Google CodeLabs: Android Room
 * with a View (Q1 2018)
 */

@Dao
public interface PainDao {
    @Query("SELECT * FROM Pain")
    LiveData<List<Pain>> getAllPains();

    @Query("SELECT * FROM Pain WHERE id IN (:painIds)")
    LiveData<List<Pain>> loadAllByIds(int[] painIds);

    @Query("SELECT * FROM Pain WHERE timestamp IN (:timestamp) LIMIT 1")
    Pain getPainByTimestamp(long timestamp);

    //Select the most recent pain entry.
    @Query("SELECT * FROM Pain WHERE id = (SELECT MAX(id) FROM Pain)")
    Pain getPainRecent();

    @Insert
    void insert(Pain pain);

    @Insert
    void insertAll(Pain... pains);

    @Delete
    void delete(Pain pain);
}
