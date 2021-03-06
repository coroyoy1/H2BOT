package com.example.administrator.h2bot.deliveryman;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DeliveryManMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FusedLocationProviderClient fusedLocationClient;
    FirebaseAuth mAuth;
    GoogleMap map;
    FirebaseUser currentUser;
    TextView nav_inprogress_dm;
    private ArrayList<OrderModel> adapter2;
    String currendId;
    int countInprogress;
    private ProgressDialog progressDialog;
    private NotificationManagerCompat notificationManager;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter2 = new ArrayList<>();
        setContentView(R.layout.activity_delivery_man_main);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currendId = currentUser.getUid();
        notificationManager = NotificationManagerCompat.from(this);
        progressDialog = new ProgressDialog(DeliveryManMainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.dmdrawer_layout);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_dmdrawer_open, R.string.navigation_dmdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_dm);
        navigationView.setNavigationItemSelectedListener(this);
        nav_inprogress_dm = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_inprogress_dm));
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeCountDrawer();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                    new DMInProgressFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inprogress_dm);
        }
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                String lat = location.getLatitude()+"";
//                String lon = location.getLongitude()+"";
//
//                UserLocationAddress userLocationAddress = new UserLocationAddress(
//                        mAuth.getCurrentUser().getUid(),
//                        lat,
//                        lon
//                );
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_LatLong");
//                databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(userLocationAddress);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
//            }
//        };
        //configureButton();
    }

    private void configureButton()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }else{
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configureButton();
                break;
            default:
                break;
        }
    }
    private void initializeCountDrawer(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter2.clear();
                nav_inprogress_dm.setVisibility(View.VISIBLE);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for (DataSnapshot post : dataSnapshot1.child(currendId).getChildren())
                    {
                        OrderModel orderModel = post.getValue(OrderModel.class);
                        if(orderModel != null)
                        {
                            if(orderModel.getOrder_status().equalsIgnoreCase("In-Progress"))
                            {
                                String text="You have in-progress order(s) that has been accepted by your station";
                                sendNotification(orderModel.getOrder_no(), text);
                            }
                            if(orderModel.getOrder_merchant_id().equals(currendId)
                                    && orderModel.getOrder_status().equalsIgnoreCase("In-Progress")|| orderModel.getOrder_status().equalsIgnoreCase("Dispatched"))
                            {
                                adapter2.add(orderModel);
                                adapter2.size();
                                countInprogress = adapter2.size();
                                Log.d("CountInprogress", ""+countInprogress);
                                if(countInprogress != 0)
                                {
                                    nav_inprogress_dm.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_inprogress_dm.setTextSize(20);
                                    nav_inprogress_dm.setTypeface(null, Typeface.BOLD);
                                    nav_inprogress_dm.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_inprogress_dm.setText("" + countInprogress);
                                }
                            }
                            else
                            {
                                countInprogress  = adapter2.size();

                                if (countInprogress==0)
                                {
                                    Log.d("InProgress", ""+countInprogress);
                                    nav_inprogress_dm.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    Log.d("InProgress", ""+countInprogress);
                                    nav_inprogress_dm.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_inprogress_dm.setTextSize(20);
                                    nav_inprogress_dm.setTypeface(null, Typeface.BOLD);
                                    nav_inprogress_dm.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_inprogress_dm.setText(""+ countInprogress);
                                }
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
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(count == 0)
            {
                super.onBackPressed();
                //map.clear();

            }
            else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId())
        {
            case R.id.nav_transactions_dm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                        new DMCompleteFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Completed Orders");
                break;
            case R.id.nav_inprogress_dm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                        new DMInProgressFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("In-Progress");
                break;
            case R.id.nav_accountsettings_dm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                        new DMAccountSettingsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
                break;
            case R.id.nav_logout_dm:
                mAuth.signOut();
                finish();
                Intent intent3 = new Intent(DeliveryManMainActivity.this, LoginActivity.class);
                startActivity(intent3);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                progressDialog.dismiss();
                showMessages("Successfully Logout");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showMessages(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    private void sendNotification(String orderno, String text) {
        android.app.Notification notification = new NotificationCompat.Builder(this,"notificationforpending")
                .setSmallIcon(R.drawable.ic_look1)
                .setContentTitle("H2BOT")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .build();

        notificationManager.notify(1,notification);
    }
}
