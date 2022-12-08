package com.example.giftme.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.giftme.Fragments.NotificationFragment;
import com.example.giftme.Fragments.SettingFragment;
import com.example.giftme.Fragments.WishlistFragment;
import com.example.giftme.Fragments.WishlistMyCollectionData;
import com.example.giftme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SettingFragment.SignStatusListener{

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

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, pendingDynamicLinkData -> {
            Uri deeplink = null;
            if (pendingDynamicLinkData != null) {
                deeplink = pendingDynamicLinkData.getLink();
            }
            if (deeplink != null) {
                System.out.println("The deeplink is: " + deeplink);
                Toast.makeText(this, deeplink.toString(), Toast.LENGTH_SHORT).show();
                String temp = deeplink.toString();
                int index = temp.charAt('=');
                String subString = temp.substring(index);
                System.out.println("Link: " + temp);
                System.out.println("index: " + index);
                System.out.println("substring: " + subString);
            }
        }).addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, "Cannot get deep link", Toast.LENGTH_SHORT).show());
    }

    private void getDataFromFireStore(String email, String collection_id) {

    }

    @Override
    public void updateData(boolean status) {
        Toast.makeText(this, "Sign status is changed!", Toast.LENGTH_SHORT).show();
        List<Fragment> list = getSupportFragmentManager().getFragments();
        Fragment fragment = null;
        for (int i = 0; i< list.size(); i++) {
            if (list.get(i) instanceof WishlistMyCollectionData) {
                fragment = list.get(i);
            }
        }
        if (fragment instanceof WishlistMyCollectionData) {
            if (status) {
                ((WishlistMyCollectionData) fragment).signedInState();
            } else {
                ((WishlistMyCollectionData) fragment).signedOutState();
            }
        }
    }
}