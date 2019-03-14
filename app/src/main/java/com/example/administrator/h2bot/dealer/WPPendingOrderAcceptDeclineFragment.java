package com.example.administrator.h2bot.dealer;
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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WPPendingOrderAcceptDeclineFragment extends Fragment implements View.OnClickListener {
    Button acceptButton, declineButton, viewLocationButton, submitReason;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
   // private List<TransactionHeaderFileModel> headerPO;
   // private List<TransactionDetailFileModel> detailPO;
    //private PendingListAdapter POAdapter;
    ProgressDialog progressDialog;
    String transactionNo,name;

    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon, service, deliveryFee, totalPrice, address, deliveryMethod, deliveryDate;
    String transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail, transStatusDetail, transWaterTypeDetail;
    String customerIDUser, contactNoUser;
    String orderNoGET , customerNoGET , merchantNOGET , dataIssuedGET , deliveryStatusGET , transStatusGET , transTotalAmountGET , transDeliveryFeeGET, transTotalNoGallonGET;
    CircleImageView imageView;
    String customerNo, customerId, merchantId;
    Bundle args;
    EditText reason;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

     public WPPendingOrderAcceptDeclineFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_pendingacception, container, false);

        sentPI = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(getActivity(), 0, new Intent(DELIVERED), 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        progressDialog.show();

        orderNo = view.findViewById(R.id.orderNoPOACC);
        customer = view.findViewById(R.id.customerPOACC);
        contactNo = view.findViewById(R.id.contactNoPOACC);
        waterType = view.findViewById(R.id.waterTypePOACC);
        itemQuantity = view.findViewById(R.id.itemQuantityPOACC);
        pricePerGallon = view.findViewById(R.id.pricePerGallonPOACC);
        deliveryFee = view.findViewById(R.id.deliveryFeePOACC);
        totalPrice = view.findViewById(R.id.totalPricePOACC);
        imageView = view.findViewById(R.id.imageViewPOACC);
        address = view.findViewById(R.id.addressPOACC);
        deliveryMethod = view.findViewById(R.id.MethodPOACC);
        deliveryDate = view.findViewById(R.id.datedeliveredPOACC);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        acceptButton = view.findViewById(R.id.acceptPOA);
        declineButton = view.findViewById(R.id.declinePOA);
        viewLocationButton = view.findViewById(R.id.viewLocationButtonPOA);

        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
        viewLocationButton.setOnClickListener(this);

        args = this.getArguments();
        if(args != null)
        {
            transactionNo = args.getString("transactionno");
            Log.d("transactionno",""+transactionNo);
            customerNo = args.getString("transactioncustomer");
        }
        getOrderData();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("business_name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        attemptToExit();
                        return true;
                    }
                }
                return false;
            }
        });


        return view;
    }

    protected void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }



    protected void getOrderData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
        reference.child(firebaseUser.getUid()).child(customerNo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
                        if(merchantCustomerFile != null)
                        {
                            String customerId = merchantCustomerFile.getCustomer_id();
                            String merchantId = merchantCustomerFile.getStation_id();
                            String status = merchantCustomerFile.getStatus();
                            if(status.equals("AC"))
                            {
                                Log.d("CustomerID",","+customerId+","+merchantId+","+transactionNo);
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                reference1.child(customerId).child(merchantId).child(transactionNo)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                                                if(orderModel != null)
                                                {
                                                    if(orderModel.getOrder_status().equals("Pending")) {
                                                        orderNo.setText(orderModel.getOrder_no());
                                                        itemQuantity.setText(orderModel.getOrder_qty());
                                                        pricePerGallon.setText(orderModel.getOrder_price_per_gallon());
                                                        totalPrice.setText(orderModel.getOrder_total_amt());
                                                        waterType.setText(orderModel.getOrder_water_type());
                                                        address.setText(orderModel.getOrder_address());
                                                        deliveryMethod.setText(orderModel.getOrder_delivery_method());

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
                                                                            String fullname = userFile.getUser_firtname() + " " + userFile.getUser_lastname();
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

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                progressDialog.dismiss();
                                            }
                                        });
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


    protected void dialogView()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        progressDialog.show();
                        updateStatus();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to accept the order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    protected void dialogView2()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        progressDialog.show();
                        updateStatusCancelled();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to decline the order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    protected void updateStatus()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
        reference.child(firebaseUser.getUid()).child(customerNo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
                        if(merchantCustomerFile != null)
                        {
                            customerId = merchantCustomerFile.getCustomer_id();
                            String merchantId = merchantCustomerFile.getStation_id();
                            String status = merchantCustomerFile.getStatus();
                            if(status.equals("AC"))
                            {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                reference1.child(customerId).child(merchantId).child(transactionNo).child("order_status").setValue("In-Progress")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                String message = "Your order:"+transactionNo+" has been accepted by "+name+". We will notify you for further details. Thank You!";
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
                                                    showMessages("Updated Successfully");
                                                WSInProgressFragment additem = new WSInProgressFragment();
                                                AppCompatActivity activity = (AppCompatActivity)getContext();
                                                activity.getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                        .replace(R.id.fragment_container_wp, additem)
                                                        .addToBackStack(null)
                                                        .commit();
                                                Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessages("Error to update order");
                                                progressDialog.dismiss();
                                            }
                                        });

                            }
                        }
                    }

                    @Override

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessages("Order does not exist");
                        progressDialog.dismiss();
                    }
                });
    }
    protected void updateStatusCancelled()
    {
        progressDialog.dismiss();
        showDialogReason();
    }

    protected void viewLocationPass()
    {
        MapMerchantFragmentRenew additem = new MapMerchantFragmentRenew();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_wp, additem)
                .addToBackStack(null)
                .commit();
        args.putString("TransactNoSeen1", transactionNo);
        additem.setArguments(args);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.acceptPOA:
                dialogView();
                break;
            case R.id.declinePOA:
                dialogView2();
                break;
            case R.id.viewLocationButtonPOA:
                viewLocationPass();
                break;
        }
    }
    protected void attemptToExit()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
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
    public void showDialogReason()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.reason_decline, null);

        reason = dialogView.findViewById(R.id.reason);
        submitReason = dialogView.findViewById(R.id.submitReason);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();

        submitReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
                reference.child(firebaseUser.getUid()).child(customerNo)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
                                if(merchantCustomerFile != null)
                                {
                                    customerId = merchantCustomerFile.getCustomer_id();
                                    String merchantId = merchantCustomerFile.getStation_id();
                                    String status = merchantCustomerFile.getStatus();
                                    if(status.equals("AC"))
                                    {
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                        reference1.child(customerId).child(merchantId).child(transactionNo).child("order_status").setValue("Cancelled")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        String message = "Your order with an order # of "+transactionNo+" has been declined by "+name+" for the following reason(s): \n\n"
                                                                +reason.getText().toString();
                                                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                                                                != PackageManager.PERMISSION_GRANTED)
                                                        {
                                                            Log.d("NmNakoko","Hijhosdfgh");
                                                            ActivityCompat.requestPermissions(getActivity(), new String [] {Manifest.permission.SEND_SMS},
                                                                    MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                        }
                                                        else {
                                                            Log.d("NmNako","Hi"+message);
                                                            SmsManager sms = SmsManager.getDefault();
                                                            sms.sendTextMessage(contactNo.getText().toString(), null, message, sentPI, deliveredPI);
                                                        }
                                                        WSInProgressFragment additem = new WSInProgressFragment();
                                                        AppCompatActivity activity = (AppCompatActivity)getContext();
                                                        activity.getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                                .replace(R.id.fragment_container_wp, additem)
                                                                .addToBackStack(null)
                                                                .commit();
                                                        Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
                                                        progressDialog.dismiss();
                                                        alertDialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        showMessages("Failed to update order");
                                                        progressDialog.dismiss();
                                                    }
                                                });

                                    }
                                }
                            }

                            @Override

                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                showMessages("Order does not exist");
                                progressDialog.dismiss();
                            }
                        });
            }
        });
        alertDialog.show();

    }
}
