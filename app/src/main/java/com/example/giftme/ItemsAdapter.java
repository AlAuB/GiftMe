package com.example.giftme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        int index = holder.getAdapterPosition();
        Item item = myItems.get(index);
        //set views
        TextView itemName = holder.itemNameTV;
        itemName.setText(item.getName());
        RatingBar ratingBar = holder.ratingBar;
        ratingBar.setRating(item.getHearts());
        String collectionName = (String) collectionNameTV.getText();
        holder.currentItem = myItems.get(index);
        holder.linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this.activity, DetailedItemViewActivity.class);
            //put in name, price description, hearts, and link etc
            intent.putExtra("itemID", item.getId());
            intent.putExtra("itemName", item.getName());
            intent.putExtra("itemHearts", item.getHearts());
            intent.putExtra("itemPrice", item.getPrice());
            intent.putExtra("itemDes", item.getDescription());
            Log.d("itemDes", item.getDescription());
            intent.putExtra("itemImg", item.getImg());
            //firestore ID?
            intent.putExtra("collectionName", collectionName);
            this.activity.finish();
            this.activity.startActivity(intent);
        });
        holder.linearLayout.setOnLongClickListener(view -> {
            int index2 = holder.getAdapterPosition();
            confirmDialogForDeleteItem(index2, collectionName);
            return true;
        });
    }
    private void confirmDialogForDeleteItem(int position, String collectionName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + myItems.get(position).getName() + " ?");
        builder.setMessage("Items details will also be deleted!");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
            dataBaseHelper.deleteItemInCollection(String.valueOf(myItems.get(position).getId()),collectionName);
            //TextView textView = activity.findViewById(R.id.collectionCount);
            myItems.clear();
            //textView.setText(String.valueOf(getItemCount()));
            notifyItemRemoved(position);




        });
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }


    @Override
    public int getItemCount() {
        return myItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemNameTV;
        public RatingBar ratingBar;
        public View view;
        public Item currentItem;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTV = itemView.findViewById(R.id.item_name);
            ratingBar = itemView.findViewById(R.id.rating);
            linearLayout = itemView.findViewById(R.id.item_lv);
        }
    }
}
