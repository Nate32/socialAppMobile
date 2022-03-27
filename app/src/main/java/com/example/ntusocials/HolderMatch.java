package com.example.ntusocials;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderMatch extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView match_id, fullName_match;
    public ImageView matchImage;
    public HolderMatch(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        match_id = (TextView) itemView.findViewById(R.id.match_item_id);
        matchImage = (ImageView) itemView.findViewById(R.id.profile_matchImage_id);
        fullName_match = (TextView) itemView.findViewById(R.id.match_name_list);


    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), UserMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("match_id", match_id.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}
