package com.vathanakmao.solarnoon;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.service.LocationServicesClient;
import com.vathanakmao.solarnoon.util.StringUtil;

import java.util.UUID;

public abstract class LocationAccessActivity extends BaseActivity {
    public static final int REQUESTCODE__ENABLE_LOCATION_SERVICES = 1895624173;
    public static final int REQUESTCODE__GET_CURRENT_LOCATION = 817452369;

    private int priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
    private int intervalMillis = 60 * 60 * 1000;
    private String[] permissions = new String[] {ACCESS_COARSE_LOCATION};

    protected abstract Context getContext(); 
    protected abstract Activity getActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
        intervalMillis = 60 * 60 * 1000;
        permissions = new String[] {ACCESS_COARSE_LOCATION};
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE__ENABLE_LOCATION_SERVICES) { // If callback from ResolvableApiException.startResolutionResult().
            if (resultCode == Activity.RESULT_OK) { // If resolution successful or location services enabled
                promptToGrantAppPermissions(getActivity(), permissions, REQUESTCODE__GET_CURRENT_LOCATION);
            } else { // resolution failed or location services still disabled
                Log.d(getLocalClassName(), "Location Services still disabled.");
            }
        }
    }

    public void enableLocationSettingsAndGrantPermissions(int onActivityResultRequestCode, int onRequestPermissionsResultRequestCode) {
        // Check if Location Services enabled or location settings satisfied
        // This must be checked in onStart() to make sure
        // it runs everytime the activity comes back to the foreground.
        LocationRequest locationRequest = new LocationRequest.Builder(priority, intervalMillis).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Callback from SettingsClient.checkLocationSettings()
        task.addOnSuccessListener(response -> {
            if (response != null) {
                if (response instanceof LocationSettingsResponse) {
                    // If location access is enabled and settings are satisfied
                    promptToGrantAppPermissions(getActivity(), permissions, onRequestPermissionsResultRequestCode);
                } else {
                    Log.d(LocationServicesClient.class.getSimpleName(), "[onSuccess()] Callback from SettingsClient.checkLocationSettings(): response=" + response);
                }
            } else {
                Log.d(LocationServicesClient.class.getSimpleName(), "[onSuccess()] Callback from SettingsClient.checkLocationSettings(): reponse=" + response);
            }
        });

        // Callback from SettingsClient.checkLocationSettings()
        task.addOnFailureListener(exception -> {
            if (exception instanceof ResolvableApiException) {
                // Location access disabled or settings not satisfied,
                // but this can be fixed by showing the user a dialog.
                Log.d(LocationServicesClient.class.getSimpleName(), "[onFailure()] Location settings are not satisfied.");

                new AlertDialog.Builder(getContext())
                        .setTitle("Location Services needed!")
                        .setMessage("Location Services must be enabled to get solar noon's time based on your current location. Please grant access to continue.")
                        .setPositiveButton("Grant Acccess", (dialog, which) -> promptToEnableLocationAccess(exception, getActivity(), permissions, onActivityResultRequestCode))
                        .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                        .create().show();
            } else {
                Log.e(LocationServicesClient.class.getSimpleName(), StringUtil.getStackTrace(exception));
            }
        });
    }

    private void promptToEnableLocationAccess(Exception e, Activity activity, String[] permissions, int requestCode) {
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(activity, requestCode);
        } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
            Log.w(getClass().getSimpleName(), String.format("Failed trying to start resolution for result <activity=%s, requestCode=%s>", activity, requestCode));
        }
    }

    private void promptToGrantAppPermissions(Activity activity, String[] permissions, int requestCode) {
        if (!permissionsGranted()) {
            // Check onRequestPermissionsResult() for response
            promptUserForPermissions(requestCode);
            Log.d(getClass().getSimpleName(), "Permissions have been requested.");
        } else {
            // Otherwise, retrieve the user's location (latitude & longitude)
            // then calculate the corresponding solar noon time
            // and display it.
            Log.d(getClass().getSimpleName(), "Permissions were already granted.");
            activity.onRequestPermissionsResult(requestCode, permissions, new int[]{PERMISSION_GRANTED});
        }

    }

    public boolean permissionsGranted() {
        if (permissions.length == 0) {
            Log.d(getClass().getSimpleName(), "Permissions is empty");
            return false;
        }
        int countGrantedPermissions = 0;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) == PERMISSION_GRANTED) {
                countGrantedPermissions ++;
            }
        }
        return countGrantedPermissions == permissions.length;
    }

    public void promptUserForPermissions(int requestCode) {
        ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
    }
}
