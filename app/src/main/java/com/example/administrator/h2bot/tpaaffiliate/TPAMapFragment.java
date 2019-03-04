package com.example.administrator.h2bot.tpaaffiliate;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
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
public class TPAMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    // User Permissions
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrentLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;

    private ChildEventListener mChilExventListener;
    private Location mCurrentLocation;
    private Marker marker;
    LinearLayout linearLayout;
    public FirebaseAuth mAuth;
    DatabaseReference addressesRef, businessRef;
    DatabaseReference usersLocRef;
    LocationManager locationManager;

    ArrayList<UserFile> arrayListUserFile;
    ArrayList<UserWSBusinessInfoFile> arrayListBusinessInfo;

    Geocoder mGeocoder;
    List<Address> myListAddresses;
    List<UserWSBusinessInfoFile> myBusinessInfos;
    String myAddresses, stationName, userType, stationId, customerName;
    String addresses;
    LatLng latLong = null;

    private Map<Marker, Map<String, Object>> hashMapMarkers = new HashMap<>();
    private Map<String, Object> dataModel = new HashMap<>();

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;

    public TPAMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tpa_fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        arrayListUserFile = new ArrayList<UserFile>();
        arrayListBusinessInfo = new ArrayList<UserWSBusinessInfoFile>();

        mAuth = FirebaseAuth.getInstance();
        mGeocoder = new Geocoder(getActivity().getApplicationContext());



        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
    }

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

        GetAllStations();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity(), "" + marker.getTitle(), Toast.LENGTH_SHORT).show();
                if(!marker.getTitle().equalsIgnoreCase("You")){
                    updateBottomSheetContent(marker);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                else{
                    if(bottomSheetBehavior.STATE_COLLAPSED == BottomSheetBehavior.STATE_COLLAPSED){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
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
//        TextView stationName = bottomSheet.findViewById(R.id.stationName);
//        Button orderBtn = bottomSheet.findViewById(R.id.orderBtn);
//        stationName.setText(marker.getTitle());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        orderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                dialog.setCancelable(false);
//                dialog.setTitle("CONFIRMATION");
//                dialog.setMessage("Are you sure you want to accept this order?" );
//                dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
//                        snackBar();
//                    }
//                })
//                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                final AlertDialog alert = dialog.create();
//                alert.show();
//            }
//        });
    }


    public void snackBar(){
        View parentLayout = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "You can see the recipient's information in the IN PROGRESS menu."
                , Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snackbar.setAction("Okay", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(getResources().getColor(android.R.color.white ));
        snackbar.show();
    }

    public void GetAllStations(){

        addressesRef = FirebaseDatabase.getInstance().getReference("Order_File");
        businessRef = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");

        addressesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    OrderModel order = data.getValue(OrderModel.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        addressesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot data: dataSnapshot.getChildren()){
//                    UserFile user = data.getValue(UserFile.class);
//
//                    if(user.getUser_type().equalsIgnoreCase("Water Station") || user.getUser_type().equalsIgnoreCase("Water Dealer")
//                            && user.getUser_status().equalsIgnoreCase("active")){
//                        arrayListUserFile.add(user);
//
//                        customerName = user.getUser_firtname() + " " + user.getUser_lastname();
//                        userType = user.getUser_type();
//                        myAddresses = user.getUser_address();
//
//                        try {
//                            myListAddresses = mGeocoder.getFromLocationName(myAddresses, 1);
//                            if (myListAddresses != null) {
//                                Address location = myListAddresses.get(0);
//                                latLong = new LatLng(location.getLatitude(), location.getLongitude());
//
//                                String name = customerName;
//                                String type = "Type: " + userType;
//                                String address = "Address: " + myAddresses;
//                                String status = "Status: OPEN";
//
//                                map.addMarker(new MarkerOptions().position(latLong).title(name).snippet(type
//                                        + "\n" + address
//                                        + "\n" + status));
//                                map.addMarker(new MarkerOptions().position(latLong));
//                                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//                            }
//                        }
//                        catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        addressesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot data: dataSnapshot.getChildren()){
//
//                    UserFile users = data.getValue(UserFile.class);
//
//                    businessRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot data2: dataSnapshot.getChildren()){
//
//                                UserWSBusinessInfoFile info = data2.getValue(UserWSBusinessInfoFile.class);
//
//                                if(users.getUser_getUID().equals(info.getBusiness_id())){
//                                    if(users.getUser_type().equals("Customer")){
//                                        if(users.getUser_status().equalsIgnoreCase("active")){
//                                            arrayListUserFile.add(users);
//                                            arrayListBusinessInfo.add(info);
//
//                                            myAddresses = users.getUser_address();
//                                            stationName = info.getBusiness_name();
//                                            userType = users.getUser_type();
//                                            stationId = info.getBusiness_id();
//                                            try {
//                                                myListAddresses = mGeocoder.getFromLocationName(myAddresses, 1);
//                                                if (myListAddresses != null) {
//                                                    Address location = myListAddresses.get(0);
//                                                    latLong = new LatLng(location.getLatitude(), location.getLongitude());
//                                                    String status = "Status: OPEN";
//                                                    String type = "Type: " + userType;
//                                                    String address = "Address: " + myAddresses;
//                                                    stationId = "ID: " + stationId;
//                                                    map.addMarker(new MarkerOptions().position(latLong).title(stationName).snippet(stationId
//                                                            + "\n" + type
//                                                            + "\n" + address
//                                                            + "\n" + status));
//                                                    map.addMarker(new MarkerOptions().position(latLong));
//                                                    map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//                                                }
//                                            }
//                                            catch (Exception e){
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
