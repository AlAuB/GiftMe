package com.example.giftme.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.FrWishlistCollectionRecycleAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Objects;

public class WishlistFriendCollectionData extends Fragment {

    View view1;
    Context context1;
    TextView collectionCount1;
    RecyclerView recyclerView1;
    FloatingActionButton floatingActionButton1;
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

        floatingActionButton1 = view1.findViewById(R.id.action1);

        final Bundle args = getArguments();
        Log.d("WISHLISTFRIEND", "ARGS IS NOT NULL " + (args!=null));
        if(args !=null){
            try {
                String userID = args.getString("user_id");
                String wishlistID = args.getString("collection_id");

                getCollectionName(userID, wishlistID);

            }catch(Exception e){
                System.out.println("Error");
            }

        }

        getAllFriends();
        //TESTING START
        floatingActionButton1.setOnClickListener(view -> {
            confirmDialog();
        });
        //TESTING END
        recyclerView1.setLayoutManager(new LinearLayoutManager(context1));
        recyclerView1.setHasFixedSize(true);
        FrWishlistCollectionRecycleAdapter = new FrWishlistCollectionRecycleAdapter(this.getActivity(), context1, ids, collectionNames, collectionIDs, friendIds, friendNames, friendImgs);
        recyclerView1.setAdapter(FrWishlistCollectionRecycleAdapter);

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

                    Log.d("friend", "Name: " + friend[0]);

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
                                collectionNames.clear();
                                collectionIDs.clear();
                                friendNames.clear();
                                friendIds.clear();
                                friendImgs.clear();
                                getAllFriends();
                                FrWishlistCollectionRecycleAdapter.notifyItemInserted(collectionNames.size() - 1);
                                //Update collection count
                                collectionCount1.setText(String.valueOf(FrWishlistCollectionRecycleAdapter.getItemCount()));
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
        View view = getLayoutInflater().inflate(R.layout.add_collection_alert_dialog, null);
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
                System.out.println("Link: " + deepLink);
                System.out.println("INDEX " + index_parseDeepLink);
                //lesleychen456%40gmail.com%20collectionID
//                int index = deepLink.indexOf('=');
                int index = deepLink.indexOf("%20");
                String userEmail = deepLink.substring(0, index);
                //user ID lesleychen456%40gmail.com
                int indexAt = userEmail.indexOf('%');
                String userIDName = userEmail.substring(0,indexAt);
                String userIDEnd = userEmail.substring(indexAt+3);
                String userID = userIDName + "@" + userIDEnd;

                String wishlistID = deepLink.substring(index+3);
                System.out.println("substring: " + userID);
                System.out.println("substring: " + wishlistID);

//                int indexPlus = subString.indexOf("+");
//                String userID = subString.substring(1, indexPlus);
//                String wishlistID = subString.substring(indexPlus+1);
//                System.out.println("substring: " + userID);
//                System.out.println("substring: " + wishlistID);
                //Add Friend's collection

                getCollectionName(userID, wishlistID);

            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }


}