package com.banledcamung.bicat;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    int tabIndex;
    private int selectedItem = RecyclerView.NO_POSITION;
    LayoutInflater inflater;
    public Adapter( List<String> titles, List<Integer> images, int tabIndex){
        this.titles = titles;
        this.images = images;
        this.tabIndex = tabIndex;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false), this);
    }

    @NonNull
   // @Override
  //  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.custom_item_layout,parent,false);
//        return new ViewHolder(view);
  //      return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false), this);
  //  }

//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.title.setText("String");
//        holder.gridIcon.setImageResource(R.drawable.s1_img);
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));

        if(position == selectedItem){
            holder.buttonPanel.setVisibility(View.VISIBLE);
        } else {
            holder.buttonPanel.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(v->{
            int previousSelectedItem = selectedItem;
            selectedItem = position;
            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItem);
        });

        holder.okBtn.setOnClickListener(v->{
            Context context = holder.itemView.getContext();
            if(context instanceof  MainActivity){
                try {
                    ((MainActivity) context).selectItem(position, tabIndex);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView gridIcon;
        RelativeLayout item;
        Adapter adapter;
        RelativeLayout buttonPanel;
        Button okBtn;
        public ViewHolder(@NonNull View itemView, Adapter adapter) {
            super(itemView);
            this.adapter = adapter;
            title=itemView.findViewById(R.id.name_);
            gridIcon = itemView.findViewById(R.id.avatar_);
            item = itemView.findViewById(R.id.item_);
            buttonPanel = itemView.findViewById(R.id.buttonPanel);
            okBtn = itemView.findViewById(R.id.ok_btn);
            item.setOnClickListener(v->{
                buttonPanel.setVisibility(View.VISIBLE);
                buttonPanel.bringToFront();

//                Context context = itemView.getContext();
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
//                View popupView = inflater.inflate(R.layout.popup_select,null);
//                PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT , true);
//                popupWindow.showAtLocation(item, Gravity.BOTTOM,0,0);
//                if(context instanceof  MainActivity){
//                    ((MainActivity) context).showSelectPopup(item);
//                }

            });

        }
    }
}
