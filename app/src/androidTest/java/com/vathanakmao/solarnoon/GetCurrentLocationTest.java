package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GetCurrentLocationTest {
    private Context context;

    public GetCurrentLocationTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testIsGooglePlayServicesAvailable() {
        assertEquals(ConnectionResult.SUCCESS, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context));
    }

}
