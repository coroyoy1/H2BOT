package com.example.administrator.h2bot.tpaaffiliate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.dealer.WPInProgressAccept;
import com.example.administrator.h2bot.models.AffiliateStationOrderModel;
import com.example.administrator.h2bot.models.CaptureActivityPortrait;
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


public class TPAAcceptedOrdersInfo extends Fragment implements View.OnClickListener{
    TextView orderNo,customerName,customerAddress,customerContactNo,waterStationName,stationAddress,stationContactNo,expectedDate,pricePerGallon,quantity,waterType,deliveryFee,totalPrice;
    Button viewLocationCustomer,launchSMSCustomer,launchCallCustomer,viewLocationStation,launchSMSStation,launchCallStation,Dispatch,launchQRScanner;
    public String stationID,customerID,orderNumber;
    ImageView imageviewprofile;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    Bundle bundle;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    public TPAAcceptedOrdersInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tpaaccepted_orders_info, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        orderNo = view.findViewById(R.id.orderNo);
        customerName = view.findViewById(R.id.customerName);
        customerAddress = view.findViewById(R.id.customerAddress);
        customerContactNo = view.findViewById(R.id.customerContactNo);
        waterStationName = view.findViewById(R.id.waterStationName);
        stationAddress = view.findViewById(R.id.stationAddress);
        stationContactNo = view.findViewById(R.id.stationContactNo);
        expectedDate = view.findViewById(R.id.expectedDate);
        pricePerGallon = view.findViewById(R.id.pricePerGallon);
        quantity = view.findViewById(R.id.quantity);
        waterType = view.findViewById(R.id.waterType);
        deliveryFee = view.findViewById(R.id.deliveryFee);
        totalPrice = view.findViewById(R.id.totalPrice);

        viewLocationCustomer = view.findViewById(R.id.viewLocationCustomer);
        launchSMSCustomer = view.findViewById(R.id.launchSMSCustomer);
        launchCallCustomer = view.findViewById(R.id.launchCallCustomer);
        viewLocationStation = view.findViewById(R.id.viewLocationStation);
        launchSMSStation = view.findViewById(R.id.launchSMSStation);
        launchCallStation = view.findViewById(R.id.launchCallStation);
        Dispatch = view.findViewById(R.id.Dispatch);
        launchQRScanner = view.findViewById(R.id.launchQRScanner);

        viewLocationCustomer.setOnClickListener(this);
        launchSMSCustomer.setOnClickListener(this);
        launchCallCustomer.setOnClickListener(this);
        viewLocationStation.setOnClickListener(this);
        launchSMSStation.setOnClickListener(this);
        launchCallStation.setOnClickListener(this);
        Dispatch.setOnClickListener(this);
        launchQRScanner.setOnClickListener(this);

        progressDialogDeclatation();
        getBunle();
        getFromCustomerFile();
        getFromBusinessFile();
        getFromUserFile();
        return view;
    }

    public void getFromCustomerFile()
    {
        DatabaseReference customerFile = FirebaseDatabase.getInstance().getReference("Customer_File").child(customerID).child(stationID).child(orderNumber);
        customerFile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderNo.setText(orderNumber);
                customerAddress.setText(dataSnapshot.child("order_address").getValue(String.class));
                expectedDate.setText(dataSnapshot.child("order_delivery_date").getValue(String.class));
                pricePerGallon.setText(dataSnapshot.child("order_price_per_gallon").getValue(String.class));
                quantity.setText(dataSnapshot.child("order_qty").getValue(String.class));
                totalPrice.setText(dataSnapshot.child("order_total_amt").getValue(String.class));
                waterType.setText(dataSnapshot.child("order_water_type").getValue(String.class));
                deliveryFee.setText(dataSnapshot.child("order_delivery_fee").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getFromBusinessFile()
    {
        DatabaseReference businessfile = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(stationID);
        businessfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stationAddress.setText(dataSnapshot.child("business_address").getValue(String.class));
                stationContactNo.setText(dataSnapshot.child("business_tel_no").getValue(String.class));
                waterStationName.setText(dataSnapshot.child("business_name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getFromUserFile()
    {
        DatabaseReference userfile = FirebaseDatabase.getInstance().getReference("User_File").child(customerID);
        userfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerAddress.setText(dataSnapshot.child("user_firstname").getValue(String.class)+" "+dataSnapshot.child("user_lastname").getValue(String.class));
                customerAddress.setText(dataSnapshot.child("user_address").getValue(String.class));
                customerContactNo.setText(dataSnapshot.child("user_phone_no").getValue(String.class));
                Picasso.get().load(dataSnapshot.child("user_uri").getValue(String.class)).into(imageviewprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getBunle()
    {
        bundle = this.getArguments();
        if (bundle != null)
        {
            orderNumber = bundle.getString("orderno");
            customerID = bundle.getString("customerid");
            stationID = bundle.getString("stationid");
            Log.d("Hi","Ho");
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.viewLocationCustomer:
                break;
            case R.id.launchSMSCustomer:
                SMSCustomer();
                break;
            case R.id.launchCallCustomer:
                CallCustomer();
                break;
            case R.id.viewLocationStation:
                break;
            case R.id.launchSMSStation:
                SMSStation();
                break;
            case R.id.launchCallStation:
                CallStation();
                break;
            case R.id.Dispatch:
                QRScanner();
                break;
            case R.id.launchQRScanner:
                break;
        }
    }
    public void SMSCustomer()
    {
        Uri uri = Uri.parse("smsto:"+customerContactNo.getText().toString());
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }
    public void SMSStation()
    {
        Uri uri = Uri.parse("smsto:"+stationContactNo.getText().toString());
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }
    public void CallCustomer()
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+customerContactNo.getText().toString()));
        startActivity(intent);
        Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
    }
    public void CallStation()
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+stationContactNo.getText().toString()));
        startActivity(intent);
        Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
    }
    public void QRScanner()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        IntentIntegrator integrator =  IntentIntegrator.forSupportFragment(TPAAcceptedOrdersInfo.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(true);
                        integrator.setCaptureActivity(CaptureActivityPortrait.class);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Launch QR scanner?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        String text;
        String QRcode;
        if(result != null)
        {
            if(result.getContents()==null)
            {
                text="You cancelled scanning";
                snackBar(text);
            }
            else
            {
                QRcode = result.getContents();
                progressDialog.show();
                if(QRcode.equals(stationID+""+customerID+""+orderNumber))
                {
                    updateOrder();
                }
                else
                {
                    text="Incorrect QR code";
                    snackBar(text);
                    progressDialog.dismiss();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
            text = "Error to scan";
            snackBar(text);
        }
    }
    public void progressDialogDeclatation()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
    }
    public void snackBar(String text){
        View parentLayout = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, ""+text
                , Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        snackbar.setAction("Okay", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(getResources().getColor(android.R.color.white ));
        snackbar.show();
    }

    public void updateOrder()
    {
        FirebaseDatabase.getInstance().getReference("Customer_File").child(customerID).child(stationID).child(orderNumber)
                .child("order_status").setValue("Completed with affiliate");
        FirebaseDatabase.getInstance().getReference("Affiliate_WaterStation_Order_File").child(customerID).child(stationID).child(orderNumber)
                .child("order_status").setValue("Completed with affiliate");
    }
}
