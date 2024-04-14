package com.vathanakmao.solarnoon.ui;

import static androidx.test.uiautomator.Until.hasObject;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.os.Build;
import android.util.Log;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import java.io.IOException;

public class UiAutomatorHelper {
    public static final String START_SETTINGS_APP_COMMAND = "am start -a android.settings.SETTINGS";
    public static final String SETTINGS_SWITCH_WIDGET_RESOURCENAME = "com.android.settings:id/switch_widget";

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
        try {
            device.pressHome();

            // Wait for launcher
            final String launcherPackage = device.getLauncherPackageName();
            assertThat(launcherPackage, notNullValue());
            device.wait(hasObject(By.pkg(launcherPackage).depth(0)), 5000);

            device.executeShellCommand(START_SETTINGS_APP_COMMAND);
        } catch (IOException e) {
            throw new IOException("Failed to start Settings app. ", e);
        }

        // Open Location Settings (you might need to adjust selectors based on your device)
        UiObject2 locationSettings = device.wait(Until.findObject(By.text("Location")), 3000);
        if (locationSettings != null) {
            locationSettings.click();
        } else {
            throw new UiObjectNotFoundException("Couldn't find an item with text 'Location' in Settings app.");
        }

        // Simulate enabling/disabling location services using UI interactions (replace with your specific logic)
//        UiObject locationToggle = device.findObject(new UiSelector().resourceId(SETTINGS_SWITCH_WIDGET_RESOURCENAME));
//        UiObject2 locationToggle = device.findObject(By.res("com.android.settings", "switch_widget"));
//        UiObject2 locationToggle = device.findObject(By.text("Use location"));
        UiObject2 locationToggle = findSwitchWidgetByText(device, "Use location");
        if (locationToggle != null) {
//            Log.d(UiAutomatorHelper.class.getSimpleName(), "> locationToggle: className=" + locationToggle.getClassName()
//                    + ", class=" + locationToggle.getClass()
//                    + ", text=" + locationToggle.getText()
//                    + ", contentDescription=" + locationToggle.getContentDescription()
//                    + ", childCount" + locationToggle.getChildCount()
//                    + ", resourceName=" + locationToggle.getResourceName()
//                    + ", applicationPackage=" + locationToggle.getApplicationPackage()
//                    + ", parent: className=" + locationToggle.getParent().getClassName() + ", resourceName=" + locationToggle.getParent().getResourceName() + ", childCount=" + locationToggle.getParent().getChildCount()
//                    + ", child 2: className=" + locationToggle.getParent().getChildren().get(1).getClassName() + ", resourceName=" + locationToggle.getParent().getChildren().get(1).getResourceName()
//                    );

            if ( (!locationToggle.isChecked() && enable)
                    || (locationToggle.isChecked() && !enable) ) {
                locationToggle.click();
            }
        } else {
            throw new UiObjectNotFoundException("Couldn' find an item with resource name '" + SETTINGS_SWITCH_WIDGET_RESOURCENAME + "' in Settings app.");
        }
    }

    public static UiObject2 findSwitchWidgetByText(UiDevice device, String text) {
        UiObject2 switchtextView = device.findObject(By.text(text));
        if (switchtextView != null) {
            UiObject2 parent = switchtextView.getParent();
            if (parent != null) {
                for (UiObject2 child : parent.getChildren()) {
                    if (SETTINGS_SWITCH_WIDGET_RESOURCENAME.equals(child.getResourceName())) {
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
                    String.format("Unable to find an object/view with the given text '%s'.", text));
        }
        return null;
    }
}
