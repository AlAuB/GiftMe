package com.example.giftme.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;

import java.io.File;
import java.util.ArrayList;

public class FriendItemsDetailViewAdapter extends RecyclerView.Adapter<FriendItemsDetailViewAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<Item> items;
    TextView collectionNameTV;
    String friendID;
    String collectionID;
    DataBaseHelper dataBaseHelper;

    public FriendItemsDetailViewAdapter(Activity activity, Context context, ArrayList<Item> items, String friendID, String collectionID) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        this.friendID = friendID;
        this.collectionID = collectionID;
        collectionNameTV = ((Activity) context).findViewById(R.id.collection_name);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_items_detail_row, parent, false);
        dataBaseHelper = new DataBaseHelper(context);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int index = holder.getBindingAdapterPosition();
        Item item = items.get(index);
        holder.name.setText(item.getName());
        holder.price.setText("$" + item.getPrice());
        holder.date.setText(item.getDate());
        if(item.getImg()!=null){
            File file = new File(item.getImg());
            Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
            holder.imageView.setImageBitmap(getBitMap);
        }
        holder.ratingBar.setRating(item.getHearts());
        if(item.getClaimed() == true){
            holder.cardView.setBackgroundColor(ContextCompat.getColor(this.context, R.color.pink));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RatingBar ratingBar;
        TextView name, price, date;
        public CardView cardView;
        public LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.friend_detail_view_item_image);
            name = itemView.findViewById(R.id.friend_detail_view_item_name);
            price = itemView.findViewById(R.id.friend_detail_view_item_price);
            date = itemView.findViewById(R.id.friend_detail_view_item_date);
            ratingBar = itemView.findViewById(R.id.friend_detail_view_item_rating);
            linearLayout = itemView.findViewById(R.id.friend_items_row);
            cardView = itemView.findViewById(R.id.friend_detail_cardView);
        }
    }
}
