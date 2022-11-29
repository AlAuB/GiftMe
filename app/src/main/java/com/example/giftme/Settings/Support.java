package com.example.giftme.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.giftme.R;

public class Support extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        WebView page4 = findViewById(R.id.page4);
        page4.loadUrl("file:///android_asset/Support.html");
    }
}