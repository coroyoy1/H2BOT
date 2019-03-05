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
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WSInProgressFragment extends Fragment implements WSInProgressOrdersAdapter.OnItemClickListener{
    private RecyclerView recyclerView;
    private WSInProgressOrdersAdapter POAdapter;
    private List<OrderModel> uploadPO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ws_inprogress, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uploadPO = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
        reference.child(firebaseUser.getUid())
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
                                reference1.child(customerId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot post : dataSnapshot.getChildren())
                                                {
                                                    OrderModel orderModel = post.child(merchantId).getValue(OrderModel.class);
                                                    if(orderModel != null)
                                                    {
                                                        if(orderModel.getOrder_station_id().equals(merchantId)
                                                                && orderModel.getOrder_customer_id().equals(customerId)
                                                                && orderModel.getOrder_status().equals("In-Progress"))
                                                        {
                                                            uploadPO.add(orderModel);
                                                        }
                                                    }
                                                }
                                                POAdapter = new WSInProgressOrdersAdapter(getActivity(), uploadPO);
                                                recyclerView.setAdapter(POAdapter);
                                                POAdapter.setOnItemClickListener(WSInProgressFragment.this::onItemClick);
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
        return view;
    }
    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(int position) {

    }
}
