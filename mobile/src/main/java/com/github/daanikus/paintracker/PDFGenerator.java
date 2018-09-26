package com.github.daanikus.paintracker;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class PDFGenerator {

    private LiveData<List<Pain>> pains;
    private PainViewModel mPainViewModel;

    public PDFGenerator(PainViewModel painViewModel) {
        this.mPainViewModel = painViewModel;
        this.pains = mPainViewModel.getAllPains();
    }


}
