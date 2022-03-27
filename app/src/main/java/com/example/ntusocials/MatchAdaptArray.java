package com.example.ntusocials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MatchAdaptArray extends RecyclerView.Adapter<HolderMatch> {

    private List<MatchPojo> matchPojoList;
    private Context contex;

    public MatchAdaptArray(List<MatchPojo> matchPojoList, Context context ){
        this.matchPojoList =matchPojoList;
        this.contex = context;
    }

    @Override
    public HolderMatch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, null, false);
        RecyclerView.LayoutParams  prams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
        , ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(prams);
        HolderMatch matchHolder = new HolderMatch(layout);
        return matchHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMatch holder, int position) {
       // holder.match_id.setText(matchPojoList.get(position).getUserId());
        holder.fullName_match.setText(matchPojoList.get(position).getFullName());
       // holder.match_id.setText(matchPojoList.get(position).getImageUrl());
        if(!matchPojoList.get(position).getImageUrl().equals("default")) {
            Glide.with(contex).load(matchPojoList.get(position).getImageUrl()).into(holder.matchImage);
        }
    }

    @Override
    public int getItemCount() {
        return matchPojoList.size();
    }
}
