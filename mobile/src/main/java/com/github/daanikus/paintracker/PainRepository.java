package com.github.daanikus.paintracker.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import java.util.List;

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
