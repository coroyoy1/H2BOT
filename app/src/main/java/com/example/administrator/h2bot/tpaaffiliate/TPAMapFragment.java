package com.example.administrator.h2bot.tpaaffiliate;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.dealer.WPInProgressAccept;
import com.example.administrator.h2bot.maps.GetDistance;
import com.example.administrator.h2bot.models.AffiliateStationOrderModel;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.example.administrator.h2bot.models.UserWallet;
import com.example.administrator.h2bot.objects.WaterStationOrDealer;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class TPAMapFragment extends Fragment
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    FirebaseUser firebaseUser;
    String affiliatename, merchantno;
    String affiliateNo;
    // User Permissions
    String oldpointsaffiliatefinal,oldpointsstationfinal;
    Double newpointsaffiliatefinal,newpointsstationfinal;
    TextView noOfGallons,Profit,stationadd,fundAmt;
    private GoogleMap map;
    String pointsoldaffiliate, pointsoldstation, pointsnewaffiliate, pointsnewstation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    String stationaddress;
    private Marker mCurrentLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    public FirebaseAuth mAuth;
    DatabaseReference userFileRef, merchantRef, userLatLongRef, orderModel, businessRef;
    String stationName, userType, stationId, station_id_snip,orderno,customer_id;
    String customerAddress, orderDeliveryDate, orderDeliveryFee, orderPricePerGallon, orderQty, orderTotalAmount, orderWaterType;
    String stationAddress, stationBussinessName, businessTelNo;
    double points,pointsNeeded;
    LatLng latLong = null;
    double latitude, longitude;
    private BottomSheetBehavior sheetBehavior, bottomSheetBehavior;
    private View bottomSheet, bottomSheetCustomer;
    private List<MerchantCustomerFile> merchantCustomerFileList;
    private List<UserLocationAddress> userLocationAddressList;
    private List<OrderModel> orderModelList;
    private List<UserWSBusinessInfoFile> businessInfoFileList;
    private ArrayList<WaterStationOrDealer> thisList;
    private List<UserFile> userFileList;
    private List<WaterStationOrDealer> affiliateModel;
    private LatLng currentLocation;
    private GetDistance getDistance = null;
    public String API_KEY = "";
    private SeekBar radius;
    private TextView currentRadius;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    double oldpointstation, newpointstation;
    double oldpointsaffiliate, newpointsaffiliate;
    public TPAMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tpa_fragment_map, container, false);

        merchantCustomerFileList = new ArrayList<>();
        userLocationAddressList = new ArrayList<>();
        orderModelList = new ArrayList<>();
        businessInfoFileList = new ArrayList<>();
        userFileList = new ArrayList<>();
        affiliateModel = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getBusiness();
        getUserLatLng();
        getOrderModel();
        getBusinessFile();
        getUserFile();

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        noOfGallons = view.findViewById(R.id.noOfGallons);
        Profit = view.findViewById(R.id.Profit);
        stationadd = view.findViewById(R.id.stationadd);
        fundAmt = view.findViewById(R.id.fundAmt);

        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        API_KEY = getResources().getString(R.string.google_maps_key);
        radius = getActivity().findViewById(R.id.seekBar);
        currentRadius = getActivity().findViewById(R.id.current_radius);
        radius.setOnSeekBarChangeListener(seekBarChangeListener);

        //bottomSheetCustomer = view.findViewById(R.id.bottom_sheet_customer);
        //bottomSheetCustomer.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentRadius.setText(progress + " km");
            showNearest();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        }
        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity(), "" + marker.getTitle(), Toast.LENGTH_SHORT).show();
                //bottomSheetCustomer.setVisibility(View.VISIBLE);
                if(!marker.getTitle().equalsIgnoreCase("You")){
                    updateBottomSheetContent(marker);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                else{
                    if(bottomSheetBehavior.STATE_COLLAPSED == BottomSheetBehavior.STATE_COLLAPSED){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        bottomSheetCustomer.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

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

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               // bottomSheetCustomer.setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void getBusiness(){
        merchantRef = FirebaseDatabase.getInstance().getReference("Merchant_File");
        merchantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot infoFile : dataSnapshot.getChildren()) {
                    for(DataSnapshot infoFile2 : infoFile.getChildren()){
                        MerchantCustomerFile merchantCustomerFile = infoFile2.getValue(MerchantCustomerFile.class);
                        merchantCustomerFileList.add(merchantCustomerFile);
                    }
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserFile(){
        userFileRef = FirebaseDatabase.getInstance().getReference("User_File");
        userFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot infoFile : dataSnapshot.getChildren()) {
                    UserFile userFile = infoFile.getValue(UserFile.class);
                    userFileList.add(userFile);
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserLatLng(){
        userLatLongRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
        userLatLongRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot latlongFile : dataSnapshot.getChildren()) {
                    UserLocationAddress locationFile = latlongFile.getValue(UserLocationAddress.class);
                    userLocationAddressList.add(locationFile);
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBusinessFile(){
        businessRef = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        businessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData3 : dataSnapshot.getChildren()) {
                    UserWSBusinessInfoFile userData4 = userData3.getValue(UserWSBusinessInfoFile.class);
                    businessInfoFileList.add(userData4);
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOrderModel(){
        orderModel = FirebaseDatabase.getInstance().getReference("Customer_File");
        orderModel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot order : dataSnapshot.getChildren()) {
                    for(DataSnapshot userData2 : order.getChildren()) {
                        for (DataSnapshot userData3 : userData2.getChildren()) {
                            OrderModel userData4 = userData3.getValue(OrderModel.class);
                            orderModelList.add(userData4);
                        }
                    }
                }

                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getList(){
        if(orderModelList.size() == 0 || merchantCustomerFileList.size() == 0 || businessInfoFileList.size() == 0
                || userLocationAddressList.size() == 0 || userFileList.size() == 0){
            return;
        }

        affiliateModel = new ArrayList<>();

        for(MerchantCustomerFile merchantCustomerFile: merchantCustomerFileList){
            for(OrderModel orderModel: orderModelList){
                String orderStatus = orderModel.getOrder_status();
                if(merchantCustomerFile.getCustomer_id().equalsIgnoreCase(orderModel.getOrder_customer_id())
                        && orderModel.getOrder_status().equalsIgnoreCase("Broadcasting")
                        && merchantCustomerFile.getStation_id().equalsIgnoreCase(orderModel.getOrder_merchant_id())){
                    orderno = orderModel.getOrder_no();
                    customer_id = orderModel.getOrder_customer_id();
                    stationId = orderModel.getOrder_merchant_id();

                    for(UserLocationAddress userLocationAddress: userLocationAddressList){
                        if(userLocationAddress.getUser_id().equalsIgnoreCase(merchantCustomerFile.getStation_id())){
                            latitude = Double.parseDouble(userLocationAddress.getUser_latitude());
                            longitude = Double.parseDouble(userLocationAddress.getUser_longtitude());
                            break;
                        }
                    }

                    for(UserFile list: userFileList){
                        if(merchantCustomerFile.getStation_id().equalsIgnoreCase(list.getUser_getUID())
                                && list.getUser_type().equalsIgnoreCase("Water Station")){
                            userType = list.getUser_type();
                            break;
                        }
                    }

                    for(UserWSBusinessInfoFile infoFile: businessInfoFileList){
                        if(infoFile.getBusiness_id().equalsIgnoreCase(stationId)){
                            stationName = infoFile.getBusiness_name();
                            break;
                        }
                    }

                    String type = "Type: " + userType;

                    WaterStationOrDealer affiliate = new WaterStationOrDealer();
                    affiliate.setStation_dealer_name(stationName);
                    affiliate.setUserType(userType);
                    affiliate.setLat(latitude);
                    affiliate.setLng(longitude);
                    affiliate.setType(type);
                    affiliate.setStationID(stationId);
                    affiliateModel.add(affiliate);
                }
            }
        }
    }

    public void showNearest(){
            if(affiliateModel.size() != 0){
            Object[] transferData = new Object[7];
            transferData[0] = affiliateModel;
            transferData[1] = currentLocation;
            transferData[2] = map;
            transferData[3] = API_KEY;
            transferData[4] = currentRadius;
            transferData[5] = null;
            transferData[6] = TPAMapFragment.this;
            if(getDistance == null) {
                getDistance = new GetDistance();
                getDistance.execute(transferData);
            }
            else
                getDistance.Display();
        }
    }

    public void setList(ArrayList<WaterStationOrDealer> thisList){
        this.thisList = thisList;
    }

    public boolean checkUserLocationPermission(){
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

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mCurrentLocationMarker != null){
            mCurrentLocationMarker.remove();
        }
        LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation = mLatLng;

        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, zoomLevel));

        if(mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        showNearest();
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

    private void updateBottomSheetContent(Marker marker) {
        TextView stationName = bottomSheet.findViewById(R.id.stationName);
        TextView fundAmt = bottomSheet.findViewById(R.id.fundAmt);
        Log.d("Points",""+pointsNeeded);
        Button orderBtn = bottomSheet.findViewById(R.id.orderBtn);

//        Button scanCode = bottomSheetCustomer.findViewById(R.id.scanCode);
//        Button callBtn = bottomSheetCustomer.findViewById(R.id.callBtn);
//        Button sendMsg = bottomSheetCustomer.findViewById(R.id.sendMsg);

        stationName.setText(marker.getTitle());
        String amtNeeded = fundAmt.getText().toString();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        userFileRef = FirebaseDatabase.getInstance().getReference("User_File");
        businessRef = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        userLatLongRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
        orderModel = FirebaseDatabase.getInstance().getReference("Customer_File");
        userFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    UserFile userFile = userData.getValue(UserFile.class);
                    businessRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot infoFile : dataSnapshot.getChildren()) {
                                userLatLongRef.addValueEventListener(new ValueEventListener() {
                                    UserWSBusinessInfoFile businessInfo = infoFile.getValue(UserWSBusinessInfoFile.class);
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot latlongFile : dataSnapshot.getChildren()) {
                                            UserLocationAddress locationFile = latlongFile.getValue(UserLocationAddress.class);
                                            if (userFile.getUser_getUID().equals(businessInfo.getBusiness_id())
                                                    && businessInfo.getBusiness_id().equals(locationFile.getUser_id())) {
                                                if (userFile.getUser_type().equals("Water Station")) {
                                                    if (userFile.getUser_status().equalsIgnoreCase("Active")) {
                                                        orderModel.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot order : dataSnapshot.getChildren()) {
                                                                    for(DataSnapshot userData2 : order.getChildren()) {
                                                                        for (DataSnapshot userData3 : userData2.getChildren()) {
                                                                            OrderModel userData4 = userData3.getValue(OrderModel.class);
                                                                            String statusOrder = userData4.getOrder_status();
                                                                            Log.d("Kaykay", "" + statusOrder);
                                                                            if (statusOrder.equals("Broadcasting")) {

                                                                                Log.d("sAddress",userData3.child("order_merchant_id")+"="+businessInfo.getBusiness_id());
                                                                                if (userData3.child("order_merchant_id").getValue(String.class).equals(businessInfo.getBusiness_id()))
                                                                                {
                                                                                   stationaddress = businessInfo.getBusiness_address();
                                                                                    Log.d("sAddress","hi"+stationAddress);
                                                                                }
                                                                                noOfGallons.setText(userData4.getOrder_qty());
                                                                                Profit.setText(userData4.getOrder_delivery_fee());
                                                                                stationadd.setText(stationaddress);
                                                                                fundAmt.setText(userData3.child("order_partial_amt").getValue(String.class));
                                                                                String type = "Type: " + userType;
                                                                                station_id_snip = stationId;


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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
            }
        });
//        sendMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("smsto:09234288302");
//                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//                intent.putExtra("sms_body", "");
//                startActivity(intent);
//            }
//        });
//
//        callBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel: 09234288302"));
//                startActivity(intent);
//                Toast.makeText(getActivity(), "Calling....", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        scanCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), TPAScanCodeActivity.class));
//            }
//        });
//
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsNeeded = Double.parseDouble(fundAmt.getText().toString());
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(false);
                dialog.setTitle("CONFIRMATION");
                dialog.setMessage("Use " + amtNeeded + " of load?");
                dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        statePoints();
                }
                })

                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });
    }

    public void statePoints()
    {
        DatabaseReference wallet = FirebaseDatabase.getInstance().getReference("User_Wallet").child(firebaseUser.getUid());
        wallet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                points = Double.parseDouble(dataSnapshot.child("user_points").getValue(String.class));
                UserWallet userWallet = dataSnapshot.getValue(UserWallet.class);
                if (userWallet != null)
                {
                    String points = userWallet.getUser_points();
                    double pointAff = Double.parseDouble(points);

                    if (pointAff >= pointsNeeded)
                    {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Wallet");
                        databaseReference.child(stationId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserWallet userWallet1 = dataSnapshot.getValue(UserWallet.class);
                                if (userWallet1 != null)
                                {
                                    double stationPoint = Double.parseDouble(userWallet1.getUser_points());
                                    double pointAffRemaining = pointAff - pointsNeeded;
                                    double pointStationAdded = stationPoint + pointsNeeded;

                                    String getPointAff = String.valueOf(pointAffRemaining);
                                    String getPointStation = String.valueOf(pointStationAdded);

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Wallet");
                                        reference.child(firebaseUser.getUid()).child("user_points")
                                                .setValue(getPointAff)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_Wallet");
                                                            reference1.child(stationId).child("user_points")
                                                                    .setValue(getPointStation)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                                    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
                                    reference3.child(stationId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            merchantno = dataSnapshot.child("business_tel_no").getValue(String.class);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    Log.d("Hello", stationId + "," + customer_id + "," + orderno);
                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                    reference2.child(customer_id).child(stationId).child(orderno).child("order_status").setValue("Accepted")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    String message = "Your broadcasted order#:" + orderno + " has  been accepted by an affiliate named :" + affiliatename + ". For further information about " + affiliatename;
                                                    String message2 = "Contact # of " + affiliatename + ":" + affiliateNo;
                                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                                                            != PackageManager.PERMISSION_GRANTED) {
                                                        Log.d("NmNakoko", "Hijhosdfgh");
                                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS},
                                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                    } else {
                                                        Log.d("NmNako", "Hi" + message);
                                                        SmsManager sms = SmsManager.getDefault();
                                                        Log.d("Log.d", "" + merchantno);
                                                        sms.sendTextMessage(merchantno, null, message, sentPI, deliveredPI);
                                                        sms.sendTextMessage(merchantno, null, message2, sentPI, deliveredPI);
                                                    }
                                                    addStationAffiliate();
                                                    TPAAcceptedFragment detail = new TPAAcceptedFragment();
                                                    AppCompatActivity activity = (AppCompatActivity)getContext();
                                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detail).addToBackStack(null).commit();
                                                    Bundle args1 = new Bundle();
                                                    args1.putString("transactionno", orderno);
                                                    args1.putString("transactioncustomerid", customer_id);
                                                    args1.putString("stationid", stationId);
                                                    detail.setArguments(args1);
                                                }
                                            });
                                    Log.d("stations", "hahastation" + stationId + orderno + customer_id);
                                    Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                                    snackBar();
                                }
                                else
                                {
                                    snackBar2();
                                }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "No more points", Toast.LENGTH_LONG).show();
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
//                                if(points>=pointsNeeded)
//                                {
//
////                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_File");
////                                    reference1.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                            affiliatename = dataSnapshot.child("user_firstname").getValue(String.class) + " " + dataSnapshot.child("user_lastname").getValue(String.class);
////                                            affiliateNo = dataSnapshot.child("user_phone_no").getValue(String.class);
////                                        }
////                                        @Override
////                                        public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                        }
////                                    });
////
////                                    Log.d("idid","kakaloka"+firebaseUser.getUid());
////                                    DatabaseReference wallet1 = FirebaseDatabase.getInstance().getReference("User_Wallet").child(firebaseUser.getUid());
////                                    wallet1.addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////                                            Log.d("pistyyawa",dataSnapshot.child("user_id").getValue(String.class)+""+firebaseUser.getUid());
////                                            if(dataSnapshot.child("user_id").getValue(String.class).equals(firebaseUser.getUid()))
////                                            {
////                                                oldpointsaffiliatefinal = dataSnapshot.child("user_points").getValue(String.class);
////
////                                            }
////                                            Log.d("pasudla ko",Double.valueOf(oldpointsaffiliatefinal)+"kakalokagago ka");
////                                        }
////                                        @Override
////                                        public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                        }
////                                    });
////                                    DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference("User_Wallet").child(stationId);
////                                    reference5.addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                            UserWallet userWallet = dataSnapshot.getValue(UserWallet.class);
////                                            if (userWallet != null)
////                                            {
////                                                oldpointsstationfinal = userWallet.getUser_points();
////                                                Log.d("pasudla ko gago",Double.valueOf(oldpointsstationfinal)+"kakalokagago ka");
////                                                newpointsaffiliatefinal = Double.valueOf(oldpointsaffiliatefinal) - pointsNeeded;
////                                                newpointsstationfinal = Double.valueOf(oldpointsstationfinal) + pointsNeeded;
////                                                Log.d("newpointstation", "" + newpointstation);
////                                                FirebaseDatabase.getInstance().getReference("User_Wallet").child(firebaseUser.getUid()).child("user_points").setValue(String.valueOf(newpointsaffiliatefinal.toString()));
////                                                FirebaseDatabase.getInstance().getReference("User_Wallet").child(stationId).child("user_points").setValue(String.valueOf(newpointsstationfinal.toString()));
////                                            }
////                                        }
////
////                                        @Override
////                                        public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                        }
////                                    });
//                                    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
//                                    reference3.child(stationId).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            merchantno = dataSnapshot.child("business_tel_no").getValue(String.class);
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                    Log.d("Hello", stationId + "," + customer_id + "," + orderno);
//                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Customer_File");
//                                    reference2.child(customer_id).child(stationId).child(orderno).child("order_status").setValue("Accepted")
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//
//                                                    String message = "Your broadcasted order#:" + orderno + " has  been accepted by an affiliate named :" + affiliatename + ". For further information about " + affiliatename;
//                                                    String message2 = "Contact # of " + affiliatename + ":" + affiliateNo;
//                                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
//                                                            != PackageManager.PERMISSION_GRANTED) {
//                                                        Log.d("NmNakoko", "Hijhosdfgh");
//                                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS},
//                                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
//                                                    } else {
//                                                        Log.d("NmNako", "Hi" + message);
//                                                        SmsManager sms = SmsManager.getDefault();
//                                                        Log.d("Log.d", "" + merchantno);
//                                                        sms.sendTextMessage(merchantno, null, message, sentPI, deliveredPI);
//                                                        sms.sendTextMessage(merchantno, null, message2, sentPI, deliveredPI);
//                                                    }
//                                                    addStationAffiliate();
//                                                    TPAAcceptedFragment detail = new TPAAcceptedFragment();
//                                                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detail).addToBackStack(null).commit();
//                                                    Bundle args1 = new Bundle();
//                                                    args1.putString("transactionno", orderno);
//                                                    args1.putString("transactioncustomerid", customer_id);
//                                                    args1.putString("stationid", stationId);
//                                                    detail.setArguments(args1);
//                                                }
//                                            });
//                                    Log.d("stations", "hahastation" + stationId + orderno + customer_id);
//                                    Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
//                                    snackBar();
//                                }
//                                else
//                                {
//                                    snackBar2();
//                                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    });
    }


    public void snackBar(){
        View parentLayout = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "You can see the recipient's information in the IN PROGRESS menu."
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
    public void snackBar2(){
        View parentLayout = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "Insufficient points, please reload.", Snackbar.LENGTH_LONG);
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
    public void attemptToExit()
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to exit the application?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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
    public void addStationAffiliate()
    {
        Log.d("Hi",""+orderno);
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference3.child(customer_id).child(stationId).child(orderno).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("HiHi",""+orderno);
                customerAddress = dataSnapshot.child("order_address").getValue(String.class);
                orderDeliveryDate = dataSnapshot.child("order_delivery_date").getValue(String.class);
                orderDeliveryFee = dataSnapshot.child("order_delivery_fee").getValue(String.class);
                orderPricePerGallon = dataSnapshot.child("order_address").getValue(String.class);
                orderQty = dataSnapshot.child("order_address").getValue(String.class);
                orderTotalAmount = dataSnapshot.child("order_address").getValue(String.class);
                orderWaterType = dataSnapshot.child("order_address").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        databaseReference4.child(stationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stationAddress = dataSnapshot.child("order_address").getValue(String.class);
                stationBussinessName = dataSnapshot.child("order_address").getValue(String.class);
                businessTelNo = dataSnapshot.child("order_address").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        AffiliateStationOrderModel ASmodel = new AffiliateStationOrderModel(firebaseUser.getUid(),
                stationId,
                customer_id,
                orderno,
                "Accepted");
        FirebaseDatabase.getInstance().getReference("Affiliate_WaterStation_Order_File").child(firebaseUser.getUid()).child(stationId).child(orderno).setValue(ASmodel);
    }
}
