package com.example.administrator.h2bot.customer;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.absampletestphase.RatingModel;
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionNoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.util.BackOffUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerAllOrdersFragment extends Fragment {

    String getStation_Id = CustomerStationAdapter.station_id;
    private RecyclerView recyclerView;
    private CustomerAllOrdersAdapter customerAllOrdersAdapter;
    private ArrayList<TransactionNoModel> transactionList;
    private ArrayList<OrderFileModel> orderList;
    private FirebaseUser firebaseUser;
    private DatabaseReference orderFileRef;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //Marvel Rate
    private Button laterButton, submitButton;
    private EditText additonalComment;
    private RatingBar ratingBar;

    public CustomerAllOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.customer_fragment_all_orders, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        transactionList = new ArrayList<TransactionNoModel>();
        orderList = new ArrayList<OrderFileModel>();
        getDataForRecycler();
        return view;
    }

    public void getDataForRecycler()
    {
        orderFileRef = db.getReference("Customer_File").child(firebaseUser.getUid()).child(getStation_Id);
        orderFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                transactionList.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    TransactionNoModel transNo = data.getValue(TransactionNoModel.class);
                    OrderFileModel order = data.getValue(OrderFileModel.class);
                    transactionList.add(transNo);
                    orderList.add(order);
                    transNo.setTransOrderNo(data.getKey());
                    order.setOrderAddress(data.child("order_address").getValue(String.class));
                    order.setOrderCustomerId(data.child("order_customer_id").getValue(String.class));
                    order.setOrderDateIssued(data.child("order_date_issued").getValue(String.class));
                    order.setOrderDeliveryDate(data.child("order_date").getValue(String.class));
                    order.setOrderMethod(data.child("order_method").getValue(String.class));
                    order.setOrderMerchantId(data.child("order_merchant_id").getValue(String.class));
                    order.setOrderNo(data.child("order_no").getValue(String.class));
                    order.setOrderPricePerGallon(data.child("order_price_per_gallon").getValue(String.class));
                    order.setOrderQty(data.child("order_qty").getValue(String.class));
                    order.setOrderServiceType(data.child("order_service_type").getValue(String.class));
                    order.setOrderStatus(data.child("order_status").getValue(String.class));
                    order.setOrderTotalAmt(data.child("order_total_amt").getValue(String.class));
                    order.setOrderWaterType(data.child("order_water_type").getValue(String.class));
                }
                customerAllOrdersAdapter = new CustomerAllOrdersAdapter(getActivity(), transactionList, orderList);
                recyclerView.setAdapter(customerAllOrdersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
