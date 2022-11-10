package com.example.giftme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MyCollectionItems extends AppCompatActivity {

    TextView itemCount, itemName;
    ImageButton share;
    RecyclerView recyclerView;
    FloatingActionButton addNewItem;
    ArrayList<String> name, price, imagePath, date;
    ArrayList<Integer> favorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_items);

        itemName = findViewById(R.id.item_name);
        itemCount = findViewById(R.id.num_items);
        share = findViewById(R.id.share);
        recyclerView = findViewById(R.id.recycle_items);
        addNewItem = findViewById(R.id.add_new_item);

        name = new ArrayList<>();
        price = new ArrayList<>();
        imagePath = new ArrayList<>();
        date = new ArrayList<>();
        favorite = new ArrayList<>();

        if (getIntent().hasExtra("name")) {
            itemName.setText(getIntent().getStringExtra("name"));
        }

        name.add("Jordan Hydro XI Retro");
        price.add("$65");
        date.add("2022-11-6");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        MyCollectionItemsAdapter myCollectionItemsAdapter =
                new MyCollectionItemsAdapter(this, this, name, price, imagePath, favorite, date);
        recyclerView.setAdapter(myCollectionItemsAdapter);

        addNewItem.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddNewItemManually.class);
            startActivity(intent);
        });
    }
}