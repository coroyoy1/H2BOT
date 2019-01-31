package com.example.administrator.h2bot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.DatabaseReference;

public class WSProductListFragment extends Fragment {

    private RecyclerView recyclerViewFragWS;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<WSProductListSetterGetter, WSProductListFragment.ListHolderWS> RVAdapter;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewopen =  inflater.inflate(R.layout.fragment_ws_productlist, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users Items").child(FirebaseAuth.getInstance().getCurrentUser().toString());
        mDatabase.keepSynced(true);
        recyclerViewFragWS = (RecyclerView) viewopen.findViewById(R.id.recyclerViewWS);
        DatabaseReference personRef = FirebaseDatabase.getInstance().getReference().child("User Items").child(FirebaseAuth.getInstance().getCurrentUser().toString());
        Query personQuery = personRef.orderByKey();
        recyclerViewFragWS.hasFixedSize();
        RecyclerView.LayoutManager RVManage = new LinearLayoutManager(getContext());
        recyclerViewFragWS.setLayoutManager(RVManage);
        return viewopen;
    }
    public static class ListHolderWS extends RecyclerView.ViewHolder
    {
        View mView;
        public ListHolderWS(View itemView)
        {
            super(itemView);
            mView = itemView;
        }
        public void setItemName(String itemName)
        {
            TextView post_item_name = (TextView) mView.findViewById(R.id.itemNameDisplayWS);
        }
    }
}
