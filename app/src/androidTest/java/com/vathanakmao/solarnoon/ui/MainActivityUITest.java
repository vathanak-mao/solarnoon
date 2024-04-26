package com.vathanakmao.solarnoon.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest extends BaseUITest {
    public static final String SUPPORTED_LANGUAGES_SPINNER_ID = "spinnerSupportedLanguages";
    public static final String SUPPORTED_LANGUAGES_SPINNER_RESOURCEID = "com.vathanakmao.solarnoon:id/spinnerSupportedLanguages";
    public static final String DESCRIPTION_TEXTVIEW_ID = "textviewDesc";
    public static final String SOLAR_TIME_TEXTVIEW_ID = "textviewSolarnoonTime";

    public static final long FIND_OBJECT_TIMEOUT = 15000;

    public static final String TEXTVIEW_CLASS = "android.widget.TextView";

    @Test
    public void changeLanguage() throws UiObjectNotFoundException, IOException {
        startMainActivityFromHomeScreenV2();
        UiAutomatorHelper.clickGrantAppPermissionsIfAsked(device);
        clickNextIfDialogToNotifyLocationServicesNeededAppears();
        clickOkIfDialogToEnableLocationAppears();

        // =========================================================================

        // Open the dropdown
//        Espresso.onView(withId(R.id.spinnerSupportedLanguages)).perform(click());

//        UiObject languagesDropdown = device.findObject(new UiSelector().resourceId(SUPPORTED_LANGUAGES_SPINNER_RESOURCEID));
        UiObject2 languagesDropdown = device.wait(Until.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID)), FIND_OBJECT_TIMEOUT);
        languagesDropdown.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown appears
//        languagesDropdown.waitForExists(10000); // Wait until the dropdown appears
//        languagesDropdown.clickAndWaitForNewWindow(NEW_WINDOW_TIMEOUT);

        // Select "English"
        UiObject2 englishItem = device.wait(Until.findObject(By.clazz(LINEAR_LAYOUT_CLASS).hasChild(By.text("English"))), FIND_OBJECT_TIMEOUT);
        englishItem.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown disappears

        // Verify text description
        UiObject2 descriptionTextview = device.findObject(By.res(APP_PACKAGE, DESCRIPTION_TEXTVIEW_ID));
        assertEquals("Today's Solar Noon", descriptionTextview.getText());

        // Verify solar time
        // Sometimes the internet is slow and the request to get location takes longer than normal
        // so the solar time might not show up on time and this test might fail.
        // The timeout to wait for it to show is set to longer than 10 seconds.
        UiObject2 solarTimeTxt = device.wait(Until.findObject(By.res(APP_PACKAGE, SOLAR_TIME_TEXTVIEW_ID).textContains(":")), FIND_OBJECT_TIMEOUT);
        assertTrue(solarTimeTxt.getText().length() > 0);
        assertTrue(isSubset(solarTimeTxt.getText().toCharArray(), ":", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

        // ===========================================================================

        // Open the dropdown again
        UiObject2 languagesDropdown2 = device.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID));
        languagesDropdown2.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown appears

        // Select "ខ្មែរ"
        UiObject2 khmerItem = device.findObject(By.clazz(LINEAR_LAYOUT_CLASS).hasChild(By.text("ខ្មែរ")));
        khmerItem.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown disappears

        // Verify text description
        descriptionTextview = device.findObject(By.res(APP_PACKAGE, DESCRIPTION_TEXTVIEW_ID));
        assertEquals("ថ្ងៃនេះ ថ្ងៃត្រង់ម៉ោង", descriptionTextview.getText());

        // Verify solar time
        // Sometimes the internet is slow and the request to get location takes longer than normal
        // so the solar time might not show up on time and this test might fail.
        // The timeout to wait for it to show is set to longer than 10 seconds.
        solarTimeTxt = device.wait(Until.findObject(By.res(APP_PACKAGE, SOLAR_TIME_TEXTVIEW_ID).textContains(":")), FIND_OBJECT_TIMEOUT);
        assertTrue(solarTimeTxt.getText().length() > 0);
        assertTrue(isSubset(solarTimeTxt.getText().toCharArray(), ":", "០", "១", "២", "៣", "៤", "៥", "៦", "៧", "៨", "៩"));
    }

    private boolean isSubset(char[] childSet, String ...parentSet) {
        final List parentList = Arrays.asList(parentSet);
        for (char ch : childSet) {
            if (!parentList.contains("" + ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * The purpose of this test is check whether or not
     * certain dialogs appear when the Location is enabled.
     **/
    @Test
    public void launchWhenLocationIsEnabled() throws IOException, UiObjectNotFoundException {
        // Make sure Location Services is enabled
        UiAutomatorHelper.enableLocation(true, device);

        // Start app from home screen
        startMainActivityFromHomeScreenV2();

        // Click the "While using the app" button to grant app permissions if asked
        UiAutomatorHelper.clickGrantAppPermissionsIfAsked(device);

        // Verify that the dropdown to change language exists
        assertNotNull(device.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID)));
    }

    /**
     * The purpose of this test is verifying that
     * certain dialogs appear one after another
     * when the Location is disabled.
     **/
    @Test
    public void launchWhenLocationIsDisabled() throws IOException, UiObjectNotFoundException {
        // Disable Location
        UiAutomatorHelper.enableLocation(false, device);

        // Start app from home screen
        startMainActivityFromHomeScreenV2();

        // Click the "While using the app" button to grant app permissions if asked
        UiAutomatorHelper.clickGrantAppPermissionsIfAsked(device);

        // Alert dialog appears to notify Location Services is needed
        // so click Next button
        UiObject2 nextBtn = device.findObject(By.text("NEXT").clazz(BUTTON_CLASS));
        assertNotNull(nextBtn);
        nextBtn.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);

        // Another dialog appears to enable Location Services
        // so click OK button
        UiObject2 okBtn = device.findObject(By.text("OK").clazz(BUTTON_CLASS));
        assertNotNull(okBtn);
        okBtn.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);

        // Verify that the dropdown to change language exists
        assertNotNull(device.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID)));
    }

    @Test
    public void findObjectByResourceId() {
        startMainActivityFromHomeScreenV2();
        UiAutomatorHelper.clickGrantAppPermissionsIfAsked(device);
        clickNextIfDialogToNotifyLocationServicesNeededAppears();
        clickOkIfDialogToEnableLocationAppears();

        // Using UiSelector to find object by resourceId
        assertFalse(device.findObject(new UiSelector().resourceId("xnbmsueiyiqpdfudfuw")).exists());
        assertTrue(device.findObject(new UiSelector().resourceId("com.vathanakmao.solarnoon:id/" + SUPPORTED_LANGUAGES_SPINNER_ID)).exists());

        // Using BySelector to find object by resourceId
        assertNull(device.findObject(By.res(APP_PACKAGE, "xnbmsueiyiqpdfudfuw")));
        assertNotNull(device.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID)));
    }

    @Test
    public void findObjectByResourceName() {
        startMainActivityFromHomeScreenV2();
        UiAutomatorHelper.clickGrantAppPermissionsIfAsked(device);
        clickNextIfDialogToNotifyLocationServicesNeededAppears();
        clickOkIfDialogToEnableLocationAppears();

        // Using BySelector to find object by resourceName
        assertNull(device.findObject(By.res("com.vathanakmao.solarnoon:id/xnbmsueiyiqpdfudfuw")));
        assertNotNull(device.findObject(By.res("com.vathanakmao.solarnoon:id/" + SUPPORTED_LANGUAGES_SPINNER_ID)));
    }
}
