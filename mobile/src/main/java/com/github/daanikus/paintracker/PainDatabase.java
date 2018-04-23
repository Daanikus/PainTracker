package com.github.daanikus.paintracker.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Pain.class}, version = 1)
public abstract class PainDatabase extends RoomDatabase {

    public abstract PainDao dao();

    private static PainDatabase INSTANCE;

    static PainDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PainDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PainDatabase.class, "word_database")
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}