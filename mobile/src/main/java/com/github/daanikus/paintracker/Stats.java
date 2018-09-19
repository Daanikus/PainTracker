package com.github.daanikus.paintracker;

import android.util.Log;

//This class provides various statistics, that are derived from the user's input data
public class Stats {
    private static long mostRecent = 0;
    private static int totalEntries = 0;
    private static int totalPain = 0;
    private static int avePainLevel = 0;
    private static int min = 0;
    private static int max = 0;

    public Stats() {

    }

    public static void setMostRecent(long mostRecent) {
        Stats.mostRecent = mostRecent;
    }

    public static long getMostRecent() {
        return mostRecent;
    }

    public static void setTotalEntries(int totalEntries) {
        Stats.totalEntries = totalEntries;
    }

    public static int getTotalEntries() {
        return totalEntries;
    }

    public static void updateStats(Pain pain){
        if (pain.getTimestamp() > mostRecent) {
            mostRecent = pain.getTimestamp();
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
        Log.i("Stats", "\nMost Recent\t"+mostRecent+"\n"+
                                "Total Entries\t"+totalEntries+"\n"+
                                "Total Pain\t"+totalPain+"\n"+
                                "Average Pain\t"+avePainLevel+"\n"+
                                "Minimum\t"+min+"\n"+
                                "Maximum\t"+max+"\n");
    }

}
