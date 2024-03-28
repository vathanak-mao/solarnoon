package com.vathanakmao.solarnoon;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.util.StringUtil;

public class LocationAccessActivity extends BaseActivity {
    public static final int LOCATION_ACCESS_REQUEST_CODE = 1895624173;

    // These properties can be overridden in onCreated() method.
    private int priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
    private int intervalMillis = 60 * 1000;
    private OnPermissionsGrantedListener onPermissionsGrantedListener;

    public interface OnPermissionsGrantedListener {
        void onPermissionsGranted();
    }
    /**
     * Check if the location access is on and its settings are satisfied.
     * If they are, prompt a user to grant app permissions,
     * and the onRequestPermissionsResult() method will be invoked after the user responds.
     * Otherwise, prompt the user to enable location service with one tap,
     * then prompt the user to grant app permissions.
     */
    protected void grantAppPermissions(String[] permissions, OnPermissionsGrantedListener callback) {
        // it is used in onRequestPermissionsResult()
        this.onPermissionsGrantedListener = callback;

        if (!permissionsGranted(permissions)) {
            // Prompt user to grant app permissions.
            // Check onRequestPermissionsResult() for response
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_ACCESS_REQUEST_CODE);
            Log.d(getClass().getSimpleName(), "Permissions have been requested.");
        } else {
            Log.d(getClass().getSimpleName(), "Permissions were already granted.");
            onRequestPermissionsResult(
                    LOCATION_ACCESS_REQUEST_CODE,
                    permissions,
                    new int[]{PERMISSION_GRANTED});
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If it's a callback from ActivityCompat.requestPermissions()
        // which prompts the user to grant app permissions for location access
        if (requestCode == LOCATION_ACCESS_REQUEST_CODE) {
            // If the user has granted app permissions for location access
            // by clicking on either "Allow only while using the app" or "Ask every time" button.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check if location service is enabled (location settings are satisfied)
                LocationRequest locationRequest = new LocationRequest.Builder(priority, intervalMillis).build();
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

                SettingsClient client = LocationServices.getSettingsClient(this);
                Task task = client.checkLocationSettings(builder.build());

                task.addOnSuccessListener(response -> {
                    // If location service is enabled, proceed with location access task
                    onPermissionsGrantedListener.onPermissionsGranted();
                });

                task.addOnFailureListener(this, exception -> {
                    // If location service is disabled
                    if (exception instanceof ResolvableApiException) {
                        new AlertDialog.Builder(this)
                                .setTitle("Location Service needed!")
                                .setMessage("Location Service must be enabled for the app to function. Please click Next to start granting access.")
                                .setPositiveButton("Next", (dialog, which) -> promptToEnableLocationService(exception, this, requestCode))
                                .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                                .create().show();
                    }
                });
            } else { // Or the user might have clicked on the "Do not allow" button
                Log.d(getLocalClassName(), "Permissions have been denied because the user might have clicked on the 'Do not allow' button.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If it's a callback from ResolvableApiException.startResolutionResult()
        // which shows a dialog to enable location service with one tap.
        if (requestCode == LOCATION_ACCESS_REQUEST_CODE) {
            // If the user has clicked OK button
            if (resultCode == Activity.RESULT_OK) {
                Log.d(getLocalClassName(), "User has clicked OK button so location service is now enabled.");

                // Proceed with location access task
                onPermissionsGrantedListener.onPermissionsGranted();
            } else { // Or the user has clicked "No thanks" button
                Log.d(getLocalClassName(), "User has clicked 'No thanks' button so location service is still disabled.");
            }
        }
    }

    private void promptToEnableLocationService(Exception exception, Activity activity, int requestCode) {
        ResolvableApiException resolvable = (ResolvableApiException) exception;
        try {
            // Prompt the user to enable location service with one tap.
            // The onActivityResult() is called after the user has responded.
            resolvable.startResolutionForResult(activity, requestCode);
        } catch (IntentSender.SendIntentException e) {
            // Handle error launching the resolution intent
            Log.e(LocationAccessActivity.class.getSimpleName(), StringUtil.getStackTrace(e));
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
