package com.vathanakmao.solarnoon.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;
import android.widget.Button;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest extends BaseUITest {

    @Test
    public void languageIconDisplayed() {
        UiObject dummyIconByResourceId = device.findObject(new UiSelector().resourceId("xnbmsueiyiqpdfudfuw"));
        assertFalse(dummyIconByResourceId.exists());
        UiObject langIconByResourceId = device.findObject(new UiSelector().resourceId("com.vathanakmao.solarnoon:id/spinnerSupportedLanguages"));
        assertTrue(langIconByResourceId.exists());

        UiObject2 dummyIcon2ByResourceId = device.findObject(By.res(APP_PACKAGE, "xnbmsueiyiqpdfudfuw"));
        assertNull(dummyIcon2ByResourceId);
        UiObject2 langIcon2ByResourceId = device.findObject(By.res(APP_PACKAGE, "spinnerSupportedLanguages"));
        assertNotNull(langIcon2ByResourceId);

        UiObject2 dummyIcon2ByResourceName = device.findObject(By.res("com.vathanakmao.solarnoon:id/xnbmsueiyiqpdfudfuw"));
        assertNull(dummyIcon2ByResourceName);
        UiObject2 langIconByResourceName = device.findObject(By.res("com.vathanakmao.solarnoon:id/spinnerSupportedLanguages"));
        assertNotNull(langIconByResourceName);
    }

}
