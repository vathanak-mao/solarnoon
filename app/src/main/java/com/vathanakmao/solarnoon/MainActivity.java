package com.vathanakmao.solarnoon;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final int MYPERMISSIONREQUESTCODE_GETCURRENTLOCATION = 1;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SolarNoonCalc solarnoonCalc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        solarnoonCalc = new SolarNoonCalc();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {ACCESS_COARSE_LOCATION}, MYPERMISSIONREQUESTCODE_GETCURRENTLOCATION);
            Log.d(getLocalClassName(), "Permissions have been requested!");
        } else {
            Log.d(getLocalClassName(), "Permissions were already granted!");
            calculateSolarNoonTime();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(getLocalClassName(), "onRequestPermissionResult() called");

        switch (requestCode) {
            case MYPERMISSIONREQUESTCODE_GETCURRENTLOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    Log.d(getLocalClassName(), "Permissions have been granted!");
                    calculateSolarNoonTime();
                } else {
                    Log.d(getLocalClassName(), "Permissions have been denied!");
                }
                return;
            }
        }
    }

    private void calculateSolarNoonTime() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getCurrentLocation(new CurrentLocationRequest.Builder().build(), null);
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.d(getLocalClassName(), "onSuccess() called");

                    if (location != null) {
                        Log.d(getLocalClassName(), "Location: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());

                        final double timezoneOffset = MathUtil.toHours(ZonedDateTime.now().getOffset().getTotalSeconds());
                        final LocalTime solarnoonLocalTime = solarnoonCalc.getTime(location.getLatitude(), location.getLongitude(), timezoneOffset, new GregorianCalendar(), SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00);
                        Log.d(getLocalClassName(), String.format("Solar noon: %s:%s:%s", solarnoonLocalTime.getHour(), solarnoonLocalTime.getMinute(), solarnoonLocalTime.getSecond()));

                        TextView display = findViewById(R.id.solarnoonView);
                        display.setText(String.format("Today, solar noon is at %s:%s", solarnoonLocalTime.getHour(), solarnoonLocalTime.getMinute()));

                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(getLocalClassName(), e.getStackTrace().toString());
                }
            });
        }
    }
}