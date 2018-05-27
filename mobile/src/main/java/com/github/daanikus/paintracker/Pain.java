package com.github.daanikus.paintracker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Pain object class. This is the object that gets stored in the room DB
 */

@Entity
public class Pain {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String comment;
    private long timestamp;
    private int painLevel;
    private int locationX, locationY;

    public Pain(String comment, int painLevel, long timestamp, int locationX, int locationY) {
        this.comment = comment;
        this.painLevel = painLevel;
        this.timestamp = timestamp;
        this.locationX = locationX;
        this.locationY = locationY;
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

    public int getLocationX() { return this.locationX; }

    public int getLocationY() { return this.locationY; }

    public String getTimeAsFormattedString() {
        Date date = new Date(this.timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, h:mm a");
        return sdf.format(date);
    }


}
