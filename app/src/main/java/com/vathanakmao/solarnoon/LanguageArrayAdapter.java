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

import java.util.Locale;

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
            Locale locale = new Locale(languageCodes[position]);
            return locale.getDisplayName(locale);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.textView);
//        text.setText(String.valueOf(getItem(position)));
        text.setText("");
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the layout for the dropdown item
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);

        TextView text = (TextView) view.findViewById(R.id.textView);
        ImageView checkmark = (ImageView) view.findViewById(R.id.checkmark);

//        MyData data = getItem(position);

        text.setText(String.valueOf(getItem(position)));
//        checkmark.setVisibility(data.isChecked() ? View.VISIBLE : View.GONE);

        // Optional: Customize the view further for the dropdown
        // - Set different background color or text style
        // - Remove the checkmark if not desired

        return view;
    }
}
