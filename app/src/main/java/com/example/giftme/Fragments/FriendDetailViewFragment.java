package com.example.giftme.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.AddNewItemManually;
import com.example.giftme.Adapters.FriendItemsAdapter;
import com.example.giftme.Adapters.FriendItemsDetailViewAdapter;
import com.example.giftme.Adapters.MyCollectionItemsAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FriendDetailViewFragment extends Fragment {

    View view;
    Context context;
    Activity activity;
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    FriendItemsDetailViewAdapter friendItemsDetailAdapter;
    DataBaseHelper dataBaseHelper;
    ArrayList<Item> items;
    String friendName;
    String collection_name;
    String collectionID;

   itemNumListener itemNumListener;

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
        actionButton = view.findViewById(R.id.friend_detailed_view_action);
        dataBaseHelper = new DataBaseHelper(context);
        items = new ArrayList<>();
        if (getArguments() != null) {
            collection_name = getArguments().getString("collection_name");
        }
        getAllItemsFirestore();
        return view;
    }

    //get all items from firestore with collectionID
    private void getAllItemsFirestore() {

//            //TEST FIRE STORE START
//            //get fire store collection wishlist items
//
        String userID = "jinpenglyu0605@gmail.com";
        String wishlistID = "L74q60KF4tB3PmiR6YiC";

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fireStore.collection("users").document(userID);
        DocumentReference collectionRef = userRef.collection("wishlists").document(wishlistID);

        collectionID = wishlistID; //parse from dynamic link

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()){
                        //doc.get("field name")
                        Map<String, Object> itemsInWishlist = doc.getData();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            itemsInWishlist.forEach((key, value) -> {
                                if( value instanceof HashMap){
                                    Item currentItem = dataBaseHelper.convertMapIntoItem( (Map<String, Object>) value, key);
                                    Log.d("FIRESTORE", currentItem.toString());
                                    items.add(currentItem);
                                }
                            });
                        }
                        // DISPLAYING THE ITEMS FROM FRIEND WISHLIST
                        friendItemsDetailAdapter = new FriendItemsDetailViewAdapter(getActivity(), context, items, friendName, collectionID);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(friendItemsDetailAdapter);
//        itemNumListener.updateItemNum(String.valueOf(friendItemAdapter.getItemCount()));
                        activity = getActivity();
                        // DISPLAYING THE ITEMS FROM FRIEND WISHLIST
                    }
                }else
                {
                    Log.d("ToastError", "error");
                }
            }
        });

    }

    public interface itemNumListener {
        void updateItemNum(String count);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FriendDetailViewFragment.itemNumListener) {
            itemNumListener = (FriendDetailViewFragment.itemNumListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemNumListener = null;
    }

}