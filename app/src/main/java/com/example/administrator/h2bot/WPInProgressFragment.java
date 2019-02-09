package com.example.administrator.h2bot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WPInProgressFragment extends Fragment implements WPInProgressAdapter.OnItemClickListener{
    RecyclerView recyclerView;

    public static final String EXTRA_transactionNo = "transactionNo";
    private WPInProgressAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabaseRef;
    private List<WPTransactionModel> mUploads;
    public WPInProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wpin_progress, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        mUploads = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("dealerTransactions");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    WPTransactionModel transaction = postSnapshot.getValue(WPTransactionModel.class);
                    mUploads.add(transaction);
                }
                mAdapter = new WPInProgressAdapter(getActivity(), mUploads);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(WPInProgressFragment.this::onItemClick);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getActivity(), OrderDetailsConfirmOrDecline.class);
        WPTransactionModel clickedItem = mUploads.get(position);

        detailIntent.putExtra(EXTRA_transactionNo, clickedItem.getTransactionNo());

        startActivity(detailIntent);
    }
}
