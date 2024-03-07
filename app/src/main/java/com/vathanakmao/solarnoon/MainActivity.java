package com.vathanakmao.solarnoon;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vathanakmao.solarnoon.model.LocalTime;
import com.vathanakmao.solarnoon.service.SolarNoonCalc;
import com.vathanakmao.solarnoon.util.MathUtil;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemSelectedListener {

    public static final int MYPERMISSIONREQUESTCODE_GETCURRENTLOCATION = 1;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SolarNoonCalc solarnoonCalc;
    private Location cacheCurrentLocation;

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final TextView selectedTextView = view.findViewById(R.id.textviewListItemValue);
        final String selectedLangCode = String.valueOf(selectedTextView.getText());
        translateUIComponents(selectedLangCode);

        Settings.savePreferredLanguage(selectedLangCode, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
        spinner.setOnItemSelectedListener(this);

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

                        setCurrentLocation(findViewById(R.id.textviewLocation), Settings.getPreferredLanguage(MainActivity.this), location.getLatitude(), location.getLongitude());
                        cacheCurrentLocation = location;

                        final double timezoneOffset = MathUtil.toHours(ZonedDateTime.now().getOffset().getTotalSeconds());
                        final LocalTime solarnoonTime = solarnoonCalc.getTime(location.getLatitude(), location.getLongitude(), timezoneOffset, new GregorianCalendar(), SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00);
                        Log.d(getLocalClassName(), String.format("Solar noon: %s:%s:%s", solarnoonTime.getHour(), solarnoonTime.getMinute(), solarnoonTime.getSecond()));

                        TextView display = findViewById(R.id.time);
                        display.setText(String.format("%s:%s %s", solarnoonTime.getHour(), solarnoonTime.getMinute(), solarnoonTime.getHour() < 12 ? "AM" : "PM"));
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

    /**
     * Display the location name (city's name) corresponding to the given latitude and longitude,
     * and is translated based on the given locale.
     * Suppose the location name is "Phnom Penh",
     * and if the locale is "en", it shows "Phnom Penh",
     * but if the locale is "km", it shows "ភ្នំពេញ".
     *
     * @param textviewLocation
     * @param langCode
     * @param latitude
     * @param longitude
     */
    private void setCurrentLocation(TextView textviewLocation, String langCode, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, new Locale(langCode));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                Log.d(getLocalClassName(), String.format("The API level of the running system is %s so falling back to the deprecated method Geocoder.getFromLocation(latitude, longitude, maxResults)", Build.VERSION.SDK_INT));

                final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                setLocationAddress(findViewById(R.id.textviewLocation), addresses);
            } catch (IOException e) {
                Log.e(getLocalClassName(), String.format("Error retrieving addresses for latitude %s and longitude %s. ", latitude, longitude), e);
            }
        } else {
            try {
                Log.d(getLocalClassName(), String.format("The API level of the running system is %s so calling Geocoder.getFromLocation(latitude, longitude, maxResults, Geocoder.GeocoderListeneer).", Build.VERSION.SDK_INT));

                // this method is only supported by API level 34
                geocoder.getFromLocation(latitude, longitude, 1, new Geocoder.GeocodeListener() {
                    @Override
                    public void onGeocode(List<Address> addresses) {
                        setLocationAddress(findViewById(R.id.textviewLocation), addresses);
                    }
                });
            } catch (IllegalArgumentException e) {
                Log.e(getLocalClassName(), String.format("Error retrieving addresses based on latitude %s and longitude %. \n%s", latitude, longitude, e.getStackTrace().toString()));
            }
        }
    }

    private void setLocationAddress(TextView textView, @NonNull List<Address> addresses) {
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

    private void translateUIComponents(String langCode) {
        TextView textviewDesc = findViewById(R.id.textviewDesc);
        textviewDesc.setText(createContext(langCode).getString(R.string.solarnoon_desc));

        if (cacheCurrentLocation != null) {
            setCurrentLocation(findViewById(R.id.textviewLocation), langCode, cacheCurrentLocation.getLatitude(), cacheCurrentLocation.getLongitude());
        }
    }

}