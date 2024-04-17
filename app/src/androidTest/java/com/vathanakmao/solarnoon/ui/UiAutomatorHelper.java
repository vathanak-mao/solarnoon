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
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import java.io.IOException;

public class UiAutomatorHelper {
    public static final String SETTINGSAPP_CLASS = "com.android.settings.Settings";
    public static final String SETTINGSAPP_PACKAGE = "com.android.settings";
    public static final String SWITCH_WIDGET_RESOURCENAME = "com.android.settings:id/switch_widget";

    private static final String BUTTON_CLASS = "android.widget.Button";

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final int NEW_WINDOW_TIMEOUT = 3000;

    /**
     * Automate launching Settings app and navigate to find Location switch
     * to turn it on/off.
     *
     * @param enable If true, switch it on; otherwise, switch it off.
     * @param device
     * @throws IOException
     * @throws UiObjectNotFoundException
     */
    public static final void enableLocation(boolean enable, UiDevice device) throws IOException, UiObjectNotFoundException {
        // Open Settings app
        launchSettingsApp(device);

        final String locationSwitchLabel;
        final String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("xiaomi")) {
            locationSwitchLabel = "Location access";
        } else if (manufacturer.equalsIgnoreCase("google")) {
            locationSwitchLabel = "Use location";
        } else {
            throw new UnsupportedOperationException(String.format("Couldn't navigate through Settings app to find the Location switch for unsupported device's manufacturer %s", manufacturer));
        }

        UiObject2 locationSwitch = navigateAndFindLocationSwitch(device, locationSwitchLabel);
        if (locationSwitch != null) {
            Log.d(UiAutomatorHelper.class.getSimpleName(),
                    String.format("> enable=%s, locationToggle<isChecked()=%s>", enable, locationSwitch.isChecked()));

            if (locationSwitch.isChecked() != enable) {
                locationSwitch.click();
            }
        } else {
            throw new UiObjectNotFoundException(String.format("Couldn't find the Location switch by label '%s'", locationSwitchLabel));
        }
    }

    private static UiObject2 navigateAndFindLocationSwitch(UiDevice device, String switchText) throws UiObjectNotFoundException {
        // Scroll down to see Location
        UiScrollable settingsLandingPage = new UiScrollable(new UiSelector().scrollable(true));
        settingsLandingPage.scrollIntoView(new UiSelector().text("Location"));

        // Click Location option
        UiObject2 locationOption = device.findObject(By.text("Location"));
        if (locationOption != null) {
            locationOption.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        } else {
            throw new UiObjectNotFoundException("Couldn't find an option with the text 'Location' in Settings app.");
        }

        // Scroll down to see the Location switch
        UiScrollable locationPage = new UiScrollable(new UiSelector().scrollable(true));
        locationPage.scrollIntoView(new UiSelector().text(switchText));

        // Find Location switch by text
        return getSwitchWidget(device, switchText);
    }

    /**
     * The toggle button to switch on/off location service in the Settings app is a switch widget.
     * The toggle button is of type "android.widget.Switch" with ID "switch_widget".
     * It has a parent view, which is of type "android.widget.LinearLayout" with ID "switch_bar".
     * It also a sibling view, which is of type "android.widget.TextView" with ID "switch_text",
     * to display text like "Location access",
     *
     * @param device
     * @param switchText
     * @return
     */
    private static UiObject2 getSwitchWidget(UiDevice device, String switchText) throws UiObjectNotFoundException {
        // resourceName=com.android.settings:id/switch_text, className=android.widget.TextView
        UiObject2 switchTextView = device.wait(Until.findObject(By.text(switchText)), NEW_WINDOW_TIMEOUT);
        if (switchTextView != null) {
            UiObject2 parent = switchTextView.getParent(); // resourceName=com.android.settings:id/switch_bar, className=android.widget.LinearLayout
            if (parent != null) {
                for (UiObject2 child : parent.getChildren()) {
                    if (SWITCH_WIDGET_RESOURCENAME.equals(child.getResourceName())) {
                        // Switch widget found
                        return child; // resourceName=com.android.settings:id/switch_widget, className=android.widget.Switch
                    }
                }
                Log.d(UiAutomatorHelper.class.getSimpleName(),
                        String.format("The view <className=%s, resourceName=%s> with the given text '%s' does not have a sibling with resourceName '%s'"
                        , switchTextView.getClassName(), switchTextView.getResourceName(), switchText, SWITCH_WIDGET_RESOURCENAME));
            } else {
                Log.d(UiAutomatorHelper.class.getSimpleName(),
                        String.format("The view <className=%s, resourceName=%s> with the given text '%s' does not have parent view", switchTextView.getClassName(), switchTextView.getResourceName(), switchText));
            }
        } else {
            throw new UiObjectNotFoundException(String.format("Couldn't find a view with the given text '%s'", switchText));
        }

        return null;
    }

    private static void launchSettingsApp(UiDevice device) {
        device.pressHome();

        // Wait for launcher (a system app to manage Home screen and start other apps).
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
