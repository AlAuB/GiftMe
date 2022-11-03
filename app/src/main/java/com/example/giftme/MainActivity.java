package com.example.giftme;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore fireStore;
    BottomNavigationView bottomNavigationView;
    WishlistFragment wishlistFragment;
    NotificationFragment notificationFragment;
    SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fireStore = FirebaseFirestore.getInstance();
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

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", "Ron");
        user.put("lastName", "Czik");
        user.put("role", "Professor");
        user.put("rating", "-infinity");

        fireStore.collection("users").add(user)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show());
    }
}