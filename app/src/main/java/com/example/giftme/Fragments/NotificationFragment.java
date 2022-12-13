package com.example.giftme.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.Adapters.ItemsAdapter;
import com.example.giftme.Adapters.NotificationsAdapter;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Helpers.Item;
import com.example.giftme.R;
import com.example.giftme.Helpers.SessionManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    TextView messageCountTV, notifyMessageTV, emptyText;
    ImageView emptyImage;

    View view;
    Context context;
    Activity activity;
    RecyclerView recyclerView;
    DataBaseHelper dataBaseHelper;
    NotificationsAdapter notificationsAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        emptyImage = view.findViewById(R.id.notification_empty_icon);
        emptyText = view.findViewById(R.id.notification_empty_text);
        messageCountTV = view.findViewById(R.id.notification_count);
        notifyMessageTV = view.findViewById(R.id.notify_message);

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recycleViewNotification);
        context = getContext();
        dataBaseHelper = new DataBaseHelper(context);
        activity = getActivity();

        notificationsAdapter = new NotificationsAdapter(activity, context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(notificationsAdapter);
//        itemNumListener.compactViewUpdateItemNum(String.valueOf(itemAdapter.getItemCount()));

        if(SessionManager.getUserStatus(getContext())){
            //if user is logged in
            messageCountTV.setVisibility(View.VISIBLE);
            notifyMessageTV.setVisibility(View.VISIBLE);
        }
        else{
            messageCountTV.setVisibility(View.GONE);
            notifyMessageTV.setVisibility(View.GONE);
            emptyText.setText("Please Sign In");
        }
        return view;
    }
}