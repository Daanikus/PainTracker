package com.github.daanikus.paintracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

@Entity
public class Pain {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String comment;

    private int timestamp;

    public Pain(String comment) {
        this.comment = comment;
    }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id;}

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return this.timestamp;
    }
}
