package com.example.giftme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DetailViewFragment extends Fragment {

    View view;
    Context context;
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    MyCollectionItemsAdapter myCollectionItemsAdapter;
    DataBaseHelper dataBaseHelper;
    ArrayList<String> name, price, imagePath, date;
    ArrayList<Integer> favorite;
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
        if (getArguments() != null) {
            collection_name = getArguments().getString("collection_name");
        }
        actionButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddNewItemManually.class);
            intent.putExtra("collection_name", collection_name);
            startActivity(intent);
        });
        name = new ArrayList<>();
        name.add("iPhone 14 Pro Max");
        price = new ArrayList<>();
        price.add("$999");
        imagePath = new ArrayList<>();
        date = new ArrayList<>();
        date.add("2022-11-20");
        favorite = new ArrayList<>();
        myCollectionItemsAdapter = new MyCollectionItemsAdapter(getActivity(), context, name,
                price, imagePath, favorite, date);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myCollectionItemsAdapter);
        return view;
    }
}