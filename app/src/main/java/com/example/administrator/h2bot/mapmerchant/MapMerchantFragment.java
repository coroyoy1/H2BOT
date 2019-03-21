package com.example.administrator.h2bot.mapmerchant;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.maps.DirectionsParser;
import com.example.administrator.h2bot.maps.IOBackPressed;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.CaptureActivityPortrait;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSInProgressAccept;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.android.gms.maps.model.LatLng;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapMerchantFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, IOBackPressed, View.OnClickListener
{
    private static final String API_KEY = "AIzaSyCIGlVnlwv-hL9fIjqfYSjnX5DlFIbB5bc";
    // User Permissions
    private static GoogleMap map;
    private int routeColor;
    TextView stationID;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrentLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    public static final String EXTRA_stationID = "stationID";

    private ChildEventListener mChilExventListener;
    private Location mCurrentLocation;
    Marker marker;
    LinearLayout linearLayout;
    public FirebaseAuth mAuth;
    DatabaseReference addressesRef, businessRef;
    DatabaseReference usersLocRef;
    Dialog mDialog;
    LocationManager locationManager;
    Location dislocation;

    ArrayList<UserFile> arrayListUserFile;
    ArrayList<TransactionHeaderFileModel> arrayListBusinessInfo;

    Geocoder mGeocoder;
    List<Address> myListAddresses;
    List<UserWSBusinessInfoFile> myBusinessInfos;
    String myAddresses, stationName, userType, stationId;
    String addresses;
    LatLng latLong = null;
    LatLng mLatLng = null;
    PolylineOptions polylineOptions;

    private Map<Marker, Map<String, Object>> hashMapMarkers = new HashMap<>();
    private Map<String, Object> dataModel = new HashMap<>();

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    FirebaseAuth mauth;
    FirebaseUser firebaseUser;
    String transactionNo ,customerNo;

    Button order, launchscan, sms, call, dispatched, accept, decline;
    LinearLayout linearSMSSender, linearOrderSender, linearAcceptDeclineSender;

    CircleImageView customerImage;
    TextView orderNoMMF, customerMMF, contactNoMMF, waterTypeMMF, quantityMMF,
    pricePerGallonMMF, addressMMF, dateDeliveredMMF, deliveryFeeMMF, methodMMF,
    totalPriceMMF;

    Button closeOrderDialog, submitReason;

    String orderNoString, customerString, contactString, waterTypeString, quantityString,
            pricePerGallonString, addressString, dateDeliveredString, deliveryFeeString,
            methodString, totalPriceString;
    private String customerId,name;
    EditText reason;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    private String transactNoScan;
    private String customerCheckId, merchantCheckId, deliverymanCheckId;


    ///Input and Display Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public MapMerchantFragment()
    {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_merchant_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            transactionNo = bundle.getString("transactionno");
            customerNo = bundle.getString("transactioncustomer");
            userType = bundle.getString("transactionusertype");
            merchantCheckId = bundle.getString("transactionmerchant");
        }
        inputData(view);
        getInflaterFromOrderDetails();
        mDialog = new Dialog(getActivity());
        order.setOnClickListener(this);
        sms.setOnClickListener(this);
        launchscan.setOnClickListener(this);
        call.setOnClickListener(this);
        dispatched.setOnClickListener(this);
        accept.setOnClickListener(this);
        decline.setOnClickListener(this);


        if (userType.equals("Pending"))
        {
            dispatched.setVisibility(View.VISIBLE);
            linearAcceptDeclineSender.setVisibility(View.VISIBLE);
            linearSMSSender.setVisibility(View.GONE);
            launchscan.setVisibility(View.GONE);
        }
        if (userType.equals("In-Progress"))
        {
            dispatched.setVisibility(View.VISIBLE);
            linearAcceptDeclineSender.setVisibility(View.GONE);
            linearSMSSender.setVisibility(View.GONE);
            launchscan.setVisibility(View.VISIBLE);
        }
        if (userType.equals("Dispatched"))
        {
            dispatched.setVisibility(View.GONE);
            linearAcceptDeclineSender.setVisibility(View.GONE);
            linearSMSSender.setVisibility(View.VISIBLE);
            launchscan.setVisibility(View.VISIBLE);
        }

        userTypeIdentity();

        return view;
    }

    //View Inputs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void inputData(View view)
    {
        order = view.findViewById(R.id.orderDetails);
        launchscan = view.findViewById(R.id.orderLaunchScan);
        sms = view.findViewById(R.id.orderSMS);
        call = view.findViewById(R.id.orderCall);
        dispatched = view.findViewById(R.id.orderDispatched);
        accept = view.findViewById(R.id.orderAccept);
        decline = view.findViewById(R.id.orderDecline);

        linearSMSSender = view.findViewById(R.id.linearSMSDetails);
        linearOrderSender = view.findViewById(R.id.linearOrderDetails);
        linearAcceptDeclineSender = view.findViewById(R.id.linearAcceptDetails);

    }

    public void dialogDataFromOrder(View dialogView)
    {
        orderNoMMF = dialogView.findViewById(R.id.orderNoAll);
        customerMMF = dialogView.findViewById(R.id.customerAll);
        contactNoMMF = dialogView.findViewById(R.id.contactNoAll);
        waterTypeMMF = dialogView.findViewById(R.id.waterTypeAll);
        quantityMMF = dialogView.findViewById(R.id.itemQuantityAll);
        pricePerGallonMMF = dialogView.findViewById(R.id.pricePerGallonAll);
        addressMMF = dialogView.findViewById(R.id.addressAll);
        dateDeliveredMMF = dialogView.findViewById(R.id.datedeliveredAll);
        deliveryFeeMMF = dialogView.findViewById(R.id.deliveryFeeAll);
        methodMMF = dialogView.findViewById(R.id.MethodAll);
        totalPriceMMF = dialogView.findViewById(R.id.totalPriceAll);
        customerImage = dialogView.findViewById(R.id.imageViewAll);

        closeOrderDialog = dialogView.findViewById(R.id.closeDialogAll);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.orderDetails:
                orderDetailsDialog();
                break;
            case R.id.orderLaunchScan:
                cameraDisplay();
                break;
            case R.id.orderCall:
                Intent intentcall = new Intent(Intent.ACTION_DIAL);
                intentcall.setData(Uri.parse("tel:"+contactNoMMF.getText().toString()));
                startActivity(intentcall);
                Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
                break;
            case R.id.orderSMS:
                Uri uri = Uri.parse("smsto:"+contactNoMMF.getText().toString());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                startActivity(intent);
                break;
            case R.id.orderDispatched:
                attemptToDispatched();
                break;
            case R.id.orderAccept:
                AcceptOrder();
                break;
            case R.id.orderDecline:
                CancelledOrder();
                break;
        }

    }

    //Check user type~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //Check user type~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //View Inputs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    ///Retrieving Data and Update~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Dialog Display Order~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void orderDetailsDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.a_order_details, null);
        dialogDataFromOrder(dialogView);
        mDialog.setContentView(dialogView);
        mDialog.setCancelable(false);
        closeOrderDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        dialogOrderRetrieveData();
        mDialog.show();
    }

    private void getInflaterFromOrderDetails()
    {
        LayoutInflater inflater = getLayoutInflater();
        final View flat = inflater.inflate(R.layout.a_order_details, null);
        dialogDataFromOrder(flat);
    }
    protected void AcceptOrder()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
