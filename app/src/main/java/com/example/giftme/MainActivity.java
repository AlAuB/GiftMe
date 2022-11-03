package com.example.giftme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    WishlistFragment wishlistFragment;
    NotificationFragment notificationFragment;
    SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        wishlistFragment = new WishlistFragment();
        notificationFragment = new NotificationFragment();
        settingFragment = new SettingFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.frag_view, wishlistFragment, "wishlist").
                setReorderingAllowed(true).commit();

        bottomNavigationView.setSelectedItemId(R.id.wishlist);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.wishlist) {
                fragment = wishlistFragment;
            } else if (id == R.id.notification) {
                fragment = notificationFragment;
            } else {
                fragment = settingFragment;
            }
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.frag_view, fragment).
                    setReorderingAllowed(true).commit();
            return true;
        });
    }
}