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
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    Item item;
    Intent intent;

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
        item = new Item();

        cancelButton = findViewById(R.id.cancel);
        saveButton = findViewById(R.id.save);
        dataBaseHelper = new DataBaseHelper(this);

        //get information from intent
        intent = getIntent();
        int itemID = intent.getIntExtra("itemID", 1);
        String itemName = intent.getStringExtra("itemName");
        int itemHearts = intent.getIntExtra("itemHearts", 0);
        double itemPrice = intent.getDoubleExtra("itemPrice", 0.0);
        String itemDes = intent.getStringExtra("itemDes");
        if (Objects.equals(itemDes, "null")) {
            itemDes = "";
        }
        String img = intent.getStringExtra("itemImg");
        String itemURL = intent.getStringExtra("itemURL");
        if (Objects.equals(itemURL, null)) {
            itemURL = "";
        }
        String itemDate = intent.getStringExtra("itemDate");
        String itemFSID = intent.getStringExtra("itemFSID");
        Log.d("editItem", "firestoreID from inten" + itemFSID);
        if(Objects.equals(itemFSID, "null")){ itemFSID = "";}

        String collectionName = intent.getStringExtra("collectionName");
        Log.d("debug::", "edit item activity: " + collectionName + " " + itemFSID + " " + img);
        //(re)create item obj
        item = new Item(itemID, itemURL, itemName, itemHearts, itemPrice,
                itemDes, itemDate, img);

        //set views
        nameET.setText(itemName);
        descriptionET.setText(item.getDescription());
        Log.d("itemDes", item.getDescription());
        priceET.setText(String.valueOf(item.getPrice()));

        Log.d("itemImg", "Img is null" + (item.getImg() == null));

        Log.d("itemImage", "Img " + img);
        if (img == null || img.equals("null")) {
            imgView.setImageResource(R.drawable.surprise);
        } else {
            String tempPath = getApplicationContext().getFilesDir() + "/" + img;
            File file = new File(tempPath);
            if (file.exists()) {
                Bitmap getBitMap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgView.setImageBitmap(getBitMap);
            } else {
                //use the image stored in firestore storage
                String[] imgUri = new String[1];
                String path = "images/" + SessionManager.getUserEmail(context) + "/" + img;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference mountainsRef = storageRef.child(path);
                mountainsRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Got the download URL for 'users/me/profile.png'
                    imgUri[0] = uri.toString();
                    Log.d("insideIf", "URI: " + imgUri[0]);
                    Picasso.get().load(imgUri[0]).into(imgView);
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.d("Friend_DEBUG", "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
                });
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

        String finalItemFSID = itemFSID;
        saveButton.setOnClickListener(view -> {
            try {
                String fileName;
                if (bitmap != null) {
                    Date dateObj = new Date();
                    fileName = dateObj.getTime() + ".jpg";
                    FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    // first remove old image from storage
                    dataBaseHelper.removeImageFirebase(item.getImg());
                    // then upload it to storage
                    dataBaseHelper.storeImageFirebase(bitmap, fileName);
                } else {
                    fileName = intent.getStringExtra("itemImg");
                }

                String newName = String.valueOf(nameET.getText());
                String newDescription = "";
                if (String.valueOf(descriptionET.getText()).length() > 100) {
                    Toast.makeText(context, "Too much info in description!", Toast.LENGTH_SHORT).show();
                } else {
                    newDescription = String.valueOf(descriptionET.getText());
                }
                Log.d("newDes", "NEWDES IN TRY " + newDescription);
                Log.d("NEWIMG", "TESTING1");
                double newPrice = Double.parseDouble(String.valueOf(priceET.getText()));
                Log.d("NEWIMG", "TESTING2");
                int newRating = (int) ratingBar.getRating();
                Log.d("NEWIMG", "TESTING3");
                String newLink = String.valueOf(linkET.getText());
                Log.d("NEWIMG", "TESTING4");
                if (!newLink.isEmpty()) {
                    Log.d("NEWIMG", "TESTING INSIDE IF");
                    if (!newLink.contains("http")) {
                        Log.d("NEWIMG", "TESTING INSIDE IF 2");
                        Toast.makeText(context, "Link must include https", Toast.LENGTH_SHORT).show();
                    }
                    else{//link is valid
                        Log.d("NEWIMG", "TESTING");
                        Log.d("NEWIMG", fileName);
                        item.setImg(fileName);
                        item.setHearts(newRating);
                        item.setDescription(newDescription);
                        item.setName(newName);
                        item.setWebsite(newLink);
                        item.setPrice(newPrice);
                        dataBaseHelper.updateById(collectionName, newLink, item.getId(), newName, newPrice,
                                newDescription, newRating, fileName, finalItemFSID);
                        Log.d("editItem", "firestoreID of item " + finalItemFSID);
                        Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                        getBack(collectionName);
                    }
                }
                else{
                    Log.d("NEWIMG", "TESTING");
                    Log.d("NEWIMG", fileName);
                    item.setImg(fileName);
                    item.setHearts(newRating);
                    item.setDescription(newDescription);
                    item.setName(newName);
                    item.setWebsite("");
                    item.setPrice(newPrice);
                    dataBaseHelper.updateById(collectionName, newLink, item.getId(), newName, newPrice,
                            newDescription, newRating, fileName, finalItemFSID);
                    Log.d("editItem", "firestoreID of item " + finalItemFSID);
                    Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                    getBack(collectionName);
                }
            } catch (IOException e) {
                System.out.println("Cannot get New Image in edit Activity");
            }
        });

        cancelButton.setOnClickListener(view -> getBack(collectionName));
    }

    private void getBack(String collectionName) {
        Intent myCollectionItemsIntent = new Intent(this, DetailedItemViewActivity.class);
        myCollectionItemsIntent.putExtra("itemImg", item.getImg());
        myCollectionItemsIntent.putExtra("itemID", item.getId());
        myCollectionItemsIntent.putExtra("itemName", item.getName());
        myCollectionItemsIntent.putExtra("itemHearts", item.getHearts());
        myCollectionItemsIntent.putExtra("itemPrice", item.getPrice());
        myCollectionItemsIntent.putExtra("itemDes", item.getDescription());
        myCollectionItemsIntent.putExtra("itemURL", item.getWebsite());
        myCollectionItemsIntent.putExtra("itemDate", item.getDate());
        myCollectionItemsIntent.putExtra("itemFSID", item.getFireStoreID());
        myCollectionItemsIntent.putExtra("collectionName", collectionName);
        startActivity(myCollectionItemsIntent);
    }

    private void openGallery() {
        Intent imgIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(imgIntent);
    }

}