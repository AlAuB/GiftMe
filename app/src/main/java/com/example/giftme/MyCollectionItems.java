package com.example.giftme;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyCollectionItems extends AppCompatActivity {

    TextView itemCountTV, collectionNameTV;
    ImageButton shareImgButton;
    RecyclerView recyclerView;
    FloatingActionButton addNewItemButton;
    ArrayList<Item> items;
    DataBaseHelper dataBaseHelper;
    Context context;
    String collection_name;
    ItemsAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_items);

        collectionNameTV = findViewById(R.id.collection_name);
        itemCountTV = findViewById(R.id.num_items);
        shareImgButton = findViewById(R.id.share);
        recyclerView = findViewById(R.id.recycle_items);

        addNewItemButton = findViewById(R.id.add_new_item);
        addNewItemButton.setOnClickListener(view -> confirmDialog());
        context = this;

        dataBaseHelper = new DataBaseHelper(this);
        items = new ArrayList<>();

        if (getIntent().hasExtra("collection_name")) {
            collection_name = getIntent().getStringExtra("collection_name");
            collectionNameTV.setText(collection_name);
            items = dataBaseHelper.selectAll(collection_name);
            itemCountTV.setText(String.valueOf(items.size()));
        }

        setItemAdapter(items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCollectionItems.this));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("collection_name", collectionNameTV.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        collectionNameTV.setText(savedInstanceState.getString("collection_name"));
    }

    public void setItemAdapter(ArrayList<Item> items) {
        itemAdapter = new ItemsAdapter(MyCollectionItems.this, this, items);
        recyclerView.setAdapter(itemAdapter);
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");
        View view = getLayoutInflater().inflate(R.layout.add_item_alert_dialog, null);
        EditText input = view.findViewById(R.id.input_item);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String itemName = input.getText().toString().trim();
            Item item = new Item();
            item.setName(itemName);

            //if items.getNames contains item.getName
            //maybe not an issue? what if I want two socks for my birthday
            if (items.contains(item)) {
                Toast.makeText(context, "Duplicate item name", Toast.LENGTH_LONG).show();
            } else {
                //Add item to Collection
                dataBaseHelper.insertItemIntoCollection(collection_name, item);
                Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();

                //Notify insertion change to RecycleView Adapter
                items.clear();
                items = dataBaseHelper.selectAll(collection_name);
                setItemAdapter(items);
                itemAdapter.notifyItemInserted(items.size() - 1);
                Log.d("items", items.toString());
                //Update collection count
                itemCountTV.setText(String.valueOf(itemAdapter.getItemCount()));
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }
}