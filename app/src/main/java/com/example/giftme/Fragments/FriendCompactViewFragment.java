package com.example.giftme.Fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.FriendItemsAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.Value;

public class FriendCompactViewFragment extends Fragment {

    View view;
    Context context;
    RecyclerView recyclerView;
    DataBaseHelper dataBaseHelper;
    FriendItemsAdapter friendItemAdapter;
    ArrayList<Item> items;
    String collection_name;
    itemNumListener itemNumListener;
    Activity activity;

    public static String collectionName;

    public FriendCompactViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.friend_fragment_compact_view, container, false);
        recyclerView = view.findViewById(R.id.friend_compact_view_recycler_view);
        context = getContext();
        dataBaseHelper = new DataBaseHelper(context);
        items = new ArrayList<>();
        if (getArguments() != null) {
            collection_name = getArguments().getString("collection_name");
        }
        getAllItemsFirestore();
        Log.d("FIRESTORE AFTER ", items.toString());

        return view;
    }

    //get all items from firestore with collectionID
    private void  getAllItemsFirestore() {

//            //TEST FIRE STORE START
//            //get fire store collection wishlist items
//
        String userID = "wycalex@bu.edu";
        String wishlistID = "PktmAeturc9c0TTa1adG";

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fireStore.collection("usersTest").document(userID);
        DocumentReference collectionRef = userRef.collection("wishlists").document(wishlistID);

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
                                            Item currentItem = dataBaseHelper.convertMapIntoItem( (Map<String, Object>) value, wishlistID);
                                            Log.d("FIRESTORE", currentItem.toString());
                                            items.add(currentItem);
                                        }
                                    }
//                                    Log.d("TEST", key + " " + value)

//                                    Log.d("TEST_VALUE", value.getClass().toString())
//                                    dataBaseHelper.convertMapIntoItem( (Map<String, Object>) value)

                            );
                        }
//                        dataBaseHelper.convertMapIntoItem(itemsInWishlist);
//                        Log.d("fireStore", String.valueOf(itemsInWishlist));
//                        String wishlistName = (String) doc.get("Collection Name");
//                        Log.d("friendWLName", wishlistName);
                        collectionName =  (String) doc.get("Collection Name");
                        // DISPLAYING THE ITEMS FROM FRIEND WISHLIST
                        friendItemAdapter = new FriendItemsAdapter(getActivity(), context, items);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(friendItemAdapter);
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


//            //TEST FIRE STORE END
        Log.d("FIRESTORE AFTER3 ", items.toString());

    }

    public interface itemNumListener {
        void updateItemNum(String count);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof itemNumListener) {
            itemNumListener = (itemNumListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemNumListener = null;
    }
}