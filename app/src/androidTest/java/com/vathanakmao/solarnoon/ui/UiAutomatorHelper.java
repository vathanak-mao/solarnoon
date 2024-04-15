package com.vathanakmao.solarnoon.ui;

import static androidx.test.uiautomator.Until.hasObject;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import java.io.IOException;

public class UiAutomatorHelper {
    public static final String SETTINGSAPP_CLASS = "com.android.settings.Settings";
    public static final String SETTINGSAPP_PACKAGE = "com.android.settings";
    public static final String SETTINGS_SWITCH_WIDGET_RESOURCENAME = "com.android.settings:id/switch_widget";

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final int NEW_WINDOW_TIMEOUT = 3000;

    public static final void enableLocation(boolean enable, UiDevice device) throws IOException, UiObjectNotFoundException {
        String manufacturer = Build.MANUFACTURER;

        if (manufacturer.equalsIgnoreCase("samsung")) {
            enableLocationForXiaomi(enable, device);
        } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
            enableLocationForXiaomi(enable, device);
        } else if (manufacturer.equalsIgnoreCase("google")) {
            enableLocationForXiaomi(enable, device);
        } else {
            throw new UnsupportedOperationException(String.format("Unabled to enable Location for %s's device.", manufacturer));
        }
    }

    public static void enableLocationForXiaomi(boolean enable, UiDevice device) throws UiObjectNotFoundException, IOException {
        launchSettingsApp(device);

        // Open Settings > Location
        UiScrollable locationList = new UiScrollable(new UiSelector().scrollable(true));
        locationList.scrollIntoView(new UiSelector().text("Location"));
        UiObject2 locationSettings = device.findObject(By.text("Location"));
        if (locationSettings != null) {
            locationSettings.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        } else {
            throw new UiObjectNotFoundException("Couldn't find an item with text 'Location' in Settings app.");
        }

        // Simulate enabling/disabling location services using UI interactions (replace with your specific logic)
        UiObject2 locationToggle = findSwitchWidgetByText(device, "Use location");
        if (locationToggle != null) {
            Log.d(UiAutomatorHelper.class.getSimpleName(),
                    String.format("> enable=%s, locationToggle<isChecked()=%s>", enable, locationToggle.isChecked()));

            if (locationToggle.isChecked() != enable) {
                locationToggle.click();
                Log.d(UiAutomatorHelper.class.getSimpleName(), "'Use location' toggle clicked.");
            }
        } else {
            throw new UiObjectNotFoundException("Couldn't find an item with resource name '" + SETTINGS_SWITCH_WIDGET_RESOURCENAME + "' in Settings app.");
        }
    }

    /**
     * The toggle to switch on/off location service in the Settings app is a switch widget.
     *
     * @param device
     * @param text
     * @return
     */
    public static UiObject2 findSwitchWidgetByText(UiDevice device, String text) {
        // resourceName=com.android.settings:id/switch_text, className=android.widget.TextView
        UiObject2 switchtextView = device.wait(Until.findObject(By.text(text)), NEW_WINDOW_TIMEOUT);
        if (switchtextView != null) {
            // resourceName=com.android.settings:id/switch_bar, className=android.widget.LinearLayout
            UiObject2 parent = switchtextView.getParent();
            if (parent != null) {
                for (UiObject2 child : parent.getChildren()) {
                    if (SETTINGS_SWITCH_WIDGET_RESOURCENAME.equals(child.getResourceName())) {
                        // resourceName=com.android.settings:id/switch_widget, className=android.widget.Switch
                        return child;
                    }
                }
                Log.d(UiAutomatorHelper.class.getSimpleName(),
                        String.format("The view <className=%s, resourceName=%s> with the given text '%s' does not have a sibling with resourceName '%s'"
                        , switchtextView.getClassName(), switchtextView.getResourceName(), text, SETTINGS_SWITCH_WIDGET_RESOURCENAME));
            } else {
                Log.d(UiAutomatorHelper.class.getSimpleName(),
                        String.format("The view <className=%s, resourceName=%s> with the given text '%s' does not have parent view", switchtextView.getClassName(), switchtextView.getResourceName(), text));
            }
        } else {
            Log.d(UiAutomatorHelper.class.getSimpleName(),
                    String.format("Couldn't find an object/view with the given text '%s'.", text));
        }
        return null;
    }

    private static void launchSettingsApp(UiDevice device) {
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        //  Context can be obtained from your test setup
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(SETTINGSAPP_PACKAGE, SETTINGSAPP_CLASS));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);

        device.wait(hasObject(By.pkg(SETTINGSAPP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    private static Context getContext() {
        return ApplicationProvider.getApplicationContext();
    }
}
