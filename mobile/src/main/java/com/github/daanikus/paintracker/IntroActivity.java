package com.github.daanikus.paintracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntroFragment.newInstance(getString(R.string.slide1_title), getString(R.string.slide1_desc), R.drawable.slide01, Color.BLACK));
        addSlide(AppIntroFragment.newInstance(getString(R.string.slide2_title), getString(R.string.slide2_desc), R.drawable.slide02, Color.BLACK));
        //addSlide(thirdFragment);
        //addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
//        SliderPage sliderPage = new SliderPage();
//        sliderPage.setTitle("First Slide");
//        sliderPage.setDescription("Desc");
//        //sliderPage.setImageDrawable();
//        //sliderPage.setBgColor(backgroundColor);
//        addSlide(AppIntroFragment.newInstance(sliderPage));
//
//        SliderPage sliderPage2 = new SliderPage();
//        sliderPage.setTitle("Second Slide");
//        sliderPage.setDescription("Desc 2");
//        //sliderPage.setImageDrawable();
//        //sliderPage.setBgColor(backgroundColor);
//        addSlide(AppIntroFragment.newInstance(sliderPage2));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent settingsIntent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent settingsIntent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
}