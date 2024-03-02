package com.vathanakmao.solarnoon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Locale;

public class LanguageAdapter extends BaseAdapter {
    private Context context;
    private String[] languageCodes;

    public LanguageAdapter(Context context, String[] languageCodes) {
        this.context = context;
        this.languageCodes = languageCodes;
    }

    @Override
    public int getCount() {
        return languageCodes != null ? languageCodes.length : 0;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.textView);
        text.setText(String.valueOf(getItem(position)));
        return convertView;
    }
}
