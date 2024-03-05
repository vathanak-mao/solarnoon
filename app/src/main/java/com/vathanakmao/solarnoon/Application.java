package com.vathanakmao.solarnoon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Application {

    /**
     * Get previously saved language code from preferences.
     *
     * @param context
     * @return
     */
    public static String getPreferredLanguage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.key_preferences_filename), Context.MODE_PRIVATE);
        final String defaultLangCode = context.getResources().getString(R.string.default_preferred_language);
        return sharedPref.getString(context.getResources().getString(R.string.key_preferred_language), defaultLangCode);
    }

    /**
     * Save language code in preferences.
     * @param languageCode e.g. 'en', 'km'
     * @param context
     */
    public static void savePreferredLanguage(String languageCode, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.key_preferences_filename), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getResources().getString(R.string.key_preferred_language), languageCode);
        editor.apply();

        Log.d(Application.class.getSimpleName(), String.format("Saved language code '%s' in preferences.", languageCode));
    }
}
