package com.example.giftme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        addNewItem.setOnClickListener(view -> confirmDialog());

        if (getIntent().hasExtra("name")) {
            itemName.setText(getIntent().getStringExtra("name"));
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");
        View view = getLayoutInflater().inflate(R.layout.add_item_alert_dialog, null);
        EditText input = view.findViewById(R.id.input_item);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
//            String insert = input.getText().toString().trim();
//            if (collections.contains(insert)) {
//                Toast.makeText(context, "Duplicate collection name", Toast.LENGTH_LONG).show();
//            } else {
//                //Add collection name to Collection Table
//                dataBaseHelper.addNewCollection(insert);
//                //Create collection-name Table in database
//
//                //Notify insertion change to RecycleView Adapter
//                ids.clear();
//                collections.clear();
//                getAllCollection();
//                myWishlistCollectionRecycleAdapter.notifyItemInserted(collections.size() - 1);
//                //Update collection count
//                collectionCount.setText(String.valueOf(myWishlistCollectionRecycleAdapter.getItemCount()));
//            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }

}