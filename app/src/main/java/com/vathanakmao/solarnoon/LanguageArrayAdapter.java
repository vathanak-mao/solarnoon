package com.vathanakmao.solarnoon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vathanakmao.solarnoon.util.LocaleUtil;

public class LanguageArrayAdapter extends ArrayAdapter {
    private Context context;
    private String[] languageCodes;

    public LanguageArrayAdapter(Context context, @LayoutRes int resource, @IdRes int textViewResourceId , String[] languageCodes) {
        super(context, resource, textViewResourceId, languageCodes);
        this.context = context;
        this.languageCodes = languageCodes;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        if (languageCodes != null) {
            return LocaleUtil.getDisplayName(languageCodes[position], true);
        }
        return null;
    }

//    @Override
//    public int getPosition(@Nullable Object item) {
//        if (languageCodes != null) {
//            for (int i = 0; i < languageCodes.length; i++) {
//                if (languageCodes[i].equals(item)) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Then the spinner only show the background image like an icon/button for users to click
        TextView textviewDisplayText = (TextView) convertView.findViewById(R.id.textviewListItemDisplayText);
        textviewDisplayText.setText("");

        TextView textviewValue = convertView.findViewById(R.id.textviewListItemValue);
        textviewValue.setText(languageCodes[position]);
//
//        ImageView checkmark = (ImageView) convertView.findViewById(R.id.imageviewListItemCheckmark);
//        if (languageCodes != null
//                && languageCodes[position].equals(Application.getPreferredLanguage(context))) {
//            checkmark.setVisibility(View.VISIBLE);
//        } else {
//            checkmark.setVisibility(View.INVISIBLE);
//        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView displayText = (TextView) convertView.findViewById(R.id.textviewListItemDisplayText);
        displayText.setText(String.valueOf(getItem(position)));

        TextView value = (TextView) convertView.findViewById(R.id.textviewListItemValue);
        value.setText(languageCodes[position]);

        ImageView checkmark = (ImageView) convertView.findViewById(R.id.imageviewListItemCheckmark);
        if (languageCodes != null
                && languageCodes[position].equals(Application.getPreferredLanguage(context))) {
            checkmark.setVisibility(View.VISIBLE);
        } else {
            checkmark.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
