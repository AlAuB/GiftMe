package com.example.giftme.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
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

import com.example.giftme.Activities.ClaimFriendItemActivity;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
        Log.d("ITEMS_ALL", items.toString());
        holder.name.setText(item.getName());
        holder.price.setText("$" + item.getPrice());
        holder.date.setText(item.getDate());
        Log.d("detailviewadapter", item.toString());
        Log.d("ITEM_IMAGE", "IMG: " + item.getImg());
        String imgUrl= item.getImg();
        Log.d("ITEM_URL", "IMG: " +  imgUrl);
//        if(!imgUrl.equals("null")) || item.getImg() != null){
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
                Log.d("insideIf", "URI: " + imgUri[0]);
                Picasso.get().load(imgUri[0]).into(holder.imageView);

            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.d("Friend_DEBUG", "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
            });
        }
        holder.ratingBar.setRating(item.getHearts());
        ImageView claimedImgView = holder.claimedPFP;
        if(item.getClaimed() == true){
            claimedImgView.setVisibility(View.VISIBLE);
        }
        else{
            claimedImgView.setVisibility(View.GONE);
        }

        String collectionName = (String) collectionNameTV.getText();
        //ON CLICK--------------------------------------------------------------------
        holder.currentItem = items.get(index);
        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(this.activity, ClaimFriendItemActivity.class);
            //put in name, price description, hearts, and link etc
            intent.putExtra("itemID", item.getId());
            intent.putExtra("itemName", item.getName());
            intent.putExtra("itemHearts", item.getHearts());
            intent.putExtra("itemPrice", item.getPrice());
            if(item.getDescription() != null){
                intent.putExtra("itemDes", item.getDescription());
            }
            else{
                String noDescription = "Your friend has not set a description.";
                intent.putExtra("itemDes", noDescription);
            }

            //get image ------------------------------------------------------------
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
                    intent.putExtra("itemURL", item.getWebsite());
                    intent.putExtra("itemDate", item.getDate());
                    intent.putExtra("collectionID", collectionID);
                    intent.putExtra("collectionName", collectionName);

                    //friend firestore id: email
                    intent.putExtra("friendID", friendID);
                    intent.putExtra("itemFsID", item.getFireStoreID());

                    this.activity.startActivity(intent);
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.d("Friend_DEBUG", "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
                });
            }
            //get img end --------------------------------------------------------------------
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, claimedPFP;
        RatingBar ratingBar;
        TextView name, price, date;
        public Item currentItem;
        public CardView cardView;
//        public LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.friend_detail_view_item_image);
            name = itemView.findViewById(R.id.friend_detail_view_item_name);
            price = itemView.findViewById(R.id.friend_detail_view_item_price);
            date = itemView.findViewById(R.id.friend_detail_view_item_date);
            ratingBar = itemView.findViewById(R.id.friend_detail_view_item_rating);
//            linearLayout = itemView.findViewById(R.id.friend_items_row);
            cardView = itemView.findViewById(R.id.friend_detail_cardView);
            claimedPFP = itemView.findViewById(R.id.friend_claimed_pfp);
        }
    }
}
