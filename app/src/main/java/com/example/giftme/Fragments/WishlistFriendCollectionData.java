package com.example.giftme.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.FrWishlistCollectionRecycleAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class WishlistFriendCollectionData extends Fragment {

    View view1;
    Context context1;
    TextView collectionCount1;
    RecyclerView recyclerView1;
    FloatingActionButton floatingActionButton1;
    com.example.giftme.Adapters.FrWishlistCollectionRecycleAdapter FrWishlistCollectionRecycleAdapter;

    ArrayList<String> ids;
    ArrayList<String> collections;
    ArrayList<String> friendIds;
    ArrayList<String> friendNames;

    DataBaseHelper dataBaseHelper;

    private static final String TABLE_NAME = "COLLECTIONS";

    public WishlistFriendCollectionData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(R.layout.fragment_wishlist_friend_collection_data, container, false);
        context1 = this.getContext();
        collectionCount1 = view1.findViewById(R.id.collectionCount1);
        recyclerView1 = view1.findViewById(R.id.recycleView1);

        dataBaseHelper = new DataBaseHelper(context1);
        ids = new ArrayList<>();
        collections = new ArrayList<>();
        friendIds = new ArrayList<>();
        friendNames = new ArrayList<>();
        getAllFriends();


        recyclerView1.setLayoutManager(new LinearLayoutManager(context1));
        recyclerView1.setHasFixedSize(true);
        FrWishlistCollectionRecycleAdapter = new FrWishlistCollectionRecycleAdapter(this.getActivity(), context1, ids, collections, friendIds, friendNames);
        recyclerView1.setAdapter(FrWishlistCollectionRecycleAdapter);

        floatingActionButton1 = view1.findViewById(R.id.action1);
        //TESTING START
        floatingActionButton1.setOnClickListener(view -> {
            String userID = "jinpenglyu0605@gmail.com";
            String friendName = dataBaseHelper.getFriendName(userID);
            String wishlistID = "QafItkFJs4A9NA57zOMS";
            String wishlistName = dataBaseHelper.getCollectionName(userID, wishlistID);

            dataBaseHelper.addNewFriendCollection(wishlistName, userID, wishlistID);
            ids.clear();
            collections.clear();
            friendIds.clear();
            getAllFriends();
            FrWishlistCollectionRecycleAdapter.notifyItemInserted(collections.size() - 1);
            //Update collection count
        });
        //TESTING END
        return view1;
    }

    public void getAllFriends(){
        Cursor cursor = dataBaseHelper.readCollectionTableAllData(TABLE_NAME);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //if these collections belong to friends
                if(cursor.getString(2) != null){
                    ids.add(cursor.getString(0));
                    collections.add(cursor.getString(1));
                    String friendID = cursor.getString(2);
                    friendIds.add(friendID);
                    friendNames.add(dataBaseHelper.getFriendName(friendID));
                }
            }
        }
    }
}