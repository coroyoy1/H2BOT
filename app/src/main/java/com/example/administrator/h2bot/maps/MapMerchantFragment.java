package com.example.administrator.h2bot.maps;

import android.Manifest;
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
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.customer.CustomerChatbotActivity;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.example.administrator.h2bot.waterstation.WSPendingOrderAcceptDeclineFragment;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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

public class MapMerchantFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap map;
    private final static String API_KEY = "AIzaSyBC4uUz5QHs3X_TswSKUDWl4I98BqZ17ac";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrentLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    String transactNum, transactCust;
    double latOf = 0, longOf = 0;

    public FirebaseAuth mAuth;
    ArrayList<UserFile> arrayListUserFile;
    ArrayList<TransactionHeaderFileModel> arrayListBusinessInfo;

    Geocoder mGeocoder;
    List<Address> myListAddresses;
    String myAddress, stationName, userType, stationId;
    LatLng latLong = null, mLatLng = null, setLatLong=null;

    private Map<Marker, Map<String, Object>> hashMapMarkers = new HashMap<>();
    private Map<String, Object> dataModel = new HashMap<>();

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    String myAddresses;

    public MapMerchantFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_direct, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        arrayListUserFile = new ArrayList<UserFile>();
        arrayListBusinessInfo = new ArrayList<TransactionHeaderFileModel>();

        mAuth = FirebaseAuth.getInstance();
        mGeocoder = new Geocoder(getActivity().getApplicationContext());
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            transactNum = bundle.getString("TransactNoSeen1");
        }
        map = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        }
        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        // SELECT * FROM TABLE
        DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("User_File");
        DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");

        businessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    TransactionHeaderFileModel users = data.getValue(TransactionHeaderFileModel.class);
                    if(users.getMerchant_id().equals(mAuth.getCurrentUser().getUid()) && users.getTrans_no().equals(transactNum)) {
                        addressesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data2 : dataSnapshot.getChildren()) {
                                    UserFile info = data2.getValue(UserFile.class);

                                    if (info.getUser_getUID().equals(users.getCustomer_id())) {
                                        arrayListUserFile.add(info);
                                        arrayListBusinessInfo.add(users);
                                        myAddresses = info.getUser_address();
                                        try {
                                            myListAddresses = mGeocoder.getFromLocationName(myAddresses, 1);
                                            if (myListAddresses != null) {
                                                Address location = myListAddresses.get(0);
                                                latLong = new LatLng(location.getLatitude(), location.getLongitude());
                                                String address = info.getUser_address();
                                                String fullname = info.getUser_lastname() + ", " + info.getUser_firtname();
                                                map.addMarker(new MarkerOptions().position(latLong).snippet("Customer Name: " + fullname + "\n" + "Address: " + address));
                                                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data.", Toast.LENGTH_SHORT).show();
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
    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Bundle bundle = this.getArguments();
//        if(bundle != null)
//        {
//            transactNum = bundle.getString("TransactNoSeen1");
//            transactCust = bundle.getString("TransactCustSeen1");
//
//        }
//        map = googleMap;
//        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//            buildGoogleApiClient();
//            map.setMyLocationEnabled(true);
//            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
//        }
//        float zoomLevel = 16.0f;
//        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");//.child(transactNum);
//        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_File");//.child(transactCust);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    String transNoId = postSnapshot.child("trans_no").getValue(String.class);
//                    if(transactNum.equals(transNoId))
//                    {
//                    String customerid = dataSnapshot.child("customer_id").getValue(String.class);
//                    databaseReference1.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
//                                String userCustomerid = postSnap.child("user_getUID").getValue(String.class);
//                                if (transactCust.equals(userCustomerid)) {
//                                    myAddresses = postSnap.child("user_address").getValue(String.class);
//                                    try {
//                                        myListAddresses = mGeocoder.getFromLocationName(myAddresses, 1);
//                                        if (myListAddresses != null) {
//                                            Address location = myListAddresses.get(0);
//                                            latLong = new LatLng(location.getLatitude(), location.getLongitude());
//                                            String address = myAddress;
//                                            String fullname = dataSnapshot.child("user_lastname").getValue(String.class) + ", " + dataSnapshot.child("user_firtname").getValue(String.class);
//                                            map.addMarker(new MarkerOptions().position(latLong).snippet("Customer Name: " + fullname + "\n" + "Address: " + address));
//                                            map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//                                        }
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            showMessage("Error to get location");
//                        }
//                    });
//                }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                showMessage("Error to get location");
//            }
//        });
//
//        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                LinearLayout linearLayout = new LinearLayout(getActivity());
//                linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//                TextView title = new TextView(getActivity());
//                title.setText(marker.getTitle());
//                title.setTextColor(Color.BLACK);
//                title.setGravity(Gravity.CENTER);
//
//                TextView snippets = new TextView(getActivity());
//                snippets.setTextColor(Color.GRAY);
//                snippets.setText(marker.getSnippet());
//                linearLayout.addView(title);
//                linearLayout.addView(snippets);
//                return linearLayout;
//            }
//        });
//
//    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


//        @Override
//    public void onMapReady(GoogleMap googleMap) throws DatabaseException {
//        Bundle bundle = this.getArguments();
//        if(bundle != null)
//        {
//            transactNum = bundle.getString("TransactNoSeen1");
//            transactCust = bundle.getString("TransactCustSeen1");
//        }
//        map = googleMap;
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            buildGoogleApiClient();
//            map.setMyLocationEnabled(true);
//            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
//        }
//        float zoomLevel = 16.0f;
//        map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//
//        // SELECT * FROM TABLE
//       DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("User_File").child(transactCust);
//       DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference("Transaction_Header_File").child(transactNum);
//
//        addressesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot data: dataSnapshot.getChildren()){
//                    UserFile users = data.getValue(UserFile.class);
//                    businessRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot data2: dataSnapshot.getChildren()){
//                                TransactionHeaderFileModel info = data2.getValue(TransactionHeaderFileModel.class);
//
//                                if(users.getUser_getUID().equals(info.getCustomer_id()) && info.getTrans_no().equals(transactNum)){
//
//                                            arrayListUserFile.add(users);
//                                            arrayListBusinessInfo.add(info);
//
//                                            //no business add
//                                            myAddresses = users.getUser_address();
//                                            String myAddresshere = "Talamban Cebu City";
//                                            try {
//                                                myListAddresses = mGeocoder.getFromLocationName(myAddresses, 1);
//                                                List<Address> mListAddresses = mGeocoder.getFromLocationName(myAddresshere, 1);
//                                                if (myListAddresses != null)
//                                                {
//                                                    Address location = myListAddresses.get(0);
//                                                    Address location1 = mListAddresses.get(0);
//                                                    latLong = new LatLng(location.getLatitude(), location.getLongitude());
//                                                    String address = users.getUser_address();
//                                                    String fullname = users.getUser_lastname()+", "+users.getUser_firtname();
//                                                    map.addMarker(new MarkerOptions().position(latLong).snippet("Customer Name: "+fullname+"\n"+"Address: "+address));
//                                                    map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//                                                }
//                                            }
//                                            catch (Exception e){
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//
//
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
//
//        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                LinearLayout linearLayout = new LinearLayout(getActivity());
//                linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//                TextView title = new TextView(getActivity());
//                title.setText(marker.getTitle());
//                title.setTextColor(Color.BLACK);
//                title.setGravity(Gravity.CENTER);
//
//                TextView snippets = new TextView(getActivity());
//                snippets.setTextColor(Color.GRAY);
//                snippets.setText(marker.getSnippet());
//                linearLayout.addView(title);
//                linearLayout.addView(snippets);
//                return linearLayout;
//            }
//        });
//    }

    private String getRequestURL(LatLng origin, LatLng dest)
    {
        String str_org = "origin=" + origin.latitude + "," +origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," +dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+API_KEY;
        String param = str_org + "&" + str_dest + "&" +sensor+ "&" +mode+"&"+key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
        return url;
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
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>
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
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(2);
            lineOptions.color(Color.RED);
            MarkerOptions markerOptions = new MarkerOptions();
            for(int i=0;i<result.size();i++){
                List<HashMap<String, String>> path = result.get(i);
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
            }
            if(points.size()!=0) {
                map.addPolyline(lineOptions);
            }
            else
            {
                showMessage("Direction does not available");
            }
        }
    }

//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
//            List<LatLng> points;
//            PolylineOptions polylineOptions = null;
//            for(List<HashMap<String, String>>path:lists)
//            {
//                points = new ArrayList();
//                polylineOptions = new PolylineOptions();
//                for (HashMap<String, String>point:path)
//                {
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lon = Double.parseDouble(point.get("lon"));
//
//                    points.add(new LatLng(lat, lon));
//                }
//                polylineOptions.addAll(points);
//                polylineOptions.width(15);
//                polylineOptions.color(Color.BLUE);
//                polylineOptions.geodesic(true);
//            }
//            if(polylineOptions!=null)
//            {
//                map.addPolyline(polylineOptions);
//            }
//            else
//            {
//                //map.addPolyline(polylineOptions);
//                Toast.makeText(getActivity(), "Direction not found", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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
            String urlString = getRequestURL(mLatLng, latLong);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(urlString);
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

        if(mLocationRequest != null)
        {

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
        stationName.setText(marker.getTitle());
        marker.getSnippet();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
