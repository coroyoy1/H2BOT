package com.example.administrator.h2bot.waterstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.administrator.h2bot.maps.MapMerchantActivity;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.CaptureActivityPortrait;
import com.example.administrator.h2bot.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WSInProgressAccept extends Fragment implements View.OnClickListener, Switch.OnCheckedChangeListener {


    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice;
    Button launchQR, viewLocation;
    Switch switcBroadcast;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser;
    CircleImageView imageView;
    ProgressDialog progressDialog;
    String transactNoScan;
    private static GoogleMap googleMap;
    Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_inprogressacception, container, false);

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
        }
        getOrderData();

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
        IntentIntegrator integrator =  IntentIntegrator.forSupportFragment(WSInProgressAccept.this);
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
                    setOrderSucess(transactNoScan);
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        databaseReference.child(transactionSet).child("trans_status").setValue("Completed")
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
                databaseReference1.child(transactionSet).child("trans_status").setValue("Completed")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessages("Successfully perform task");
                                WSTransactionsFragment additem = new WSTransactionsFragment();
                                AppCompatActivity activity = (AppCompatActivity)getContext();
                                activity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
                                showMessages("Fail to complete the task, please check internet connection");
                                progressDialog.dismiss();
                            }
                        });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessages("Fail to complete the task, please repeat it again or check internet connection");
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
//        Intent intent = new Intent(getActivity(), MapMerchantFragmentRenew.class);
//        intent.putExtra("TransactNoSeen1", transactionNo);
//        startActivity(intent);

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



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.isChecked())
        {
            Intent intent = new Intent(getActivity(), WSBroadcast.class);
            startActivity(intent);
        }
    }
}
