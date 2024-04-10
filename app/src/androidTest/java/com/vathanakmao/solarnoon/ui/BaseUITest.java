package com.vathanakmao.solarnoon.ui;

import static androidx.test.uiautomator.Until.hasObject;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.vathanakmao.solarnoon.util.StringUtil;

import org.junit.Before;

public class BaseUITest {
    protected static final String APP_PACKAGE = "com.vathanakmao.solarnoon";
    protected static final String BUTTON_CLASS = "android.widget.Button";
    protected static final String ALERT_DIALOG_CLASS = "android.app.AlertDialog";
    protected static final int LAUNCH_TIMEOUT = 5000;
    protected static final int NEW_WINDOW_TIMEOUT = 3000;
    protected UiDevice device;

    @Before
    public void setUp() {
        startMainActivityFromHomeScreen();
        clickGrantAppPermissionsIfAsked();
        clickNextIfLocationSeviceNeededAlertAppears();
        clickOkIfTurnOnDeviceLocationDialogAppears();
    }

    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the app
        Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(hasObject(By.pkg(APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    private void clickGrantAppPermissionsIfAsked() {
        // Grant app permissions to access location
        UiObject2 grantAppPermissionsBtn = device.wait(Until.findObject(By.text("While using the app").clazz(BUTTON_CLASS)), NEW_WINDOW_TIMEOUT);
        if (grantAppPermissionsBtn != null) {
            log(grantAppPermissionsBtn.getText() + " clicked.");
            grantAppPermissionsBtn.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        }
    }

    private void clickNextIfLocationSeviceNeededAlertAppears() {
        UiObject2 nextBtn = device.wait(Until.findObject(By.text("NEXT").clazz(BUTTON_CLASS)), NEW_WINDOW_TIMEOUT);
        if (nextBtn != null) {
            nextBtn.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        }
    }

    private void clickOkIfTurnOnDeviceLocationDialogAppears() {
        UiObject2 okBtn = device.wait(Until.findObject(By.text("OK").clazz(BUTTON_CLASS)), NEW_WINDOW_TIMEOUT);
        if (okBtn != null) {
            okBtn.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        }
    }

    protected void log(String message) {
        Log.d(getClass().getSimpleName(), message);
    }
}
