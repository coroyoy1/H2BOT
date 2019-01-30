package com.example.administrator.h2bot;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class TPAGoogleMapFragment extends Fragment implements OnMapReadyCallback {


    GoogleMap map;
    //    Button viewMoreBtn, orderBtn;
    public TPAGoogleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng pp = new LatLng(10.358060499999999, 123.9136566);
        MarkerOptions options = new MarkerOptions();
        options.position(pp).title("Me");

        map.addMarker(options).showInfoWindow();
        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pp, zoomLevel));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getContext(), "I clicked the map", Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.station_popup);
                Button viewMoreBtn = dialog.findViewById(R.id.viewMoreBtn);
                Button orderBtn = dialog.findViewById(R.id.orderBtn);

                viewMoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "View more info", Toast.LENGTH_SHORT).show();
                    }
                });

                orderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Order from Chatbot", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
                return false;
            }
        });
    }
}
