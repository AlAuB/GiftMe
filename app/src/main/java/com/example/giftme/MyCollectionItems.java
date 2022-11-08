package com.example.giftme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyCollectionItems extends AppCompatActivity {

    TextView itemCount, itemName;
    ImageButton share;
    RecyclerView recyclerView;
    FloatingActionButton addNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_items);

        itemName = findViewById(R.id.item_name);
        itemCount = findViewById(R.id.num_items);
        share = findViewById(R.id.share);
        recyclerView = findViewById(R.id.recycle_items);
        addNewItem = findViewById(R.id.add_new_item);

        if (getIntent().hasExtra("name")) {
            itemName.setText(getIntent().getStringExtra("name"));
        }
    }
}