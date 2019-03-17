package com.example.administrator.h2bot.mapmerchant;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.maps.DirectionsParser;
import com.example.administrator.h2bot.maps.IOBackPressed;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserLocationAddress;
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

    Button order, launchscan, sms, call, dispatched;
    LinearLayout linearSMSSender, linearOrderSender;

    CircleImageView customerImage;
    TextView orderNoMMF, customerMMF, contactNoMMF, waterTypeMMF, quantityMMF,
    pricePerGallonMMF, addressMMF, dateDeliveredMMF, deliveryFeeMMF, methodMMF,
    totalPriceMMF;

    Button closeOrderDialog;

    String orderNoString, customerString, contactString, waterTypeString, quantityString,
            pricePerGallonString, addressString, dateDeliveredString, deliveryFeeString,
            methodString, totalPriceString;
    private String customerId;


    ///Input and Display Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public MapMerchantFragment()
    {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_merchant_fragment, container, false);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            transactionNo = bundle.getString("transactionno");
            customerNo = bundle.getString("transactioncustomer");
        }
        inputData(view);
        order.setOnClickListener(this);
        launchscan.setOnClickListener(this);
        call.setOnClickListener(this);
        dispatched.setOnClickListener(this);
        return view;
    }

    //View Inputs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void inputData(View view)
    {
        order = view.findViewById(R.id.orderDetails);
        launchscan = view.findViewById(R.id.orderLaunchScan);
        sms = view.findViewById(R.id.launchSMS);
        call = view.findViewById(R.id.launchCall);
        dispatched = view.findViewById(R.id.orderDispatched);

        linearSMSSender = view.findViewById(R.id.linearSMSDetails);
        linearOrderSender = view.findViewById(R.id.linearOrderDetails);

        linearSMSSender.setVisibility(View.GONE);
        launchscan.setVisibility(View.GONE);
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
                break;
            case R.id.launchCall:
                break;
            case R.id.launchSMS:
                break;
            case R.id.orderDispatched:
                attemptToDispatched();
                break;
        }

    }

    //View Inputs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    ///Retrieving Data and Update~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Dialog Display Order~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void orderDetailsDialog()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.anim.fab_scale_up);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.a_order_details, null);

        dialogDataFromOrder(dialogView);

        dialogOrderRetrieveData();

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();

        closeOrderDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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


    //Warning Dialog~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///Retrieving Data and Update~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void dialogOrderRetrieveData()
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
                                                            || orderModel.getOrder_status().equals("Accepted"))
                                                    {
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
        reference.child(firebaseUser.getUid()).child(customerNo)
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
                                                    reference1.child(customerId).addValueEventListener(new ValueEventListener() {
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


    ///Retrieving Data and Update~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///Input and Display Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //Map Displaying Consistency~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
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
    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
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
