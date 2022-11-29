package com.example.giftme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        WebView page3 = findViewById(R.id.page3);
        page3.loadUrl("file:///android_asset/FAQ.html");
    }
}