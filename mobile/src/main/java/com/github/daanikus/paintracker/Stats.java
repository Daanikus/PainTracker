package com.github.daanikus.paintracker;

/**
 * This class provides various statistics, that are derived from the user's input data that
 * accumulates over time.
 */
public class Stats {
    private static long mostRecent = 0;
    private static int totalEntries = 0;
    private static int totalPain = 0;
    private static float avePainLevel = 0;
    private static int min = -1;
    private static int max = 0;


    public static void setMax(int painLevel) {
        if(painLevel > getMax()) {
            Stats.max = painLevel;
        }
    }

    public static int getMax() {
        return max;
    }

    public static void setMin(int painLevel) {
        if (Stats.min == -1 || painLevel <= Stats.min) {
            Stats.min = painLevel;
        }
    }

    public static int getMin() {
        return min;
    }

    public static float getAvePainLevel() { return avePainLevel; }

    public static void setMostRecent(long mostRecent) {
        Stats.mostRecent = mostRecent;
    }

    public static long getMostRecent() { return mostRecent; }

    public static void setTotalEntries(int totalEntries) {
        Stats.totalEntries = totalEntries;
    }

    public static int getTotalEntries() {
        return totalEntries;
    }

    public static void setTotalPain(int totalPain) {
        Stats.totalPain = totalPain;
    }

    public static void updateAvePainLevel() {
        Stats.avePainLevel = totalPain/totalEntries;
    }



}
