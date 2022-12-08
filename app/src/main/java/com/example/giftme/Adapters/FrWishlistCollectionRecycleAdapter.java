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

import com.example.giftme.Activities.FriendCollectionItems;
import com.example.giftme.Activities.MyCollectionItems;
import com.example.giftme.R;

import java.util.ArrayList;

public class FrWishlistCollectionRecycleAdapter extends RecyclerView.Adapter<FrWishlistCollectionRecycleAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<String> collections;
    ArrayList<String> ids;
    ArrayList<String> friendIds;
    ArrayList<String> friendNames;

    public FrWishlistCollectionRecycleAdapter(Activity activity, Context context, ArrayList<String> ids, ArrayList<String> collections, ArrayList<String> emails, ArrayList<String> names) {
        this.activity = activity;
        this.context = context;
        this.ids = ids;
        this.collections = collections;
        this.friendIds = emails;
        this.friendNames = names;
    }

    @NonNull
    @Override
    public FrWishlistCollectionRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fr_collection_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FrWishlistCollectionRecycleAdapter.MyViewHolder holder, int position) {
        //populate
        int index = holder.getAdapterPosition();
        String wishlistName = collections.get(index);
        String friendID = friendIds.get(index);
        String friendName = friendNames.get(index);

        String title = friendName + "'s " + wishlistName + " Wishlist";
        holder.wlTitleTV.setText(title);

        holder.linearLayout.setOnClickListener(view -> {
            int index1 = holder.getAdapterPosition();
            Intent intent = new Intent(context, FriendCollectionItems.class);
            intent.putExtra("collection_name", wishlistName);
            intent.putExtra("friend_name", friendName);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView wlTitleTV;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wlTitleTV = itemView.findViewById(R.id.friend_wishlist_title);
            linearLayout = itemView.findViewById(R.id.friend_card);
        }
    }
}
