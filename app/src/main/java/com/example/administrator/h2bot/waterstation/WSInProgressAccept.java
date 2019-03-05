package com.example.administrator.h2bot.waterstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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

public class WSInProgressAccept extends Fragment implements View.OnClickListener, Switch.OnCheckedChangeListener {


    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice, deliveryMethod, deliveryDate;
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
    FirebaseUser firebaseUser;


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
        deliveryMethod = view.findViewById(R.id.MethodINACC);
        deliveryDate = view.findViewById(R.id.datedeliveredINACC);



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
        }
        getCustomerOrder();

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

    public void getCustomerOrder()
    {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
            reference.child(firebaseUser.getUid())
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
                                    reference1.child(customerId).child(merchantId).child(transactionNo)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                                                    if(orderModel != null)
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

    private void updateOrder(String transactionSet)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
        reference.child(firebaseUser.getUid())
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
                                reference1.child(customerId).child(merchantId).child(transactionSet).setValue("order_status")
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
