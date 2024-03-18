package com.vathanakmao.solarnoon;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    public Context createContext(String langCode) {
        final Locale preferredLocale = new Locale(langCode);
        Configuration config = new Configuration();
        config.setLocale(preferredLocale);
        return createConfigurationContext(config);
    }

}
