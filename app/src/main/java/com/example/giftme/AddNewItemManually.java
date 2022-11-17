package com.example.giftme;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class AddNewItemManually extends AppCompatActivity {

    EditText name;
    ImageView imageView;
    Button cancel, save;
    String collectionName;
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

        name = findViewById(R.id.item_name_input);
        imageView = findViewById(R.id.item_image_input);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);

        imageView.setOnClickListener(view -> openGallery());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add image path into database, save image in folder, and return to MyCollectionItems
                try {
                    FileOutputStream fileOutputStream = context.openFileOutput("Test.jpg", Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    File file = new File(context.getApplicationContext().getFilesDir() + "/Test.jpg");
                } catch (Exception e) {
                    Toast.makeText(AddNewItemManually.this, "Save image NOT success", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyCollectionItems.class);
            intent.putExtra("collection_name", collectionName);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }
}