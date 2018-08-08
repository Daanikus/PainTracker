package com.github.daanikus.paintracker;

import android.arch.lifecycle.LiveData;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    private LiveData<List<Pain>> pains;
    private PainViewModel mPainViewModel;

    public PDFGenerator(PainViewModel painViewModel) {
        this.mPainViewModel = painViewModel;
        this.pains = mPainViewModel.getAllPains();
    }


}
