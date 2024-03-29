package com.vathanakmao.solarnoon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.exception.GetCurrentLocationException;
import com.vathanakmao.solarnoon.model.LocalTime;
import com.vathanakmao.solarnoon.service.LocationServiceClient;
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

    private LocationServiceClient locationServiceClient;
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
        initLoadingImage();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Prompt the user to grant app permissions for location access.
        // This must be done in onStart() to make sure
        // it runs everytime the activity comes back to the foreground.
        locationServiceClient = new LocationServiceClient();
        locationServiceClient.grantAppPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                () -> retrieveCurrentLocation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationServiceClient.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationServiceClient.onActivityResult(requestCode, resultCode, data);
    }

    public void retrieveCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getCurrentLocation(new CurrentLocationRequest.Builder().build(), null);
        Log.d(getLocalClassName(), "fusedLocationProviderClient.getCurrentLocation() called.");

        // check onSuccess()
        task.addOnSuccessListener(this);

        // check onFailure()
        task.addOnFailureListener(this, e -> {
            onFailure(new GetCurrentLocationException(e));
        });
    }

    @Override
    public void onSuccess(Object response) {
        if (response != null) {
            if (response instanceof Location) { // If getting current location successful
                final Location location = (Location) response;

                Log.d(getLocalClassName(), String.format("onSuccess() called for fusedLocationProviderClient.getCurrentLocation() - Location=%s", location));

                userLocationCache = location;
                showUserLocation(location, Settings.getPreferredLanguage(this));
                showSolarnoonTime(location, Settings.getPreferredLanguage(this));
            } else {
                Log.d(getLocalClassName(), String.format("[onSuccess()] response is unknown <%s>.", response));
            }
        } else {
            Log.d(getLocalClassName(), "[onSuccess] response is null.");
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (e instanceof GetCurrentLocationException) {
            Log.e(getLocalClassName(), String.format("Error getting current location. Cause: %s", StringUtil.getStackTrace(e)));
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

    public void initLoadingImage() {
        ImageView imageviewLoading = findViewById(R.id.imageviewLoading);
        Glide.with(this)
                .load(R.drawable.loading_icon)
                .into(imageviewLoading);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final TextView selectedTextView = findViewById(R.id.textviewListItemValue);
        if (selectedTextView != null) {
            final String selectedLangCode = String.valueOf(selectedTextView.getText());
            translateUIComponents(selectedLangCode);

            Settings.savePreferredLanguage(selectedLangCode, this);
        } else {
            Log.e(getLocalClassName(), "[onItemSelected()] selectedTextView is null");
        }
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
                        setCurrentLocationName(findViewById(R.id.textviewLocation), addresses);
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
                        setCurrentLocationName(findViewById(R.id.textviewLocation), addresses);
                    }
                });
            } catch (IllegalArgumentException e) {
                Log.e(getLocalClassName(), String.format("Error retrieving addresses based on latitude %s and longitude %. \n%s", location.getLatitude(), location.getLongitude(), e.getStackTrace().toString()));
            }
        }
    }

    private void setCurrentLocationName(TextView textView, @NonNull List<Address> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            final String locationName;
            final Address address = addresses.get(0);
            if (address.getSubLocality() != null) {
                locationName = address.getSubLocality(); // District name (e.g., Chamkarmorn)
            } else if (address.getLocality() != null) {
                locationName = address.getLocality(); // City name (e.g., Phnom Penh)
            } else if (address.getAdminArea() != null) {
                locationName = address.getAdminArea(); // Province/state
            } else {
                locationName = address.getCountryName();
            }
            textView.setText(locationName);
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