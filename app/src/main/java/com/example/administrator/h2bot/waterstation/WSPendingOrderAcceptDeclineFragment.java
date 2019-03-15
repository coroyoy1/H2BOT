package com.example.administrator.h2bot.waterstation;
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
import com.example.administrator.h2bot.models.MerchantToCustomerNotify;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WSPendingOrderAcceptDeclineFragment  extends Fragment implements View.OnClickListener {
    Button acceptButton, declineButton, viewLocationButton,submitReason;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
   // private List<TransactionHeaderFileModel> headerPO;
   // private List<TransactionDetailFileModel> detailPO;
    //private PendingListAdapter POAdapter;
    ProgressDialog progressDialog;
    String transactionNo;

    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon, service, deliveryFee, totalPrice, address, deliveryMethod, deliveryDate;
    String transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail, transStatusDetail, transWaterTypeDetail;
    String customerIDUser, contactNoUser;
    String orderNoGET , customerNoGET , merchantNOGET , dataIssuedGET , deliveryStatusGET , transStatusGET , transTotalAmountGET , transDeliveryFeeGET, transTotalNoGallonGET, customerNo;
    CircleImageView imageView;
    Bundle args;
    String customerId,name;
    EditText reason;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
     public WSPendingOrderAcceptDeclineFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_pendingacception, container, false);


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
            customerNo = args.getString("transactioncustomer");
            showMessages(transactionNo);
        }
        getCustomerOrder();
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
        return view;
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public void getCustomerOrder()
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
                            Log.d("merchant IDDDDD ",""+merchantCustomerFile.getStation_id());
                            Log.d("customerKOYAWAKA",""+merchantCustomerFile.getCustomer_id());
                            String status = merchantCustomerFile.getStatus();
                            if(status.equals("AC"))
                            {
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

    public void declineData()
    {
        firebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                String token = getTokenResult.getToken();
                String dateStr = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                MerchantToCustomerNotify notificationModel = new MerchantToCustomerNotify(
                        transactionNo,
                        firebaseUser.getUid(),
                        customerNo,
                        token,
                        dateStr,
                        "Your Order has been cancelled",
                        "Decline"
                );
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification");
                databaseReference.child("Merchant_Notification").child(firebaseUser.getUid()).child(customerNo).child(transactionNo).setValue(notificationModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessages("Sent successfully");
                                //Add remove the database
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessages("Fail to send message, please check your internet connection");
                            }
                        });
            }
        });
    }

    public void dialogView()
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

    private void updateStatus()
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
                                                showMessages("Successfully updated");
                                            WSInProgressFragment additem = new WSInProgressFragment();
                                            AppCompatActivity activity = (AppCompatActivity)getContext();
                                            activity.getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                    .replace(R.id.fragment_container_ws, additem)
                                                    .addToBackStack(null)

                                                    .commit();
                                            Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
                                            progressDialog.dismiss();
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
                        showMessages("Order does not exists");
                        progressDialog.dismiss();
                    }
                });
    }

//    private void updateStatus()
//    {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
//                    if (postSnap.child("trans_no").getValue(String.class).equals(transactionNo)
//                            && postSnap.child("trans_status").getValue(String.class).equals("Pending")) {
//                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
//                        reference1.child(transactionNo).child("trans_status").setValue("In-Progress")
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
//                                databaseReference.child(transactionNo).child("trans_status").setValue("In-Progress")
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            showMessages("Successfully updated");
//                                            WSInProgressFragment additem = new WSInProgressFragment();
//                                            AppCompatActivity activity = (AppCompatActivity)getContext();
//                                            activity.getSupportFragmentManager()
//                                                    .beginTransaction()
//                                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                                                    .replace(R.id.fragment_container_ws, additem)
//                                                    .addToBackStack(null)
//                                                    .commit();
//                                            Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
//                                            progressDialog.dismiss();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            showMessages("Failed to update task, please check internet connection");
//                                            progressDialog.dismiss();
//                                        }
//                                    });
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                showMessages("Failed to update data");
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                showMessages("Data cant be retrieve");
//            }
//        });
//    }

    public void viewLocationPass()
    {
        MapMerchantFragmentRenew additem = new MapMerchantFragmentRenew();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, additem)
                .addToBackStack(null)
                .commit();
        Bundle args1 = new Bundle();
        args1.putString("transcationno", transactionNo);
        args1.putString("transactioncustomer", customerId);
        additem.setArguments(args1);
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
    protected void updateStatusCancelled()
    {
        progressDialog.dismiss();
        showDialogReason();
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
                                        reference1.child(customerId).child(merchantId).child(transactionNo).child("order_status").setValue("Declined")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        String message = "Your order:"+transactionNo+" has been declined by "+name+" for the following reasons: \n"
                                                                +reason.getText().toString();
                                                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                                                                != PackageManager.PERMISSION_GRANTED)
                                                        {
                                                            ActivityCompat.requestPermissions(getActivity(), new String [] {Manifest.permission.SEND_SMS},
                                                                    MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                        }
                                                        else {
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
