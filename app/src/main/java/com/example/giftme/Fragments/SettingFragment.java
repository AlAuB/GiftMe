package com.example.giftme.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.giftme.Activities.FriendCollectionItems;
import com.example.giftme.Activities.MainActivity;
import com.example.giftme.Settings.FAQ;
import com.example.giftme.Settings.PrivacyPolicy;
import com.example.giftme.R;
import com.example.giftme.Helpers.SessionManager;
import com.example.giftme.Helpers.DataBaseHelper;
import com.example.giftme.Settings.Support;
import com.example.giftme.Settings.TermsUse;
import com.example.giftme.Settings.ThemeStore;
import com.example.giftme.Settings.User_Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SettingFragment extends Fragment implements View.OnClickListener {

    SignInButton signInButton;
    Button signOutButton;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    FirebaseAuth firebaseAuth;
    TextView settingUserNameTV;
    ImageView pfpIV;
    DataBaseHelper dataBaseHelper;
    SignStatusListener listener;
    ArrayList<String> collectionIds;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // initialize switch variable
        SwitchMaterial mode_switch = view.findViewById(R.id.switch0);
        TextView mode = view.findViewById(R.id.mode);
//        Objects.requireNonNull(getSupportActionBar()).setTitle("Light/Dark Mode Switch");

        // switch
        mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    mode.setText("Dark Mode");
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    mode.setText("Light Mode");
                }
            }
        });

        // set the pre theme when app starts
        boolean isNightModeOn = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        mode_switch.setChecked(isNightModeOn);

        if (isNightModeOn){
            mode.setText("Dark Mode");
        }else{
            mode.setText("Light Mode");
        }

        //initialize the views
        TextView policy = view.findViewById(R.id.privacy);
        policy.setOnClickListener(this);

        TextView terms = view.findViewById(R.id.term);
        terms.setOnClickListener(this);

        TextView faq = view.findViewById(R.id.FAQ);
        faq.setOnClickListener(this);

        TextView support = view.findViewById(R.id.support);
        support.setOnClickListener(this);

        pfpIV = view.findViewById(R.id.settings_profile_pic);
        settingUserNameTV = view.findViewById(R.id.settings_user_name);

        signInButton = view.findViewById(R.id.google_sign_in_button);
        signOutButton = view.findViewById(R.id.sign_out_button);

        if (SessionManager.getUserStatus(this.getContext())) {
            //user signed in
            signedInState();
        } else {
            signedOutState();
        }

        dataBaseHelper = new DataBaseHelper(this.getContext());
        collectionIds = new ArrayList<>();

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.privacy) {
            Intent intent3 = new Intent(getActivity(), PrivacyPolicy.class);
            startActivity(intent3);
        } else if (view.getId() == R.id.term) {
            Intent intent4 = new Intent(getActivity(), TermsUse.class);
            startActivity(intent4);
        } else if (view.getId() == R.id.FAQ) {
            Intent intent5 = new Intent(getActivity(), FAQ.class);
            startActivity(intent5);
        } else if (view.getId() == R.id.support) {
            Intent intent6 = new Intent(getActivity(), Support.class);
            startActivity(intent6);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        signInButton = view.findViewById(R.id.google_sign_in_button);
        signOutButton = view.findViewById(R.id.sign_out_button);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(v -> signIn());
        signOutButton.setOnClickListener(v -> signOut());

        super.onViewCreated(view, savedInstanceState);
    }


    private void getAllMyCollection() {
        Cursor cursor = dataBaseHelper.readCollectionTableAllData("COLLECTIONS");
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                collectionIds.add(cursor.getString(0));
//                //if this isn't the friend's wishlist
                if (cursor.getBlob(3) == null) {
                    collectionIds.add(cursor.getString(0));
                }
            }
        }
    }

    private void getAllCollection() {
        Cursor cursor = dataBaseHelper.readCollectionTableAllData("COLLECTIONS");
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                collectionIds.add(cursor.getString(0));
//                //if this isn't the friend's wishlist
//                if (cursor.getBlob(3) == null) {
//                    collectionIds.add(cursor.getString(0));
//                }
            }
        }
    }

    private void signOut() {
        GoogleSignIn.getClient(
                requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        //clear out local SQLite database---start
        getAllCollection();
        Log.d("COLLECTIONIDS", String.valueOf(collectionIds));
        for (String id: collectionIds){
            dataBaseHelper.deleteCollectionSQL(id);
        }
        dataBaseHelper.deleteAll("COLLECTIONS");
        collectionIds.clear();
//        dataBaseHelper.deleteTable("COLLECTIONS");
        //clear out local SQLite database end---
        SessionManager.clearSession(getContext());
        listener.updateData(false);

        signedOutState();
    }

    private void signedOutState() {
        if (signInButton.getVisibility() == View.GONE) {
            signInButton.setVisibility(View.VISIBLE);
        }
        if (signOutButton.getVisibility() == View.VISIBLE) {
            signOutButton.setVisibility(View.GONE);
        }
        settingUserNameTV.setText(R.string.guest);
        pfpIV.setImageResource(R.drawable.anony_user);
    }

    private void signedInState() {
        //sign in button should be replaced by sign out button
        if (signInButton.getVisibility() == View.VISIBLE) {
            signInButton.setVisibility(View.GONE);
        }
        if (signOutButton.getVisibility() == View.GONE) {
            signOutButton.setVisibility(View.VISIBLE);
        }
        settingUserNameTV.setText(SessionManager.getUserName(getContext()));
        if (SessionManager.getUserStatus(getContext())) {
            Picasso.get().load(SessionManager.getUserPFP(getContext()))
                    .transform(new CropCircleTransformation())
                    .into(pfpIV);
        } else {
            pfpIV.setImageResource(R.drawable.anony_user);
        }
    }

    private void signIn() {
        Log.d("debugging::", "signIn");
        Intent intent = googleSignInClient.getSignInIntent();
        //need to grab collections info from firestore!
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Log.d("debugging::", "onActivityResult: " + requestCode);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("debugging::", "onActivityResult task finished");
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("debugging::", "onActivityResult account:" + account.getId());
                Log.d("debugging::", "onActivityResult account:" + account.getDisplayName());
                Log.d("debugging::", "onActivityResult account:" + account.getEmail());
                Log.d("debugging::", "onActivityResult account:" + account.getIdToken());
                // update the UI so the sign-in button disappears
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("debugging::", "task failed: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("debugging::", "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        Log.d("debugging::", "firebaseAuthWithGoogle: " + authCredential.getProvider());
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debugging::", "onComplete: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d("debugging::", "firebaseAuth: " + user.getEmail());
                            Log.d("debugging::", "firebaseAuth: " + user.getDisplayName());
                            Log.d("debugging::", "firebaseAuth: " + user.getIdToken(true));
                            // after connecting the account to firebase, pass the info to the next activity
                            // navigateToSecondActivity();

                            SessionManager.setSession(getContext(), user.getEmail(), user.getDisplayName(), user.getPhotoUrl().toString());
                            settingUserNameTV.setText(SessionManager.getUserName(getContext()));
                            
                            dataBaseHelper.checkUserExists(user.getEmail(), new DataBaseHelper.UserExists() {
                                public void onCallback(boolean exists) {
                                    if (exists) {
                                        Log.d("debugging::", "user exists");
                                        // if the user already exists in the database, then just update the user's email
                                        dataBaseHelper.setUserEmail(user.getEmail());

                                        //gets Collections from User
                                        dataBaseHelper.getCollectionsFromUser(user.getEmail());
                                        getAllMyCollection();
                                        listener.updateData(true);
//                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    } else {
                                        Log.d("debugging::", "user does not exist");
                                        dataBaseHelper.createUser(user.getEmail(), user.getDisplayName(), user.getPhotoUrl().toString());
                                    }
                                    dataBaseHelper.setDeviceMessagingToken(user.getEmail());
                                    getAllMyCollection();
                                    listener.updateData(true);
                                }
                            });

                            if (!SessionManager.getUserPFP(getContext()).equals("")) {
                                Picasso.get().load(SessionManager.getUserPFP(getContext()))
                                        .transform(new CropCircleTransformation())
                                        .into(pfpIV);
                            }

                            if (signInButton.getVisibility() == View.VISIBLE) {
                                signInButton.setVisibility(View.GONE);
                            }
                            if (signOutButton.getVisibility() == View.GONE) {
                                signOutButton.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Log.d("debugging::", "firebaseAuth failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    public interface SignStatusListener {
        void updateData(boolean status);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignStatusListener)
            listener = (SignStatusListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}