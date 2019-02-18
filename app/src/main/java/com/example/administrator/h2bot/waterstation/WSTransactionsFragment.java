package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WPInProgressAdapter;
import com.example.administrator.h2bot.WPInProgressFragment;
import com.example.administrator.h2bot.adapter.WSCompleterdOrdersAdapter;
import com.example.administrator.h2bot.models.wptransactionheaderfilemodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WSTransactionsFragment extends Fragment implements WSCompleterdOrdersAdapter.OnItemClickListener{
    @Nullable
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    private DatabaseReference mDatabaseRef;
    private RecyclerView.LayoutManager mLayoutManager;
    private WSCompleterdOrdersAdapter mAdapter;
    private List<wptransactionheaderfilemodel> mUploads;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_transactions, container, false);
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
                    wptransactionheaderfilemodel transactionHeader = postSnapshot.getValue(wptransactionheaderfilemodel.class);
                    if(transactionHeader.getMerchant_id().equals(currendId) && transactionHeader.getTrans_status().equals("Completed"))
                    // wptransactiondetailfilemodel transactionDetail = postSnapshot.getValue(wptransactiondetailfilemodel.class);
                    mUploads.add(transactionHeader);

                }
                mAdapter = new WSCompleterdOrdersAdapter(getActivity(), mUploads);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(WSTransactionsFragment.this::onItemClick);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;

    }


    @Override
    public void onItemClick(int position) {

    }
}
