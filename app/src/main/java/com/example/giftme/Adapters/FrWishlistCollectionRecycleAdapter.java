package com.example.giftme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.FriendCollectionItems;
import com.example.giftme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FrWishlistCollectionRecycleAdapter extends RecyclerView.Adapter<FrWishlistCollectionRecycleAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<String> collectionNames;
    ArrayList<String> collectionIDs;
    ArrayList<String> ids;
    ArrayList<String> friendIds;
    ArrayList<String> friendNames;
    ArrayList<String> friendProfileImages;

    public FrWishlistCollectionRecycleAdapter(Activity activity, Context context,
                                              ArrayList<String> ids, ArrayList<String> collectionNames,
                                              ArrayList<String> collectionIDs,
                                              ArrayList<String> emails, ArrayList<String> names,
                                              ArrayList<String> imgs) {
        this.activity = activity;
        this.context = context;
        this.ids = ids;
        this.collectionNames = collectionNames;
        this.collectionIDs = collectionIDs;
        this.friendIds = emails;
        this.friendNames = names;
        this.friendProfileImages = imgs;
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
        String wishlistName = collectionNames.get(index);
        String wishlistID = collectionIDs.get(index);
        String friendID = friendIds.get(index);
        String friendName = friendNames.get(index);
        String friendPFP = friendProfileImages.get(index);

        String title = friendName + "'s " + wishlistName + " Wishlist";
        holder.wlTitleTV.setText(title);
//        holder.imgView.setImageURI(Uri.parse(friendPFP));

        Picasso.get().load(friendPFP)
                .transform(new CropCircleTransformation())
                .into(holder.imgView);

        holder.linearLayout.setOnClickListener(view -> {
            int index1 = holder.getAdapterPosition();
            Intent intent = new Intent(context, FriendCollectionItems.class);
            intent.putExtra("collection_name", wishlistName);
            intent.putExtra("friend_name", friendName);
            intent.putExtra("friend_id", friendID);
            intent.putExtra("collection_id", wishlistID);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return collectionNames.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView wlTitleTV;
        LinearLayout linearLayout;
        ImageView imgView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wlTitleTV = itemView.findViewById(R.id.friend_wishlist_title);
            linearLayout = itemView.findViewById(R.id.friend_card);
            imgView = itemView.findViewById((R.id.friend_user_avatar));
        }
    }
}
