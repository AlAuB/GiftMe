package com.example.giftme;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyCollectionItemsAdapter extends RecyclerView.Adapter<MyCollectionItemsAdapter.MyViewHolder>{

    Activity activity;
    Context context;
    ArrayList<String> name, price, imagePath, date;
    ArrayList<Integer> favorite;

    public MyCollectionItemsAdapter(Activity activity, Context context, ArrayList<String> name, ArrayList<String> price,
                                    ArrayList<String> imagePath, ArrayList<Integer> favorite, ArrayList<String> date) {
        this.activity = activity;
        this.context = context;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.favorite = favorite;
        this.date = date;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_collection_items_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.date.setText(date.get(0));
        holder.name.setText(name.get(0));
        holder.price.setText(price.get(0));
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, price, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
            date = itemView.findViewById(R.id.item_date);
        }
    }
}
