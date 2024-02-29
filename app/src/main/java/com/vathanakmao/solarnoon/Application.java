package com.vathanakmao.solarnoon;

import android.content.Context;
import android.content.SharedPreferences;

public class Application {

    public static String getPreferredLanguage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String defaultValue = context.getResources().getString(R.string.default_preferred_language);
        return sharedPref.getString(context.getResources().getString(R.string.preferred_language_key), defaultValue);
    }

    public static void setPreferredLanguage(String lang, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getResources().getString(R.string.preferred_language_key), lang);
        editor.apply();
    }
}
