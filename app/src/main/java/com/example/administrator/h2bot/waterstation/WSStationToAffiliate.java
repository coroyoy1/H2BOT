package com.example.administrator.h2bot.waterstation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.deliveryman.DMCompletedAcception;
import com.example.administrator.h2bot.models.AffiliateStationOrderModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class WSStationToAffiliate extends Fragment implements View.OnClickListener{

    private CircleImageView imageCustomer, imageAffiliate;
    private TextView customerOrderNo, customerName, customerContactNo, customerWaterType, customerQuantity,
            customerDeliveryPriceGallon, customerAddress, customerDateToDeliver, customerService, customerTotalPrice,
            affiliateName, affilateContact, affiliateEmail, affiliateAddress, stationPoints, status;
    private Button back, smsToCustomer, callToCustomer, smsToAffiliate, callToAffiliate;
    private String orderNo, customerNo, statusArgs, merchantNo,
            fullname, contactNo,
            affiliateCustomerId, affiliateMerchantId, affiliateId, affiliateOrderNo;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_customer_affiliate, container, false);

        //methods
        displayText(view);
        firebaseUserData();
        thisArguments();
        customerInfo();
        affiliateInfo();
        retrieveDataCustomerOrder();

        return view;
    }

    private void firebaseUserData()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }
    private void thisArguments()
    {
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            orderNo = bundle.getString("transactionno");
            customerNo = bundle.getString("transactioncustomer");
            statusArgs = bundle.getString("status");
            merchantNo = bundle.getString("transactionmerchant");
        }
    }
    private void displayText(View view)
    {
        //TextView
        //Customer
        customerOrderNo = view.findViewById(R.id.orderNoAFF);
        customerName = view.findViewById(R.id.customerAFF);
        customerContactNo = view.findViewById(R.id.contactNoAFF);
        customerWaterType = view.findViewById(R.id.waterTypeAFF);
        customerQuantity = view.findViewById(R.id.itemQuantityAFF);
        customerDeliveryPriceGallon = view.findViewById(R.id.deliveryPPGAFF);
        customerAddress = view.findViewById(R.id.addressAFF);
        customerDateToDeliver = view.findViewById(R.id.datedeliveredAFF);
        customerService = view.findViewById(R.id.serviceAFF);
        customerTotalPrice = view.findViewById(R.id.totalPriceAFF);

        //Affiliate
        affiliateName = view.findViewById(R.id.affiliateNameaffAFF);
        affilateContact = view.findViewById(R.id.contactNoaffAFF);
        affiliateEmail = view.findViewById(R.id.emailaffAFF);
        affiliateAddress = view.findViewById(R.id.addressaffAFF);
        stationPoints = view.findViewById(R.id.pointsaffAFF);

        //Image View
        imageCustomer = view.findViewById(R.id.imageViewCustomerAFF);
        imageAffiliate = view.findViewById(R.id.imageViewaffAFF);

        //Status and Button
        status = view.findViewById(R.id.statusAFF);
        back = view.findViewById(R.id.backbuttonAFF);
        smsToCustomer = view.findViewById(R.id.smsCustomer);
        callToCustomer = view.findViewById(R.id.callCustomer);
        smsToAffiliate = view.findViewById(R.id.smsAffiliate);
        callToAffiliate = view.findViewById(R.id.callAffiliate);

        back.setOnClickListener(this);
        smsToAffiliate.setOnClickListener(this);
        callToAffiliate.setOnClickListener(this);
        smsToCustomer.setOnClickListener(this);
        callToCustomer.setOnClickListener(this);
    }
    private void customerInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
        reference.child(customerNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserFile userFile = dataSnapshot.getValue(UserFile.class);
                if (userFile != null)
                {
                    fullname = userFile.getUser_lastname() +", "+userFile.getUser_firstname();
                    contactNo = userFile.getUser_phone_no();
                    Picasso.get().load(userFile.getUser_uri()).fit().centerCrop().into(imageCustomer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to retrieve customer data");
            }
        });
    }
    private void affiliateInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Affiliate_WaterStation_Order_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren())
                    {
                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren())
                        {
                            for (DataSnapshot dataSnapshot4: dataSnapshot3.getChildren())
                            {
                                AffiliateStationOrderModel affiliate = dataSnapshot4.getValue(AffiliateStationOrderModel.class);
                                if (affiliate != null)
                                {
                                    if (affiliate.getStationid().equalsIgnoreCase(firebaseUser.getUid())
                                    || affiliate.getCustomerId().equalsIgnoreCase(customerNo)
                                    || affiliate.getOrderNo().equalsIgnoreCase(orderNo))
                                    {
                                        affiliateId = affiliate.getAffiliateId();
                                        affiliateCustomerId = affiliate.getCustomerId();
                                        affiliateOrderNo = affiliate.getOrderNo();
                                        affiliateMerchantId = affiliate.getStationid();
                                        retrieveDataAffiliateAcceptOrder();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to retrieve affiliate data");
            }
        });
    }
    private void retrieveDataAffiliateAcceptOrder()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
        reference.child(affiliateId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserFile userFile = dataSnapshot.getValue(UserFile.class);
                if (userFile != null)
                {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                    reference1.child(affiliateId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                            if (userAccountFile != null)
                            {
                                affiliateEmail.setText(userAccountFile.getUser_email_address());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showMessage("Failed to retrieve affiliate email address");
                        }
                    });
                    String fullname = userFile.getUser_lastname() +", "+userFile.getUser_firstname();
                    affiliateName.setText(fullname);
                    affilateContact.setText(userFile.getUser_phone_no());
                    affiliateAddress.setText(userFile.getUser_address());
                    Picasso.get().load(userFile.getUser_uri()).fit().centerCrop().into(imageAffiliate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to retrieve affiliate accept order data");
            }
        });
    }
    private void retrieveDataCustomerOrder()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference.child(customerNo).child(firebaseUser.getUid()).child(orderNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                if (orderModel != null)
                {
                    customerOrderNo.setText(orderModel.getOrder_no());
                    customerName.setText(fullname);
                    customerContactNo.setText(contactNo);
                    customerWaterType.setText(orderModel.getOrder_water_type());
                    customerQuantity.setText(orderModel.getOrder_qty());
                    customerDeliveryPriceGallon.setText("Php "+orderModel.getOrder_price_per_gallon());
                    customerAddress.setText(orderModel.getOrder_address());
                    customerDateToDeliver.setText(orderModel.getOrder_address());
                    customerService.setText(orderModel.getOrder_method());
                    customerTotalPrice.setText("Php "+orderModel.getOrder_total_amt());
                    status.setText(orderModel.getOrder_status());
                    retrievePoints(orderModel.getOrder_water_type());
                    if (orderModel.getOrder_status().equalsIgnoreCase("Completed with affiliate"))
                    {
                        smsToAffiliate.setVisibility(View.GONE);
                        callToAffiliate.setVisibility(View.GONE);
                        smsToCustomer.setVisibility(View.GONE);
                        callToCustomer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to retrieve data");
            }
        });
    }
    private void retrievePoints(String waterType)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
        reference.child(firebaseUser.getUid()).child(waterType)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserWSWDWaterTypeFile water = dataSnapshot.getValue(UserWSWDWaterTypeFile.class);
                        if (water != null)
                        {
                            double pickupPrice = Double.parseDouble(water.getPickup_price_per_gallon());
                            double quantity = Double.parseDouble(customerQuantity.getText().toString());
                            double totalPoints = pickupPrice * quantity;
                            stationPoints.setText(String.valueOf(totalPoints));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backbuttonAFF:
                if (status.getText().toString().equalsIgnoreCase("Completed with affiliate"))
                {
                    WSTransactionsFragment additem = new WSTransactionsFragment();
                    AppCompatActivity activity = (AppCompatActivity)v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container_ws, additem)
                            .addToBackStack(null)
                            .commit();
                }
                else if (status.getText().toString().equalsIgnoreCase("Dispatched by affiliate")
                || status.getText().toString().equalsIgnoreCase("In-Progress by affiliate"))
                {
                    WSInProgressFragment additem = new WSInProgressFragment();
                    AppCompatActivity activity = (AppCompatActivity)v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container_ws, additem)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.smsCustomer:
                Uri uri = Uri.parse("smsto:"+customerContactNo.getText().toString());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                startActivity(intent);
                break;
            case R.id.callCustomer:
                Intent intentcall = new Intent(Intent.ACTION_DIAL);
                intentcall.setData(Uri.parse("tel:"+customerContactNo.getText().toString()));
                startActivity(intentcall);
                Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
                break;
            case R.id.smsAffiliate:
                Uri uriAff = Uri.parse("smsto:"+affilateContact.getText().toString());
                Intent intentAff = new Intent(Intent.ACTION_SENDTO, uriAff);
                intentAff.putExtra("sms_body", "");
                startActivity(intentAff);
                break;
            case R.id.callAffiliate:
                Intent intentAffCall = new Intent(Intent.ACTION_DIAL);
                intentAffCall.setData(Uri.parse("tel:"+affilateContact.getText().toString()));
                startActivity(intentAffCall);
                Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
