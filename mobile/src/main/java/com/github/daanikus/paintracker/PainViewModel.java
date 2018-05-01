package com.github.daanikus.paintracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class PainViewModel extends AndroidViewModel {

    private PainRepository mRepository;

    private LiveData<List<Pain>> mAllPains;

    public PainViewModel (Application application) {
        super(application);
        mRepository = new PainRepository(application);
        mAllPains = mRepository.getAllPains();
    }

    LiveData<List<Pain>> getAllPains() { return mAllPains; }

    public void insert(Pain Pain) { mRepository.insert(Pain); }

    public Void getPainByTimestamp(long timestamp) {
        // mRepository.getPainByTimestamp(timestamp); // TODO Would like to actually call db for this
        return null;
    }
}
