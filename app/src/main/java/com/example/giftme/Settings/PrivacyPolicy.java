package com.example.giftme.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.giftme.R;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView page1 = findViewById(R.id.page1);
        page1.loadUrl("file:///android_asset/PrivacyPolicy.html");
    }
}