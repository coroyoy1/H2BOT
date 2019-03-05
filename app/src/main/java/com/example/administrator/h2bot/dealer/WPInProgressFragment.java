package com.example.administrator.h2bot.dealer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.WPInProgressOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSCompleterdOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionDetailFileModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
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

public class WPInProgressFragment extends Fragment implements WPInProgressAdapter.OnItemClickListener{
    RecyclerView recyclerView;

    private WPInProgressOrdersAdapter POAdapter;
    private List<OrderModel> uploadPO;
    public WPInProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wpin_progress, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uploadPO = new ArrayList<>();


//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
//        reference.child(firebaseUser.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
//                        if(merchantCustomerFile != null)
//                        {
//                            String merchantId = merchantCustomerFile.getStation_id();
//                            String customerId = merchantCustomerFile.getCustomer_id();
//                            String status = merchantCustomerFile.getStatus();
//                            if(status.equals("AC"))
//                            {
//                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_Order_File");
//                                reference1.child(customerId).child(merchantId)
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                for (DataSnapshot post : dataSnapshot.getChildren())
//                                                {
//                                                    OrderModel orderModel = post.getValue(OrderModel.class);
//                                                    if(orderModel != null)
//                                                    {
//                                                        if(orderModel.getOrder_station_id().equals(merchantId)
//                                                                && orderModel.getOrder_customer_id().equals(customerId)
//                                                                && orderModel.getOrder_status().equals("Completed"))
//                                                        {
//                                                            uploadPO.add(orderModel);
//                                                        }
//                                                    }
//                                                }
//                                                POAdapter = new WSInProgressOrdersAdapter(getActivity(), uploadPO);
//                                                recyclerView.setAdapter(POAdapter);
//                                                POAdapter.setOnItemClickListener(WSInProgressFragment.this::onItemClick);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                            }
//                        }
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });


//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
//                {
////                    if(postSnapshot.child())
//                    TransactionHeaderFileModel transactionHeaderFileModel = postSnapshot.getValue(TransactionHeaderFileModel.class);
//                    if(transactionHeaderFileModel.getMerchant_id().equals(firebaseUser.getUid())
//                            && transactionHeaderFileModel.getTrans_status().equals("In-Progress"))
//                    {
//                        uploadPO.add(transactionHeaderFileModel);
//                    }
//                }
//                POAdapter = new WPInProgressOrdersAdapter(getActivity(), uploadPO);
//                recyclerView.setAdapter(POAdapter);
//                POAdapter.setOnItemClickListener(WPInProgressFragment.this::onItemClick);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                showMessage("Data does not exists!");
//            }
//        });
        return view;
    }
    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(int position) {

    }
}
