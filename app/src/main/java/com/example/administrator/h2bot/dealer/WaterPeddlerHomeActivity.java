package com.example.administrator.h2bot.dealer;
import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.customer.*;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.waterstation.WSProductListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class WaterPeddlerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAuth;
    Dialog dialog;
    TextView nav_pendingorders_wp, nav_inprogress_wp;
    private ArrayList<OrderModel> adapter;
    private ArrayList<OrderModel> adapter2;
    FirebaseUser currentUser;
    String currendId;
    int countPending;
    int countInprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_peddler_home);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currendId = currentUser.getUid();
        mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.wpdrawer_layout);
        drawerLayout.closeDrawers();
        adapter = new ArrayList<OrderModel>();
        adapter2 = new ArrayList<OrderModel>();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_wsdrawer_open, R.string.navigation_wsdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_wp);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        nav_pendingorders_wp=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_pendingorders_ws));
        nav_inprogress_wp=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_inprogress_ws));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                    new WPPendingOrdersFragment()).commit();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Pending");
            showMessages("Pending");
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
                nav_pendingorders_wp.setVisibility(View.VISIBLE);
                Log.d("ambotnimo","AMBOT");
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Log.d("gago","ka");
                    for (DataSnapshot post : dataSnapshot1.child(currendId).getChildren())
                    {
                        Log.d("mas","ka");
                        OrderModel orderModel = post.getValue(OrderModel.class);
                        if(orderModel != null)
                        {
                            Log.d("kako ","ka");
                            if(orderModel.getOrder_merchant_id().equals(currendId)
                                    && orderModel.getOrder_status().equals("Pending"))
                            {
                                adapter.add(orderModel);
                                adapter.size();
                                nav_pendingorders_wp.setVisibility(View.VISIBLE);
                                countPending = adapter.size();
                                Log.d("CountPending", ""+countPending);

                                nav_pendingorders_wp.setGravity(Gravity.CENTER_VERTICAL);
                                nav_pendingorders_wp.setTextSize(20);
                                nav_pendingorders_wp.setTypeface(null, Typeface.BOLD);
                                nav_pendingorders_wp.setTextColor(getResources().getColor(R.color.colorAccent));
                                nav_pendingorders_wp.setText("" + countPending);

                            }
                            else
                            {
                                countPending = adapter.size();

                                if (countPending==0)
                                {
                                    nav_pendingorders_wp.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    nav_pendingorders_wp.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_pendingorders_wp.setTextSize(20);
                                    nav_pendingorders_wp.setTypeface(null, Typeface.BOLD);
                                    nav_pendingorders_wp.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_pendingorders_wp.setText(""+ countPending);
                                }
                            }
                            if(orderModel.getOrder_merchant_id().equals(currendId)
                                    && orderModel.getOrder_status().equals("In-Progress"))
                            {
                                adapter2.add(orderModel);
                                adapter2.size();
                                countInprogress = adapter2.size();
                                Log.d("CountInprogress", ""+countInprogress);
                                if(countInprogress != 0)
                                {
                                    nav_inprogress_wp.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_inprogress_wp.setTextSize(20);
                                    nav_inprogress_wp.setTypeface(null, Typeface.BOLD);
                                    nav_inprogress_wp.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_inprogress_wp.setText("" + countInprogress);
                                }
                            }
                            else
                            {
                                countPending = adapter.size();

                                if (countPending==0)
                                {
                                    nav_pendingorders_wp.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    nav_pendingorders_wp.setGravity(Gravity.CENTER_VERTICAL);
                                    nav_pendingorders_wp.setTextSize(20);
                                    nav_pendingorders_wp.setTypeface(null, Typeface.BOLD);
                                    nav_pendingorders_wp.setTextColor(getResources().getColor(R.color.colorAccent));
                                    nav_pendingorders_wp.setText(""+ countPending);
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            case R.id.nav_pending_orders_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WPPendingOrdersFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Pending Orders");
                showMessages("Pending Orders");
                break;
            case R.id.nav_transactions_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WPTransactionFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Completed Orders");
                showMessages("Transactions");
                break;
            case R.id.nav_inprogress_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WPInProgressFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("In-Progress");
                showMessages("In-Progress");
                break;
            case R.id.nav_salesreport_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WPSalesReportFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Sales Report");
                showMessages("Sales Report");
                break;
            case R.id.nav_businessinfo_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WPBusinessInfoFragment2()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Business Info");
                showMessages("Business Info");
                break;
            case R.id.nav_productlist_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WSProductListFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Product List");
                showMessages("Product List");
                break;
            case R.id.nav_accountsettings_wp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                        new WPAccountSettingsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
                showMessages("Account Settings");
                break;
            case R.id.nav_rate_wp:
                final Dialog dialog = new Dialog(this);

                dialog.show();
                break;

            case R.id.nav_logout_wp:
                mAuth.signOut();
                finish();
                Intent intent3 = new Intent(WaterPeddlerHomeActivity.this, LoginActivity.class);
                startActivity(intent3);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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


    public void ShowPopUpAccountSettingUpdateWP(View view) {
        Button cancelBtn;
        Button saveChangesBtn;
        dialog.setContentView(R.layout.business_info_popup_update);
        cancelBtn = dialog.findViewById(R.id.cancelButton);
        saveChangesBtn = dialog.findViewById(R.id.saveButton);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WaterPeddlerHomeActivity.this, "Temporary", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
