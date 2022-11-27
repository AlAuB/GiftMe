package com.example.giftme.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;

import java.io.File;
import java.util.ArrayList;

public class MyCollectionItemsAdapter extends RecyclerView.Adapter<MyCollectionItemsAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<Item> items;
    TextView collectionNameTV;
    DataBaseHelper dataBaseHelper;

    public MyCollectionItemsAdapter(Activity activity, Context context, ArrayList<Item> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        collectionNameTV = ((Activity) context).findViewById(R.id.collection_name);
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
        int index = holder.getAdapterPosition();
        Item item = items.get(index);
        holder.name.setText(item.getName());
        holder.price.setText("$" + item.getPrice());
        holder.date.setText(item.getDate());
        File file = new File(item.getImg());
        Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
        holder.imageView.setImageBitmap(getBitMap);
        holder.ratingBar.setRating(item.getHearts());

        String collectionName = (String) collectionNameTV.getText();
        holder.linearLayout.setOnLongClickListener(view -> {
            int index2 = holder.getAdapterPosition();
            confirmDialogForDeleteItem(index2, collectionName);
            return true;
        });
    }

    private void confirmDialogForDeleteItem(int position, String collectionName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + items.get(position).getName() + " ?");
        builder.setMessage("Items details will also be deleted!");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            dataBaseHelper.deleteItemInCollection(String.valueOf(items.get(position).getId()),collectionName);
            items.clear();
            getAllItems();
            notifyItemRemoved(position);
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }
    private void getAllItems() {
        Cursor cursor = dataBaseHelper.selectAll(collectionNameTV.getText().toString());
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Item currentItem
                        = new Item(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                items.add(currentItem);
            }
            cursor.close();
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RatingBar ratingBar;
        TextView name, price, date;
        public LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detail_view_item_image);
            name = itemView.findViewById(R.id.detail_view_item_name);
            price = itemView.findViewById(R.id.detail_view_item_price);
            date = itemView.findViewById(R.id.detail_view_item_date);
            ratingBar = itemView.findViewById(R.id.detail_view_item_rating);
            linearLayout = itemView.findViewById(R.id.items_row);
        }
    }
}
