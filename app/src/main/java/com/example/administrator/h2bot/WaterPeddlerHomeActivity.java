package com.example.administrator.h2bot;
import com.example.administrator.h2bot.customer.*;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.h2bot.waterstation.WSProductListFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class WaterPeddlerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAuth;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_peddler_home);

        mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.wpdrawer_layout);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_wsdrawer_open, R.string.navigation_wsdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_wp);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp,
                    new WPPendingOrdersFragment()).commit();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
            showMessages("Map");
        }
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
                Objects.requireNonNull(getSupportActionBar()).setTitle("Transactions");
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
                        new WPBusinessInfoFragment()).commit();
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
                        new CustomerAccountSettingFragment()).commit();
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
