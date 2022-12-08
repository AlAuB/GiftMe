package com.example.giftme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.MyCollectionItems;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MyWishlistCollectionRecycleAdapter extends RecyclerView.Adapter<MyWishlistCollectionRecycleAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<String> collections;
    ArrayList<String> ids;
    DataBaseHelper dataBaseHelper;

    public MyWishlistCollectionRecycleAdapter(Activity activity, Context context, ArrayList<String> ids, ArrayList<String> collections) {
        this.activity = activity;
        this.context = context;
        this.ids = ids;
        this.collections = collections;
        dataBaseHelper = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public MyWishlistCollectionRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_collection_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWishlistCollectionRecycleAdapter.MyViewHolder holder, int position) {
        int index = holder.getBindingAdapterPosition();
        String name = collections.get(index);
        holder.textView.setText(name);
        holder.cardView.setOnClickListener(view -> {
            int index1 = holder.getBindingAdapterPosition();
            Intent intent = new Intent(context, MyCollectionItems.class);
            intent.putExtra("collection_name", collections.get(index1));
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        MaterialCardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.collection_name_in_row);
            cardView = itemView.findViewById(R.id.row);
        }
    }
}
