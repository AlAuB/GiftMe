package com.example.giftme;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
