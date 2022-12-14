package com.example.giftme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.DetailedItemViewActivity;
import com.example.giftme.Helpers.Item;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    Context context;
    Activity activity;
    TextView collectionNameTV;
    List<Item> myItems;

    public ItemsAdapter(Activity activity, Context context, List<Item> items) {
        this.context = context;
        this.activity = activity;
        this.myItems = items;
        collectionNameTV = ((Activity) context).findViewById(R.id.collection_name);
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        //populate
        int index = holder.getBindingAdapterPosition();
        Item item = myItems.get(index);
        //set views
        holder.itemNameTV.setText(item.getName());
        holder.ratingBar.setRating(item.getHearts());
        holder.time.setText(item.getDate());
        holder.currentItem = myItems.get(index);
        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(this.activity, DetailedItemViewActivity.class);
            //put in name, price description, hearts, and link etc
            intent.putExtra("itemID", item.getId());
            intent.putExtra("itemName", item.getName());
            intent.putExtra("itemHearts", item.getHearts());
            intent.putExtra("itemPrice", item.getPrice());
            intent.putExtra("itemDes", item.getDescription());
            intent.putExtra("itemDate", item.getDate());
            intent.putExtra("itemLink", item.getWebsite());
            intent.putExtra("itemDate", item.getDate());
            intent.putExtra("itemFsID", item.getFireStoreID());
            Log.d("itemsAdapter", "put in intent" + item.getFireStoreID());
            Log.d("itemDes", item.getDescription());

            String imgUrl = item.getImg();
            //get image ------------------------------------------------------------
            if( imgUrl == null || imgUrl.toLowerCase().equals(null)) {
                Log.d("CATCH_EXCEPTION", "IMG: " + item.getImg());
            }
            else{
                if(imgUrl.contains("/") ){
                    //
                    intent.putExtra("itemImg", imgUrl);
                }
                else{
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
            intent.putExtra("collectionName", collectionNameTV.getText().toString());
            this.activity.startActivity(intent);
            this.activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return myItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTV, time;
        RatingBar ratingBar;
        Item currentItem;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTV = itemView.findViewById(R.id.item_name);
            time = itemView.findViewById(R.id.item_card_time);
            ratingBar = itemView.findViewById(R.id.rating);
            cardView = itemView.findViewById(R.id.item_lv);
        }
    }
}
