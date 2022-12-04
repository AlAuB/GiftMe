package com.example.giftme.Fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.ItemsAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CompactViewFragment extends Fragment {

    View view;
    Context context;
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    DataBaseHelper dataBaseHelper;
    ItemsAdapter itemAdapter;
    ArrayList<Item> items;
    String collection_name;
    itemNumListener itemNumListener;
    Activity activity;

    public CompactViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_compact_view, container, false);
        recyclerView = view.findViewById(R.id.compact_view_recycler_view);
        actionButton = view.findViewById(R.id.compact_view_action);
        context = getContext();
        dataBaseHelper = new DataBaseHelper(context);
        items = new ArrayList<>();
        if (getArguments() != null) {
            collection_name = getArguments().getString("collection_name");
        }
        actionButton.setOnClickListener(view -> confirmDialog());
        getAllItems();
        itemAdapter = new ItemsAdapter(getActivity(), context, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);
        itemNumListener.updateItemNum(String.valueOf(itemAdapter.getItemCount()));

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        activity = getActivity();
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
            dataBaseHelper = new DataBaseHelper(context);
            dataBaseHelper.deleteItemInCollection(String.valueOf(items.get(position).getId()), collection_name);
            items.clear();
            getAllItems();
            itemAdapter.notifyItemRemoved(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context, R.color.pink))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(context, R.color.white))
                    .create().decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void getAllItems() {
        Cursor cursor = dataBaseHelper.selectAll(collection_name);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Item currentItem
                        = new Item(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                items.add(currentItem);
            }
            cursor.close();
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add New Item");
        View view = getLayoutInflater().inflate(R.layout.add_collection_alert_dialog, null);
        TextInputEditText input = view.findViewById(R.id.input);
        builder.setView(view);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            String itemName = Objects.requireNonNull(input.getText()).toString().trim();
            if (itemName.length() == 0 || itemName.length() > 30) {
                Toast.makeText(context, "Invalid collection name", Toast.LENGTH_LONG).show();
            } else {
                Item item = new Item();
                item.setName(itemName);
                //Add item to Collection
                dataBaseHelper.insertItemIntoCollection(collection_name, item);
                Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show();
                //Notify insertion change to RecycleView Adapter
                items.clear();
                getAllItems();
                itemAdapter.notifyItemInserted(items.size() - 1);
                //Update collection count
                itemNumListener.updateItemNum(String.valueOf(itemAdapter.getItemCount()));
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
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