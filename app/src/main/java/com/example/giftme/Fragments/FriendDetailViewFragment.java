package com.example.giftme.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.FriendItemsDetailViewAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendDetailViewFragment extends Fragment {

    View view;
    Context context;
    Activity activity;
    RecyclerView recyclerView;
    FriendItemsDetailViewAdapter friendItemsDetailAdapter;
    DataBaseHelper dataBaseHelper;
    ArrayList<Item> items;
    String friend_name, friend_id;
    String collection_name, collection_id;

    public FriendDetailViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.friend_fragment_detail_view, container, false);
        context = getContext();
        activity = getActivity();
        recyclerView = view.findViewById(R.id.friend_recycler_view_detail_view);
        dataBaseHelper = new DataBaseHelper(context);
        items = new ArrayList<>();
        if (getArguments() != null) {
            collection_name = getArguments().getString("collection_name");
            collection_id = getArguments().getString("collection_id");
            friend_name = getArguments().getString("friend_name");
            friend_id = getArguments().getString("friend_id");
        }
        getAllItemsFirestore();
        return view;
    }

    //get all items from firestore with collectionID
    private void getAllItemsFirestore() {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fireStore.collection("users").document(friend_id);
        DocumentReference collectionRef = userRef.collection("wishlists").document(collection_id);

        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    //doc.get("field name")
                    Map<String, Object> itemsInWishlist = doc.getData();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        itemsInWishlist.forEach((key, value) -> {
                            if (value instanceof HashMap) {
                                Item currentItem = dataBaseHelper.convertMapIntoItem((Map<String, Object>) value, key);
                                Log.d("FIRESTORE", currentItem.toString());
                                items.add(currentItem);
                            }
                        });
                    }
                    // DISPLAYING THE ITEMS FROM FRIEND WISHLIST
                    friendItemsDetailAdapter = new FriendItemsDetailViewAdapter(activity, context, items, friend_id, collection_id);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(friendItemsDetailAdapter);
                    // DISPLAYING THE ITEMS FROM FRIEND WISHLIST
                }
            } else {
                Log.d("ToastError", "error");
            }
        });
    }
}