package com.vathanakmao.solarnoon;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    public Context createContext(String langCode) {
        final Locale preferredLocale = new Locale(langCode);
        Configuration config = new Configuration();
        config.setLocale(preferredLocale);
        return createConfigurationContext(config);
    }
}
