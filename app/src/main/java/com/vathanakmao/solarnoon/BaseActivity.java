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

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private AlertDialog dialogLocationServices;

    public Context createContext(String langCode) {
        final Locale preferredLocale = new Locale(langCode);
        Configuration config = new Configuration();
        config.setLocale(preferredLocale);
        return createConfigurationContext(config);
    }

    public void promptEnableLocationServices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.enable_location_dialog, null);

        builder.setView(viewDialog).setTitle("Location Services Disabled");

        TextView textviewMessage = viewDialog.findViewById(R.id.dialogtextviewMessage);
        Button buttonEnable = viewDialog.findViewById(R.id.dialogbuttonEnable);
        Button buttonCancel = viewDialog.findViewById(R.id.dialogbuttonCancel);

        buttonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                hideDialog();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        dialogLocationServices = builder.show();
    }

    public void hideDialog() {
        if (dialogLocationServices != null) {
            dialogLocationServices.dismiss();
        }
    }
}
