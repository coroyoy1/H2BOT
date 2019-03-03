package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class WPCompletedAccept extends Fragment implements View.OnClickListener {

    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice;
    Button backButton;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser;
    CircleImageView imageView;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_completedtransactionacception, container, false);

        orderNo = view.findViewById(R.id.orderNoCOMACC);
        customer = view.findViewById(R.id.customerCOMACC);
        contactNo = view.findViewById(R.id.contactNoCOMACC);
        waterType = view.findViewById(R.id.waterTypeCOMACC);
        itemQuantity = view.findViewById(R.id.itemQuantityCOMACC);
        pricePerGallon = view.findViewById(R.id.pricePerGallonCOMACC);
        service = view.findViewById(R.id.serviceCOMACC);
        address = view.findViewById(R.id.addressCOMACC);
        deliveryFee = view.findViewById(R.id.deliveryFeeCOMACC);
        totalPrice = view.findViewById(R.id.totalPriceCOMACC);
        backButton = view.findViewById(R.id.backCOMACC);
        imageView = view.findViewById(R.id.imageViewINACC);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        progressDialog.show();

        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            transactionNo = bundle.getString("transactionno");
        }
        getOrderData();

        return view;
    }

    public void getOrderData()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren())
                {
                    if(postSnapShot.child("trans_no").getValue(String.class).equals(transactionNo)
                            && postSnapShot.child("trans_status").getValue(String.class).equals("Completed"))
                    {
                        orderNoGET = postSnapShot.child("trans_no").getValue(String.class);
                        customerNoGET = postSnapShot.child("customer_id").getValue(String.class);
                        merchantNOGET = postSnapShot.child("merchant_id").getValue(String.class);
                        dataIssuedGET = postSnapShot.child("trans_date_issued").getValue(String.class);
                        deliveryStatusGET = postSnapShot.child("trans_delivered_service").getValue(String.class);
                        transStatusGET = postSnapShot.child("trans_status").getValue(String.class);
                        transTotalAmountGET = postSnapShot.child("trans_total_amount").getValue(String.class);
                        transDeliveryFeeGET = postSnapShot.child("trans_total_delivery_fee").getValue(String.class);
                        transTotalNoGallonGET = postSnapShot.child("trans_total_no_of_gallons").getValue(String.class);

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot postSnap : dataSnapshot.getChildren())
                                {
                                    if(postSnap.child("trans_no").getValue(String.class).equals(transactionNo))
                                    {
                                        transDeliveryFeePerGallonDetail = postSnap.child("trans_delivery_fee_per_gallon").getValue(String.class);
                                        transNoDetail = postSnap.child("trans_no").getValue(String.class);
                                        transNoOfGallonDetail = postSnap.child("trans_no_of_gallons").getValue(String.class);
                                        transPartialAmountDetail = postSnap.child("trans_partial_amount").getValue(String.class);
                                        transPricePerGallonDetail = postSnap.child("trans_price_per_gallon").getValue(String.class);
                                        transStatusDetail = postSnap.child("trans_status").getValue(String.class);
                                        transWaterTypeDetail = postSnap.child("trans_water_type").getValue(String.class);
                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User_File");
                                        databaseReference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot post : dataSnapshot.getChildren())
                                                {
                                                    if(customerNoGET.equals(post.child("user_getUID").getValue(String.class)))
                                                    {
                                                        customerIDUser = post.child("user_firtname").getValue(String.class)+" "+post.child("user_lastname").getValue(String.class);
                                                        contactNoUser = post.child("user_phone_no").getValue(String.class);
                                                        String imageUi = post.child("user_uri").getValue(String.class);
                                                        Picasso.get().load(imageUi).fit().centerCrop().into(imageView);
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                                orderNo.setText(orderNoGET);
                                                customer.setText(customerIDUser);
                                                contactNo.setText(contactNoUser);
                                                waterType.setText(transWaterTypeDetail);
                                                itemQuantity.setText(transTotalNoGallonGET);
                                                pricePerGallon.setText(transPricePerGallonDetail);
                                                service.setText(deliveryStatusGET);
                                                deliveryFee.setText(transDeliveryFeeGET);
                                                totalPrice.setText(transTotalAmountGET);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                showMessages("User file does not have this data");
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        showMessages("Data is not available");
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                showMessages("Data is not available");
                                progressDialog.dismiss();
                            }
                        });
                    }
                    else
                    {
                        showMessages("Data does not available");
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Data does not available");
                progressDialog.dismiss();
            }
        });
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backCOMACC:
                WPCompletedAccept additem = new WPCompletedAccept();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_wp, additem)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
