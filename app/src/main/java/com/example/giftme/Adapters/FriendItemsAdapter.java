package com.example.giftme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.ClaimFriendItemActivity;
import com.example.giftme.Activities.DetailedItemViewActivity;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendItemsAdapter extends RecyclerView.Adapter<FriendItemsAdapter.ViewHolder> {

    Context context;
    Activity activity;
    TextView collectionNameTV;
    List<Item> friendItems;
    String friendID;
    String collectionID;

    public FriendItemsAdapter(Activity activity, Context context, List<Item> items, String friendID, String collectionID) {
        this.context = context;
        this.activity = activity;
        this.friendItems = items;
        this.friendID = friendID;
        this.collectionID = collectionID;
        collectionNameTV = ((Activity) context).findViewById(R.id.collection_name);
    }

    @NonNull
    @Override
    public FriendItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.friend_item_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendItemsAdapter.ViewHolder holder, int position) {
        //populate
        int index = holder.getBindingAdapterPosition();
        Item item = friendItems.get(index);
        //set views
        TextView itemName = holder.itemNameTV;
        itemName.setText(item.getName());
        RatingBar ratingBar = holder.ratingBar;
        ratingBar.setRating(item.getHearts());
        ImageView claimedImgView = holder.claimedPFP;
        if(item.getClaimed() == true){
            claimedImgView.setVisibility(View.VISIBLE);
        }
        else{
            claimedImgView.setVisibility(View.GONE);
        }

        String collectionName = (String) collectionNameTV.getText();
        holder.currentItem = friendItems.get(index);
        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(this.activity, ClaimFriendItemActivity.class);
            //put info into intent
            intent.putExtra("itemID", item.getId());
            intent.putExtra("itemName", item.getName());
            intent.putExtra("itemHearts", item.getHearts());
            intent.putExtra("itemPrice", item.getPrice());
            intent.putExtra("itemURL", item.getWebsite());
            intent.putExtra("itemDate", item.getDate());
            intent.putExtra("collectionID", collectionID);
            intent.putExtra("collectionName", collectionName);

            //friend firestore id: email
            intent.putExtra("friendID", friendID);
            intent.putExtra("itemFSID", item.getFireStoreID());

            if(item.getDescription() != null){
                intent.putExtra("itemDes", item.getDescription());
            }
            else{
                String noDescription = "Your friend has not set a description.";
                intent.putExtra("itemDes", noDescription);
            }
            //get image ------------------------------------------------------------
            String imgUrl= item.getImg();
            if( imgUrl == null || imgUrl.toLowerCase().equals(null)) {
                Log.d("CATCH_EXCEPTION", "IMG: " + item.getImg());
            }
            else{
                String[] imgUri = new String[1];
                String path = "images/" + friendID + "/" + imgUrl;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference mountainsRef = storageRef.child(path);
                mountainsRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Got the download URL for 'users/me/profile.png'
                    imgUri[0] = uri.toString();
                    intent.putExtra("itemImg", imgUri[0]);

                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.d("Friend_DEBUG", "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
                });
            }
            //get img end --------------------------------------------------------------------
            this.activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return friendItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNameTV;
        public RatingBar ratingBar;
        public ImageView claimedPFP;
        public View view;
        public Item currentItem;
        public MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTV = itemView.findViewById(R.id.friend_item_name);
            ratingBar = itemView.findViewById(R.id.friend_rating);
            claimedPFP = itemView.findViewById(R.id.claimed_pfp);
            cardView = itemView.findViewById(R.id.friend_item_lv);
        }
    }
}
