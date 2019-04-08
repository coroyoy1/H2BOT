package com.example.administrator.h2bot.maps;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.customer.CustomerMapFragment;
import com.example.administrator.h2bot.customer.SearchMerchantAdapter;
import com.example.administrator.h2bot.objects.WaterStationOrDealer;
import com.example.administrator.h2bot.tpaaffiliate.TPAMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetDistance extends AsyncTask<Object, String, String> {

    private String url;
    private LatLng orig, dest;
    private String API_KEY;
    private List<WaterStationOrDealer> list;
    private String[] name, userType, type, status, stationId;
    private LatLng[] destination;
    private String[] googleDirectionsData;
    private String duration, distance;
    private ArrayList<WaterStationOrDealer> thisList = new ArrayList<>();
    private GoogleMap mMap;
    private Circle mCirle;
    private TextView currentRadius;
    private Marker mCurrentLocationMarker;
    private CustomerMapFragment customerMapFragment;
    private TPAMapFragment tpaMapFragment;

    @Override
    protected String doInBackground(Object... objects) {

        list = (List<WaterStationOrDealer>) objects[0];
        orig = (LatLng) objects[1];
        mMap = (GoogleMap) objects[2];
        API_KEY = (String) objects[3];
        currentRadius = (TextView) objects[4];
        customerMapFragment = (CustomerMapFragment) objects[5];
        tpaMapFragment = (TPAMapFragment) objects[6];

        name = new String[list.size()];
        googleDirectionsData = new String[list.size()];
        userType = new String[list.size()];
        destination = new LatLng[list.size()];
        type = new String[list.size()];
        status = new String[list.size()];
        stationId = new String[list.size()];


        int ctr = 0;
        for (WaterStationOrDealer myList:list) {
            DownloadUrl downloadUrl = new DownloadUrl();
            dest = new LatLng( myList.getLat(), myList.getLng());
            name[ctr] = myList.getStation_dealer_name();
            userType[ctr] = myList.getUserType();
            destination[ctr] = dest;
            type[ctr] = myList.getType();
            status[ctr] = myList.getStatus();
            stationId[ctr] = myList.getStationID();

            url = getRequestUrl(orig,dest);
            try {
                googleDirectionsData[ctr] = downloadUrl.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ctr++;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        thisList = new ArrayList<>();
        for(int i = 0; i < googleDirectionsData.length; i++){
            HashMap<String, String> directionsList = null;
            DirectionsParser parser = new DirectionsParser();
            directionsList = parser.parseDirections(googleDirectionsData[i]);
            duration = directionsList.get("duration");
            distance = directionsList.get("distance");

            WaterStationOrDealer value = new WaterStationOrDealer();
            value.setStation_dealer_name(name[i]);
            value.setDuration("Travel Time: " + duration);
            value.setDistance("Distance: " + distance);
            value.setLat(destination[i].latitude);
            value.setLng(destination[i].longitude);
            value.setUserType(userType[i]);
            value.setType(type[i]);
            value.setStatus(status[i]);
            value.setStationID(stationId[i]);

            thisList.add(value);
        }
        Display();
    }

    private String getRequestUrl(LatLng origin, LatLng dest){
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + API_KEY;
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
    }

    public void Display(){
        mMap.clear();
        MarkerOptions mMarkerOption = new MarkerOptions();
        mMarkerOption.position(orig);
        if(customerMapFragment!=null) {
            mCirle = mMap.addCircle(new CircleOptions()
                    .center(orig)
                    .fillColor(Color.argb(100, 173, 216, 230))
                    .strokeColor(Color.BLUE)
                    .strokeWidth(1)
                    .radius(Double.parseDouble(currentRadius.getText().toString().substring(0, currentRadius.getText().toString().length() - 3))));
            mMarkerOption.title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            double radiusLimit = Double.parseDouble(currentRadius.getText().toString().substring(0, currentRadius.getText().toString().length()-3));
            mCurrentLocationMarker = mMap.addMarker(mMarkerOption);
            mMap.addMarker(mMarkerOption);
            mCirle.setRadius(radiusLimit * 1000);
            ArrayList<WaterStationOrDealer> searchList = new ArrayList<>();
            for(int i = 0; i < thisList.size(); i++){
                LatLng latLng = new LatLng(thisList.get(i).getLat(), thisList.get(i).getLng());
                double stationDistance = Double.parseDouble(thisList.get(i).getDistance().substring(10, thisList.get(i).getDistance().length()-3));
                if(stationDistance <= radiusLimit){
                    searchList.add(thisList.get(i));

                    if(thisList.get(i).getUserType().equalsIgnoreCase("Water Station")){
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng).title(thisList.get(i).getStation_dealer_name())
                                .snippet(thisList.get(i).getType() + "\n" + thisList.get(i).getStatus())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                                .setTag(thisList.get(i).getStationID());
                    }
                    else{
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng).title(thisList.get(i).getStation_dealer_name())
                                .snippet(thisList.get(i).getType() + "\n" + thisList.get(i).getStatus())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                                .setTag(thisList.get(i).getStationID());
                    }
                }
            }
            if(customerMapFragment != null) {
                customerMapFragment.setList(thisList);
                customerMapFragment.setSearchList(searchList);
            }
            else if(tpaMapFragment != null)
                tpaMapFragment.setList(thisList);
        }
        else{
            mCirle = mMap.addCircle(new CircleOptions()
                    .center(orig)
                    .fillColor(Color.argb(100, 173, 216, 230))
                    .strokeColor(Color.BLUE)
                    .strokeWidth(1)
                    .radius(Double.parseDouble(currentRadius.getText().toString().substring(0, currentRadius.getText().toString().length() - 3))));
            mMarkerOption.title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            double radiusLimit = Double.parseDouble(currentRadius.getText().toString().substring(0, currentRadius.getText().toString().length() - 3));
            mCurrentLocationMarker = mMap.addMarker(mMarkerOption);
            mMap.addMarker(mMarkerOption);
            mCirle.setRadius(radiusLimit * 1000);
            ArrayList<WaterStationOrDealer> searchList = new ArrayList<>();
            for(int i = 0; i < thisList.size(); i++){
                LatLng latLng = new LatLng(thisList.get(i).getLat(), thisList.get(i).getLng());
                double stationDistance = Double.parseDouble(thisList.get(i).getDistance().substring(10, thisList.get(i).getDistance().length()-3));
                if(stationDistance <= radiusLimit){
                    searchList.add(thisList.get(i));

                    if(thisList.get(i).getUserType().equalsIgnoreCase("Water Station")){
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng).title(thisList.get(i).getStation_dealer_name())
                                .snippet(thisList.get(i).getType() + "\n" + thisList.get(i).getStatus())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                                //.icon(bitmapDescriptorFromVector(tpaMapFragment.getActivity(), R.drawable.station)))
                                .setTag(thisList.get(i).getStationID());
                    }
                    else
                        {
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng).title(thisList.get(i).getStation_dealer_name())
                                .snippet(thisList.get(i).getType() + "\n" + thisList.get(i).getStatus())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                                .setTag(thisList.get(i).getStationID());
                    }
                }
            }
            if(customerMapFragment != null) {
                customerMapFragment.setList(thisList);
                customerMapFragment.setSearchList(searchList);
            }
            else if(tpaMapFragment != null)
                tpaMapFragment.setList(thisList);
        }


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
