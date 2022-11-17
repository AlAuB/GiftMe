package com.example.giftme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailedItemViewActivity extends AppCompatActivity {
    ImageView imgView;
    TextView itemNameTV;
    TextView descriptionTV;
    TextView priceTV;
    //EditText linkET;
    RatingBar ratingBar;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_item_view);

        //get information from intent
        Intent intent = getIntent();
        int itemID = intent.getIntExtra("itemID", 1);
        String itemName = intent.getStringExtra("itemName");
        int itemHearts = intent.getIntExtra("itemHearts", 0);
        int itemPrice = intent.getIntExtra("itemPrice", 0);
        String itemDes = intent.getStringExtra("itemDes");
        int img = intent.getIntExtra("itemImg", 0);
        String date = " ";
        String fsID= "FSid here";
        String collectionName = intent.getStringExtra("collectionName");

        //(re)create item obj
        Item item = new Item(itemID, itemName, itemHearts, itemPrice,
        itemDes, date, img, fsID );

        //assign the views
        itemNameTV = findViewById(R.id.itemNameTV);
        priceTV = findViewById(R.id.itemPriceTV);

        //set the views
        itemNameTV.setText(item.getName());
        priceTV.setText(String.valueOf(item.getPrice()));


        editButton = findViewById(R.id.button_edit);
        editButton.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, EditItemActivity.class);
            //put in name, price description, hearts, and link etc
            newIntent.putExtra("itemID", item.getId());
            newIntent.putExtra("itemName", item.getName());
            newIntent.putExtra("itemHearts", item.getHearts());
            newIntent.putExtra("itemPrice", item.getPrice());
            newIntent.putExtra("itemDes", item.getDescription());
            newIntent.putExtra("itemImg", item.getImg());
            newIntent.putExtra("itemFSID", item.getTableID());
            newIntent.putExtra("collectionName", collectionName);
            startActivity(newIntent);

        });

    }
}
