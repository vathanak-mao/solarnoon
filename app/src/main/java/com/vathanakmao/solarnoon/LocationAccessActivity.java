package com.vathanakmao.solarnoon;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.util.StringUtil;

public class LocationAccessActivity extends BaseActivity {
    public static final int REQUESTCODE__ENABLE_LOCATION_SERVICES = 1895624173;

    // These properties can be overridden in onCreated() method.
    private int priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
    private int intervalMillis = 60 * 1000;
    private String[] permissions = new String[] {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};
    private int onRequestPermissionsResultRequestCode = 967583124;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE__ENABLE_LOCATION_SERVICES) { // If callback from ResolvableApiException.startResolutionResult().
            if (resultCode == Activity.RESULT_OK) { // If resolution successful or location services enabled
                promptToGrantAppPermissions(this, permissions, onRequestPermissionsResultRequestCode);
            } else { // resolution failed or location services still disabled
                Log.d(getLocalClassName(), "Location Services still disabled.");
            }
        }
    }

    /**
     * Check if the location access is on and settings are satisfied.
     * If they are, prompt a user to grant app permissions,
     * and the onRequestPermissionsResult() method will be invoked after the user responds.
     * Otherwise, prompt the user to enable location access and its settings with one tap,
     * then prompt the user to grant app permissions.
     *
     * @param onRequestPermissionsResultRequestCode this will be used to check the user's response in onRequestPermissionsResult()
     */
    public void grantAppPermissions(String[] permissions, int onRequestPermissionsResultRequestCode) {
        // it is used in onRequestPermissionsResult()
        this.permissions = permissions;
        this.onRequestPermissionsResultRequestCode = onRequestPermissionsResultRequestCode;

        // Check if Location Services enabled or location settings satisfied
        // This must be checked in onStart() to make sure
        // it runs everytime the activity comes back to the foreground.
        LocationRequest locationRequest = new LocationRequest.Builder(priority, intervalMillis).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Callback from SettingsClient.checkLocationSettings()
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse response) {
                if (response != null) {
                    if (response instanceof LocationSettingsResponse) {
                        // If location access is enabled and settings are satisfied
                        promptToGrantAppPermissions(LocationAccessActivity.this, permissions, onRequestPermissionsResultRequestCode);
                    } else {
                        Log.d(LocationAccessActivity.class.getSimpleName(), "[onSuccess()] Callback from SettingsClient.checkLocationSettings(): response=" + response);
                    }
                } else {
                    Log.d(LocationAccessActivity.class.getSimpleName(), "[onSuccess()] Callback from SettingsClient.checkLocationSettings(): reponse=" + response);
                }
            }
        });

        // Callback from SettingsClient.checkLocationSettings()
        task.addOnFailureListener(exception -> {
            if (exception instanceof ResolvableApiException) {
                // Location access disabled or settings not satisfied,
                // but this can be fixed by showing the user a dialog.
                Log.d(LocationAccessActivity.class.getSimpleName(), "[onFailure()] Location settings are not satisfied.");

                new AlertDialog.Builder(this)
                        .setTitle("Location Services needed!")
                        .setMessage("Location Services must be enabled to get solar noon's time based on your current location. Please grant access to continue.")
                        .setPositiveButton("Grant Acccess", (dialog, which) -> promptToEnableLocationAccess(exception, LocationAccessActivity.this, REQUESTCODE__ENABLE_LOCATION_SERVICES))
                        .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                        .create().show();
            } else {
                Log.e(LocationAccessActivity.class.getSimpleName(), StringUtil.getStackTrace(exception));
            }
        });
    }

    private void promptToEnableLocationAccess(Exception e, Activity activity, int requestCode) {
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
        if (!permissionsGranted(permissions)) {
            // Prompt user to grant app permissions.
            // Check onRequestPermissionsResult() for response
            ActivityCompat.requestPermissions(this, permissions, requestCode);
            Log.d(getClass().getSimpleName(), "Permissions have been requested.");
        } else {
            // Otherwise, retrieve the user's location (latitude & longitude)
            // then calculate the corresponding solar noon time
            // and display it.
            Log.d(getClass().getSimpleName(), "Permissions were already granted.");
            activity.onRequestPermissionsResult(requestCode, permissions, new int[]{PERMISSION_GRANTED});
        }
    }

    /**
     * Check if the defined permissions, such as ACCESS_COARSE_LOCATION, have been granted.
     *
     * @return
     */
    private boolean permissionsGranted(String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            Log.d(getClass().getSimpleName(), "Permissions is null or empty");
            return false;
        }
        int countGrantedPermissions = 0;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED) {
                countGrantedPermissions ++;
            }
        }
        return countGrantedPermissions == permissions.length;
    }
}
