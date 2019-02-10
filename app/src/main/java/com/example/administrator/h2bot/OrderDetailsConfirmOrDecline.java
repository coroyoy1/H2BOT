package com.example.administrator.h2bot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.administrator.h2bot.WPInProgressFragment.EXTRA_transactionNo;

public class OrderDetailsConfirmOrDecline extends AppCompatActivity {

    TextView orderNumberTextView,customerNameTextView,contactNumberTextView,waterTypeTextView,pricePerGallonTextView,serviceTextView,
    addressTextView,deliveryFeeTextView,totalPriceTextView,itemQuantityTextView;
    Button viewLocationButton,declineButton,confirmButton;

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_confirm_or_decline);

        //TextView
        orderNumberTextView = findViewById(R.id.orderNumberTextView);
        customerNameTextView = findViewById(R.id.customerNameTextView);
        contactNumberTextView = findViewById(R.id.contactNumberTextView);
        waterTypeTextView = findViewById(R.id.waterTypeTextView);
        pricePerGallonTextView = findViewById(R.id.pricePerGallonTextView);
        serviceTextView = findViewById(R.id.serviceTextView);
        addressTextView = findViewById(R.id.addressTextView);
        deliveryFeeTextView = findViewById(R.id.deliveryFeeTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        itemQuantityTextView = findViewById(R.id.itemQuantityTextView);

        //Button
        viewLocationButton = findViewById(R.id.viewLocationButton);
        declineButton = findViewById(R.id.declineButton);
        confirmButton = findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_transactionNo);

        //database reference pointing to root of database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("dealerTransactions");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderNumberTextView.setText(dataSnapshot.child("mOrderNo").getValue(String.class));
                customerNameTextView.setText(dataSnapshot.child("mCustomerName").getValue(String.class));
                addressTextView.setText(dataSnapshot.child("mAddress").getValue(String.class));
                contactNumberTextView.setText(dataSnapshot.child("mContactNo").getValue(String.class));
                waterTypeTextView.setText(dataSnapshot.child("mWaterType").getValue(String.class));
                pricePerGallonTextView.setText(dataSnapshot.child("mPricePerGallon").getValue(String.class));
                serviceTextView.setText(dataSnapshot.child("mService").getValue(String.class));
                deliveryFeeTextView.setText(dataSnapshot.child("mDeliveryFee").getValue(String.class));
                totalPriceTextView.setText(dataSnapshot.child("mTotalPrice").getValue(String.class));
                itemQuantityTextView.setText(dataSnapshot.child("mItemQuantity").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
