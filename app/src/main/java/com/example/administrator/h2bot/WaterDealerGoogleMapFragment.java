package com.example.administrator.h2bot;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class WaterDealerGoogleMapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    LocationManager locationManager;

    public WaterDealerGoogleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_water_dealer_google_map, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                    try{
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        Address obj = addressList.get(0);

                        String address = addressList.get(0).getAddressLine(0);
                        String city = addressList.get(0).getLocality();
                        String state = addressList.get(0).getAdminArea();
                        String country = addressList.get(0).getCountryName();
                        String postalCode = addressList.get(0).getPostalCode();
                        String knownName = addressList.get(0).getFeatureName();

                        String str = knownName+" "+state;

                        str += addressList.get(0).getCountryName();
                        map.addMarker(new MarkerOptions().position(latLng).title(str));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                        try{
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String str = addressList.get(0).getLocality();
                            str += addressList.get(0).getCountryName();
                            map.addMarker(new MarkerOptions().position(latLng).title(str));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
            }
        }

        @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

//        LatLng pp = new LatLng(10.358060499999999, 123.9136566);
//        MarkerOptions options = new MarkerOptions();
//        options.position(pp).title("Me");
//
//        map.addMarker(options).showInfoWindow();
//        float zoomLevel = 16.0f;
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pp, zoomLevel));
    }
}
