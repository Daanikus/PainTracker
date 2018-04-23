package com.github.daanikus.paintracker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PainDao {
    @Query("SELECT * FROM Pain")
    LiveData<List<Pain>> getAllPains();

    @Query("SELECT * FROM Pain WHERE id IN (:painIds)")
    LiveData<List<Pain>> loadAllByIds(int[] painIds);

    @Insert
    void insert(Pain pain);

    @Insert
    void insertAll(Pain... pains);

    @Delete
    void delete(Pain pain);
}
