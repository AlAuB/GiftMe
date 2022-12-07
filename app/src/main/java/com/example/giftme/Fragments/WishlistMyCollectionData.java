package com.example.giftme.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
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

import com.example.giftme.Adapters.MyWishlistCollectionRecycleAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WishlistMyCollectionData extends Fragment {

    View view;
    Activity activity;
    Context context;
    ImageView emptyImage;
    TextView collectionCount, emptyText, collectionText;
    RecyclerView recyclerView;
    ExtendedFloatingActionButton floatingActionButton;
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
        collectionText = view.findViewById(R.id.collection_text);
        recyclerView = view.findViewById(R.id.recycleView);
        floatingActionButton = view.findViewById(R.id.action);
        emptyText = view.findViewById(R.id.empty_text);
        emptyImage = view.findViewById(R.id.empty_icon);
        ids = new ArrayList<>();
        collections = new ArrayList<>();
        activity = getActivity();
        dataBaseHelper = new DataBaseHelper(this.getContext());
        collectionCount.setText(String.valueOf(collections.size()));
        getAllCollection();
        myWishlistCollectionRecycleAdapter
                = new MyWishlistCollectionRecycleAdapter(activity, this.getContext(), ids, collections);
        recyclerView.setAdapter(myWishlistCollectionRecycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        //if user is logged in, then show their collections
        if (SessionManager.getUserStatus(context)) {
            signedInState();
        } else {
            signedOutState();
        }
        floatingActionButton.setOnClickListener(view -> confirmDialog());
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();
            dataBaseHelper.deleteData(ids.get(position), "COLLECTIONS");
            dataBaseHelper.deleteTable(collections.get(position));
            TextView textView = activity.findViewById(R.id.collectionCount);
            ids.clear();
            collections.clear();
            getAllCollection();
            textView.setText(String.valueOf(myWishlistCollectionRecycleAdapter.getItemCount()));
            myWishlistCollectionRecycleAdapter.notifyItemRemoved(position);
            checkEmptyUI();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(context,R.color.white))
                    .create().decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void checkEmptyUI() {
        if (collections.isEmpty()) {
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
    }

    public void signedInState() {
        recyclerView.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
        collectionCount.setVisibility(View.VISIBLE);
        collectionText.setVisibility(View.VISIBLE);
        collectionCount.setText(String.valueOf(collections.size()));
        checkEmptyUI();
    }

    @SuppressLint("SetTextI18n")
    public void signedOutState() {
        recyclerView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
        //Force to set Visible
        emptyImage.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        emptyText.setText("Please Sign-In");
        collectionCount.setVisibility(View.GONE);
        collectionText.setVisibility(View.GONE);
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
        TextInputEditText input = view.findViewById(R.id.input);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String insert = Objects.requireNonNull(input.getText()).toString().trim();
            if (collections.contains(insert) || insert.length() == 0 || insert.length() > 30) {
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
                checkEmptyUI();
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }
}