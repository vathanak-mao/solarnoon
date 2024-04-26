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
    public static final String SETTINGSAPP_LOCATION = "Location";

    public static final String GOOGLE_SWITCH_WIDGET_RESOURCENAME = "com.android.settings:id/switch_widget";
    public static final String XIAOMI_SWITCH_WIDGET_RESOURCENAME = "android:id/switch_widget";

    public static final String XIAOMI_MANUFACTURER = "xiaomi";
    public static final String GOOGLE_MANUFACTURER = "google";

    private static final String BUTTON_CLASS = "android.widget.Button";

    private static final int LAUNCH_TIMEOUT = 10000;
    private static final int NEW_WINDOW_TIMEOUT = 10000;
    private static final int FIND_OBJECT_TIMEOUT = 10000;

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
        launchSettingsAppV2(device);

        final UiObject2 locationSwitch;
        if (isEmulator()) {
            locationSwitch = navigateAndFindLocationSwitch(device, "Use location", GOOGLE_SWITCH_WIDGET_RESOURCENAME);
        } else {
            final String manufacturer = Build.MANUFACTURER.toLowerCase();
            switch (manufacturer) {
                case XIAOMI_MANUFACTURER:
                    locationSwitch = navigateAndFindLocationSwitch(device, "Location access", XIAOMI_SWITCH_WIDGET_RESOURCENAME);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("Couldn't navigate through Settings app to find the Location switch for unsupported device's manufacturer %s", manufacturer));
            }
        }

        if (locationSwitch != null) {
            Log.d(UiAutomatorHelper.class.getSimpleName(),
                    String.format("> enable=%s, locationToggle<isChecked()=%s>", enable, locationSwitch.isChecked()));

            if (locationSwitch.isChecked() != enable) {
                locationSwitch.click();
            }
        }
    }

    private static UiObject2 navigateAndFindLocationSwitch(UiDevice device, String switchText, String resourceName) throws UiObjectNotFoundException {
        // Scroll down to see Location
        scrollTo(device, SETTINGSAPP_LOCATION);

        // Click Location option
        UiObject2 locationOption = device.findObject(By.text(SETTINGSAPP_LOCATION));
        if (locationOption != null) {
            locationOption.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        } else {
            throw new UiObjectNotFoundException("Couldn't find an option with the text 'Location' in Settings app.");
        }

        // Scroll down/up to see the Location switch
        scrollTo(device, switchText);

        // Find Location switch by text
        UiObject2 result = findSwitchWidgetByTitle(device, switchText, resourceName);
        if (result == null) {
            logD("Couldn't find the Location switch by title '%s'", switchText);
        }
        return result;
    }

    public static void scrollTo(UiDevice device, String text) {
        UiScrollable page = new UiScrollable(new UiSelector().scrollable(true));
        try {
            page.scrollIntoView(new UiSelector().text(text));
        } catch (UiObjectNotFoundException e) {
            logD("Couldn't scroll to text '%s' because the page is not scrollable.", text);
        }
    }

    /**
     * The resourceName of the switch widget can be the same as
     * of the other switch widget in the same screen/page.
     * Hence, starting by finding its title (TextView)
     * then traverse from the top of the view tree of the UI component
     * to find the switch widget view by its resourceName to reduce
     * the chance of getting the wrong one.
     *
     * @param device
     * @param title
     * @param resourceName
     * @return
     * @throws UiObjectNotFoundException
     */
    private static UiObject2 findSwitchWidgetByTitle(UiDevice device, String title, String resourceName) throws UiObjectNotFoundException {
        UiObject2 switchWidgetTitle = device.findObject(By.text(title));
        if (switchWidgetTitle != null) {
            logD("> Found view with the given text '%s'", title);

            UiObject2 parent = switchWidgetTitle.getParent();
            if (parent != null) {
                if (parent.getParent() != null) {
                    logD("> The view with text '%s' have a grandparent.", title);
                    return findChildByResourceName(parent.getParent(), resourceName);
                } else {
                    logD("> The view with text '%s' does not have a grandparent.", title);
                    return findChildByResourceName(parent, resourceName);
                }
            } else {
                logD("> The view with text '%s' does not have a parent.", title);
            }
        } else {
            logD("> Couldn't find any view with text '%s'", title);
        }
        return null;
    }

    private static UiObject2 findChildByResourceName(UiObject2 parent, String resourceName) throws UiObjectNotFoundException {
        UiObject2 result = null;

        if (parent != null) {
            logD(parent, "parent");

            if (parent.getChildCount() == 0) {
                return null;
            }

            for (UiObject2 child : parent.getChildren()) {
                logD(child, "child");

                if (resourceName.equals(child.getResourceName())) {
                    result = child;
                } else {
                    result = findChildByResourceName(child, resourceName);
                }
            }
        }
        return result;
    }

    /**
     * This works on both emulator and real device.
     *
     * @param device
     * @throws IOException
     */
    private static void launchSettingsAppV2(UiDevice device) throws IOException {
        // Press Home button
        device.pressHome();

        // Wait for launcher (a system app to manage Home screen and start other apps).
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the Settings app
        device.executeShellCommand("am start -n com.android.settings/.Settings");
    }

    /**
     * This works only on emulator, not on real device.
     *
     * @param device
     */
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


    // =============================================================================================
    // =============================================================================================
    // =============================================================================================


    public static boolean isEmulator() {
        logD("> Build.FINGERPRINT=" + Build.FINGERPRINT);
        return Build.FINGERPRINT.contains("emulator");
    }
    public static void clickGrantAppPermissionsIfAsked(UiDevice device) {
        final String buttonText;

        if (isEmulator()) {
            buttonText = "While using the app";
        } else {
            final String manufacturer = Build.MANUFACTURER.toLowerCase();
            switch (manufacturer) {
                case XIAOMI_MANUFACTURER: buttonText = "WHILE USING THE APP"; break;
                case GOOGLE_MANUFACTURER: buttonText = "WHILE USING THE APP"; break;
                default: throw new UnsupportedOperationException(String.format("Couldn't navigate through Settings app to find the Location switch for unsupported device's manufacturer %s", manufacturer));
            }
        }

        // Grant app permissions to access location
        UiObject2 grantAppPermissionsBtn = device.wait(Until.findObject(By.text(buttonText).clazz(BUTTON_CLASS)), FIND_OBJECT_TIMEOUT);
        if (grantAppPermissionsBtn != null) {
            logD(grantAppPermissionsBtn.getText() + " clicked.");
            grantAppPermissionsBtn.clickAndWait(Until.newWindow(), NEW_WINDOW_TIMEOUT);
        }
    }


    // =============================================================================================
    // =============================================================================================
    // =============================================================================================


    private static Context getContext() {
        return ApplicationProvider.getApplicationContext();
    }

    private static void logD(UiObject2 obj, String label) {
        Log.d(UiAutomatorHelper.class.getSimpleName(), String.format("> %s: className=%s, resourceName=%s", label, obj.getClassName(), obj.getResourceName()));
    }
    private static void logD(String message, Object... placeholders) {
        Log.d(UiAutomatorHelper.class.getSimpleName(), String.format(message, placeholders));
    }
}
