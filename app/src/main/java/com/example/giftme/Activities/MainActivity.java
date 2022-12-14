package com.example.giftme.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.giftme.Fragments.NotificationFragment;
import com.example.giftme.Fragments.SettingFragment;
import com.example.giftme.Fragments.WishlistFragment;
import com.example.giftme.Fragments.WishlistMyCollectionData;
import com.example.giftme.Helpers.FCMSend;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SettingFragment.SignStatusListener,
        WishlistMyCollectionData.MyFriendCollectionListener {

    BottomNavigationView bottomNavigationView;

    WishlistFragment wishlistFragment = new WishlistFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingFragment settingFragment = new SettingFragment();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_view, wishlistFragment).commit();

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(6);

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 44);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });

        //----RECEIVING DYNAMIC LINKS START---
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, pendingDynamicLinkData -> {
            Uri deeplink = null;
            if (pendingDynamicLinkData != null) {
                deeplink = pendingDynamicLinkData.getLink();
            }
            if (deeplink != null) {
                Toast.makeText(this, deeplink.toString(), Toast.LENGTH_SHORT).show();
                String temp = deeplink.toString();

                int index = temp.indexOf('=');
                String subString = temp.substring(index);

                int indexPlus = subString.indexOf("+");
                String userID = subString.substring(1, indexPlus);
                String collectionID = subString.substring(indexPlus + 1);

                sendNotification(userID);

                Fragment fragmentWishlist = WishlistFragment.newInstance(userID, collectionID);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_view, fragmentWishlist)
                        .commit();
            }
        }).addOnFailureListener(this, e ->
                Toast.makeText(MainActivity.this, "Cannot get deep link", Toast.LENGTH_SHORT).show());
    }

    private void sendNotification(String friendEmail) {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference reference = fireStore.collection("users").document(friendEmail);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    String token = Objects.requireNonNull(snapshot.getString("deviceMessagingToken")).trim();
                    String title = "Your friend get your collection";
                    String body = SessionManager.getUserName(getApplicationContext()) + " adds your collection";
                    System.out.println("Reach here");
                    FCMSend.pushNotification(getApplicationContext(), token, title, body);
                }
            }
        }).addOnFailureListener(e -> System.out.println("Cannot get Token from Firestore."));
    }

    @Override
    public void updateData(boolean status) {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        Fragment fragment = null;
        for (int i = 0; i < list.size(); i++) {
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


    @Override
    public void goToFriendCollection() {
        //WISHLISTFRIENDCOLLECTIONDATA FRAGMENT
    }
}