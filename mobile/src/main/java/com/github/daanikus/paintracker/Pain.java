package com.github.daanikus.paintracker.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Pain {
    @PrimaryKey
    private int id;

    private String comment;

    private int timestamp;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setTime(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTime() {
        return this.timestamp;
    }
}
