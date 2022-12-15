package com.example.giftme.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.squareup.picasso.Picasso;

public class ClaimFriendItemActivity extends AppCompatActivity {
    TextView itemNameTV;
    TextView descriptionTV;
    TextView priceTV;
    TextView dateTV;
    ImageView imageView;
    RatingBar ratingBar;
    Button claimButton;
    Button cancelButton;
    Button shopButton;
    DataBaseHelper dataBaseHelper;
    String collectionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detailed_item_view);

        dataBaseHelper = new DataBaseHelper(this);

        //get information from intent
        Intent intent = getIntent();
        int itemID = intent.getIntExtra("itemID", 1);
        String itemName = intent.getStringExtra("itemName");
        int itemHearts = intent.getIntExtra("itemHearts", 0);
        double itemPrice = intent.getDoubleExtra("itemPrice", 0);
        String itemDes = intent.getStringExtra("itemDes");
        String img = intent.getStringExtra("itemImg");
        System.out.println("The image path in claim is: " + img);
        String date = intent.getStringExtra("itemDate");
        String url = intent.getStringExtra("itemURL");
        boolean claimed = intent.getBooleanExtra("itemClaimed", false);

        String friendID = intent.getStringExtra("friendID");
        String itemFSID = intent.getStringExtra("itemFSID");
        String friendCollectionID = intent.getStringExtra("collectionID");
        collectionName = intent.getStringExtra("collectionName");

        //(re)create item obj
        Item item = new Item(itemID, url, itemName, itemHearts, itemPrice,
                itemDes, date, img);
        //assign the views
        itemNameTV = findViewById(R.id.friend_itemNameTV);
        priceTV = findViewById(R.id.friend_itemPriceTV);
        descriptionTV = findViewById(R.id.friend_descriptionTV);
        ratingBar = findViewById(R.id.friend_ratingBar);
        dateTV  = findViewById(R.id.friend_textView_date);
        imageView = findViewById(R.id.friend_image_itemImage);

        //set the views
        itemNameTV.setText(item.getName());
        String displayPrice = "$" + item.getPrice();
        priceTV.setText(displayPrice);
        descriptionTV.setText(item.getDescription());
        ratingBar.setRating(item.getHearts());
        dateTV.setText(item.getDate());

        if(item.getImg() != null){
            Picasso.get().load(item.getImg()).into(imageView);
        }

        shopButton = findViewById(R.id.button_shop);
        shopButton.setOnClickListener(view -> {
            //if there is no link
            if ((url == null) || (url.equals("null")) || (url.isEmpty())){
                Toast.makeText(this, "There is no link", Toast.LENGTH_SHORT).show();
            } else{
                //if there is a link
                Intent shopIntent = new Intent(Intent.ACTION_VIEW);
                shopIntent.setData(Uri.parse(url));
                shopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(shopIntent);
            }
        });

        claimButton = findViewById(R.id.button_claim);
        //item is already claimed by someone
        //set anony pfp too
        claimButton.setEnabled(!claimed);
        claimButton.setOnClickListener(view -> {
            dataBaseHelper.editClaimed(friendID, friendCollectionID, itemFSID, true);
            dataBaseHelper.sendNotification(friendID, "Claimed!", "Someone claims one of your items in " + collectionName);
            claimButton.setEnabled(false);
            finish();
        });
        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view-> finish());
    }
}
