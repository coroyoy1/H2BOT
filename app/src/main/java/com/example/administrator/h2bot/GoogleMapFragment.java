package com.example.administrator.h2bot;


import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleMapFragment extends Fragment implements OnMapReadyCallback{

    GoogleMap map;
    private ChildEventListener mChilExventListener;
    DatabaseReference mUsers;
    Marker marker;
    private FirebaseAuth mAuth;

    LocationManager locationManager;
    private List<Users>uploadPL;
    String strAddress;


    //    Button viewMoreBtn, orderBtn;
    public GoogleMapFragment() {
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
        ChildEventListener mChildEventListener;


        //mUsers.push().setValue(marker);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("tempOrders");

        Toast.makeText(getActivity(), "HELLO", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        mUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot s: dataSnapshot.getChildren())
//                {
//                Users user = s.getValue(Users.class);
//                uploadPL.add(user);
                  strAddress = dataSnapshot.child("mOrderNo").getValue(String.class);
                Toast.makeText(getActivity(), ""+strAddress, Toast.LENGTH_LONG).show();
//                Geocoder coder = new Geocoder(getActivity().getApplicationContext());
//                List<Address> address;
//                LatLng p1 = null;
//                try {
//                    // May throw an IOException
//                    address = coder.getFromLocationName(strAddress, 1);
//                    if (address == null) {
//                        return;
//                    }
//                    Address location = address.get(0);
//                    p1 = new LatLng(location.getLatitude(), location.getLongitude());
//                    map.addMarker(new MarkerOptions()
//                        .position(p1));
//
//
//                } catch (IOException ex) {
//
//                    ex.printStackTrace();
//                }
//                MarkerOptions options = new MarkerOptions();
//                options.position(p1).title(strAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                map.addMarker(options).showInfoWindow();
//                float zoomLevel = 16.0f;
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(p1, zoomLevel));
                 }
           // }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






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
