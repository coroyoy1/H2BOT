package com.example.administrator.h2bot.customer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.administrator.h2bot.maps.DirectionsParser;
import com.example.administrator.h2bot.maps.DownloadUrl;
import com.example.administrator.h2bot.maps.GetDistance;
import com.example.administrator.h2bot.maps.MapMerchantActivity;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.firebase.geofire.GeoQuery;
//import com.firebase.geofire.GeoQueryDataEventListener;
//import com.firebase.geofire.GeoQueryEventListener;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.objects.WaterStationOrDealer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.http.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerMapFragment extends Fragment implements
        OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener{


    // User Permissions
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private static final int REQUEST_USER_LOCATION_CODE = 99;
    public static final String EXTRA_stationID = "station_id";
    public static final String EXTRA_stationName = "station_name";
    public String API_KEY = "";
    public static Boolean isExist = false;

    private ChildEventListener mChilExventListener;
    public FirebaseAuth mAuth;
    DatabaseReference userFileRef, businessRef, userLatLongRef;
    DatabaseReference usersLocRef;
    LocationManager locationManager;

    ArrayList<UserFile> arrayListUserFile;
    ArrayList<UserWSBusinessInfoFile> arrayListBusinessInfo;
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
    private GetDistance getDistance = null;
    private LatLng currentLocation;
    private List<UserFile> userFileList;
    private List<UserWSBusinessInfoFile> businessInfoFileListis;
    private List<UserLocationAddress> userLocationAddressList;
    private ArrayList<WaterStationOrDealer> thisList;

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

        arrayListUserFile = new ArrayList<UserFile>();
        arrayListBusinessInfo = new ArrayList<UserWSBusinessInfoFile>();
        arrayListMerchantLatLong = new ArrayList<UserLocationAddress>();

        mAuth = FirebaseAuth.getInstance();
        mGeocoder = new Geocoder(getActivity().getApplicationContext());


//        usersLocRef = FirebaseDatabase.getInstance().getReference("User_File").child(mAuth.getCurrentUser().getUid());
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;

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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
//            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        }

        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        map.setOnMarkerClickListener(marker -> {
            if (!marker.getTitle().equalsIgnoreCase("You")){
                updateBottomSheetContent(marker);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                marker.showInfoWindow();
            } else {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
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

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progressDialog.dismiss();
            }
        });
    }

    private void nav(LatLng dest){
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
                    UserWSBusinessInfoFile businessInfo = infoFile.getValue(UserWSBusinessInfoFile.class);
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
            Object[] transferData = new Object[6];
            transferData[0] = waterStationOrDealers;
            transferData[1] = currentLocation;
            transferData[2] = map;
            transferData[3] = API_KEY;
            transferData[4] = currentRadius;
            transferData[5] = CustomerMapFragment.this;
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
        LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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

    private void updateBottomSheetContent(Marker marker) {
        TextView stationName = bottomSheet.findViewById(R.id.stationName);
        TextView station_id = bottomSheet.findViewById(R.id.station_id);
        TextView business_hours = bottomSheet.findViewById(R.id.businessHours);
        TextView address = bottomSheet.findViewById(R.id.address);
        TextView distance = bottomSheet.findViewById(R.id.distance);
        TextView duration = bottomSheet.findViewById(R.id.duration);
        TextView deliveryMethod = bottomSheet.findViewById(R.id.deliveryMethod);
        Button orderBtn = bottomSheet.findViewById(R.id.orderBtn);

        stationName.setText(marker.getTitle());
        station_id.setText(marker.getTag().toString());
        waterTypeList.clear();

        FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File").child(marker.getTag().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()){
                            UserWSWDWaterTypeFile userWSWDWaterTypeFile = data.getValue(UserWSWDWaterTypeFile.class);
                            if(userWSWDWaterTypeFile.getWater_status().equalsIgnoreCase("active")){
                                waterTypeList.add(userWSWDWaterTypeFile);
                                isExist = true;
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

        FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(marker.getTag().toString())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String startTime = dataSnapshot.child("business_start_time").getValue(String.class);
                        String endTime = dataSnapshot.child("business_end_time").getValue(String.class);
                        String delivery_method = dataSnapshot.child("business_delivery_fee_method").getValue(String.class);
                        String deliveryPrice = dataSnapshot.child("business_delivery_fee").getValue(String.class);
                        business_hours.setText(startTime + " - " + endTime);
                        address.setText(dataSnapshot.child("business_address").getValue(String.class));
//                        deliveryMethod.setText(String.format(delivery_method + " - %.2f", deliveryPrice));
                        for(WaterStationOrDealer list: thisList){
                            if(list.getStationID().equalsIgnoreCase(marker.getTag().toString())){
                                distance.setText(list.getDistance());
                                duration.setText(list.getDuration());
                                break;
                            }
                        }
                        for(WaterStationOrDealer list: thisList){
                            if(list.getStationID().equalsIgnoreCase(marker.getTag().toString())){
                                nav(new LatLng(list.getLat(), list.getLng()));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
}
