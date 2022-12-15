package com.example.giftme.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class AddNewItemManually extends AppCompatActivity {

    TextInputEditText name, website, price, extraInfo;
    ImageView imageView;
    Button cancel, save;
    RatingBar ratingBar;
    String collectionName;
    DataBaseHelper dataBaseHelper;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Bitmap bitmap;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item_manually);
        context = getApplicationContext();
        if (getIntent().hasExtra("collection_name")) {
            collectionName = getIntent().getStringExtra("collection_name");
        }
        website = findViewById(R.id.website_link);
        name = findViewById(R.id.item_name_input);
        price = findViewById(R.id.item_price_input);
        extraInfo = findViewById(R.id.extraInfo);
        imageView = findViewById(R.id.item_image_input);
        cancel = findViewById(R.id.button_cancel);
        save = findViewById(R.id.button_save);
        ratingBar = findViewById(R.id.item_rating);
        dataBaseHelper = new DataBaseHelper(context);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Uri image = intent.getData();
                        imageView.setImageURI(image);
                        try {
                            // Could lead to OOM if image too large (10K * 10K)
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                        } catch (Exception e) {
                            Toast.makeText(this, "Cannot convert Uri to bitmap", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddNewItemManually.this,
                                "No image selected", Toast.LENGTH_SHORT).show();
                    }
                });

        imageView.setOnClickListener(view -> openGallery());

        save.setOnClickListener(view -> {
            //Add image path into database, save image in folder, and return to MyCollectionItems
            try {
                String date;
                Date dateObj = new Date();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    date = LocalDate.now().toString();
                } else {
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat
                            = new SimpleDateFormat("yyyy-MM-dd");
                    date = dateFormat.format(dateObj);
                }
                String fileName = dateObj.getTime() + ".jpg";
                FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
                dataBaseHelper.storeImageFirebase(bitmap, fileName);
                String itemName = Objects.requireNonNull(name.getText()).toString();
                String itemPrice = Objects.requireNonNull(price.getText()).toString();
                String itemExtraInfo = Objects.requireNonNull(extraInfo.getText()).toString();
                if (itemName.length() > 30 || itemPrice.length() > 10 || itemExtraInfo.length() > 100) {
                    Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show();
                } else {
                    Item item = new Item();
                    Log.d("debug::", "onCreate: " + item.getClaimed());
                    String url = "";
                    if (website.getText() != null)
                        url = website.getText().toString();
                    if (url.length() != 0 && !url.contains("http")) {
                        Toast.makeText(context, "Link must include http", Toast.LENGTH_SHORT).show();
                    } else {
                        item.setWebsite(url);
                        item.setDate(date);
                        item.setName(itemName);
                        item.setDescription(itemExtraInfo);
                        item.setHearts((int) ratingBar.getRating());
                        item.setPrice(Integer.parseInt(itemPrice));
                        item.setImg(fileName);
                        Log.d("debug::", "onCreate: " + item.getImg());
                        dataBaseHelper.insertItemIntoCollection(collectionName, item);
                        Intent intent = new Intent(context, MyCollectionItems.class);
                        intent.putExtra("view", "Detailed");
                        intent.putExtra("collection_name", collectionName);
                        startActivity(intent);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(AddNewItemManually.this, "Save image NOT success", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyCollectionItems.class);
            intent.putExtra("collection_name", collectionName);
            intent.putExtra("view", "Detailed");
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }
}