package com.example.giftme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewItemManually extends AppCompatActivity {

    EditText name;
    ImageView imageView;
    Button cancel, save;
    String collectionName;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item_manually);

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

                //Add data into database, save image in folder, and return to MyCollectionItems
            }
        });

        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyCollectionItems.class);
            intent.putExtra("name", collectionName);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }
}