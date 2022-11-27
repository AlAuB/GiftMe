package com.example.giftme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.giftme.R;
import com.example.giftme.Helpers.SessionManager;

public class NotificationFragment extends Fragment {

    TextView messageCountTV;
    TextView notifMessageTV;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        messageCountTV = view.findViewById(R.id.messg_count);
        notifMessageTV = view.findViewById(R.id.notif_message);

        if(SessionManager.getUserStatus(getContext())){
            //if user is logged in
            messageCountTV.setVisibility(View.VISIBLE);
            notifMessageTV.setText(R.string.notif_messg);
        }
        else{
            messageCountTV.setVisibility(View.GONE);
            notifMessageTV.setText(R.string.guest_notif_messg);
        }
        return view;
    }
}