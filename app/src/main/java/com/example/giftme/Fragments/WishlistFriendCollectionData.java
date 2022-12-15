package com.example.giftme.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.FrWishlistCollectionRecycleAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WishlistFriendCollectionData extends Fragment {

    View view1;
    Context context1;
    TextView collectionCount1;
    RecyclerView recyclerView1;
    ExtendedFloatingActionButton extendedFloatingActionButton;
    com.example.giftme.Adapters.FrWishlistCollectionRecycleAdapter FrWishlistCollectionRecycleAdapter;

    ArrayList<String> ids;
    ArrayList<String> collectionNames;
    ArrayList<String> collectionIDs;
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
        collectionNames = new ArrayList<>();
        collectionIDs = new ArrayList<>();
        friendIds = new ArrayList<>();
        friendNames = new ArrayList<>();
        friendImgs = new ArrayList<>();
        extendedFloatingActionButton = view1.findViewById(R.id.action1);

        //if bundle was sent
        final Bundle args = getArguments();
        if(args !=null){
            try {
                String userID = args.getString("user_id");
                String wishlistID = args.getString("collection_id");
                addFriendCollection(userID, wishlistID);
            }catch(Exception e){
                System.out.println("Error");
            }
        }
        //if user is logged in, then show their FRIEND collections
        if (SessionManager.getUserStatus(context1)) {
            getAllFriends();
        }
        collectionCount1.setText(String.valueOf(collectionNames.size()));
        
        //floating action button to add friend's wishlist via shared link manually
        extendedFloatingActionButton.setOnClickListener(view -> {
            confirmDialog();
        });
        recyclerView1.setLayoutManager(new LinearLayoutManager(context1));
        recyclerView1.setHasFixedSize(true);
        FrWishlistCollectionRecycleAdapter = new FrWishlistCollectionRecycleAdapter(this.getActivity(), context1, ids, collectionNames, collectionIDs, friendIds, friendNames, friendImgs);
        recyclerView1.setAdapter(FrWishlistCollectionRecycleAdapter);

        return view1;
    }

    //getting the collection Name and inserting the collection into the COLLECTIONS table in SQLite
    public void addFriendCollection(String userID, String collectionID){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fireStore.collection("users").document(userID);
        DocumentReference collectionRef = userRef.collection("wishlists").document(collectionID);
        String collection_name = "Collection Name";

        String displayName = "displayName";
        String photoURL = "photoUrl";
        final String[] friend = new String[2];
        //note: friend[0] = name; friend[1] = profile picture
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot user = task.getResult();
                    friend[0] = user.getString(displayName);
                    friend[1] = user.getString(photoURL);
                    final String[] collectionName= new String[1];

                    collectionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()) {
                                    //collection exists
                                    //add collection to our sqlite table 'COLLECTIONS'
                                    DocumentSnapshot collection = task.getResult();
                                    collectionName[0] = collection.getString(collection_name);
                                    dataBaseHelper.addNewFriendCollection(friend[0], collectionName[0], userID, collectionID, friend[1]);
                                    //refresh everything
                                    ids.clear();
                                    collectionNames.clear();
                                    collectionIDs.clear();
                                    friendNames.clear();
                                    friendIds.clear();
                                    friendImgs.clear();
                                    getAllFriends();
                                    FrWishlistCollectionRecycleAdapter.notifyItemInserted(collectionNames.size() - 1);
                                    collectionCount1.setText(String.valueOf(FrWishlistCollectionRecycleAdapter.getItemCount()));
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void getAllFriends(){
        Cursor cursor = dataBaseHelper.readCollectionTableAllData(TABLE_NAME);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //if these collections belong to friends
                if(cursor.getString(3) != null){
                    ids.add(cursor.getString(0));
                    collectionNames.add(cursor.getString(1));
                    friendNames.add(cursor.getString(2));
                    friendIds.add(cursor.getString(3));
                    friendImgs.add(cursor.getString(4));
                    collectionIDs.add(cursor.getString(5));
                }
            }
        }
    }

    /**
     * Private function that pop up confirm dialog that wait user input
     */
    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        builder.setTitle("Add Friend's Collection");
        View view = getLayoutInflater().inflate(R.layout.add_link_collection, null);
        TextInputEditText input = view.findViewById(R.id.input);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String insert = Objects.requireNonNull(input.getText()).toString().trim();
            if (collectionNames.contains(insert) || insert.length() == 0 || insert.length() > 200) {
                Toast.makeText(context1, "Invalid collection name", Toast.LENGTH_LONG).show();
            } else {
                //get link from input
                int index_parseDeepLink = insert.indexOf("3D");
                String deepLink = insert.substring(index_parseDeepLink+2);
                //need to parse the userID and collectionID
                int index = deepLink.indexOf("%20");
                String userEmail = deepLink.substring(0, index);
                int indexAt = userEmail.indexOf('%');
                //ex: test123@gmail.com becomes test123%20gmail.com so we need to break apart and
                //remake the email
                String userIDName = userEmail.substring(0,indexAt);
                String userIDEnd = userEmail.substring(indexAt+3);
                String userID = userIDName + "@" + userIDEnd;

                String wishlistID = deepLink.substring(index+3);
                addFriendCollection(userID, wishlistID);

            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }


}