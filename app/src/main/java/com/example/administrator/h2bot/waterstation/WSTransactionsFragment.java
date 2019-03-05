package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.WSCompleterdOrdersAdapter;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WSTransactionsFragment extends Fragment implements WSCompleterdOrdersAdapter.OnItemClickListener{
    @Nullable
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    private DatabaseReference mDatabaseRef, mDatabaseRef2;
    private RecyclerView.LayoutManager mLayoutManager;
    private WSCompleterdOrdersAdapter mAdapter;
    private List<OrderModel> mUploads;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_transactions, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        mUploads = new ArrayList<>();
        //mUploads2 = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currendId = currentUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
        reference.child(currendId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
                            if(merchantCustomerFile != null)
                            {
                                String merchantId = merchantCustomerFile.getStation_id();
                                String customerId = merchantCustomerFile.getCustomer_id();
                                String status = merchantCustomerFile.getStatus();
                                if(status.equals("AC"))
                                {
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_Order_File");
                                    reference1.child(customerId).child(merchantId)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot post : dataSnapshot.getChildren())
                                                    {
                                                        OrderModel orderModel = post.getValue(OrderModel.class);
                                                        if(orderModel != null)
                                                        {
                                                            if(orderModel.getOrder_station_id().equals(merchantId)
                                                                    && orderModel.getOrder_customer_id().equals(customerId)
                                                                    && orderModel.getOrder_status().equals("Completed"))
                                                            {
                                                                mUploads.add(orderModel);
                                                            }
                                                        }
                                                    }
                                                    mAdapter = new WSCompleterdOrdersAdapter(getActivity(), mUploads);
                                                    recyclerView.setAdapter(mAdapter);
                                                    mAdapter.setOnItemClickListener(WSTransactionsFragment.this::onItemClick);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//        mDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Log.d("Orderno","Hahahys");
//                        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Order_File").child(currendId);
//                        mDatabaseRef2.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot postSnapshot2 : dataSnapshot.getChildren()) {
//                                    OrderModel orderModel = postSnapshot2.getValue(OrderModel.class);
//                                    Log.d("Orderno",""+orderModel.getOrder_no());
//                                    if (orderModel.getOrder_station_id().equals(currendId) && orderModel.getOrder_status().equals("Completed"))
//                                        // TransactionDetailFileModel transactionDetail = postSnapshot.getValue(TransactionDetailFileModel.class);
//                                        mUploads.add(orderModel);
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                }
//                mAdapter = new WSCompleterdOrdersAdapter(getActivity(), mUploads);
//                recyclerView.setAdapter(mAdapter);
//                mAdapter.setOnItemClickListener(WSTransactionsFragment.this::onItemClick);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        return view;

    }


    @Override
    public void onItemClick(int position) {

    }
}
