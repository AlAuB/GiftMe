package com.example.giftme.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Objects;

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
    Bitmap bitmap;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Context context;

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
        context = getApplicationContext();

        cancelButton = findViewById(R.id.cancel);
        saveButton = findViewById(R.id.save);
        dataBaseHelper = new DataBaseHelper(this);

        //get information from intent
        Intent intent = getIntent();
        int itemID = intent.getIntExtra("itemID", 1);
        String itemName = intent.getStringExtra("itemName");
        int itemHearts = intent.getIntExtra("itemHearts", 0);
        double itemPrice = intent.getDoubleExtra("itemPrice", 0.0);
        String itemDes = intent.getStringExtra("itemDes");
        if(Objects.equals(itemDes, "null")){ itemDes = "";}
        String img = intent.getStringExtra("itemImg");
        String itemURL = intent.getStringExtra("itemURL");
        if(Objects.equals(itemURL, "null")){ itemURL = "";}
        String itemDate = intent.getStringExtra("itemDate");
//        if(Objects.equals(itemDate, "null")){ itemDate = "";}
        String itemFsID = intent.getStringExtra("itemFsID");
        Log.d("editItem", "firestoreID from inten" + itemFsID);
        if(Objects.equals(itemFsID, "null")){ itemFsID = "";}

        String collectionName = intent.getStringExtra("collectionName");
        Log.d("debug::", "edit item activity: " + collectionName + " " + itemFsID);
        //(re)create item obj
        Item item = new Item(itemID, itemURL, itemName, itemHearts, itemPrice,
                itemDes, itemDate, img);

        //set views
        nameET.setText(itemName);
        descriptionET.setText(item.getDescription());
        Log.d("itemDes", item.getDescription());
        priceET.setText(String.valueOf(item.getPrice()));

        Log.d("itemImg", "Img is null" + (item.getImg()==null));

        Log.d("itemImage", "Img " + img);
        if(img == null || img.equals("null")){
//            File file = new File(img);
//            Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imgView.setImageBitmap(getBitMap);
            imgView.setImageResource(R.drawable.black_text);
        }
        else{
            if(!img.contains("/firebasestorage")){
                //get bitmap
                File file = new File(img);
                Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgView.setImageBitmap(getBitMap);
            }
            else{
                //use link from firestore storage
                Picasso.get().load(img).into(imgView);
            }
        }

        //get link
        linkET.setText(item.getWebsite());
        ratingBar.setRating(itemHearts);

        //choose image
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent imgIntent = result.getData();
                    if (imgIntent != null) {
                        Uri image = imgIntent.getData();
                        imgView.setImageURI(image);
                        try {
                            // Could lead to OOM if image too large (10K * 10K)
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                        } catch (Exception e) {
                            Toast.makeText(this, "Cannot convert Uri to bitmap", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditItemActivity.this,
                                "No image selected", Toast.LENGTH_SHORT).show();
                    }
                });

        imgView.setOnClickListener(view -> openGallery());
        //choose image end --------------

        String finalItemFsID = itemFsID;
        saveButton.setOnClickListener(view -> {
            try{
                Date dateObj = new Date();
                String fileName = dateObj.getTime() + ".jpg";

                FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
                dataBaseHelper.storeImageFirebase(bitmap, fileName);

                String newName = String.valueOf(nameET.getText());
                String newDescription = String.valueOf(descriptionET.getText());
                Log.d("newDes", newDescription);
                int newPrice = Integer.parseInt(String.valueOf(priceET.getText()));
                int newRating = (int) ratingBar.getRating();
                String newLink = String.valueOf(linkET.getText());
                if(!newLink.isEmpty()){
                    if(!newLink.contains("http")){
                        Toast.makeText(context, "Link must include https", Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
                String newImg = context.getApplicationContext().getFilesDir() + "/" + fileName;

                dataBaseHelper.updateById(collectionName, newLink, item.getId(), newName, newPrice,
                        newDescription, newRating, newImg, finalItemFsID);
                Log.d("editItem", "firestoreID of item " + finalItemFsID);
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();

                Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
                myCollectionItemsIntent.putExtra("collection_name", collectionName);
                finish();
                startActivity(myCollectionItemsIntent);
            }catch(Exception e){
                String newName = String.valueOf(nameET.getText());
                String newDescription = String.valueOf(descriptionET.getText());
                Log.d("newDes", newDescription);
                double newPrice = Double.parseDouble(String.valueOf(priceET.getText()));
                int newRating = (int) ratingBar.getRating();
                String newLink = String.valueOf(linkET.getText());
                if(!newLink.isEmpty()){
                    if(!newLink.contains("http")){
                        Toast.makeText(context, "Link must include https", Toast.LENGTH_SHORT).show();
                    }
                }
                dataBaseHelper.updateById(collectionName, newLink, item.getId(), newName, newPrice,
                        newDescription, newRating, item.getImg(), finalItemFsID);
                Log.d("editItem", "firestoreID of item " + finalItemFsID);
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();

                Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
                myCollectionItemsIntent.putExtra("collection_name", collectionName);
                finish();
                startActivity(myCollectionItemsIntent);
            }
        });

        cancelButton.setOnClickListener(view -> {
            //edit flow later: this --> detaileditemview -> mycollectionitems
            Intent myCollectionItemsIntent = new Intent(this, MyCollectionItems.class);
            myCollectionItemsIntent.putExtra("collection_name", collectionName);
            finish();
            startActivity(myCollectionItemsIntent);
        });
    }

    private void openGallery() {
        Intent imgIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(imgIntent);
    }

}