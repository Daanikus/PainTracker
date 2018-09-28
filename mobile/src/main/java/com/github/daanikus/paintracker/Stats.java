package com.github.daanikus.paintracker;

import android.util.Log;

//This class provides various statistics, that are derived from the user's input data
public class Stats {
    private static long mostRecentEntryTime = 0;
    private static int mostRecentEntryValue = -1;
    private static int totalEntries = 0;
    private static int totalPain = 0;
    private static int avePainLevel = 0;
    private static int min = 0;
    private static int max = 0;

    public Stats() {

    }

    public static void setMostRecentEntryTime(long mostRecent) {
        Stats.mostRecentEntryTime = mostRecent;
    }

    public static long getMostRecentEntryTime() {
        return mostRecentEntryTime;
    }

    public static void setMostRecentEntryValue(int mostRecentEntryValue) {
        Stats.mostRecentEntryValue = mostRecentEntryValue;
    }

    public static int getMostRecentEntryValue() {
        return mostRecentEntryValue;
    }

    public static int getMax() {
        return max;
    }

    public static int getMin() {
        return min;
    }

    public static int getAvePainLevel() {
        return avePainLevel;
    }

    public static int getTotalEntries() {
        return totalEntries;
    }

    public static void updateStats(Pain pain){
        if (pain.getTimestamp() > mostRecentEntryTime) {
            mostRecentEntryTime = pain.getTimestamp();
        }
        totalEntries++;

        totalPain += pain.getPainLevel();

        if(pain.getPainLevel() > max) {
            max = pain.getPainLevel();
        }

        if(pain.getPainLevel() <= min || totalEntries == 1) {
            min = pain.getPainLevel();
        }

        avePainLevel = totalPain/totalEntries;
    }

    public static void printStats(){
        Log.i("Stats", "mostRecent "+mostRecentEntryTime+"\n"+
                                "totalEntries "+totalEntries+"\n"+
                                "totalPain "+totalPain+"\n"+
                                "avePain "+avePainLevel+"\n"+
                                "minimum "+min+"\n"+
                                "maximum "+max+"\n");
    }

}
