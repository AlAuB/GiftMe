package com.example.giftme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class WishlistItemsFragment extends Fragment {

    RecyclerView recyclerView;
    TextView textView;
    ImageButton shareButton;
    Toolbar toolbar;


    FloatingActionButton floatingActionButton;

    View view;

    public WishlistItemsFragment() {
        // Required empty public constructor
    }

    public interface WishlistItemsFragmentListener{
        void onInputReceiver(CharSequence input);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_wishlist_items, container, false);

        recyclerView = view.findViewById(R.id.listOfItems);
        textView = view.findViewById(R.id.wishlistName);
        shareButton = view.findViewById(R.id.shareButton);
        toolbar = view.findViewById(R.id.toolbar);

        floatingActionButton = view.findViewById(R.id.floatingAddItem);


        return view;


    }


}
