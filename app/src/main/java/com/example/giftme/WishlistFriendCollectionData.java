package com.example.giftme;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishlistFriendCollectionData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishlistFriendCollectionData extends Fragment {

    View view1;
    Context context1;
    TextView collectionCount1;
    RecyclerView recyclerView1;
    FloatingActionButton floatingActionButton1;
    FrWishlistCollectionRecycleAdapter FrWishlistCollectionRecycleAdapter;

    private static final String COLLECTION_TABLE_NAME = "COLLECTIONS";

    ArrayList<String> ids;
    ArrayList<String> collections;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public WishlistFriendCollectionData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WishlistFriendCollectionData.
     */
    // TODO: Rename and change types and number of parameters
    public static WishlistFriendCollectionData newInstance(String param1, String param2) {
        WishlistFriendCollectionData fragment = new WishlistFriendCollectionData();
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
        view1 = inflater.inflate(R.layout.fragment_wishlist_friend_collection_data, container, false);
        context1 = this.getContext();
        collectionCount1 = view1.findViewById(R.id.collectionCount1);
        recyclerView1 = view1.findViewById(R.id.recycleView1);
        floatingActionButton1 = view1.findViewById(R.id.action1);
        ids = new ArrayList<>();
        collections = new ArrayList<>();
        recyclerView1.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView1.setHasFixedSize(true);
        FrWishlistCollectionRecycleAdapter = new FrWishlistCollectionRecycleAdapter(this.getContext(), ids, collections);
        recyclerView1.setAdapter(FrWishlistCollectionRecycleAdapter);

        return view1;
    }
}