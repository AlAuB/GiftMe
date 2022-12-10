package com.example.giftme.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.giftme.Adapters.TabViewPagerAdapter;
import com.example.giftme.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class WishlistFragment extends Fragment {

    View view;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabViewPagerAdapter tabViewPagerAdapter;

    public WishlistFragment() {
        // Required empty public constructor
    }

    public static WishlistFragment newInstance(String userID, String collectionID){
        Bundle args = new Bundle();
        args.putString("user_id", userID);
        args.putString("collection_id", collectionID);
        WishlistFragment fragment = new WishlistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager2 = view.findViewById(R.id.pages);

        Bundle newBundle = new Bundle();
        if( getArguments()!=null) {
            newBundle.putString("user_id", getArguments().getString("user_id"));
            newBundle.putString("collection_id", getArguments().getString("collection_id"));
        }
        tabViewPagerAdapter = new TabViewPagerAdapter(this.requireActivity(), newBundle);
        viewPager2.setAdapter(tabViewPagerAdapter);

        //---TO SET VIEW TO FRIENDS' COLLECTIONS TAB START---
        if( getArguments()!=null){
            viewPager2.setCurrentItem(1);
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
        }
        //---TO SET VIEW TO FRIENDS' COLLECTIONS TAB END---

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
        return view;
    }
}

