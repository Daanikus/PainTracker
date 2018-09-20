package com.github.daanikus.paintracker;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChangeTextBehaviorTest {

    private String mStringToBeTyped;

    @Rule
    public ActivityTestRule<NewPainActivity> mActivityRule = new ActivityTestRule<>(
            NewPainActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        mStringToBeTyped = "Description of pain";
    }

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
        onView(withId(R.id.edit_pain))
                .perform(typeText(mStringToBeTyped), closeSoftKeyboard());
        onView(withId(R.id.button_save)).perform(click());

//        // Check that the text was changed.
//        onView(withId(R.id.textToBeChanged))
//                .check(matches(withText(mStringToBeTyped)));
    }
}
