package com.example.administrator.h2bot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.models.TransactionDetailFileModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class WSCompletedOrdersInformationFragment extends Fragment {

    TextView orderNumberTextView,customerNameTextView,contactNumberTextView,waterTypeTextView,pricePerGallonTextView,serviceTextView,
    addressTextView,deliveryFeeTextView,totalPriceTextView,itemQuantityTextView;
    private DatabaseReference transHeader, transDetail, userFile;
    private DatabaseReference mDatabase;
    String customerID;
    String transaction;
    String customerName;
    String contactNo;
    String waterType;
    String address;
    String itemQuantity;
    String deliveryFee;
    String pricePerGallon ;
    String service;
    String totalPrice;
    private List<TransactionHeaderFileModel> mUploads;
    private List<TransactionDetailFileModel> mUploads2;
    private List<UserFile> mUploads3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_completed_orders_information, container, false);

        //TextView
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);
        customerNameTextView = view.findViewById(R.id.customerNameTextView);
        contactNumberTextView = view.findViewById(R.id.contactNumberTextView);
        waterTypeTextView = view.findViewById(R.id.waterTypeTextView);
        pricePerGallonTextView = view.findViewById(R.id.pricePerGallonTextView);
        serviceTextView = view.findViewById(R.id.serviceTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        deliveryFeeTextView = view.findViewById(R.id.deliveryFeeTextView);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        itemQuantityTextView = view.findViewById(R.id.itemQuantityTextView);

        mUploads = new ArrayList<>();
        mUploads2 = new ArrayList<>();
        mUploads3= new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             transaction = bundle.getString("transactionno");
             customerName = bundle.getString("customername");

        }

        transHeader = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        transDetail = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
        userFile = FirebaseDatabase.getInstance().getReference("User_File");

        transHeader.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(postSnapshot.child("trans_no").getValue(String.class).equals(transaction)) {
                        customerID = postSnapshot.child("customer_id").getValue(String.class);
                        orderNumberTextView.setText(postSnapshot.child("trans_no").getValue(String.class));
                        serviceTextView.setText(postSnapshot.child("trans_delivery_service").getValue(String.class));
                        deliveryFeeTextView.setText(postSnapshot.child("trans_total_delivery_fee").getValue(String.class));
                        totalPriceTextView.setText(postSnapshot.child("trans_total_amount").getValue(String.class));
                        itemQuantityTextView.setText(postSnapshot.child("trans_total_no_of_gallons").getValue(String.class));
                        //mUploads.add(transHeader);
                    }
                }
               transDetail.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                           if(postSnapshot.child("trans_no").getValue(String.class).equals(transaction)) {
                               waterTypeTextView.setText(postSnapshot.child("trans_water_type").getValue(String.class));
                               pricePerGallonTextView.setText(postSnapshot.child("trans_price_per_gallon").getValue(String.class));
                           }
                       }
                        userFile.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    UserFile userFile = postSnapshot.getValue(UserFile.class);
                                    Toast.makeText(getActivity(), ""+transaction, Toast.LENGTH_SHORT).show();
                                    if(userFile.getUser_getUID().equals(customerID)){
                                        customerNameTextView.setText(postSnapshot.child("user_firtname").getValue(String.class)+" "+postSnapshot.child("user_lastname").getValue(String.class));
                                        addressTextView.setText(postSnapshot.child("user_address").getValue(String.class));
                                        contactNumberTextView.setText(postSnapshot.child("user_phone_no").getValue(String.class));
                                    }
                                }
                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
