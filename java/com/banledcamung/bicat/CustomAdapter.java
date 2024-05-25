package com.banledcamung.bicat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    List<String> titles;
    List<Bitmap> images;
    List<Integer> slots;
    int tabIndex;
    private int selectedItem = RecyclerView.NO_POSITION;
    LayoutInflater inflater;
    public CustomAdapter(List<String> titles, List<Bitmap> images, List<Integer> slots){
        this.titles = titles;
        this.images = images;
        this.slots = slots;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_layout,parent,false), this);
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
        holder.gridIcon.setImageBitmap(images.get(position));

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
                    ((MainActivity) context).selectCustomItem(slots.get(position));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        holder.deleteBtn.setOnClickListener(v->{
            int slot = slots.get(position);
            removeItem(position);
            SharedPreferences imgSP = holder.itemView.getContext().getSharedPreferences("imgSP",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = imgSP.edit();
            String isDeleteKey = "isDelete" + slot;
            String keyImg = "imgByteArray" + slot;
            String keyName = "keyName"+slot;
            editor.remove(keyImg);
            editor.remove(keyName);
            editor.putBoolean(isDeleteKey,true);
            editor.apply();
            Context context = holder.itemView.getContext();
            if(context instanceof  MainActivity){
                try {
                    ((MainActivity) context).moveToListFragment();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void removeItem(int position) {
        titles.remove(position);
        images.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView gridIcon;
        RelativeLayout item;
        CustomAdapter adapter;
        RelativeLayout buttonPanel;
        Button okBtn, deleteBtn;
        public ViewHolder(@NonNull View itemView, CustomAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            title=itemView.findViewById(R.id.custom_name_);
            gridIcon = itemView.findViewById(R.id.custom_avatar_);
            item = itemView.findViewById(R.id.custom_item_);
            buttonPanel = itemView.findViewById(R.id.custom_buttonPanel);
            okBtn = itemView.findViewById(R.id.custom_ok_btn);
            deleteBtn = itemView.findViewById(R.id.custom_deleteBtn);
           //resetItemClick();
//            item.setOnClickListener(v->{
//                buttonPanel.setVisibility(View.VISIBLE);
//                buttonPanel.bringToFront();

//                Context context = itemView.getContext();
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
//                View popupView = inflater.inflate(R.layout.popup_select,null);
//                PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT , true);
//                popupWindow.showAtLocation(item, Gravity.BOTTOM,0,0);
//                if(context instanceof  MainActivity){
//                    ((MainActivity) context).showSelectPopup(item);
//                }

//            });

        }

        private void resetItemClick() {
            item.setOnClickListener(v-> {
                buttonPanel.setVisibility(View.VISIBLE);
                buttonPanel.bringToFront();
            });
        }
    }
}
