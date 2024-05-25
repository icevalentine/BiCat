package com.banledcamung.bicat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


    List<Item> items;
    int selectedItem = RecyclerView.NO_POSITION;

    public MyAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.descript.setText(items.get(position).getDescription());
        holder.remainTime.setText(items.get(position).getRemainTime());
        holder.totalTime.setText(items.get(position).getTotalTime());
        holder.avatar.setImageResource(items.get(position).getImage());
        holder.itemView.setActivated(position == selectedItem);
        holder.pos = items.get(position).getPos();
    }

    public void setSelectedItem(int position){
        selectedItem = position;
        notifyDataSetChanged();
    }

    public void addItem(Item newItem){
        for (Item item : items) {
            if (newItem.getPos() < item.getPos()) {
                int insertPositon = items.indexOf(item);
                items.add(insertPositon, newItem);
                notifyItemInserted(insertPositon);
                return;
            }
        }
        items.add(newItem);
        notifyItemInserted(items.size()-1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void removeItem(int pos) {
        for(Item item : items){
            if(item.getPos() == pos){
                int p = items.indexOf(item);
                items.remove(p);
                notifyItemRemoved(p);
                return;
            }
        }
    }
}
