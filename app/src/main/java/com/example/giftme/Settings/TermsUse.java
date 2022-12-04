package com.example.giftme.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.giftme.R;

public class TermsUse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_use);

        WebView page2 = findViewById(R.id.page2);
        page2.loadUrl("file:///android_asset/TermsUse.html");
    }
}