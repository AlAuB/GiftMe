package com.example.giftme;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {
    ImageView imgView;
    EditText nameET;
    EditText descriptionET;
    EditText priceET;
    EditText linkET;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item_screen);


    }
}
