package com.github.daanikus.paintracker;

//This class provides various statistics, that are derived from the user's input data
public class Stats {
    private static long mostRecent;
    private static int totalEntries;
    private static int avePainLevel;
    private static int min;
    private static int max;

    public Stats() {

    }

    public static void setMostRecent(long mostRecent) {
        Stats.mostRecent = mostRecent;
    }

    public static long getMostRecent() {
        return mostRecent;
    }
}
