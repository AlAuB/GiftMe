package com.example.giftme.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

public class DetailedItemViewActivity extends AppCompatActivity {
    TextView itemNameTV;
    TextView descriptionTV;
    TextView priceTV;
    TextView dateTV;
    RatingBar ratingBar;
    Button shopButton;
    Button editButton;
    Button deleteButton;
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

        double itemPrice = intent.getDoubleExtra("itemPrice", 0);
        String itemDes = intent.getStringExtra("itemDes");
        if(itemDes.equals("null")){ itemDes = "";}
        Log.d("itemDes", "itemDes is null " + itemDes.equals(""));
        Log.d("itemDesDetailed", itemDes);

        String img = intent.getStringExtra("itemImg");
        String itemURL = intent.getStringExtra("itemURL");
        if(Objects.equals(itemURL, "null")){ itemDes = "";}
        String itemDate = intent.getStringExtra("itemDate");
        if(Objects.equals(itemDate, "null")){ itemDate = "";}
        String collectionName = intent.getStringExtra("collectionName");
        String itemFsID = intent.getStringExtra("itemFsID");
        Log.d("detailItem", "firestoreID from intent" + itemFsID);
        if(Objects.equals(itemFsID, "null")){ itemFsID = "";}

        //(re)create item obj
        Item item = new Item(itemID, itemURL, itemName, itemHearts, itemPrice,
                itemDes, itemDate, img);

        //assign the views
        itemNameTV = findViewById(R.id.itemNameTV);
        priceTV = findViewById(R.id.itemPriceTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        ratingBar = findViewById(R.id.ratingBar);
        itemImageView = findViewById(R.id.image_itemImage);
        dateTV = findViewById(R.id.textView_date);

        String displayPrice = "$" + itemPrice;
        itemDate = "Saved: " + itemDate;

        //set the views
        itemNameTV.setText(itemName);
        priceTV.setText(displayPrice);
        descriptionTV.setText(itemDes);
        ratingBar.setRating(itemHearts);
        dateTV.setText(itemDate);

        String imgUrl = item.getImg();
        if( imgUrl == null || imgUrl.toLowerCase() == null) {
            Log.d("CATCH_EXCEPTION", "IMG: " + item.getImg());
        }
        else{
            if(imgUrl.contains("/")){
                //get bitmap
                File file = new File(imgUrl);
                Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
                itemImageView.setImageBitmap(getBitMap);
            }
            else{
                //use link from firestore storage
                Picasso.get().load(imgUrl).into(itemImageView);
            }

        }
        shopButton = findViewById(R.id.button_shop);
        shopButton.setOnClickListener(view ->{
            //if there is no link
            if ((itemURL == null) || (itemURL.equals("null")) || (itemURL.isEmpty())){
                    Toast.makeText(this, "There is no link", Toast.LENGTH_SHORT).show();
            }
            //if there is a link
            else{
                Log.d("itemURL", itemURL);
                Intent shopIntent = new Intent(Intent.ACTION_VIEW);
                shopIntent.setData(Uri.parse(itemURL));
                shopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(shopIntent);
            }
        });

//        backButton = findViewById(R.id.imageButton_backToPrevious);
//        backButton.setOnClickListener((view -> {
//            Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
//            myCollectionItemsIntent.putExtra("collection_name", collectionName);
//            startActivity(myCollectionItemsIntent);
//            finish();
//        }));

        editButton = findViewById(R.id.button_edit);
        String finalItemFsID = itemFsID;
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
            newIntent.putExtra("itemFsID", finalItemFsID);
            Log.d("detailItem", "firestoreID to pass " + finalItemFsID);
            newIntent.putExtra("collectionName", collectionName);
            startActivity(newIntent);
            finish();
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
