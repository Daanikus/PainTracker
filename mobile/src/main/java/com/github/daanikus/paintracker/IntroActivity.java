package com.github.daanikus.paintracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide1_title), getString(R.string.slide1_desc), R.drawable.slide01, Color.BLACK));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide2_title), getString(R.string.slide2_desc), R.drawable.slide02, Color.BLACK));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide3_title), getString(R.string.slide3_desc), R.drawable.slide03, Color.BLACK));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide4_title), getString(R.string.slide4_desc), R.drawable.slide04, Color.BLACK));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));
        setBarColor(getResources().getColor(R.color.colorPrimary));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent settingsIntent = new Intent(this, MainActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent settingsIntent = new Intent(this, MainActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
}