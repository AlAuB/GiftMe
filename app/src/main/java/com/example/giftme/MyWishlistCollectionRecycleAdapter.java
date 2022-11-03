package com.example.giftme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyWishlistCollectionRecycleAdapter extends RecyclerView.Adapter<MyWishlistCollectionRecycleAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<String> collections;
    ArrayList<String> ids;

    public MyWishlistCollectionRecycleAdapter(Activity activity, Context context, ArrayList<String> ids, ArrayList<String> collections) {
        this.activity = activity;
        this.context = context;
        this.ids = ids;
        this.collections = collections;
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
        String name = collections.get(position);
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.collection_name_in_row);
            linearLayout = itemView.findViewById(R.id.row);
        }
    }
}
