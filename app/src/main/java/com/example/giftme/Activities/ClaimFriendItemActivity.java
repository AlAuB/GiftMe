package com.example.giftme.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.giftme.Fragments.FriendCompactViewFragment;
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
    ImageButton backButton;
    DataBaseHelper dataBaseHelper;

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
        int itemPrice = intent.getIntExtra("itemPrice", 0);
        String itemDes = intent.getStringExtra("itemDes");
        String img = intent.getStringExtra("itemImg");
        String date = intent.getStringExtra("itemDate");
        String url = intent.getStringExtra("itemURL");

        String friendID = intent.getStringExtra("friendID");
        Log.d("FRIENDID_CLAIM", "friendID: " + friendID);
        String itemFsID = intent.getStringExtra("itemFsID");
        String friendCollectionID = intent.getStringExtra("collectionID");
        String collectionName = intent.getStringExtra("collectionName");
        //

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
        Log.d("itemImgClaim", "IMG: " + item.getImg());
        if(item.getImg() != null){
            Picasso.get().load(item.getImg()).into(imageView);
        }
        backButton = findViewById(R.id.imageButton_backToPrevious);
        backButton.setOnClickListener((view -> {
            finish();

        }));

        shopButton = findViewById(R.id.button_shop);
        shopButton.setOnClickListener(view -> {
            //if there is no link
            Log.d("claimLink", "URL is :" + url);
            if ((url.equals(null)) || (url.equals("null")) || (url.equals("")) || (url.isEmpty())){
                Toast.makeText(this, "There is no link", Toast.LENGTH_SHORT).show();
            }
            //if there is a link
            else{
                Intent shopIntent = new Intent(Intent.ACTION_VIEW);
                shopIntent.setData(Uri.parse(url));
                shopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(shopIntent);
            }
        });

        claimButton = findViewById(R.id.button_claim);
        if(item.getClaimed() == true){
            //item is already claimed by someone
            claimButton.setClickable(false);
            //set anony pfp too
        }
        else{
            claimButton.setClickable(true);
        }
        claimButton.setOnClickListener(view -> {
            Log.d("friendClaim", friendID + " " + friendCollectionID + " " + itemFsID);
            dataBaseHelper.editClaimed(friendID, friendCollectionID, itemFsID, true);
            finish();
        });
        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view->{
            finish();
        });

    }
}
