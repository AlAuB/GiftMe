package com.example.giftme.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.giftme.Fragments.WishlistFriendCollectionData;
import com.example.giftme.Fragments.WishlistMyCollectionData;

public class TabViewPagerAdapter extends FragmentStateAdapter {

    public TabViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new WishlistFriendCollectionData();
        }
        return new WishlistMyCollectionData();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
