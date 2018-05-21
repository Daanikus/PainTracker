package com.github.daanikus.paintracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private PainDao mUserDao;
    private PainDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, PainDatabase.class).build();
        mUserDao = mDb.dao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        Pain pain = new Pain("Test comment 1", 7, System.currentTimeMillis(), 200, 200);
        long time = pain.getTimestamp();
        mUserDao.insert(pain);
        Pain byId = mUserDao.getPainByTimestamp(time);
        assertThat(byId, equalTo(pain));
    }
}