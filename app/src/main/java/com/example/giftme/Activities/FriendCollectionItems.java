package com.example.giftme.Activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Fragments.FriendCompactViewFragment;
import com.example.giftme.Fragments.FriendDetailViewFragment;
import com.example.giftme.R;

public class FriendCollectionItems extends AppCompatActivity {

    TextView collectionName;
    ImageButton detailedViewButton;
    ImageButton compactViewButton;
    String collection_name, collection_id;
    String friend_name, friend_id;
    Bundle bundle;
    FriendCompactViewFragment friendCompactViewFragment;
    FriendDetailViewFragment friendDetailViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_collection_items);

        //set the view
        collectionName = findViewById(R.id.collection_name);
        detailedViewButton = findViewById(R.id.detail_view);
        compactViewButton = findViewById(R.id.compact_view);

        if (getIntent().hasExtra("collection_name") || getIntent().hasExtra("friend_name")) {
            collection_name = getIntent().getStringExtra("collection_name");
            friend_name = getIntent().getStringExtra("friend_name");
            collection_id = getIntent().getStringExtra("collection_id");
            friend_id = getIntent().getStringExtra("friend_id");
            String wishlist_title = friend_name + "'s " + collection_name;
            collectionName.setText(wishlist_title);

            bundle = new Bundle();
            bundle.putString("collection_name", wishlist_title);
            bundle.putString("collection_id", collection_id);
            bundle.putString("friend_id", friend_id);
        }

        //Default view
        if (getIntent().hasExtra("view")) {
            setButtonsAlpha(0);
            friendDetailViewFragment = new FriendDetailViewFragment();
            friendDetailViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendDetailViewFragment, "detail").
                    setReorderingAllowed(true).commit();
        } else {
            setButtonsAlpha(1);
            friendCompactViewFragment = new FriendCompactViewFragment();
            friendCompactViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendCompactViewFragment, "Compact").
                    setReorderingAllowed(true).commit();
        }

        //change to detail view
        detailedViewButton.setOnClickListener(view -> {
            setButtonsAlpha(0);
            friendDetailViewFragment = new FriendDetailViewFragment();
            friendDetailViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendDetailViewFragment, "detail").
                    setReorderingAllowed(true).commit();
        });

        //change to compact view
        compactViewButton.setOnClickListener(view -> {
            setButtonsAlpha(1);
            friendCompactViewFragment = new FriendCompactViewFragment();
            friendCompactViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendCompactViewFragment, "Compact").
                    setReorderingAllowed(true).commit();
        });
    }

    private void setButtonsAlpha(int position) {
        if (position == 0) {
            compactViewButton.setAlpha(0.4f);
            detailedViewButton.setAlpha(1.0f);
        } else {
            detailedViewButton.setAlpha(0.4f);
            compactViewButton.setAlpha(1.0f);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("collection_name", collectionName.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        collectionName.setText(savedInstanceState.getString("collection_name"));
    }
}