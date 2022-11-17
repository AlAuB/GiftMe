package com.example.giftme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SettingFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        TextView profile = (TextView) view.findViewById(R.id.profile);
        profile.setOnClickListener(this);

        TextView themes = (TextView) view.findViewById(R.id.themestore);
        themes.setOnClickListener(this);

        TextView policy = (TextView) view.findViewById(R.id.privacy);
        policy.setOnClickListener(this);

        TextView terms = (TextView) view.findViewById(R.id.term);
        terms.setOnClickListener(this);

        TextView faq = (TextView) view.findViewById(R.id.FAQ);
        faq.setOnClickListener(this);

        TextView support = (TextView) view.findViewById(R.id.support);
        support.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.profile) {
            Intent intent = new Intent(getActivity(), user_profile.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.themestore){
            Intent intent2 = new Intent(getActivity(), ThemeStore.class);
            startActivity(intent2);
        }
        else if (view.getId() == R.id.privacy){
            Intent intent3 = new Intent(getActivity(), PrivacyPolicy.class);
            startActivity(intent3);
        }
        else if (view.getId() == R.id.term){
            Intent intent4 = new Intent(getActivity(), TermsUse.class);
            startActivity(intent4);
        }
        else if (view.getId() == R.id.FAQ){
            Intent intent5 = new Intent(getActivity(), FAQ.class);
            startActivity(intent5);
        }
        else if (view.getId() == R.id.support){
            Intent intent6 = new Intent(getActivity(), Support.class);
            startActivity(intent6);
        }
    }
}