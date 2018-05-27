package com.github.daanikus.paintracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import java.util.List;

/**
 * This repository abstracts access to the data source. Used for async calls to DB.
 * The majority of this class is boilerplate code retrieved from Google CodeLabs: Android Room
 *  * with a View (Q1 2018)
 *
 */

public class PainRepository {

    private PainDao mDao;
    private LiveData<List<Pain>> mAllPains;

    PainRepository(Application application) {
        PainDatabase db = PainDatabase.getDatabase(application);
        mDao = db.dao();
        mAllPains = mDao.getAllPains();
    }

    LiveData<List<Pain>> getAllPains() {
        return mAllPains;
    }

    public void insert (Pain pain) {
        new insertAsyncTask(mDao).execute(pain);
    }

    /**
     * This class extends AsyncTask for an asynchronous call to add
     * the given Pain entry to the DB
     */
    private static class insertAsyncTask extends AsyncTask<Pain, Void, Void> {

        private PainDao mAsyncTaskDao;

        insertAsyncTask(PainDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Pain... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
