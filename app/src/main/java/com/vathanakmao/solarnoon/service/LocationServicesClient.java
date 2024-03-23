package com.vathanakmao.solarnoon.service;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.IntentSender;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.util.StringUtil;

public class LocationServicesClient {
    private Activity activity;
    private int priority; // Used to check location setting priority
    private int intervalMillis;
    private String[] permissions;

    public LocationServicesClient(Activity activity, int priority, int intervalMillis, String[] permissions) {
        this.activity = activity;
        this.priority = priority;
        this.intervalMillis = intervalMillis;
        this.permissions = permissions;
    }

    public void enableLocationSettingsAndGrantPermissions(int onActivityResultRequestCode, int onRequestPermissionsResultRequestCode) {
        // Check if Location Services enabled or location settings satisfied
        // This must be checked in onStart() to make sure
        // it runs everytime the activity comes back to the foreground.
        LocationRequest locationRequest = new LocationRequest.Builder(priority, intervalMillis).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Callback from SettingsClient.checkLocationSettings()
        task.addOnSuccessListener(response -> {
            if (response != null) {
                if (response instanceof LocationSettingsResponse) {
                    // If location access is enabled and settings are satisfied
                    promptToGrantAppPermissions(activity, permissions, onRequestPermissionsResultRequestCode);
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

                new AlertDialog.Builder(activity)
                        .setTitle("Location Services needed!")
                        .setMessage("Location Services must be enabled to get solar noon's time based on your current location. Please grant access to continue.")
                        .setPositiveButton("Grant Acccess", (dialog, which) -> promptToEnableLocationAccess(exception, activity, permissions, onActivityResultRequestCode))
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

//    private class ActivityWrapper extends Activity {
//        private Activity activity;
//        private String[] permissions;
//
//        ActivityWrapper(Activity activity, String[] permissions) {
//            this.activity = activity;
//            this.permissions = permissions;
//        }
//
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            try {
//                Method onActivityResult = activity.getClass().getDeclaredMethod("onActivityResult", int.class, int.class, Intent.class);
//                onActivityResult.setAccessible(true);
//                onActivityResult.invoke(activity, requestCode, resultCode, data);
//
//                if (resultCode == Activity.RESULT_OK) {
//                    // If resolution successful or location services enabled
//                    promptToGrantAppPermissions(activity, permissions, requestCode);
//                } else {
//                    // resolution failed or location services still disabled
//                    Log.d(getClass().getSimpleName(), "Location Services are still disabled.");
//                }
//            } catch (Exception e) {
//                Log.e(LocationServicesClient.class.getSimpleName(), StringUtil.getStackTrace(e));
//            }
//        }
//    }

    private void promptToGrantAppPermissions(Activity activity, String[] permissions, int requestCode) {
        if (permissionsGranted()) {
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
            if (ActivityCompat.checkSelfPermission(activity, permission) == PERMISSION_GRANTED) {
                countGrantedPermissions ++;
            }
        }
        return countGrantedPermissions == permissions.length;
    }

    public void promptUserForPermissions(int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
