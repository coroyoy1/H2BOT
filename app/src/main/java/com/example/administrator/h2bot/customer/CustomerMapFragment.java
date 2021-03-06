package com.example.administrator.h2bot.customer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.maps.GetDistance;
import com.example.administrator.h2bot.maps.MapMerchantActivity;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerMapFragment extends Fragment implements
        OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener{


    // User Permissions

    public LatLng navLatLng, navCurrentLocation;
    private Dialog dialog;
    public GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;


    private static final int REQUEST_USER_LOCATION_CODE = 99;
    public static final String EXTRA_stationID = "station_id";
    public static final String EXTRA_stationName = "station_name";
    public String API_KEY = "";
    public static Boolean isExist = false;
    public LatLng mLatLng;
    private Circle circle;

    private ChildEventListener mChilExventListener;
    public FirebaseAuth mAuth;
    DatabaseReference userFileRef, businessRef, userLatLongRef;
    DatabaseReference usersLocRef;
    LocationManager locationManager;

    ArrayList<UserFile> arrayListUserFile;
    ArrayList<WSBusinessInfoFile> arrayListBusinessInfo;
    ArrayList<UserLocationAddress> arrayListMerchantLatLong;
    List<WaterStationOrDealer> waterStationOrDealers;

    Geocoder mGeocoder;
    List<Address> myListAddresses;
    String stationAddress, stationName, userType, stationId, station_id_snip;
    LatLng latLong = null;
    double latitude, longitude;

    private Map<Marker, Map<String, Object>> hashMapMarkers = new HashMap<>();
    private Map<String, Object> dataModel = new HashMap<>();

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    ProgressDialog progressDialog;
    private SeekBar radius;
    private TextView currentRadius;
    private Button searchButton, legendBtn;
    private GetDistance getDistance = null;
    public LatLng currentLocation;
    private List<UserFile> userFileList;
    private List<WSBusinessInfoFile> businessInfoFileListis;
    private List<UserLocationAddress> userLocationAddressList;
    private ArrayList<WaterStationOrDealer> thisList;
    private ArrayList<WaterStationOrDealer> searchList;

    private ArrayList<UserWSWDWaterTypeFile> waterTypeList;
    CustomerWaterTypeAdapter waterTypeAdapter;
    RecyclerView recyclerView;

    public CustomerMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_map, container, false);
        userFileList = new ArrayList<>();
        dialog = new Dialog(getActivity());
        businessInfoFileListis = new ArrayList<>();
        userLocationAddressList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        waterTypeList = new ArrayList<UserWSWDWaterTypeFile>();

        getUserFile();
        getLatLng();
        getBusiness();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading map...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.show();
        API_KEY = getResources().getString(R.string.google_maps_key);
        radius = getActivity().findViewById(R.id.seekBar);
        currentRadius = getActivity().findViewById(R.id.current_radius);
        radius.setOnSeekBarChangeListener(seekBarChangeListener);
        radius.setProgress(1);
        searchButton = view.findViewById(R.id.searchButton);
        legendBtn = view.findViewById(R.id.legendBtn);
        legendBtn.setOnClickListener(legend);

        arrayListUserFile = new ArrayList<UserFile>();
        arrayListBusinessInfo = new ArrayList<WSBusinessInfoFile>();
        arrayListMerchantLatLong = new ArrayList<UserLocationAddress>();

        mAuth = FirebaseAuth.getInstance();
        mGeocoder = new Geocoder(getActivity().getApplicationContext());


        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyMerchants();
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED)
                    return;
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    public View.OnClickListener legend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ShowLegend();
        }
    };

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
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }

        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        map.setOnMarkerClickListener(marker -> {
            if (!marker.getTitle().equalsIgnoreCase("You")){
                updateBottomSheetContent(marker);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                marker.showInfoWindow();
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                marker.showInfoWindow();
            }
            return false;
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

        map.setOnMapClickListener(latLng -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            showNearest();
        });

        map.setOnMapLoadedCallback(() -> progressDialog.dismiss());
    }

    public void nav(LatLng dest){
        String url = getRequestUrl(currentLocation, dest);
        MapMerchantActivity mapMerchantActivity = new MapMerchantActivity();
        mapMerchantActivity.setMap(map);
        MapMerchantActivity.TaskRequestDirections taskRequestDirections = new MapMerchantActivity.TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    private String getRequestUrl(LatLng origin, LatLng dest){
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+API_KEY;
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
    }

    private void getLatLng(){
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

    private void getBusiness(){
        businessRef = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        businessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot infoFile : dataSnapshot.getChildren()) {
                    WSBusinessInfoFile businessInfo = infoFile.getValue(WSBusinessInfoFile.class);
                    businessInfoFileListis.add(businessInfo);
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserFile(){
        userFileRef = FirebaseDatabase.getInstance().getReference("User_File");
        userFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    UserFile userFile = userData.getValue(UserFile.class);
                    userFileList.add(userFile);
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getList(){
        waterStationOrDealers = new ArrayList<>();
        for(int i = 0; i < userFileList.size(); i++){
            for(int x = 0; x < businessInfoFileListis.size(); x++){
                for(int z = 0; z < userLocationAddressList.size(); z++){
                    if(userFileList.get(i).getUser_getUID().equalsIgnoreCase(businessInfoFileListis.get(x).getBusiness_id())
                        && businessInfoFileListis.get(x).getBusiness_id().equalsIgnoreCase(userLocationAddressList.get(z).getUser_id())){
                        if(userFileList.get(i).getUser_type().equalsIgnoreCase("Water Station")
                                || userFileList.get(i).getUser_type().equalsIgnoreCase("Water Dealer")){
                            if(userFileList.get(i).getUser_status().equalsIgnoreCase("Active")){
                                arrayListUserFile.add(userFileList.get(i));
                                arrayListBusinessInfo.add(businessInfoFileListis.get(x));
                                arrayListMerchantLatLong.add(userLocationAddressList.get(z));

                                stationId = businessInfoFileListis.get(x).getBusiness_id();
                                stationAddress = businessInfoFileListis.get(x).getBusiness_address();
                                stationName = businessInfoFileListis.get(x).getBusiness_name();
                                userType = userFileList.get(i).getUser_type();

                                latitude = Double.parseDouble(userLocationAddressList.get(z).getUser_latitude());
                                longitude = Double.parseDouble(userLocationAddressList.get(z).getUser_longtitude());

                                latLong = new LatLng(latitude, longitude);
                                final double RADIUS = 0.0062714012;

                                String status = "Status: OPEN";
                                String type = "Type: " + userType;
                                station_id_snip = stationId;
                                Log.d("TAG: ", "I was here");

                                WaterStationOrDealer waterStationOrDealer = new WaterStationOrDealer();
                                waterStationOrDealer.setStation_dealer_name(stationName);
                                waterStationOrDealer.setUserType(userType);
                                waterStationOrDealer.setLat(latitude);
                                waterStationOrDealer.setLng(longitude);
                                waterStationOrDealer.setStatus(status);
                                waterStationOrDealer.setType(type);
                                waterStationOrDealer.setStationID(stationId);
                                waterStationOrDealers.add(waterStationOrDealer);
                            }
                        }
                    }
                }
            }
        }
    }

    public void showNearest(){
        if(userFileList.size() != 0 && businessInfoFileListis.size() != 0 && userLocationAddressList.size() != 0){
            Object[] transferData = new Object[7];
            transferData[0] = waterStationOrDealers;
            transferData[1] = currentLocation;
            navCurrentLocation = currentLocation;
            transferData[2] = map;
            transferData[3] = API_KEY;
            transferData[4] = currentRadius;
            transferData[5] = CustomerMapFragment.this;
            transferData[6] = null;
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

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_USER_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        map.clear();
        mLastLocation = location;
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation = mLatLng;

        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));

        if (mGoogleApiClient != null) {
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

    public void getUser_WS_WD_Water_Type_File(Marker marker){
        FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File").child(marker.getTag().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()){
                            UserWSWDWaterTypeFile userWSWDWaterTypeFile = data.getValue(UserWSWDWaterTypeFile.class);
                            if(userWSWDWaterTypeFile.getWater_status().equalsIgnoreCase("available")){
                                waterTypeList.add(userWSWDWaterTypeFile);
                                isExist = true;
                            }
                            else{
                                isExist = false;
                            }
                        }
                        waterTypeAdapter = new CustomerWaterTypeAdapter(getActivity(), waterTypeList);
                        recyclerView.setAdapter(waterTypeAdapter);
                        Toast.makeText(getActivity(), "Size: " + waterTypeList.size(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateBottomSheetContent(Marker marker) {
        TextView stationName = bottomSheet.findViewById(R.id.stationName);
        TextView station_id = bottomSheet.findViewById(R.id.station_id);
        TextView business_hours = bottomSheet.findViewById(R.id.businessHours);
        TextView address = bottomSheet.findViewById(R.id.address);
        TextView distance = bottomSheet.findViewById(R.id.distance);
        TextView duration = bottomSheet.findViewById(R.id.duration);
        Button orderBtn = bottomSheet.findViewById(R.id.orderBtn);

        stationName.setText(marker.getTitle());
        station_id.setText(marker.getTag().toString());
        waterTypeList.clear();

        getUser_WS_WD_Water_Type_File(marker);

        for(WSBusinessInfoFile list: businessInfoFileListis){
            String id1 = list.getBusiness_id();
            String id2 = marker.getTag().toString();
            if(list.getBusiness_id().equalsIgnoreCase(marker.getTag().toString())){
                String startTime = list.getBusiness_start_time();
                String endTime = list.getBusiness_end_time();
                String delivery_method = list.getBusiness_delivery_fee_method();
                String deliveryPrice = list.getBusiness_delivery_fee();
                String contact_no = list.getBusiness_tel_no();
                business_hours.setText(startTime + " - " + endTime);
                address.setText(list.getBusiness_address());
                //                        deliveryMethod.setText(String.format(delivery_method + " - %.2f", deliveryPrice));
                for(WaterStationOrDealer list2: thisList){
                    if(list2.getStationID().equalsIgnoreCase(marker.getTag().toString())){
                        distance.setText(list2.getDistance());
                        duration.setText(list2.getDuration());
                        break;
                    }
                }
                for(WaterStationOrDealer list2: thisList){
                    if(list2.getStationID().equalsIgnoreCase(marker.getTag().toString())){
                        map.clear();
                        showNearest();
                        navLatLng = new LatLng(list2.getLat(), list2.getLng());
                        nav(new LatLng(list2.getLat(), list2.getLng()));
                        Log.d("Nav LatLong: ", navLatLng.toString());
                        break;
                    }
                }
                break;
            }
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        orderBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("stationId", stationName.toString());
            Intent detailIntent = new Intent(getActivity(), CustomerChatbotActivity.class);
            detailIntent.putExtra(EXTRA_stationID, station_id.getText().toString());
            detailIntent.putExtra(EXTRA_stationName, stationName.getText().toString());
            startActivity(detailIntent);
        });
    }
    public void setSearchList(ArrayList<WaterStationOrDealer> searchList){
        this.searchList = searchList;
    }

    public void NearbyMerchants() {
        RecyclerView mRecyclerView;
        SearchMerchantAdapter searchMerchantAdapter;
        TextView closeDialog, noMerchant, countMerchantsNearYou;

        dialog.setContentView(R.layout.search_merchant_popup);
        closeDialog = dialog.findViewById(R.id.closeDialog);
        noMerchant = dialog.findViewById(R.id.noMerchant);
        countMerchantsNearYou = dialog.findViewById(R.id.countMerchantsNearYou);
        mRecyclerView = dialog.findViewById(R.id.mRecyclerView);
        if(searchList.size() == 0){
            noMerchant.setVisibility(View.VISIBLE);
        }
        else{
            noMerchant.setVisibility(View.GONE);
            if(searchList.size() == 0){
                countMerchantsNearYou.setText("NO MERCHANTS NEAR YOU");
            }
            else if(searchList.size() == 1){
                countMerchantsNearYou.setText("1 MERCHANT NEAR YOU");
            }
            else{
                countMerchantsNearYou.setText(String.valueOf(searchList.size()) + " MERCHANTS NEAR YOU");
            }
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            searchMerchantAdapter = new SearchMerchantAdapter(getActivity(), searchList);
            mRecyclerView.setAdapter(searchMerchantAdapter);
        }
        String a = String.valueOf(searchList.size());
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void ShowLegend() {
        TextView closeDialog;
        dialog.setContentView(R.layout.legend_popup);
        closeDialog = dialog.findViewById(R.id.closeDialog);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
