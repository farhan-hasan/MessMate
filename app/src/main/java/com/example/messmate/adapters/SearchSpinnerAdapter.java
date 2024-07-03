package com.example.messmate.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.messmate.R;

import java.util.List;

public class SearchSpinnerAdapter extends ArrayAdapter<String> {

    public SearchSpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;
        if (position == 0) {
            textView.setTextColor(getContext().getResources().getColor(R.color.primary_color));
        } else {
            textView.setTextColor(getContext().getResources().getColor(R.color.bg_color));
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0; // Disable the hint item
    }
}
