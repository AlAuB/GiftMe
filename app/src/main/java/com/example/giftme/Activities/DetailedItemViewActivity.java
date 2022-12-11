package com.example.giftme.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;

import java.io.File;
import java.util.Objects;

public class DetailedItemViewActivity extends AppCompatActivity {
    TextView itemNameTV;
    TextView descriptionTV;
    TextView priceTV;
    RatingBar ratingBar;
    Button shopButton;
    Button editButton;
    Button deleteButton;
    ImageButton backButton;
    ImageView itemImageView;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_item_view);

        //get information from intent
        Intent intent = getIntent();
        int itemID = intent.getIntExtra("itemID", 1);
        String itemName = intent.getStringExtra("itemName");
        int itemHearts = intent.getIntExtra("itemHearts", 0);
        Log.d("itemHearts", "num: " + itemHearts);

        int itemPrice = intent.getIntExtra("itemPrice", 0);
        String itemDes = intent.getStringExtra("itemDes");
        if(itemDes.equals("null")){ itemDes = "";}
        String img = intent.getStringExtra("itemImg");
        String itemURL = intent.getStringExtra("itemURL");
        if(Objects.equals(itemURL, "null")){ itemDes = "";}
        String itemDate = intent.getStringExtra("itemDate");
        if(Objects.equals(itemDate, "null")){ itemDate = "";}
        String collectionName = intent.getStringExtra("collectionName");

        //(re)create item obj
        Item item = new Item(itemID, itemURL, itemName, itemHearts, itemPrice,
                itemDes, itemDate, img);

        //assign the views
        itemNameTV = findViewById(R.id.itemNameTV);
        priceTV = findViewById(R.id.itemPriceTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        ratingBar = findViewById(R.id.ratingBar);
        itemImageView = findViewById(R.id.image_itemImage);

        String displayPrice = "$" + itemPrice;

        //set the views
        itemNameTV.setText(itemName);
        priceTV.setText(displayPrice);
        descriptionTV.setText(itemDes);
        ratingBar.setRating(itemHearts);
        File file = new File(img);
        Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
        itemImageView.setImageBitmap(getBitMap);

        shopButton = findViewById(R.id.button_shop);
        shopButton.setOnClickListener(view ->{
            Intent shopIntent = new Intent(Intent.ACTION_VIEW);
            shopIntent.setData(Uri.parse(itemURL));
            shopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(shopIntent);
        });

        backButton = findViewById(R.id.imageButton_backToPrevious);
        backButton.setOnClickListener((view -> {
            Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
            myCollectionItemsIntent.putExtra("collection_name", collectionName);
            finish();
            startActivity(myCollectionItemsIntent);
        }));

        editButton = findViewById(R.id.button_edit);
        editButton.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, EditItemActivity.class);
            //put in name, price description, hearts, and link etc
            newIntent.putExtra("itemID", item.getId());
            newIntent.putExtra("itemName", item.getName());
            newIntent.putExtra("itemHearts", item.getHearts());
            newIntent.putExtra("itemPrice", item.getPrice());
            newIntent.putExtra("itemURL", item.getWebsite());
            newIntent.putExtra("itemDes", item.getDescription());
            newIntent.putExtra("itemImg", item.getImg());
            newIntent.putExtra("itemFSID", item.getFireStoreID());
            newIntent.putExtra("collectionName", collectionName);
            finish();
            startActivity(newIntent);
        });
        deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(view->{
            Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
            myCollectionItemsIntent.putExtra("collection_name", collectionName);
            finish();
            startActivity(myCollectionItemsIntent);
            dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.deleteItem(String.valueOf(item.getId()),collectionName);
        });

    }
}
