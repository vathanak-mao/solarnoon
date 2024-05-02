package com.vathanakmao.solarnoon.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.vathanakmao.solarnoon.ui.CustomEspressoMatchers.containsOnlyCharsIn;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.vathanakmao.solarnoon.MainActivity;
import com.vathanakmao.solarnoon.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityExpressoTest extends BaseUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws IOException {
        super.setUp();

        UiAutomatorHelper.clickGrantAppPermissionsIfAsked(device);
        clickNextIfDialogToNotifyLocationServicesNeededAppears();
        clickOkIfDialogToEnableLocationAppears();
    }

    @Test
    public void changeLanguage() {
        // Open spinner dropdown
        onView(withId(R.id.spinnerSupportedLanguages)).perform(click());

        // Select the spinner's item "English"
        onView(withText("English")).perform(click());

        // Check if the description is translated
        onView(withId(R.id.textviewDesc))
                .check(matches(withText("Today's Solar Noon")))
                .check(matches(isDisplayed()));

        // Assert that the solar time is displayed and translated
        onView(withId(R.id.textviewSolarnoonTime))
                .check(matches(withText(containsOnlyCharsIn(':', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'))))
                .check(matches(isDisplayed()));

        // Assert that the loading image is not displayed
        onView(withId(R.id.imageviewLoading)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageviewLoading)).check(matches(not(doesNotExist())));

        // ==================================================================

        // Open spinner dropdown
        onView(withId(R.id.spinnerSupportedLanguages)).perform(click());

        // Select the spinner's item "English"
        onView(withText("ខ្មែរ")).perform(click());

        // Check if the description is translated
        onView(withId(R.id.textviewDesc))
                .check(matches(withText("ថ្ងៃនេះ ថ្ងៃត្រង់ម៉ោង")))
                .check(matches(isDisplayed()));

        // Assert that the solar time is displayed and translated
        onView(withId(R.id.textviewSolarnoonTime))
                .check(matches(withText(containsOnlyCharsIn(':', '០', '១', '២', '៣', '៤', '៥', '៦', '៧', '៨', '៩'))))
                .check(matches(isDisplayed()));

        // Assert that the loading image exists but invisible
        onView(withId(R.id.imageviewLoading)).check(matches(not(doesNotExist())));
        onView(withId(R.id.imageviewLoading)).check(matches(not(isDisplayed())));
    }
}
