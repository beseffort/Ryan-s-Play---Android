package com.ryansplayllc.ryansplay.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryansplayllc.ryansplay.CropingOption;
import com.ryansplayllc.ryansplay.R;

import java.util.ArrayList;

/**
 * Created by nimaikrsna on 7/10/2015.
 */
public class CropingOptionAdapter extends ArrayAdapter {
    private ArrayList<CropingOption> mOptions;
    private LayoutInflater mInflater;

    public CropingOptionAdapter(Context context, ArrayList<CropingOption> options) {
        super(context, R.layout.cropping_selector, options);

        mOptions  = options;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.cropping_selector, null);

        CropingOption item =  mOptions.get(position);

        if (item != null) {
            ((ImageView) convertView.findViewById(R.id.img_icon)).setImageDrawable(item.icon);
            ((TextView) convertView.findViewById(R.id.txt_name)).setText(item.title);

            return convertView;
        }

        return null;
    }


}

