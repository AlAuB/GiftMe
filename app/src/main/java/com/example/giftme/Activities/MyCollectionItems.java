package com.example.giftme.Activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giftme.Fragments.CompactViewFragment;
import com.example.giftme.Fragments.DetailViewFragment;
import com.example.giftme.R;

public class MyCollectionItems extends AppCompatActivity implements CompactViewFragment.itemNumListener, DetailViewFragment.itemNumListener {

    TextView itemCount, collectionName;
    ImageButton shareImgButton, detailedViewButton, compactViewButton;
    String collection_name;
    Bundle bundle;
    CompactViewFragment compactViewFragment;
    DetailViewFragment detailViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_items);

        collectionName = findViewById(R.id.collection_name);
        shareImgButton = findViewById(R.id.share);
        itemCount = findViewById(R.id.num_items);
        detailedViewButton = findViewById(R.id.detail_view);
        compactViewButton = findViewById(R.id.compact_view);

        if (getIntent().hasExtra("collection_name")) {
            collection_name = getIntent().getStringExtra("collection_name");
            collectionName.setText(collection_name);
            bundle = new Bundle();
            bundle.putString("collection_name", collection_name);
        }

        shareImgButton.setOnClickListener(view -> Toast.makeText(this, "This feature is under development", Toast.LENGTH_SHORT).show());

        //Default view
        if (getIntent().hasExtra("view")) {
            setButtonsAlpha(0);
            detailViewFragment = new DetailViewFragment();
            detailViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, detailViewFragment, "detail").
                    setReorderingAllowed(true).commit();
        } else {
            setButtonsAlpha(1);
            compactViewFragment = new CompactViewFragment();
            compactViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, compactViewFragment, "Compact").
                    setReorderingAllowed(true).commit();
        }

        detailedViewButton.setOnClickListener(view -> {
            setButtonsAlpha(0);
            compactViewButton.setAlpha(0.5f);
            detailedViewButton.setAlpha(1.0f);
            detailViewFragment = new DetailViewFragment();
            detailViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, detailViewFragment, "detail").
                    setReorderingAllowed(true).commit();
        });

        compactViewButton.setOnClickListener(view -> {
            setButtonsAlpha(1);
            compactViewFragment = new CompactViewFragment();
            compactViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.data_view, compactViewFragment, "Compact").
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

    @Override
    public void compactViewUpdateItemNum(String count) {
        itemCount.setText(count);
    }

    @Override
    public void detailedViewUpdateItemNum(String count) {
        itemCount.setText(count);
    }
}