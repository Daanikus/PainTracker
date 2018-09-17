package com.github.daanikus.paintracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * The ViewModel class is designed to store and manage UI-related data in a lifecycle
 * conscious way. The ViewModel class allows data to survive configuration changes
 * such as screen rotations.
 *
 * The majority of this class is boilerplate code retrieved from Google CodeLabs: Android Room
 * with a View (Q1 2018)
 */

public class PainViewModel extends AndroidViewModel {

    private PainRepository mRepository;

    private LiveData<List<Pain>> mAllPains;

    public PainViewModel (Application application) {
        super(application);
        mRepository = new PainRepository(application);
        mAllPains = mRepository.getAllPains();
    }

    LiveData<List<Pain>> getAllPains() { return mAllPains; }

    public void insert(Pain pain) { mRepository.insert(pain); }

    //public Pain getPainLatest() { };

}
