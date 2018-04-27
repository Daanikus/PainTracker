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
    private long timestamp;
    private int painLevel;

    public Pain(String comment, int painLevel, long timestamp) {
        this.comment = comment;
        this.painLevel = painLevel;
        this.timestamp = timestamp;
    }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id;}

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public int getPainLevel() { return this.painLevel; }

    public void setPainLevel(int painLevel) { this.painLevel = painLevel; }
}
