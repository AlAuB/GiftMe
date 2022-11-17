package com.example.giftme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {
    ImageView imgView;
    EditText nameET;
    EditText descriptionET;
    EditText priceET;
    EditText linkET;
    RatingBar ratingBar;
    Button saveButton;
    Button cancelButton;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item_view);

        //set everything
        nameET = findViewById(R.id.itemNameET);
        descriptionET = findViewById(R.id.descriptionET);
        imgView = findViewById(R.id.item_image_input);
        linkET = findViewById(R.id.linkET);
        priceET = findViewById(R.id.itemPriceET);
        ratingBar = findViewById(R.id.ratingBar);

        cancelButton = findViewById(R.id.button_cancel);
        saveButton = findViewById(R.id.button_save);
        dataBaseHelper = new DataBaseHelper(this);

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

        //set views
        nameET.setText(itemName);
        descriptionET.setText(item.getDescription());
        priceET.setText(String.valueOf(item.getPrice()));
        //imgView.setImageBitmap();
        //get link
        ratingBar.setRating(itemHearts);

        saveButton.setOnClickListener(view -> {
            String newName = String.valueOf(nameET.getText());
            String newDescription = String.valueOf(descriptionET.getText());
            int newPrice = Integer.parseInt(String.valueOf(priceET.getText()));
            int newRating = (int) ratingBar.getRating();

            dataBaseHelper.updateById(collectionName, item.getId(), newName, newPrice,
                     newDescription, newRating, item.getImg(), item.getTableID());
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(view -> {
            //edit flow later: this --> detaileditemview -> mycollectionitems
            Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
            myCollectionItemsIntent.putExtra("collection_name", collectionName);
            finish();
            startActivity(myCollectionItemsIntent);
        });
    }
}
