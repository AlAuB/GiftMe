package com.example.giftme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;

public class ClaimFriendItemActivity extends AppCompatActivity {
    TextView itemNameTV;
    TextView descriptionTV;
    TextView priceTV;
    RatingBar ratingBar;
    Button claimButton;
    Button cancelButton;
    ImageButton backButton;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detailed_item_view);

        //get information from intent
        Intent intent = getIntent();
        int itemID = intent.getIntExtra("itemID", 1);
        String itemName = intent.getStringExtra("itemName");
        int itemHearts = intent.getIntExtra("itemHearts", 0);
        int itemPrice = intent.getIntExtra("itemPrice", 0);
        String itemDes = intent.getStringExtra("itemDes");
        if(itemDes.equals("null")){ itemDes = "";}
        String img = intent.getStringExtra("itemImg");
        String date = " ";
        String url = "";
        String fsID= "FSid here";
        String collectionName = intent.getStringExtra("collectionName");

        //(re)create item obj
        Item item = new Item(itemID, url, itemName, itemHearts, itemPrice,
                itemDes, date, img);
        //assign the views
        itemNameTV = findViewById(R.id.itemNameTV);
        priceTV = findViewById(R.id.itemPriceTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        ratingBar = findViewById(R.id.ratingBar);

        //set the views
        itemNameTV.setText(item.getName());
        priceTV.setText(String.valueOf(item.getPrice()));
        descriptionTV.setText(item.getDescription());
        ratingBar.setRating(item.getHearts());

        backButton = findViewById(R.id.imageButton_backToPrevious);
        backButton.setOnClickListener((view -> {
            Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
            myCollectionItemsIntent.putExtra("collection_name", collectionName);
            finish();
            startActivity(myCollectionItemsIntent);
        }));

        claimButton = findViewById(R.id.button_claim);
        if(item.getClaimed() == true){
            //item is already claimed by someone
            claimButton.setClickable(false);
        }
        else{
            claimButton.setClickable(true);
        }
        claimButton.setOnClickListener(view -> {
//            Intent newIntent = new Intent(this, EditItemActivity.class);
//            //put in name, price description, hearts, and link etc
//            newIntent.putExtra("itemFSID", item.getTableID());
            //we have item.getTableID()
            //call setClaimed with that tableID to firestore
            finish();
        });
        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view->{
            finish();
        });

    }
}
