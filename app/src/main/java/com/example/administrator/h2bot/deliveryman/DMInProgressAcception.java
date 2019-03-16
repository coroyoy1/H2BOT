package com.example.administrator.h2bot.deliveryman;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.CaptureActivityPortrait;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSInProgressAccept;
import com.example.administrator.h2bot.waterstation.WSTransactionsFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DMInProgressAcception extends Fragment implements View.OnClickListener{

    TextView inprogressText, orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice, deliveryMethod, deliveryDate;
    Button launchQR, viewLocation, launchSMS, launchCall;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser, customerNo, transactioncust, transactionmerc;
    CircleImageView imageView;
    ProgressDialog progressDialog;
    String transactNoScan;
    private static GoogleMap googleMap;
    Bundle bundle;
    FirebaseUser firebaseUser;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    LinearLayout linearLayoutOne, linearLayoutTwo, linearLayoutRTD;
    Button dispatchButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_inprogressacception, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        orderNo = view.findViewById(R.id.orderNoINDMDM);
        customer = view.findViewById(R.id.customerINDMDM);
        contactNo = view.findViewById(R.id.contactNoINDMDM);
        waterType = view.findViewById(R.id.waterTypeINDMDM);
        itemQuantity = view.findViewById(R.id.itemQuantityINDMDM);
        pricePerGallon = view.findViewById(R.id.pricePerGallonINDMDM);
        // service = view.findViewById(R.id.serviceINACC);
        address = view.findViewById(R.id.addressINDMDM);
        deliveryFee = view.findViewById(R.id.deliveryFeeINDMDM);
        totalPrice = view.findViewById(R.id.totalPriceINDMDM);
        launchQR = view.findViewById(R.id.launchQRINDMDM);
        viewLocation = view.findViewById(R.id.viewLocationButtonINDMDM);
        imageView = view.findViewById(R.id.imageViewINDMDM);
        deliveryMethod = view.findViewById(R.id.MethodINDMDM);
        deliveryDate = view.findViewById(R.id.datedeliveredINDMDM);
        launchSMS = view.findViewById(R.id.launchSMSDMDM);
        launchCall = view.findViewById(R.id.launchCallDMDM);

        linearLayoutOne = view.findViewById(R.id.linearQRSCAN);
        linearLayoutTwo = view.findViewById(R.id.linearSMSCall);
        linearLayoutRTD = view.findViewById(R.id.linearReadyToDispatch);

        dispatchButton = view.findViewById(R.id.dispatchedButtonDMDM);
        inprogressText = view.findViewById(R.id.dispatchedText);


        linearLayoutOne.setVisibility(View.GONE);
        linearLayoutTwo.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        launchQR.setOnClickListener(this);
        viewLocation.setOnClickListener(this);

        progressDialog.show();

        bundle = this.getArguments();
        if (bundle != null)
        {
            transactionNo = bundle.getString("transactionno");
            transactioncust = bundle.getString("transactioncustomer");
            transactionmerc = bundle.getString("transactionmerchant");
        }

        launchSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:"+contactNo.getText().toString());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        });
        launchCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+contactNo.getText().toString()));
                startActivity(intent);
                Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
            }
        });
        displayAllData();

        dispatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            UserWSDMFile userWSDMFile = dataSnapshot1.child(firebaseUser.getUid()).getValue(UserWSDMFile.class);
                                            if (userWSDMFile != null) {
                                                String getStationId = userWSDMFile.getStation_id();
                                                Log.d("stationid1",""+getStationId);
                                                DatabaseReference databaseDispatch2 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                                databaseDispatch2.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                                        {
                                                            for (DataSnapshot post : dataSnapshot1.child(getStationId).getChildren())
                                                            {
                                                                Log.d("NumberNako1",""+transactionNo);
                                                                OrderModel orderModel = post.getValue(OrderModel.class);
                                                                if(orderModel.getOrder_merchant_id().equals(getStationId) && orderModel.getOrder_no().equals(transactionNo))
                                                                {
                                                                    String customerID = orderModel.getOrder_customer_id();
                                                                    Log.d("customerid",""+customerID);
                                                                    DatabaseReference databaseDispatch2 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                                                    databaseDispatch2.child(customerID).child(getStationId).child(transactionNo).child("order_status").setValue("Dispatched");
                                                                    String message = "Your order with an order#:"+transactionNo+" is now dispatched. Please be ready with your payment!";
                                                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                                                                            != PackageManager.PERMISSION_GRANTED)
                                                                    {
                                                                        ActivityCompat.requestPermissions(getActivity(), new String [] {Manifest.permission.SEND_SMS},
                                                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                                    }
                                                                    else {
                                                                        Log.d("NumberNako",""+transactionNo);
                                                                        SmsManager sms = SmsManager.getDefault();
                                                                        sms.sendTextMessage(contactNo.getText().toString(), null, message, sentPI, deliveredPI);
                                                                    }
                                                                }
                                                            }
                                                        }
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

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure to dispatch this order?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserWSDMFile userWSDMFile = dataSnapshot1.child(firebaseUser.getUid()).getValue(UserWSDMFile.class);
                    if (userWSDMFile != null) {
                        String getStationId = userWSDMFile.getStation_id();
                        DatabaseReference databaseDispatch2 = FirebaseDatabase.getInstance().getReference("Customer_File");
                        databaseDispatch2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {
                                    for (DataSnapshot post : dataSnapshot1.child(getStationId).getChildren())
                                    {
                                        Log.d("NumberNako2",""+transactionNo);
                                        OrderModel orderModel = post.getValue(OrderModel.class);
                                        if(orderModel.getOrder_merchant_id().equals(getStationId) && orderModel.getOrder_no().equals(transactionNo))
                                        {
                                            if(orderModel.getOrder_status().equalsIgnoreCase("Dispatched"))
                                            {
                                                linearLayoutOne.setVisibility(View.VISIBLE);
                                                linearLayoutTwo.setVisibility(View.VISIBLE);
                                                linearLayoutRTD.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                linearLayoutOne.setVisibility(View.GONE);
                                                linearLayoutTwo.setVisibility(View.GONE);
                                                linearLayoutRTD.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
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



    public void viewLocationMeth()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        imageCapture();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Sure to display QR scanner?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void imageCapture()
    {
        IntentIntegrator integrator =  IntentIntegrator.forSupportFragment(DMInProgressAcception.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null)
        {
            if(result.getContents()==null)
            {
                showMessages("You cancelled scanning");
            }
            else
            {
                showMessages(result.getContents());
                transactNoScan = result.getContents();
                progressDialog.show();
                if(transactNoScan.equals(transactionNo))
                {
                    setOrderNo(transactNoScan);
                }
                else
                {
                    showMessages("QR is not specific to the order number, please search for the specific order");
                    progressDialog.dismiss();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
            showMessages("Error to scan");
        }
    }

    private void displayAllData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserWSDMFile userWSDMFile = dataSnapshot1.child(firebaseUser.getUid()).getValue(UserWSDMFile.class);
                    if (userWSDMFile != null) {
                        String getStationId = userWSDMFile.getStation_id();
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {
                                    for (DataSnapshot post : dataSnapshot1.child(getStationId).getChildren())
                                    {
                                        OrderModel orderModel = post.getValue(OrderModel.class);
                                        if(orderModel != null)
                                        {
                                            if(orderModel.getOrder_merchant_id().equals(getStationId)
                                                    && orderModel.getOrder_no().equals(transactionNo))
                                            {
                                                if(orderModel.getOrder_status().equals("In-Progress") || orderModel.getOrder_status().equals("Dispatched")) {
                                                    orderNo.setText(orderModel.getOrder_no());
                                                    itemQuantity.setText(orderModel.getOrder_qty());
                                                    pricePerGallon.setText(orderModel.getOrder_price_per_gallon());
                                                    totalPrice.setText(orderModel.getOrder_total_amt());
                                                    waterType.setText(orderModel.getOrder_water_type());
                                                    address.setText(orderModel.getOrder_address());
                                                    deliveryMethod.setText(orderModel.getOrder_delivery_method());
                                                    inprogressText.setText(orderModel.getOrder_status());

                                                    DateTime date = new DateTime(orderModel.getOrder_delivery_date());
                                                    String dateString = date.toLocalDate().toString();

                                                    deliveryDate.setText(dateString);
                                                    deliveryFee.setText(orderModel.getOrder_delivery_fee());

                                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User_File");
                                                    reference2.child(orderModel.getOrder_customer_id())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    UserFile userFile = dataSnapshot.getValue(UserFile.class);
                                                                    if (userFile != null) {
                                                                        String customerPicture = userFile.getUser_uri();
                                                                        Picasso.get().load(customerPicture).into(imageView);
                                                                        contactNo.setText(userFile.getUser_phone_no());
                                                                        String fullname = userFile.getUser_firstname() + " " + userFile.getUser_lastname();
                                                                        customer.setText(fullname);
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    progressDialog.dismiss();
                                                                }
                                                            });

                                                }
                                            }
                                        }
                                    }
                                }
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
    }

    public void updateStatusToDispatched()
    {

    }

    public void setOrderNo(String transactionSet)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer_File");
        reference.child(transactionmerc).child(transactioncust).child(transactionSet)
                .child("order_status").setValue("Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessages("Successfully scanned");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessages("Something wrong to scan the order no.!");
                    }
                });
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    public void viewLocationPass()
    {
        MapMerchantFragmentRenew additem = new MapMerchantFragmentRenew();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_dm, additem)
                .addToBackStack(null)
                .commit();
        Bundle bundle1 = new Bundle();
        bundle1.putString("transactionno", transactionNo);
        bundle1.putString("transactioncustomer", customerNo);
        additem.setArguments(bundle1);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.launchQRINDMDM:
                viewLocationMeth();
                break;
            case R.id.viewLocationButtonINDMDM:
//                if(googleMap != null)
//                    googleMap.clear();
                viewLocationPass();
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(smsSentReceiver);
        getActivity().unregisterReceiver(smsDeliveredReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        break;

                    //You switched your device into airplane mode, which tells your device exactly
                    //"turn all radios off" (cell, wifi, Bluetooth, NFC, ...).
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off!", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        };
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered!", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };

        getActivity().registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        getActivity().registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }
}
