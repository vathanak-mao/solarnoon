package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertFalse;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class GeocoderTest {
    private Context context;
    private Geocoder geocoder;

    public GeocoderTest() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Before
    public void setUp() {
        geocoder = new Geocoder(context);
    }

    @Test
    public void getAddressesBasedOnLocale() {
        geocoder = new Geocoder(context, new Locale("km"));
        try {
            List<Address> addresses = geocoder.getFromLocation(11.55D, 104.94D, 1);
            assertEquals("ភ្នំពេញ", addresses.get(0).getLocality());
        } catch (IOException e) {
            assertFalse(String.format("Check if there is an internet connection. %s", e.getStackTrace()), false);
        }
    }
}
