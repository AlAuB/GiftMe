package com.example.giftme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.giftme.Settings.FAQ;
import com.example.giftme.Settings.PrivacyPolicy;
import com.example.giftme.R;
import com.example.giftme.Helpers.SessionManager;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private SignInButton signInButton;
    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private FirebaseAuth firebaseAuth;
    private TextView settingUserNameTV;
    private ImageView pfpIV;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //initialize the views
        TextView profile = view.findViewById(R.id.profile);
        profile.setOnClickListener(this);

        TextView themes = view.findViewById(R.id.themestore);
        themes.setOnClickListener(this);

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

        if(SessionManager.getUserStatus(this.getContext())){
            //user signed in
            signedInState();
        }
        else{
            signedOutState();
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.profile) {
            Intent intent = new Intent(getActivity(), User_Profile.class);
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

    private void signOut(){
        GoogleSignIn.getClient(
                requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();
        SessionManager.clearSession(getContext());
        signedOutState();
    }

    private void signedOutState(){
        if (signInButton.getVisibility()==View.GONE) {
            signInButton.setVisibility(View.VISIBLE);
        }
        if (signOutButton.getVisibility()==View.VISIBLE) {
            signOutButton.setVisibility(View.GONE);
        }
        settingUserNameTV.setText(R.string.guest);
        pfpIV.setImageResource(R.drawable.anony_user);
    }

    private void signedInState(){
        //sign in button should be replaced by sign out button
        if (signInButton.getVisibility()==View.VISIBLE) {
            signInButton.setVisibility(View.GONE);
        }
        if (signOutButton.getVisibility()==View.GONE) {
            signOutButton.setVisibility(View.VISIBLE);
        }
        settingUserNameTV.setText(SessionManager.getUserName(getContext()));
        if (SessionManager.getUserStatus(getContext())) {
            Picasso.get().load(SessionManager.getUserPFP(getContext())).into(pfpIV);
        } else{
            pfpIV.setImageResource(R.drawable.anony_user);
        }


    }
    private void signIn() {
        Log.d("debugging::", "signIn");
        Intent intent = googleSignInClient.getSignInIntent();
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

                            if (! SessionManager.getUserPFP(getContext()).equals("")) {
                                Picasso.get().load(SessionManager.getUserPFP(getContext())).into(pfpIV);
                            }

                            if (signInButton.getVisibility()==View.VISIBLE) {
                                signInButton.setVisibility(View.GONE);
                            }
                            if (signOutButton.getVisibility()==View.GONE) {
                                signOutButton.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("debugging::", "firebaseAuth failed: " + task.getException().getMessage());
                        }
                    }
                });

            // just an example of how to navigate to another activity
    //    private void navigateToSecondActivity() {
    //        Intent intent = new Intent(getActivity(), SecondActivity.class);
    //        startActivity(intent);
    //    }

        // inside the second activity, you can call the singed in account by following:
        // GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // account.getId(); account.getDisplayName(); account.getEmail(); account.getIdToken();
    }
}