package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
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
import java.util.Objects;

public class WSDashboard extends Fragment implements View.OnClickListener {

    private Button updateInfo, addProduct;
    private CardView currentIN, currentPEN, currentCOM;
    private TextView numCountPEN, numCountIN, numCountCOM;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private List<MerchantCustomerFile> merchantCustomerFileList;
    private List<OrderModel> orderModelList;

    private int countComppleted, countPending, countInProgress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_dashboard, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cardViews(view);
        onClickers();

        return view;
    }

    private void cardViews(View view)
    {
        //Button
        updateInfo = view.findViewById(R.id.updateStationInfo);
        addProduct = view.findViewById(R.id.addProduct);

        //CardView
        currentIN = view.findViewById(R.id.currentInProgress);
        currentPEN = view.findViewById(R.id.currentPending);
        currentCOM = view.findViewById(R.id.currentCompleted);

        //TextView
        numCountCOM = view.findViewById(R.id.zeroCOM);
        numCountIN = view.findViewById(R.id.zeroIN);
        numCountPEN = view.findViewById(R.id.zeroPEN);

        merchantCustomerFileList = new ArrayList<>();
        orderModelList = new ArrayList<>();

        getCustomerFile();
        getMerchantFile();
    }

    private void onClickers()
    {
        updateInfo.setOnClickListener(this);
        addProduct.setOnClickListener(this);
        currentCOM.setOnClickListener(this);
        currentIN.setOnClickListener(this);
        currentPEN.setOnClickListener(this);
    }

    //Valuelistener
    public void countChildren()
    {
        countComppleted = countPending = countInProgress = 0;
        if(merchantCustomerFileList.size() == 0 || orderModelList.size() == 0){
            return;
        }
        for(MerchantCustomerFile file : merchantCustomerFileList){
            for(OrderModel list : orderModelList){
                if(file.getStation_id().equalsIgnoreCase(firebaseUser.getUid()) && file.getCustomer_id().equalsIgnoreCase(list.getOrder_customer_id())){
                    if(list.getOrder_status().equalsIgnoreCase("Pending")){
                        countPending++;
                    }else if(list.getOrder_status().equalsIgnoreCase("Completed")
                    || list.getOrder_status().equalsIgnoreCase("Completed with Affiliate")){
                        countComppleted++;
                    }
                    else if (list.getOrder_status().equalsIgnoreCase("In-Progress")
                    || list.getOrder_status().equalsIgnoreCase("Dispatched")
                    || list.getOrder_status().equalsIgnoreCase("Dispatched by Affiliate")) {
                        countInProgress++;
                    }
                }
            }
        }
        numCountPEN.setText(countPending + "");
        numCountCOM.setText(countComppleted + "");
        numCountIN.setText(countInProgress + "");
    }

    public void getCustomerFile(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                            for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()){
                                OrderModel orderModel = dataSnapshot3.getValue(OrderModel.class);
                                if (orderModel.getOrder_merchant_id().equalsIgnoreCase(firebaseUser.getUid()))
                                {
                                    orderModelList.add(orderModel);
                                }
                            }
                        }
                    }
                    countChildren();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    public void getMerchantFile(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Merchant_File");
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot value : dataSnapshot.getChildren()){
                    MerchantCustomerFile merchantCustomerFile = value.getValue(MerchantCustomerFile.class);
                    merchantCustomerFileList.add(merchantCustomerFile);
                }
                countChildren();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //Card View Intent
    private void clickCompletedOrders(View v)
    {
        WSTransactionsFragment intent = new WSTransactionsFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Completed Orders");
    }

    private void clickPendingdOrders(View v)
    {
        WSPendingOrdersFragment intent = new WSPendingOrdersFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Pending Orders");
    }

    private void clickInProgressOrders(View v)
    {
        WSInProgressFragment intent = new WSInProgressFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("In-Progress");
    }

    //Button Intent
    private void clickUpdateStationInfo(View v)
    {
        WSBusinessInfoFragment intent = new WSBusinessInfoFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
    }

    private void clickAddProduct(View v)
    {
        WSProductAdd intent = new WSProductAdd();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateStationInfo:
                clickUpdateStationInfo(v);
                break;
            case R.id.addProduct:
                clickAddProduct(v);
                break;
            case R.id.currentCompleted:
                clickCompletedOrders(v);
                break;
            case R.id.currentPending:
                clickPendingdOrders(v);
                break;
            case R.id.currentInProgress:
                clickInProgressOrders(v);
                break;
        }
    }
}
