package com.vathanakmao.solarnoon;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.exception.GetCurrentLocationException;
import com.vathanakmao.solarnoon.model.LocalTime;
import com.vathanakmao.solarnoon.service.SolarNoonCalc;
import com.vathanakmao.solarnoon.util.MathUtil;
import com.vathanakmao.solarnoon.util.StringUtil;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        AdapterView.OnItemSelectedListener,
        OnFailureListener, OnSuccessListener {

    public static final int REQUESTCODE_GETCURRENTLOCATION = 1;
    public static final int REQUEST_ENABLE_LOCATION_SERVICES = 2;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SolarNoonCalc solarnoonCalc;
    private Location userLocationCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        solarnoonCalc = new SolarNoonCalc();

        initLanguageSpinner(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if Location Services enabled or location settings satisfied
        // This must be checked in onStart() to make sure
        // it runs everytime the activity comes back to the foreground.
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this); // Check onSuccess()
        task.addOnFailureListener(this); // Check onFailure()
    }

    @Override
    public void onSuccess(Object response) {
        if (response != null) {
            // If Location Services enabled
            // or all location settings satisfied.
            // Callback from SettingsClient.checkLocationSettings()
            if (response instanceof LocationSettingsResponse) {
                Log.d(MainActivity.this.getLocalClassName(), String.format("onSuccess called for SettingsClient.checkLocationSettings() - LocationSettingsResponse=%s", ((LocationSettingsResponse) response).getLocationSettingsStates().toString()));

                requestUserPermissionsAndCurrentLocation();
            } else if (response instanceof Location) { // If getting current location successful
                final Location location = (Location) response;

                Log.d(getLocalClassName(), String.format("onSuccess() called for fusedLocationProviderClient.getCurrentLocation() - Location=%s", location));
//                DialogFactory.showNewInfoDialog(String.format("[onSuccess()] location=%s", location), this);

                userLocationCache = location;
                showUserLocation(location, Settings.getPreferredLanguage(this));
                showSolarnoonTime(location, Settings.getPreferredLanguage(this));
            } else {
                Log.d(getLocalClassName(), String.format("[onSuccess()] response is unknown <%s>.", response));
//                DialogFactory.showNewInfoDialog(String.format("[onSuccess()] response is unknown <%s>.", response) , this);
            }
        } else {
            Log.d(getLocalClassName(), "[onSuccess] response is null.");
//            DialogFactory.showNewInfoDialog("[onSuccess()] response is null.", this);
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d(MainActivity.this.getLocalClassName(), String.format("onFailure() called for SettingsClient.checkLocationSettings() - ERROR: %s", StringUtil.getStackTrace(e)));

        // Location Services disabled
        // or Location settings not satisfied,
        // but this can be fixed by showing the user a dialog.
        // Callback from SettingsClient.checkLocationSettings()
        if (e instanceof ResolvableApiException) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Services needed!")
                    .setMessage("Location Services must be enabled to get solar noon's time based on your current location. Please grant access to continue.")
                    .setPositiveButton("Grant Acccess", (dialog, which) -> startResolution(e))
                    .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else if (e instanceof GetCurrentLocationException) {
            Log.e(getLocalClassName(), String.format("Error getting current location. Cause: %s", StringUtil.getStackTrace(e)));
        }
    }

    private void startResolution(Exception e) {
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(MainActivity.this, REQUEST_ENABLE_LOCATION_SERVICES);

            Log.d(MainActivity.this.getLocalClassName(), "startResolutionForResult() called.");
        } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(getLocalClassName(), String.format("onActivityResult() called: requestCode=%s, resultCode=%s", requestCode, resultCode));

        if (requestCode == REQUEST_ENABLE_LOCATION_SERVICES) { // If callback from ResolvableApiException.startResolutionResult().
            if (resultCode == Activity.RESULT_OK) { // If resolution successful or location services enabled
                requestUserPermissionsAndCurrentLocation();
            } else { // resolution failed or location services still disabled
                Log.d(getLocalClassName(), "Location Services still disabled.");
//                DialogFactory.showNewInfoDialog("[onActivityResult()] Location Services still disabled.", this);
            }
        }
    }

    private void requestUserPermissionsAndCurrentLocation() {
        // If location enabled but no permission to access the user's location, request it.
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            // Check onRequestPermissionsResult() for response
            ActivityCompat.requestPermissions(this, new String[] {ACCESS_COARSE_LOCATION}, REQUESTCODE_GETCURRENTLOCATION);
            Log.d(getLocalClassName(), "Permissions have been requested.");
        } else {
            // Otherwise, retrieve the user's location (latitude & longitude)
            // then calculate the corresponding solar noon time
            // and display it.
            Log.d(getLocalClassName(), "Permissions were already granted.");
//                    DialogFactory.showNewInfoDialog("[onActivityResult()] Permissions were already granted.", this);
            requestUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(getLocalClassName(), "onRequestPermissionResult() called");

        switch (requestCode) {
            case REQUESTCODE_GETCURRENTLOCATION: {
                // If a user has granted a permission to access current location
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    Log.d(getLocalClassName(), "Permissions have been granted!");
//                    DialogFactory.showNewInfoDialog("[onRequestPermissionsResult()] Permissions have been granted.", this);
                    requestUserLocation();
                } else {
//                    DialogFactory.showNewInfoDialog("[onRequestPermissionsResult()] Permissions have been denied.", this);
                    Log.d(getLocalClassName(), "Permissions have been denied!");
                }
                return;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initLanguageSpinner(Context context) {
        final String[] languageCodes = getResources().getStringArray(R.array.supported_languages);
        LanguageArrayAdapter adapter = new LanguageArrayAdapter(context, R.layout.list_item, R.id.textviewListItemValue, languageCodes);

        Spinner spinner = findViewById(R.id.spinnerSupportedLanguages);
        spinner.setAdapter(adapter);

        // By default, the spinner chooses the item at position 0 for selection when initializing,
        // then the onSelectedItem() handler method will also be called
        // and so the language at position (index) 0 would be saved in preferences
        // whether or not there is already one there.
        // After that, when a user clicks on the spinner for the first time,
        // the first item will always be checked in the dropdown.
        // Calling setSelection(position) method tells the spinner
        // to select or check the item at the given position instead of position 0.
        final int position = adapter.getPosition(Settings.getPreferredLanguage(this));
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(this); // Check onItemSelected()
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final TextView selectedTextView = view.findViewById(R.id.textviewListItemValue);
        final String selectedLangCode = String.valueOf(selectedTextView.getText());
        translateUIComponents(selectedLangCode);

        Settings.savePreferredLanguage(selectedLangCode, this);
    }

    private void requestUserLocation() throws SecurityException {
        Task<Location> task = fusedLocationProviderClient.getCurrentLocation(new CurrentLocationRequest.Builder().build(), null);
        Log.d(getLocalClassName(), "fusedLocationProviderClient.getCurrentLocation() called.");

        // check onSuccess()
        task.addOnSuccessListener(this);

        // check onFailure()
        task.addOnFailureListener(this, e -> {
            onFailure(new GetCurrentLocationException(e));
        });
    }

    /**
     * Display the location name (city's name) corresponding to the given latitude and longitude,
     * and is translated based on the given locale.
     * Suppose the location name is "Phnom Penh",
     * and if the locale is "en", it shows "Phnom Penh",
     * but if the locale is "km", it shows "ភ្នំពេញ".
     *
     * @param location
     * @param langCode
     */
    private void showUserLocation(Location location, String langCode) {
        Geocoder geocoder = new Geocoder(this, new Locale(langCode));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Log.d(getLocalClassName(), String.format("The API level of the running system is %s so falling back to the deprecated method Geocoder.getFromLocation(latitude, longitude, maxResults)", Build.VERSION.SDK_INT));

            Handler handler = new Handler();
            handler.post(new Runnable() {
                public void run() {
                    try {
                        final List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        setCurrentLocationAddress(findViewById(R.id.textviewLocation), addresses);
                    } catch (IOException e) {
                        Log.e(getLocalClassName(), String.format("Error retrieving addresses for latitude %s and longitude %s. ", location.getLatitude(), location.getLongitude()), e);
                    }
                }
            });
        } else {
            try {
                Log.d(getLocalClassName(), String.format("The API level of the running system is %s so calling Geocoder.getFromLocation(latitude, longitude, maxResults, Geocoder.GeocoderListeneer).", Build.VERSION.SDK_INT));

                // this method is only supported by API level 34
                geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1, new Geocoder.GeocodeListener() {
                    @Override
                    public void onGeocode(List<Address> addresses) {
                        setCurrentLocationAddress(findViewById(R.id.textviewLocation), addresses);
                    }
                });
            } catch (IllegalArgumentException e) {
                Log.e(getLocalClassName(), String.format("Error retrieving addresses based on latitude %s and longitude %. \n%s", location.getLatitude(), location.getLongitude(), e.getStackTrace().toString()));
            }
        }
    }

    private void setCurrentLocationAddress(TextView textView, @NonNull List<Address> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            if (addresses.get(0).getLocality() != null) {
                textView.setText(addresses.get(0).getLocality()); // city name
            } else {
                textView.setText(addresses.get(0).getSubLocality()); // district name
            }

            Log.d(getLocalClassName(), String.format("Set addresses<%s> to the textview <id=%s>", addresses.toString(), textView.getId()));
        } else {
            Log.e(getLocalClassName(), String.format("Unabled to set addresses <%s> for the text view <id=%s>", addresses, textView.getId()));
        }
    }

    private void showSolarnoonTime(Location location, String langCode) {
        final double timezoneOffset = MathUtil.toHours(ZonedDateTime.now().getOffset().getTotalSeconds());
        final LocalTime solarnoonTime = solarnoonCalc.getTime(location.getLatitude(), location.getLongitude(), timezoneOffset, new GregorianCalendar(), SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00);

        ImageView imageviewLoading = findViewById(R.id.imageviewLoading);
        imageviewLoading.setVisibility(View.GONE);

        TextView textviewSolarnoonTime = findViewById(R.id.textviewSolarnoonTime);
        textviewSolarnoonTime.setText(translateNumbers(StringUtil.prependZeroIfOneDigit(solarnoonTime.getHour()), langCode) + ":" + translateNumbers(StringUtil.prependZeroIfOneDigit(solarnoonTime.getMinute()), langCode));
    }

    private String translateNumbers(String number, String langCode) {
        Context localizedContext = createContext(langCode);
        final StringBuilder result = new StringBuilder();
        final String[] digits = localizedContext.getResources().getStringArray(R.array.digits);

        for (char digit : number.toCharArray()) {
            if (Character.isDigit(digit)) {
                int index = Character.getNumericValue(digit);
                result.append(digits[index]);
            } else {
                result.append(digit);
            }
        }
        return result.toString();
    }

    private void translateUIComponents(String langCode) {
        TextView textviewDesc = findViewById(R.id.textviewDesc);
        textviewDesc.setText(createContext(langCode).getString(R.string.solarnoon_desc));

        if (userLocationCache != null) {
            showUserLocation(userLocationCache, langCode);
            showSolarnoonTime(userLocationCache, langCode);
        }
    }

}