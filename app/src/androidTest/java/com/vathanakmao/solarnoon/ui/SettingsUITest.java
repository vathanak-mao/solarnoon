package com.vathanakmao.solarnoon.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SettingsUITest extends BaseUITest {
    private static final String TOGGLE_BUTTON_CLASS = "android.widget.ToggleButton";

    @Before
    public void setUp() throws IOException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        try {
            UiAutomatorHelper.enableLocation(false, device);
        } catch (UiObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void settingsOpened() throws InterruptedException {
        Thread.sleep(1000);
    }
}
