package com.vathanakmao.solarnoon.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.LinearLayout;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest extends BaseUITest {
    public static final String SUPPORTED_LANGUAGES_SPINNER_ID = "spinnerSupportedLanguages";
    public static final String DESCRIPTION_TEXTVIEW_ID = "textviewDesc";

    @Test
    public void changeLanguage() throws UiObjectNotFoundException {
        startMainActivityFromHomeScreen();
        clickGrantAppPermissionsIfAsked();
        clickNextIfDialogToNotifyLocationServicesNeededAppears();
        clickOkIfDialogToEnableLocationAppears();

        // =========================================================================

        // Open the dropdown
        UiObject2 languagesDropdown = device.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID));
        languagesDropdown.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown appears

        // Select "English"
        UiObject2 englishItem = device.findObject(By.clazz(LINEAR_LAYOUT_CLASS).hasChild(By.text("English")));
        englishItem.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown disappears

        // Verify text description
        UiObject2 descriptionTextview = device.findObject(By.res(APP_PACKAGE, DESCRIPTION_TEXTVIEW_ID));
        assertEquals("Today's Solar Noon", descriptionTextview.getText());

        // ===========================================================================

        // Open the dropdown again
        languagesDropdown = device.findObject(By.res(APP_PACKAGE, SUPPORTED_LANGUAGES_SPINNER_ID));
        languagesDropdown.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown appears

        // Select "ខ្មែរ"
        UiObject2 khmerItem = device.findObject(By.clazz(LINEAR_LAYOUT_CLASS).hasChild(By.text("ខ្មែរ")));
        khmerItem.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT); // Wait until the dropdown disappears

        // Verify text description
        descriptionTextview = device.findObject(By.res(APP_PACKAGE, DESCRIPTION_TEXTVIEW_ID));
        assertEquals("ថ្ងៃនេះ ថ្ងៃត្រង់ម៉ោង", descriptionTextview.getText());
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
        startMainActivityFromHomeScreen();

        // Click the "While using the app" button to grant app permissions if asked
        clickGrantAppPermissionsIfAsked();

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
        startMainActivityFromHomeScreen();

        // Click the "While using the app" button to grant app permissions if asked
        clickGrantAppPermissionsIfAsked();

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
        startMainActivityFromHomeScreen();
        clickGrantAppPermissionsIfAsked();
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
        startMainActivityFromHomeScreen();
        clickGrantAppPermissionsIfAsked();
        clickNextIfDialogToNotifyLocationServicesNeededAppears();
        clickOkIfDialogToEnableLocationAppears();

        // Using BySelector to find object by resourceName
        assertNull(device.findObject(By.res("com.vathanakmao.solarnoon:id/xnbmsueiyiqpdfudfuw")));
        assertNotNull(device.findObject(By.res("com.vathanakmao.solarnoon:id/" + SUPPORTED_LANGUAGES_SPINNER_ID)));
    }
}
