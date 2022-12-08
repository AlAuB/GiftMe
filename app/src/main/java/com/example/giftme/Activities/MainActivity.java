package com.example.giftme.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.giftme.Fragments.NotificationFragment;
import com.example.giftme.Fragments.SettingFragment;
import com.example.giftme.Fragments.WishlistFragment;
import com.example.giftme.Fragments.WishlistMyCollectionData;
import com.example.giftme.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SettingFragment.SignStatusListener{

    BottomNavigationView bottomNavigationView;

    WishlistFragment wishlistFragment = new WishlistFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingFragment settingFragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_view, wishlistFragment).commit();

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(6);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.wishlist) {
                    getSupportFragmentManager().beginTransaction().
                            setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out). // exit
                            replace(R.id.frag_view, wishlistFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.notification) {
                    getSupportFragmentManager().beginTransaction().
                            setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out). // exit
                            replace(R.id.frag_view, notificationFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.setting) {
                    getSupportFragmentManager().beginTransaction().
                            setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out). // exit
                            replace(R.id.frag_view, settingFragment).commit();
                    return true;
                }
                return false;
            }
        });

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, pendingDynamicLinkData -> {
            Uri deeplink = null;
            if (pendingDynamicLinkData != null) {
                deeplink = pendingDynamicLinkData.getLink();
            }
            if (deeplink != null) {
                Toast.makeText(this, deeplink.toString(), Toast.LENGTH_SHORT).show();
                String temp = deeplink.toString();
                int index = temp.indexOf("=");
                String subString = temp.substring(index + 1);
                String[] data = subString.split("\\+");
                getDataFromFireStore(data[0], data[1]);
            }
        }).addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, "Cannot get deep link", Toast.LENGTH_SHORT).show());
    }

    private void getDataFromFireStore(String email, String collection_id) {
        System.out.println(email + " " + collection_id);
    }

    @Override
    public void updateData(boolean status) {
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