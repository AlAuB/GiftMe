package com.example.giftme.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Activities.AddNewItemManually;
import com.example.giftme.Adapters.MyCollectionItemsAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DetailViewFragment extends Fragment {

    View view;
    Context context;
    RecyclerView recyclerView;
    ExtendedFloatingActionButton actionButton;
    MyCollectionItemsAdapter myCollectionItemsAdapter;
    DataBaseHelper dataBaseHelper;
    ArrayList<Item> items;
    String collection_name;
    itemNumListener itemNumListener;
    ImageView emptyImage;
    TextView emptyText;

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
        emptyImage = view.findViewById(R.id.detail_view_empty_icon);
        emptyText = view.findViewById(R.id.detail_view_empty_text);
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
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        itemNumListener.detailedViewUpdateItemNum(String.valueOf(myCollectionItemsAdapter.getItemCount()));
        checkEmptyUI();
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
            dataBaseHelper.deleteDataItem(String.valueOf(items.get(position).getId()), collection_name);
            items.clear();
            getAllItems();
            myCollectionItemsAdapter.notifyItemRemoved(position);
            itemNumListener.detailedViewUpdateItemNum(String.valueOf(myCollectionItemsAdapter.getItemCount()));
            checkEmptyUI();
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

    private void checkEmptyUI() {
        if (items.isEmpty()) {
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
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
                        cursor.getString(7)
                );
                items.add(currentItem);
            }
            cursor.close();
        }
    }

    public interface itemNumListener {
        void detailedViewUpdateItemNum(String count);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof itemNumListener)
            itemNumListener = (itemNumListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemNumListener = null;
    }
}