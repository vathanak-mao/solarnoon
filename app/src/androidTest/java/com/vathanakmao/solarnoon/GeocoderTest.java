package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        geocoder = new Geocoder(context);
    }

    @Test
    public void getAddressesBasedOnLocale_PhnomPenh() {
        geocoder = new Geocoder(context, new Locale("km"));
        try {
            List<Address> addresses = geocoder.getFromLocation(11.55D, 104.94D, 1);
            assertEquals("ខណ្ឌចំការមន", addresses.get(0).getSubLocality());
            assertEquals("ភ្នំពេញ", addresses.get(0).getLocality());
            assertEquals("ភ្នំពេញ", addresses.get(0).getAdminArea());
            assertEquals("កម្ពុជា", addresses.get(0).getCountryName());
        } catch (IOException e) {
            assertFalse(String.format("Check if there is an internet connection. %s", e.getStackTrace()), false);
        }
    }

    @Test
    public void getAddressesBasedOnLocale_GoogleHeadQuarterInUS() {
        geocoder = new Geocoder(context, new Locale("km"));
        try {
            List<Address> addresses = geocoder.getFromLocation(37.4220D, -122.0840D, 1);
            assertEquals("Mountain View", addresses.get(0).getLocality());
        } catch (IOException e) {
            assertFalse(String.format("Check if there is an internet connection. %s", e.getStackTrace()), false);
        }
    }

    @Test
    public void getAddressesBasedOnLocale_Oslo() {
        geocoder = new Geocoder(context, new Locale("km"));
        try {
            List<Address> addresses = geocoder.getFromLocation(59.911491D, 10.757933D, 1);
            assertNull(addresses.get(0).getLocality());
            assertEquals("Gamle Oslo", addresses.get(0).getSubLocality());
        } catch (IOException e) {
            assertFalse(String.format("Check if there is an internet connection. %s", e.getStackTrace()), false);
        }
    }
}
