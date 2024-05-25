package com.banledcamung.bicat;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView avatar;
    RelativeLayout item;
    TextView name, descript, totalTime, remainTime;
    boolean selected;
    public final int BELONG_TO_TAB_1 = 1;
    public final int BELONG_TO_TAB_2 = 2;
    public int pos;
    MyAdapter adapter;
    public MyViewHolder(@NonNull View itemView, MyAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        avatar = itemView.findViewById(R.id.avatar);
        name = itemView.findViewById(R.id.name);
        descript = itemView.findViewById(R.id.descript);
        item = itemView.findViewById(R.id.item);
//        selected = false;
//        item.setOnClickListener(v->{
//            if(!selected) {
//                item.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.layout_pressed));
//            } else {
//                item.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.layout_unpressed));
//            }
//            selected = !selected;
//        });


    }
}
