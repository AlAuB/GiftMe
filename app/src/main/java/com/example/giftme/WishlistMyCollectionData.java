package com.example.giftme;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class WishlistMyCollectionData extends Fragment {

    View view;
    Activity activity;
    Context context;
    TextView collectionCount;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    MyWishlistCollectionRecycleAdapter myWishlistCollectionRecycleAdapter;

    private static final String COLLECTION_TABLE_NAME = "COLLECTIONS";

    ArrayList<String> ids;
    ArrayList<String> collections;
    DataBaseHelper dataBaseHelper;

    public WishlistMyCollectionData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wishlist_my_collection_data, container, false);
        context = this.getContext();
        collectionCount = view.findViewById(R.id.collectionCount);
        recyclerView = view.findViewById(R.id.recycleView);
        floatingActionButton = view.findViewById(R.id.action);
        ids = new ArrayList<>();
        collections = new ArrayList<>();
        activity = getActivity();
        dataBaseHelper = new DataBaseHelper(this.getContext());
        getAllCollection();
        collectionCount.setText(String.valueOf(collections.size()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        myWishlistCollectionRecycleAdapter
                = new MyWishlistCollectionRecycleAdapter(activity, this.getContext(), ids, collections);
        recyclerView.setAdapter(myWishlistCollectionRecycleAdapter);
        floatingActionButton.setOnClickListener(view -> confirmDialog());
        return view;
    }

    /**
     * Get all rows from database for Collection Table
     */
    private void getAllCollection() {
        Cursor cursor = dataBaseHelper.readCollectionTableAllData(COLLECTION_TABLE_NAME);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(0));
                collections.add(cursor.getString(1));
            }
        }
    }

    /**
     * Private function that pop up confirm dialog that wait user input
     */
    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        builder.setTitle("Create New Collection");
        View view = getLayoutInflater().inflate(R.layout.add_collection_alert_dialog, null);
        EditText input = view.findViewById(R.id.input);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String insert = input.getText().toString().trim();
            if (collections.contains(insert) || insert.length() == 0) {
                Toast.makeText(context, "Invalid collection name", Toast.LENGTH_LONG).show();
            } else {
                //Add collection name to Collection Table
                dataBaseHelper.addNewCollection(insert);
                //Create collection-name Table in database
                dataBaseHelper.createNewTable(insert);
                //Notify insertion change to RecycleView Adapter
                ids.clear();
                collections.clear();
                getAllCollection();
                myWishlistCollectionRecycleAdapter.notifyItemInserted(collections.size() - 1);
                //Update collection count
                collectionCount.setText(String.valueOf(myWishlistCollectionRecycleAdapter.getItemCount()));
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }
}