package com.example.giftme;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SignInButton signInButton;
    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private FirebaseAuth firebaseAuth;


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
        else if(view.getId() == R.id.sign_out_button){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    // ...
                    Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        signInButton = (SignInButton) getView().findViewById(R.id.google_sign_in_button);
        signOutButton = getView().findViewById(R.id.sign_out_button);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void signIn() {
        Log.d("debugging::", "signIn");
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 100);
//        if (signInButton.getVisibility()==View.VISIBLE) {
//            signInButton.setVisibility(View.GONE);
//        }
//        if (signOutButton.getVisibility()==View.GONE) {
//            signOutButton.setVisibility(View.VISIBLE);
//        }
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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