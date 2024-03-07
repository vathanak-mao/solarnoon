package com.vathanakmao.solarnoon;

import android.content.Context;
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

public class LanguageArrayAdapter extends ArrayAdapter {
    private Context context;

    public LanguageArrayAdapter(Context context, @LayoutRes int resource, @IdRes int textViewResourceId , String[] languageCodes) {
        super(context, resource, textViewResourceId, languageCodes);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }


        // Then the spinner only show the background image like an icon/button for users to click
        TextView textviewDisplayText = convertView.findViewById(R.id.textviewListItemDisplayText);
        textviewDisplayText.setText("");

        TextView textviewValue = convertView.findViewById(R.id.textviewListItemValue);
        textviewValue.setText(String.valueOf(getItem(position)));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView itemDisplayText = convertView.findViewById(R.id.textviewListItemDisplayText);
        itemDisplayText.setText(String.valueOf(getItem(position)));

        TextView itemValue = convertView.findViewById(R.id.textviewListItemValue);
        ImageView itemCheckmark = convertView.findViewById(R.id.imageviewListItemCheckmark);
        if (String.valueOf(getItem(position)).equals(SolarnoonApp.getPreferredLanguage(context))) {
            itemValue.setText(SolarnoonApp.getPreferredLanguage(context));
            itemCheckmark.setVisibility(View.VISIBLE);
        } else {
            itemValue.setText(String.valueOf(getItem(position)));
            itemCheckmark.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
