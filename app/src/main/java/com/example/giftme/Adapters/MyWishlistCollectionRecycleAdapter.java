package com.example.giftme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Activities.MyCollectionItems;
import com.example.giftme.R;

import java.util.ArrayList;

public class MyWishlistCollectionRecycleAdapter extends RecyclerView.Adapter<MyWishlistCollectionRecycleAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<String> collections;
    ArrayList<String> ids;
    DataBaseHelper dataBaseHelper;

    public MyWishlistCollectionRecycleAdapter(Activity activity, Context context, ArrayList<String> ids, ArrayList<String> collections) {
        this.activity = activity;
        this.context = context;
        this.ids = ids;
        this.collections = collections;
        dataBaseHelper = new DataBaseHelper(context);
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
        int index = holder.getAdapterPosition();
        String name = collections.get(index);
        holder.textView.setText(name);
        holder.linearLayout.setOnClickListener(view -> {
            int index1 = holder.getAdapterPosition();
            Intent intent = new Intent(context, MyCollectionItems.class);
            intent.putExtra("collection_name", collections.get(index1));
            activity.startActivity(intent);
        });
        holder.linearLayout.setOnLongClickListener(view -> {
            int index2 = holder.getAdapterPosition();
            confirmDialogForDeleteCollection(index2);
            return true;
        });
    }

    private void confirmDialogForDeleteCollection(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + collections.get(position) + " ?");
        builder.setMessage("Items in this collection will also be deleted!");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            dataBaseHelper.deleteData(ids.get(position), "COLLECTIONS");
            dataBaseHelper.deleteTable(collections.get(position));
            TextView textView = activity.findViewById(R.id.collectionCount);
            ids.clear();
            collections.clear();
            getAllCollection();
            textView.setText(String.valueOf(getItemCount()));
            notifyItemRemoved(position);
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }

    private void getAllCollection() {
        Cursor cursor = dataBaseHelper.readCollectionTableAllData("COLLECTIONS");
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //if friendID == null
                if (cursor.getString(2) == null){
                    ids.add(cursor.getString(0));
                    collections.add(cursor.getString(1));
                }
            }
        }
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
