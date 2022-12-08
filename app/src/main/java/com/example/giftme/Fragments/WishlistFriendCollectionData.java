package com.example.giftme.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.FrWishlistCollectionRecycleAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    ArrayList<String> friendImgs;

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
        friendImgs = new ArrayList<>();
        getAllFriends();

        recyclerView1.setLayoutManager(new LinearLayoutManager(context1));
        recyclerView1.setHasFixedSize(true);
        FrWishlistCollectionRecycleAdapter = new FrWishlistCollectionRecycleAdapter(this.getActivity(), context1, ids, collections, friendIds, friendNames, friendImgs);
        recyclerView1.setAdapter(FrWishlistCollectionRecycleAdapter);
        floatingActionButton1 = view1.findViewById(R.id.action1);

        //TESTING START
        floatingActionButton1.setOnClickListener(view -> {

            String userID = "jinpenglyu0605@gmail.com";
            String wishlistID = "L74q60KF4tB3PmiR6YiC";

            getCollectionName(userID, wishlistID);


            //Update collection count
        });
        //TESTING END
        return view1;
    }

    //getting the collection Name and inserting the collection into the COLLECTIONS table in SQLite
    //MAYBE RENAME TO ADD COLLECTION?
    public void getCollectionName(String userID, String collectionID){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fireStore.collection("users").document(userID);
        DocumentReference collectionRef = userRef.collection("wishlists").document(collectionID);
        String collection_name = "Collection Name";

        String displayName = "displayName";
        String photoURL = "photoUrl";
        final String[] friend = new String[2];
        //friend[0] = name; friend[1] = pfp
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot user = task.getResult();
                    friend[0] = user.getString(displayName);
                    friend[1] = user.getString(photoURL);

                    Log.d("friend", "PFP: " + friend[1]);

                    final String[] collectionName= new String[1];

                    collectionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot collection = task.getResult();
                                collectionName[0] = collection.getString(collection_name);
                                Log.d("friendCollectionName", "Name: " + collectionName[0]);
                                Log.d("friendName2", "Name: " + friend[0]);
                                dataBaseHelper.addNewFriendCollection(friend[0], collectionName[0], userID, collectionID, friend[1]);
                                ids.clear();
                                collections.clear();
                                friendIds.clear();
                                friendImgs.clear();
                                getAllFriends();
                                FrWishlistCollectionRecycleAdapter.notifyItemInserted(collections.size() - 1);

                            }
                        }
                    });
                }
            }
        });
//        return collectionName[0];
    }

    public void getAllFriends(){
        Cursor cursor = dataBaseHelper.readCollectionTableAllData(TABLE_NAME);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //if these collections belong to friends
                if(cursor.getString(3) != null){
                    ids.add(cursor.getString(0));
                    collections.add(cursor.getString(1));
                    friendNames.add(cursor.getString(2));
                    friendIds.add(cursor.getString(3));
                    friendImgs.add(cursor.getString(4));
                }
            }
        }
    }
}