//                        progressDialog.show();
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
    protected void DispatchedOrder()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
//                        progressDialog.show();
                        updateStatus();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to dispatch this order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    public void cameraDisplay()
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
        builder.setMessage("Launch camera?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    protected void CancelledOrder()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        showDialogReason();
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

    //Dialog Display Order~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Warning Dialog~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void attemptToDispatched()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        launchscan.setVisibility(View.VISIBLE);
                        dispatched.setVisibility(View.GONE);
                        linearSMSSender.setVisibility(View.VISIBLE);
                        updateIntoDispatched();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to dispatch?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    //Warning Dialog~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///Retrieving Data and Update~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void dialogOrderRetrieveData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
        reference.child(merchantCheckId).child(customerNo)
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
                                reference1.child(customerId).child(merchantId).child(transactionNo)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                                                if(orderModel != null)
                                                {
                                                    if(orderModel.getOrder_status().equals("Pending")
                                                            || orderModel.getOrder_status().equals("In-Progress")
                                                            || orderModel.getOrder_status().equals("Completed")
                                                            || orderModel.getOrder_status().equals("Dispatched")
                                                            || orderModel.getOrder_status().equals("Accepted"))
                                                    {

                                                        if (orderModel.getOrder_status().equals("Pending"))
                                                        {
                                                            linearSMSSender.setVisibility(View.GONE);
                                                            linearAcceptDeclineSender.setVisibility(View.VISIBLE);
                                                            dispatched.setVisibility(View.GONE);
                                                        }
                                                        if (orderModel.getOrder_status().equals("Dispatched"))
                                                        {
                                                            dispatched.setVisibility(View.GONE);
                                                            linearAcceptDeclineSender.setVisibility(View.GONE);
                                                            linearSMSSender.setVisibility(View.VISIBLE);
                                                        }

                                                        orderNoMMF.setText(orderModel.getOrder_no());
                                                        quantityMMF.setText(orderModel.getOrder_qty());
                                                        pricePerGallonMMF.setText(orderModel.getOrder_price_per_gallon());
                                                        totalPriceMMF.setText(orderModel.getOrder_total_amt());
                                                        waterTypeMMF.setText(orderModel.getOrder_water_type());
                                                        addressMMF.setText(orderModel.getOrder_address());
                                                        methodMMF.setText(orderModel.getOrder_delivery_method());

                                                        DateTime date = new DateTime(orderModel.getOrder_delivery_date());
                                                        String dateString = date.toLocalDate().toString();

                                                        dateDeliveredMMF.setText(dateString);
                                                        deliveryFeeMMF.setText(orderModel.getOrder_delivery_fee());

                                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User_File");
                                                        reference2.child(orderModel.getOrder_customer_id())
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        UserFile userFile = dataSnapshot.getValue(UserFile.class);
                                                                        if (userFile != null) {
                                                                            String customerPicture = userFile.getUser_uri();
                                                                            Picasso.get().load(customerPicture).into(customerImage);
                                                                            contactNoMMF.setText(userFile.getUser_phone_no());
                                                                            String fullname = userFile.getUser_firstname() + " " + userFile.getUser_lastname();
                                                                            customerMMF.setText(fullname);
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
    public void locateCustomer(LatLng pLatLng)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
        reference.child(merchantCheckId).child(customerNo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
                        if(merchantCustomerFile != null)
                        {
                            String merchantId = merchantCustomerFile.getStation_id();
                            String customerId = merchantCustomerFile.getStation_id();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_LatLong");
                            databaseReference.child(customerId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserLocationAddress userLocationAddress = dataSnapshot.getValue(UserLocationAddress.class);
                                            if(userLocationAddress != null)
                                            {
                                                double latitude = Double.parseDouble(userLocationAddress.getUser_latitude());
                                                double longtitude = Double.parseDouble(userLocationAddress.getUser_longtitude());
                                                LatLng latLng = new LatLng(latitude, longtitude);
                                                if(mLatLng !=null)
                                                {
                                                    MapMerchantFragment.TaskRequestDirections taskRequestDirections = new MapMerchantFragment.TaskRequestDirections();
                                                    taskRequestDirections.execute(getRequestURL(pLatLng, latLng));
                                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_File");
                                                    reference1.child(customerNo).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            UserFile userFile = dataSnapshot.getValue(UserFile.class);
                                                            if(userFile != null)
                                                            {
                                                                String address = userFile.getUser_address();
                                                                String fullname = userFile.getUser_lastname()+", "+userFile.getUser_firstname();
                                                                map.addMarker(new MarkerOptions().position(latLng).snippet("Customer Name: "+fullname+"\n"+"Address: "+address)
                                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void updateStatus()
    {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
    reference.child(merchantCheckId).child(customerNo)
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
                                                SmsManager sms = SmsManager.getDefault();
                                                sms.sendTextMessage(contactNoMMF.getText().toString(), null, message, sentPI, deliveredPI);
                                            }
                                            showMessages("Successfully updated");
                                            Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
//                                                progressDialog.dismiss();


                                            WSInProgressFragment additem = new WSInProgressFragment();
                                            AppCompatActivity activity = (AppCompatActivity)getContext();
                                            activity.getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                                    .replace(R.id.fragment_container_ws, additem)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showMessages("Failed to update order");
//                                                progressDialog.dismiss();
                                        }
                                    });

                        }
                    }
                }

                @Override

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showMessages("Order does not exists");
//                        progressDialog.dismiss();
                }
            });
}
    private void updateIntoDispatched()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_File");
        reference.child(merchantCheckId).child(customerNo)
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
                                reference1.child(customerId).child(merchantId).child(transactionNo).child("order_status").setValue("Dispatched")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                String message = "Your order:"+transactionNo+" has been dispatched by "+name+". We will notify you for further details. Thank You!";
                                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                                                        != PackageManager.PERMISSION_GRANTED)
                                                {
                                                    ActivityCompat.requestPermissions(getActivity(), new String [] {Manifest.permission.SEND_SMS},
                                                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                }
                                                else {
                                                    SmsManager sms = SmsManager.getDefault();
                                                    sms.sendTextMessage(contactNoMMF.getText().toString(), null, message, sentPI, deliveredPI);
                                                }
                                                showMessages("Successfully updated");
//                                                WSInProgressFragment additem = new WSInProgressFragment();
//                                                AppCompatActivity activity = (AppCompatActivity)getContext();
//                                                activity.getSupportFragmentManager()
//                                                        .beginTransaction()
//                                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                                                        .replace(R.id.fragment_container_ws, additem)
//                                                        .addToBackStack(null)
//                                                        .commit();
                                                Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
//                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessages("Failed to update order");
//                                                progressDialog.dismiss();
                                            }
                                        });

                            }
                        }
                    }

                    @Override

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessages("Order does not exists");
//                        progressDialog.dismiss();
                    }
                });
    }
    public void userTypeIdentity()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
        databaseReference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentUserType = dataSnapshot.child("user_type").getValue(String.class);
                        if (currentUserType != null)
                        {
                            if (currentUserType.equals("Water Station"))
                            {
                                linearSMSSender.setVisibility(View.GONE);
                                dispatched.setVisibility(View.GONE);
                            }
                            if (currentUserType.equals("Delivery Man"))
                            {
                                if (userType.equals("Dispatched"))
                                {

                                }
                                else if (userType.equals("In-Progress"))
                                {
                                    linearSMSSender.setVisibility(View.GONE);
                                    launchscan.setVisibility(View.GONE);
                                }
                            }
                            if (currentUserType.equals("Water Dealer"))
                            {
                                linearAcceptDeclineSender.setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    public void updateOrder(String transactionSet)
    {
        DatabaseReference referencedata = FirebaseDatabase.getInstance().getReference("Customer_File");
        referencedata.child("OVpusHfpxNciCVz4LEAt2XxE3nA2").child("bihh6BewukOfurlAzotIJwuIsmp2").child(transactionSet);
                referencedata.child("order_status").setValue("Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessages("Successfully scanned");
                        showMessages(transactionSet);
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
                                                            sms.sendTextMessage(contactNoMMF.getText().toString(), null, message, sentPI, deliveredPI);
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
                                                        alertDialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        showMessages("Failed to update order");
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override

                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                showMessages("Order does not exist");
                            }
                        });
            }
        });
        alertDialog.show();

    }

    //LifeCycle~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

    //LifeCycle~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //IMAGE DETECTION~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void imageCapture()
    {
        IntentIntegrator integrator =  IntentIntegrator.forSupportFragment(MapMerchantFragment.this);
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
                if(transactNoScan.equals(transactionNo))
                {
                    updateOrder(transactNoScan);
                }
                else
                {
                    showMessages("Failed");
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
            showMessages("Error to scan");
        }
    }

    //IMAGE DETECTION~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    ///Retrieving Data and Update~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///Input and Display Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //Map Displaying Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapGet);
        arrayListUserFile = new ArrayList<UserFile>();
        arrayListBusinessInfo = new ArrayList<TransactionHeaderFileModel>();
        mAuth = FirebaseAuth.getInstance();
        mGeocoder = new Geocoder(getActivity().getApplicationContext());
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1100);
        mLocationRequest.setFastestInterval(1100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection suspended. . .", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed. . .", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        map = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        }
        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getActivity());
                title.setText(marker.getTitle());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);

                TextView snippets = new TextView(getActivity());
                snippets.setTextColor(Color.GRAY);
                snippets.setText(marker.getSnippet());
                linearLayout.addView(title);
                linearLayout.addView(snippets);
                return linearLayout;

            }
        });
    }
    @Override
    public boolean onBackPressed() {
        if (map != null)
        {
            map.clear();
            return true;
        }
        else
        {
            map.clear();
            return false;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try
            {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MapMerchantFragment.TaskParser taskParser = new MapMerchantFragment.TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends  AsyncTask<String, Void, List<List<HashMap<String, String>>>>
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try
            {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points;
            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                map.addPolyline(polylineOptions);
            } else {
                Log.d("PauwiNaAko","OUT");
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mCurrentLocationMarker != null){
            mCurrentLocationMarker.remove();
        }
        mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        MarkerOptions mMarkerOption = new MarkerOptions();
        mMarkerOption.position(mLatLng);
        mMarkerOption.title("You");
        mMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mCurrentLocationMarker = map.addMarker(mMarkerOption);
        map.addMarker(mMarkerOption).showInfoWindow();
        float zoomLevel = 16.0f;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, zoomLevel));

        if(mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        locateCustomer(mLatLng);


    }
    private String getRequestURL(LatLng origin, LatLng dest)
    {
        String str_org = "origin=" + origin.latitude + "," +origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," +dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+API_KEY;
        String param = str_org + "&" + str_dest + "&" +sensor+ "&" +mode+"&"+key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
    }
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpsURLConnection httpsURLConnection = null;
        try
        {
            URL url = new URL(reqUrl);
            httpsURLConnection = (HttpsURLConnection)url.openConnection();
            httpsURLConnection.connect();
            inputStream = httpsURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null)
            {
                inputStream.close();
            }
            httpsURLConnection.disconnect();
        }
        return responseString;
    }
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_USER_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(mGoogleApiClient == null){
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
    public boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            }
            return false;
        }
        else{
            return true;
        }
    }


    //Map Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //Map Data to be display~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Close Map Data to be display~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}