package com.example.giftme.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Fragments.CompactViewFragment;
import com.example.giftme.Fragments.DetailViewFragment;
import com.example.giftme.Fragments.FriendCompactViewFragment;
import com.example.giftme.Fragments.FriendDetailViewFragment;
import com.example.giftme.R;

public class FriendCollectionItems extends AppCompatActivity implements CompactViewFragment.itemNumListener{

    TextView itemCount, collectionName;
    ImageButton shareImgButton, detailedViewButton, compactViewButton;
    String collection_name;
    String friend_name;
    Bundle bundle;
    FriendCompactViewFragment friendCompactViewFragment;
    FriendDetailViewFragment friendDetailViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_items);

        collectionName = findViewById(R.id.collection_name);
        shareImgButton = findViewById(R.id.share);
        itemCount = findViewById(R.id.num_items);
        detailedViewButton = findViewById(R.id.detail_view);
        compactViewButton = findViewById(R.id.compact_view);

        if (getIntent().hasExtra("collection_name") || getIntent().hasExtra("friend_name")) {
            collection_name = getIntent().getStringExtra("collection_name");
            friend_name = getIntent().getStringExtra("friend_name");
            String wishlist_title = friend_name + "'s " + collection_name;
            collectionName.setText(wishlist_title);
            bundle = new Bundle();
            bundle.putString("collection_name", wishlist_title);
        }

       shareImgButton.setVisibility(View.GONE);

        //Default view
        if (getIntent().hasExtra("view")) {
            friendDetailViewFragment = new FriendDetailViewFragment();
            friendDetailViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendDetailViewFragment, "detail").
                    setReorderingAllowed(true).commit();
        } else {
            friendCompactViewFragment = new FriendCompactViewFragment();
            friendCompactViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendCompactViewFragment, "Compact").
                    setReorderingAllowed(true).commit();
        }

        detailedViewButton.setOnClickListener(view -> {
            friendDetailViewFragment = new FriendDetailViewFragment();
            friendDetailViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendDetailViewFragment, "detail").
                    setReorderingAllowed(true).commit();
        });

        compactViewButton.setOnClickListener(view -> {
            friendCompactViewFragment = new FriendCompactViewFragment();
            friendCompactViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, friendCompactViewFragment, "Compact").
                    setReorderingAllowed(true).commit();
        });
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

    @Override
    public void updateItemNum(String count) {
        itemCount.setText(count);
    }
}