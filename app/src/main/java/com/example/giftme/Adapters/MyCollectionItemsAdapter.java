package com.example.giftme.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.DetailedItemViewActivity;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MyCollectionItemsAdapter extends RecyclerView.Adapter<MyCollectionItemsAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<Item> items;
    DataBaseHelper dataBaseHelper;
    String collectionName;

    public MyCollectionItemsAdapter(Activity activity, Context context, ArrayList<Item> items, String collectionName) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        this.collectionName = collectionName;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_collection_items_row, parent, false);
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

        String imgUrl = item.getImg();
        if( imgUrl == null || imgUrl.toLowerCase().equals(null)) {
            Log.d("CATCH_EXCEPTION", "IMG: " + item.getImg());
        } else{
            if(imgUrl.contains("/")) {
                //use bitmap
                File file = new File(imgUrl);
                Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.imageView.setImageBitmap(getBitMap);
            } else{
                //use the image stored in firestore storage
                String[] imgUri = new String[1];
                String path = "images/" + SessionManager.getUserEmail(context) + "/" + imgUrl;
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
        }

        holder.ratingBar.setRating(item.getHearts());
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DetailedItemViewActivity.class);
            //put in name, price description, hearts, and link etc
            intent.putExtra("itemID", item.getId());
            intent.putExtra("itemName", item.getName());
            intent.putExtra("itemHearts", item.getHearts());
            intent.putExtra("itemPrice", item.getPrice());
            intent.putExtra("itemDes", item.getDescription());
            intent.putExtra("itemURL", item.getWebsite());
            intent.putExtra("itemDate", item.getDate());
            intent.putExtra("itemFsID", item.getFireStoreID());
            intent.putExtra("collectionName", collectionName);
            //get image ------------------------------------------------------------
            if( imgUrl == null || imgUrl.toLowerCase().equals(null)) {
                Log.d("CATCH_EXCEPTION", "IMG: " + item.getImg());
            }
            else{
                if(imgUrl.contains("/") ){
                    intent.putExtra("itemImg", imgUrl);
                } else{
                    String[] imgUri = new String[1];
                    String path = "images/" + SessionManager.getUserEmail(context) + "/" + imgUrl;
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference mountainsRef = storageRef.child(path);
                    mountainsRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Got the download URL for 'users/me/profile.png'
                        imgUri[0] = uri.toString();
                        intent.putExtra("itemImg", imgUri[0]);
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                        Log.d("DEBUG", "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
                    });
                }
            }
            //get img end --------------------------------------------------------------------
            this.activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RatingBar ratingBar;
        TextView name, price, date;
        MaterialCardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detail_view_item_image);
            name = itemView.findViewById(R.id.detail_view_item_name);
            price = itemView.findViewById(R.id.detail_view_item_price);
            date = itemView.findViewById(R.id.detail_view_item_date);
            ratingBar = itemView.findViewById(R.id.detail_view_item_rating);
            cardView = itemView.findViewById(R.id.items_row);
        }
    }
}
