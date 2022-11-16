package com.example.giftme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    static Context context;
    Activity activity;
    private List<Item> myItems;

    public ItemsAdapter(Activity activity, Context context, List<Item> items){
        this.context = context;
        this.activity = activity;
        this.myItems = items;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        //populate
        int index = holder.getAdapterPosition();
        Item item = myItems.get(index);
        //set views
        TextView itemName = holder.itemNameTV;
        itemName.setText(item.getName());

        RatingBar ratingBar = holder.ratingBar;
        ratingBar.setRating(item.getHearts());

        ImageButton imgButton = holder.editButton;

        holder.currentItem= myItems.get(index);

        holder.linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this.activity, EditItemActivity.class);
            //put in name, price description, hearts, and link etc
            this.activity.startActivity(intent);
        });

    }

//    public void update(ArrayList<Item> items){
//        myItems.clear();
//        myItems.addAll(items);
//    }
    @Override
    public int getItemCount() {
        return myItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemNameTV;
        public RatingBar ratingBar;
        public ImageButton editButton;
        public View view;
        public Item currentItem;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameTV = (TextView) itemView.findViewById(R.id.item_name);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_button);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
            linearLayout = itemView.findViewById(R.id.item_lv);

        }
    }
}
