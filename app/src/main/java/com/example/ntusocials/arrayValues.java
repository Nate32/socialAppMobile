package com.example.ntusocials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class arrayValues extends ArrayAdapter<populateCards> {

    Context context;

    public arrayValues(Context context, int resId, List<populateCards> val){
        super(context, resId, val);
    }

    public View getView(int pos, View covertView, ViewGroup parent){
        populateCards card_val = getItem(pos);

        if (covertView == null){
            covertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = (TextView) covertView.findViewById(R.id.settings_name);
        ImageView image = (ImageView) covertView.findViewById(R.id.profile_image_user);

        name.setText(card_val.getName());

        switch (card_val.getImageProfile()){
            case "default":
                Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:
                Glide.with(getContext()).load(card_val.getImageProfile()).into(image);
                break;
        }

        return covertView;

    }

}
