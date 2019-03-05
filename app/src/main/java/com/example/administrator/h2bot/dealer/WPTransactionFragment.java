package com.example.administrator.h2bot.dealer;

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
import com.example.administrator.h2bot.models.OrderModel;
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
    private DatabaseReference mDatabaseRef2;
    private RecyclerView.LayoutManager mLayoutManager;
    private WPCompletedOrdersAdapter mAdapter;
    private List<OrderModel> mUploads;


    public WPTransactionFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wptransaction, container, false);
        mUploads = new ArrayList<OrderModel>();
        recyclerView = view.findViewById(R.id.recyclerView);

        mUploads = new ArrayList<OrderModel>();
        //mUploads2 = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currendId = currentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Order_File");
        //mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){


                }
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    for(DataSnapshot postSnapshot2 : dataSnapshot.getChildren()) {
//                        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Order_File").child(currendId);
//                        mDatabaseRef2.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                OrderModel orderModel = postSnapshot2.getValue(OrderModel.class);
//                                if (orderModel.getOrder_station_id().equals(currendId) && orderModel.getOrder_status().equals("Completed"))
//                                    // TransactionDetailFileModel transactionDetail = postSnapshot.getValue(TransactionDetailFileModel.class);
//                                    mUploads.add(orderModel);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
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
