package com.vathanakmao.solarnoon.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.location.Location;
import android.location.LocationManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.vathanakmao.solarnoon.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest extends BaseUITest {

    @Test
    public void findObjectByResourceId() {
        // Using UiSelector to find object by resourceId
        assertFalse(device.findObject(new UiSelector().resourceId("xnbmsueiyiqpdfudfuw")).exists());
        assertTrue(device.findObject(new UiSelector().resourceId("com.vathanakmao.solarnoon:id/spinnerSupportedLanguages")).exists());

        // Using BySelector to find object by resourceId
        assertNull(device.findObject(By.res(APP_PACKAGE, "xnbmsueiyiqpdfudfuw")));
        assertNotNull(device.findObject(By.res(APP_PACKAGE, "spinnerSupportedLanguages")));
    }

    @Test
    public void findObjectByResourceName() {
        // Using BySelector to find object by resourceName
        assertNull(device.findObject(By.res("com.vathanakmao.solarnoon:id/xnbmsueiyiqpdfudfuw")));
        assertNotNull(device.findObject(By.res("com.vathanakmao.solarnoon:id/spinnerSupportedLanguages")));
    }
}
