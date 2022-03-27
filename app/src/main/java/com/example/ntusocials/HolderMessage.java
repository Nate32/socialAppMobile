package com.example.ntusocials;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderMessage extends RecyclerView.ViewHolder implements  View.OnClickListener {

    public TextView message_val;
    public LinearLayout messageContainer;
    public HolderMessage(@NonNull View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        messageContainer = itemView.findViewById(R.id.messageContainer);
        message_val = itemView.findViewById(R.id.Message_value);

    }

    @Override
    public void onClick(View view) {

    }
}
