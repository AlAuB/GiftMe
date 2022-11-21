package com.example.giftme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MyCollectionItemsAdapter extends RecyclerView.Adapter<MyCollectionItemsAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<Item> items;

    public MyCollectionItemsAdapter(Activity activity, Context context, ArrayList<Item> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_collection_items_row, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int index = holder.getAdapterPosition();
        Item item = items.get(index);
        holder.name.setText(item.getName());
        holder.price.setText("$" + item.getPrice());
        holder.date.setText(item.getDate());
        File file = new File(item.getImg());
        Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
        holder.imageView.setImageBitmap(getBitMap);
        holder.ratingBar.setRating(item.getHearts());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RatingBar ratingBar;
        TextView name, price, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detail_view_item_image);
            name = itemView.findViewById(R.id.detail_view_item_name);
            price = itemView.findViewById(R.id.detail_view_item_price);
            date = itemView.findViewById(R.id.detail_view_item_date);
            ratingBar = itemView.findViewById(R.id.detail_view_item_rating);
        }
    }
}
