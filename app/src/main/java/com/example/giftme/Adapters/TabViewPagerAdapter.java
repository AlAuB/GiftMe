package com.example.giftme.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.giftme.Fragments.WishlistFragment;
import com.example.giftme.Fragments.WishlistFriendCollectionData;
import com.example.giftme.Fragments.WishlistMyCollectionData;

public class TabViewPagerAdapter extends FragmentStateAdapter {

    //DATA FROM DEEPLINK
    private final Bundle fragmentBundle;

    public TabViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Bundle data) {
        super(fragmentActivity);
        fragmentBundle = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            WishlistFriendCollectionData fragment = new WishlistFriendCollectionData();
            fragment.setArguments(this.fragmentBundle);
            return fragment;
        }
        return new WishlistMyCollectionData();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
