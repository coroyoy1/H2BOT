package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.example.administrator.h2bot.WSCompletedOrdersInformationFragment;
import com.example.administrator.h2bot.adapter.WSCompleterdOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.deliveryman.DeliveryManMainActivity;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaterStationMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAuth;
    GoogleMap map;
    TextView nav_pendingorders_ws,nav_inprogress_ws;
    private ProgressDialog progressDialog;
    FirebaseUser currentUser;
    String currendId;
    int countPending;
    int countInprogress;

    private OrderModel Adapter;
    private ArrayList<OrderModel> adapter;
    private ArrayList<OrderModel> adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_station_main);

        progressDialog = new ProgressDialog(WaterStationMainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        adapter = new ArrayList<OrderModel>();
        adapter2 = new ArrayList<OrderModel>();
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currendId = currentUser.getUid();
        drawerLayout = findViewById(R.id.wsdrawer_layout);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_wsdrawer_open, R.string.navigation_wsdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_ws);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_pendingorders_ws=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_pendingorders_ws));
        nav_inprogress_ws=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_inprogress_ws));
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                    new WSDashboard()).commit();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard");
        }

       initializeCountDrawer();
    }

    private void initializeCountDrawer(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                adapter2.clear();
                nav_pendingorders_ws.setVisibility(View.VISIBLE);
                nav_inprogress_ws.setVisibility(View.VISIBLE);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for (DataSnapshot post : dataSnapshot1.child(currendId).getChildren())
                    {
                        OrderModel orderModel = post.getValue(OrderModel.class);
                        if(orderModel != null)
                        {
                            if(orderModel.getOrder_merchant_id().equals(currendId)
                                    && orderModel.getOrder_status().equals("Pending"))
                            {
                                adapter.add(orderModel);
                                adapter.size();
                                nav_pendingorders_ws.setVisibility(View.VISIBLE);
                                countPending = adapter.size();
                                Log.d("CountPending", ""+countPending);

                                    nav_pendingorders_ws.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_pendingorders_ws.setTextSize(20);
                                    nav_pendingorders_ws.setTypeface(null, Typeface.BOLD);
                                    nav_pendingorders_ws.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_pendingorders_ws.setText("" + countPending);

                            }
                            else
                            {
                                countPending = adapter.size();

                                if (countPending==0)
                                {
                                    nav_pendingorders_ws.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    nav_pendingorders_ws.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_pendingorders_ws.setTextSize(20);
                                    nav_pendingorders_ws.setTypeface(null, Typeface.BOLD);
                                    nav_pendingorders_ws.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_pendingorders_ws.setText(""+ countPending);
                                }
                            }
                            if(orderModel.getOrder_merchant_id().equals(currendId)
                                    && orderModel.getOrder_status().equals("In-Progress") && orderModel.getOrder_status().equalsIgnoreCase("Dispatched"))
                            {
                                adapter2.add(orderModel);
                                adapter2.size();
                                countInprogress = adapter2.size();
                                Log.d("CountInprogress", ""+countInprogress);
                                if(countInprogress != 0)
                                {
                                    nav_inprogress_ws.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_inprogress_ws.setTextSize(20);
                                    nav_inprogress_ws.setTypeface(null, Typeface.BOLD);
                                    nav_inprogress_ws.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_inprogress_ws.setText("" + countInprogress);
                                }
                            }
                            else
                            {
                                countInprogress = adapter2.size();

                                if (countInprogress==0)
                                {
                                    nav_inprogress_ws.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    nav_inprogress_ws.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_inprogress_ws.setTextSize(20);
                                    nav_inprogress_ws.setTypeface(null, Typeface.BOLD);
                                    nav_inprogress_ws.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_inprogress_ws.setText(""+ countInprogress);
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
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
            if(count > 0)
            {
                getSupportFragmentManager().popBackStackImmediate();
            }
            else {
                super.onBackPressed();
//            }
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
            case R.id.nav_dashboard_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSDashboard()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard");
                break;
            case R.id.nav_transactions_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSTransactionsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Completed Orders");
                break;
            case R.id.nav_inprogress_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSInProgressFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("In-Progress");
                break;
            case R.id.nav_dailyrecords_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSSalesReportsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Daily Records");
                break;
            case R.id.nav_additem_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSBusinessInfoFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Business Information");
                break;
            case R.id.nav_productlist_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSProductListFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Product List");
                break;
            case R.id.nav_pendingorders_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSPendingOrdersFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Pending Orders");
                break;
//            case R.id.nav_map_ws:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
//                        new WSMapFragment()).commit();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
//                showMessages("Map");
//                break;
            case R.id.nav_accountsettings_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSAccountSettingsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
                break;
            case R.id.nav_feedback_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSFeedbackFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Feedback");
                break;
            case R.id.nav_deliveryman_ws:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                        new WSDMFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Delivery Man");
                break;
            case R.id.nav_logout_ws:
                mAuth.signOut();
                finish();
                progressDialog.dismiss();
                Intent intent3 = new Intent(WaterStationMainActivity.this, LoginActivity.class);
                startActivity(intent3);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showMessages("logout Successfully");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showMessages(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
