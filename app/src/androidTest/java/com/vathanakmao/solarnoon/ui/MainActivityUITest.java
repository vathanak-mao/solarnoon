package com.vathanakmao.solarnoon.ui;

import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject2;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest extends BaseUITest {

    @Test
    public void languageIconDisplayed() {
        UiObject2 langIcon = device.findObject(By.res(APP_PACKAGE, "spinnerSupportedLanguages"));
        assertNotNull(langIcon);
    }

}
