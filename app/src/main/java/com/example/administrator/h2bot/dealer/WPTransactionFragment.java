package com.example.administrator.h2bot.dealer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.WPCompletedOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSCompleterdOrdersAdapter;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.waterstation.WSTransactionsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WPTransactionFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    private DatabaseReference mDatabaseRef;
    private RecyclerView.LayoutManager mLayoutManager;
    private WPCompletedOrdersAdapter mAdapter;
    private List<TransactionHeaderFileModel> mUploads;


    public WPTransactionFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wptransaction, container, false);
        mUploads = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        mUploads = new ArrayList<>();
        //mUploads2 = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currendId = currentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        //mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TransactionHeaderFileModel transactionHeader = postSnapshot.getValue(TransactionHeaderFileModel.class);
                    if(transactionHeader.getMerchant_id().equals(currendId) && transactionHeader.getTrans_status().equals("Completed"))
                        // TransactionDetailFileModel transactionDetail = postSnapshot.getValue(TransactionDetailFileModel.class);
                        mUploads.add(transactionHeader);
                }
                mAdapter = new WPCompletedOrdersAdapter(getActivity(), mUploads);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(WPTransactionFragment.this::onItemClick);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void onItemClick(int i) {
    }
}
