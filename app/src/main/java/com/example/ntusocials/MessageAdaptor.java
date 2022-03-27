package com.example.ntusocials;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdaptor extends RecyclerView.Adapter<HolderMessage> {

    private List<MessagePojo> messagePojoList;
    private Context contex;
   // private TextView messageRead;

    public MessageAdaptor(List<MessagePojo> messagePojoList, Context context ){
        this.messagePojoList =messagePojoList;
        this.contex = context;
    }

    @Override
    public HolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layouts = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, null, false);
        RecyclerView.LayoutParams  pram = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        layouts.setLayoutParams(pram);
        HolderMessage holderMessages = new HolderMessage(layouts);
        return holderMessages;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMessage holder, int position) {
        holder.message_val.setText(messagePojoList.get(position).getMessages());
//        if(messagePojoList.get(position).getCurrent_messenger()){
//            holder.message_val.setGravity(Gravity.END);
//            holder.message_val.setTextColor(Color.parseColor("#000000"));
//            holder.messageContainer.setBackgroundColor(Color.parseColor("#DCDCDC"));
//        } else{
//            holder.message_val.setGravity(Gravity.START);
//            holder.message_val.setBackgroundColor(Color.parseColor("#000000"));
//            holder.messageContainer.setBackgroundColor(Color.parseColor("#7CFC00"));
//        }
    }



    @Override
    public int getItemCount() {
        return this.messagePojoList.size();
    }
}
