package com.example.giftme.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.giftme.R;
import com.example.giftme.Helpers.SessionManager;

public class NotificationFragment extends Fragment {

    TextView messageCountTV, notifyMessageTV, emptyText;
    ImageView emptyImage;

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
        messageCountTV = view.findViewById(R.id.message_count);
        notifyMessageTV = view.findViewById(R.id.notify_message);

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