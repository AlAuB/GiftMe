package com.example.giftme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.R;

import java.util.ArrayList;

public class FrWishlistCollectionRecycleAdapter extends RecyclerView.Adapter<FrWishlistCollectionRecycleAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> collections;
    ArrayList<String> ids;

    public FrWishlistCollectionRecycleAdapter(Context context, ArrayList<String> ids, ArrayList<String> collections) {
        this.context = context;
        this.ids = ids;
        this.collections = collections;
    }

    @NonNull
    @Override
    public FrWishlistCollectionRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_collection_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FrWishlistCollectionRecycleAdapter.MyViewHolder holder, int position) {
        String name = collections.get(position);
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.collection_name_in_row);
        }
    }
}
