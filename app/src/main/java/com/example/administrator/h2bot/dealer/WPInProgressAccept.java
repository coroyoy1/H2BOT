package com.example.administrator.h2bot.dealer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.maps.MapMerchantActivity;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.CaptureActivityPortrait;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.waterstation.WSBroadcast;
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

public class WPInProgressAccept extends Fragment implements View.OnClickListener , Switch.OnCheckedChangeListener{



    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice, deliveryMethod, deliveryDate;
    Button launchQR, viewLocation, launchSMS, launchCall;
    Switch switcBroadcast;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser, customerNo;
    CircleImageView imageView;
    ProgressDialog progressDialog;
    String transactNoScan;
    private static GoogleMap googleMap;
    Bundle bundle;
    FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_inprogressacception, container, false);

        orderNo = view.findViewById(R.id.orderNoINACC);
        customer = view.findViewById(R.id.customerINACC);
        contactNo = view.findViewById(R.id.contactNoINACC);
        waterType = view.findViewById(R.id.waterTypeINACC);
        itemQuantity = view.findViewById(R.id.itemQuantityINACC);
        pricePerGallon = view.findViewById(R.id.pricePerGallonINACC);
        service = view.findViewById(R.id.serviceINACC);
        address = view.findViewById(R.id.addressINACC);
        deliveryFee = view.findViewById(R.id.deliveryFeeINACC);
        totalPrice = view.findViewById(R.id.totalPriceINACC);
        launchQR = view.findViewById(R.id.launchQRINACC);
        viewLocation = view.findViewById(R.id.viewLocationButtonINACC);
        imageView = view.findViewById(R.id.imageViewINACC);
        switcBroadcast = view.findViewById(R.id.switchbuttonIN);
        deliveryMethod = view.findViewById(R.id.MethodINACC);
        deliveryDate = view.findViewById(R.id.datedeliveredINACC);

        launchSMS = view.findViewById(R.id.launchSMS);
        launchCall = view.findViewById(R.id.launchCall);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        launchQR.setOnClickListener(this);
        viewLocation.setOnClickListener(this);
        switcBroadcast.setOnCheckedChangeListener(this);

        progressDialog.show();

        bundle = this.getArguments();
        if (bundle != null)
        {
            transactionNo = bundle.getString("transactionno");
            customerNo = bundle.getString("transactioncustomer");

        }
        displayAllData();
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
                intent.setData(Uri.parse("tel:"+contactNo));
                startActivity(intent);
                Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
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
        builder.setMessage("Are you sure to display?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void imageCapture()
    {
        IntentIntegrator integrator =  IntentIntegrator.forSupportFragment(WPInProgressAccept.this);
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
                    updateOrder(transactNoScan);
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
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for (DataSnapshot post : dataSnapshot1.child(firebaseUser.getUid()).getChildren())
                    {
                        OrderModel orderModel = post.getValue(OrderModel.class);
                        if(orderModel != null)
                        {
                            if(orderModel.getOrder_merchant_id().equals(firebaseUser.getUid())
                                    && orderModel.getOrder_status().equals("In-Progress") && orderModel.getOrder_no().equals(transactionNo))
                            {
                                if(orderModel.getOrder_status().equals("In-Progress")) {
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getOrderData()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File").child(transactionNo);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("trans_no").getValue(String.class).equals(transactionNo)
                            && dataSnapshot.child("trans_status").getValue(String.class).equals("In-Progress"))
                    {
                        orderNoGET = dataSnapshot.child("trans_no").getValue(String.class);
                        customerNoGET = dataSnapshot.child("customer_id").getValue(String.class);
                        merchantNOGET = dataSnapshot.child("merchant_id").getValue(String.class);
                        dataIssuedGET = dataSnapshot.child("trans_date_issued").getValue(String.class);
                        deliveryStatusGET = dataSnapshot.child("trans_delivered_service").getValue(String.class);
                        transStatusGET = dataSnapshot.child("trans_status").getValue(String.class);
                        transTotalAmountGET = dataSnapshot.child("trans_total_amount").getValue(String.class);
                        transDeliveryFeeGET = dataSnapshot.child("trans_total_delivery_fee").getValue(String.class);
                        transTotalNoGallonGET = dataSnapshot.child("trans_total_no_of_gallons").getValue(String.class);

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File").child(transactionNo);
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnap) {

                                    if(postSnap.child("trans_no").getValue(String.class).equals(transactionNo)
                                            && postSnap.child("trans_status").getValue(String.class).equals("In-Progress") )
                                    {
                                        transDeliveryFeePerGallonDetail = postSnap.child("trans_delivery_fee_per_gallon").getValue(String.class);
                                        transNoDetail = postSnap.child("trans_no").getValue(String.class);
                                        transNoOfGallonDetail = postSnap.child("trans_no_of_gallons").getValue(String.class);
                                        transPartialAmountDetail = postSnap.child("trans_partial_amount").getValue(String.class);
                                        transPricePerGallonDetail = postSnap.child("trans_price_per_gallon").getValue(String.class);
                                        transStatusDetail = postSnap.child("trans_status").getValue(String.class);
                                        transWaterTypeDetail = postSnap.child("trans_water_type").getValue(String.class);
                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User_File").child(customerNoGET);
                                        databaseReference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot post) {

                                                    if(customerNoGET.equals(post.child("user_getUID").getValue(String.class)))
                                                    {
                                                        customerIDUser = post.child("user_firtname").getValue(String.class)+" "+post.child("user_lastname").getValue(String.class);
                                                        contactNoUser = post.child("user_phone_no").getValue(String.class);
                                                        String imageUi = post.child("user_uri").getValue(String.class);
                                                        Picasso.get().load(imageUi).fit().centerCrop().into(imageView);
                                                        progressDialog.dismiss();

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


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Data does not available");
                progressDialog.dismiss();
            }
        });
    }

    public void setOrderSucess(String transactionSet)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
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
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_Order_File");
                                reference1.child(customerId).child(merchantId).child(transactionSet).child("order_status").setValue("Completed")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showMessages("Successfully updated");
                                                WSTransactionsFragment additem = new WSTransactionsFragment();
                                                AppCompatActivity activity = (AppCompatActivity)getContext();
                                                activity.getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                                        .replace(R.id.fragment_container_ws, additem)
                                                        .addToBackStack(null)
                                                        .commit();
                                                Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("Completed Orders");
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessages("Data does updated");
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
                .replace(R.id.fragment_container_ws, additem)
                .addToBackStack(null)
                .commit();
        bundle.putString("TransactNoSeen1", transactionNo);
        additem.setArguments(bundle);
    }

    public void getLocationUser()
    {
        Intent intent = new Intent(getActivity(), MapMerchantActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.launchQRINACC:
                viewLocationMeth();
                break;
            case R.id.viewLocationButtonINACC:
//                if(googleMap != null)
//                    googleMap.clear();
                //viewLocationPass();
                getLocationUser();
                break;
        }
    }
    private void updateOrder(String transactionSet)
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
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_Order_File");
                                reference1.child(customerId).child(merchantId).child(transactionSet).child("order_status").setValue("Completed")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showMessages("Successfully updated");
                                                WSTransactionsFragment additem = new WSTransactionsFragment();
                                                AppCompatActivity activity = (AppCompatActivity)getContext();
                                                activity.getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                                        .replace(R.id.fragment_container_ws, additem)
                                                        .addToBackStack(null)
                                                        .commit();
                                                Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("Completed Orders");
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessages("Data does updated");
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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.isChecked())
        {
            Intent intent = new Intent(getActivity(), WSBroadcast.class);
            startActivity(intent);
        }
    }
}
