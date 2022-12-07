package com.example.giftme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.giftme.Adapters.TabViewPagerAdapter;
import com.example.giftme.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class WishlistFragment extends Fragment {

    View view;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabViewPagerAdapter tabViewPagerAdapter;

    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager2 = view.findViewById(R.id.pages);
        tabViewPagerAdapter = new TabViewPagerAdapter(this.requireActivity());
        viewPager2.setAdapter(tabViewPagerAdapter);
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

