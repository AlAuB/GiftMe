package com.example.giftme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MyCollectionItems extends AppCompatActivity {

    TextView itemCount, collectionName;
    ImageButton share;
    RecyclerView recyclerView;
    FloatingActionButton addNewItem;

    ArrayList<String> items;
    ArrayList<String> ids;
    DataBaseHelper dataBaseHelper;
    Context context;

    String collection_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_items);

        collectionName = findViewById(R.id.collection_name);
        itemCount = findViewById(R.id.num_items);
        share = findViewById(R.id.share);
        recyclerView = findViewById(R.id.recycle_items);

        addNewItem = findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(view -> confirmDialog());
        context = this;

        items = new ArrayList<>();
        //retrieve all items in collection
        //items = getAllItems();
        dataBaseHelper = new DataBaseHelper(this);

        if (getIntent().hasExtra("collection_name")) {
            collection_name = getIntent().getStringExtra("collection_name");
            collectionName.setText(collection_name);
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");
        View view = getLayoutInflater().inflate(R.layout.add_item_alert_dialog, null);
        EditText input = view.findViewById(R.id.input_item);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String insert = input.getText().toString().trim();
            if (items.contains(insert)) {
                Toast.makeText(context, "Duplicate item name", Toast.LENGTH_LONG).show();
            } else {
                //Add item to Collection
                dataBaseHelper.insertItemIntoCollection(collection_name, insert);
                Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
//                //Notify insertion change to RecycleView Adapter
//                ids.clear();
//                items.clear();
//                getAllCollection();
//                myWishlistCollectionRecycleAdapter.notifyItemInserted(collections.size() - 1);
//                //Update collection count
//                collectionCount.setText(String.valueOf(myWishlistCollectionRecycleAdapter.getItemCount()));
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }

}