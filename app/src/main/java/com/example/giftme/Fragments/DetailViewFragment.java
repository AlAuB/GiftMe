package com.example.giftme.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.giftme.Adapters.MyCollectionItemsAdapter;
import com.example.giftme.Activities.AddNewItemManually;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DetailViewFragment extends Fragment {

    View view;
    Context context;
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    MyCollectionItemsAdapter myCollectionItemsAdapter;
    DataBaseHelper dataBaseHelper;
    ArrayList<Item> items;
    String collection_name;

    public DetailViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail_view, container, false);
        context = getContext();
        recyclerView = view.findViewById(R.id.recycler_view_detail_view);
        actionButton = view.findViewById(R.id.detailed_view_action);
        dataBaseHelper = new DataBaseHelper(context);
        items = new ArrayList<>();
        if (getArguments() != null) {
            collection_name = getArguments().getString("collection_name");
        }
        actionButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddNewItemManually.class);
            intent.putExtra("collection_name", collection_name);
            startActivity(intent);
        });
        getAllItems();
        myCollectionItemsAdapter = new MyCollectionItemsAdapter(getActivity(), context, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myCollectionItemsAdapter);
        return view;
    }

    private void getAllItems() {
        Cursor cursor = dataBaseHelper.selectAll(collection_name);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Item currentItem
                        = new Item(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );
                items.add(currentItem);
            }
            cursor.close();
        }
    }
}