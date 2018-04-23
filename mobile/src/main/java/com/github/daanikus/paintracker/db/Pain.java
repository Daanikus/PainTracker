package com.github.daanikus.paintracker.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Pain {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "comment")
    private String comment;

    @ColumnInfo(name = "timestamp")
    private int time;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }
}
