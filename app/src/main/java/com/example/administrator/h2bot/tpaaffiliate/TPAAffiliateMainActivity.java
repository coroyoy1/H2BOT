package com.example.administrator.h2bot.tpaaffiliate;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.customer.CustomerMainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class TPAAffiliateMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    Dialog dialog;
    public GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpaffiliate_main);

        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.customer_drawer);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAMapFragment()).commit();
            navigationView.setCheckedItem(R.id.map);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAMapFragment()).commit();
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.accepted_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPADeliveredOrdersFragment()).commit();
                Toast.makeText(this, "Accepted Orders", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Accepted Orders");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.delivered_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPADeliveredOrdersFragment()).commit();
                Toast.makeText(this, "Delivered Orders", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Delivered Orders");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.account_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAAccountSettingFragment()).commit();
                Toast.makeText(this, "Account Settings", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.ratings:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.ratings_popup);
                dialog.show();
                drawerLayout.isDrawerOpen(GravityCompat.START);
                break;
        }
        if (menuItem.getItemId() == R.id.logout) {
            logout();
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        mAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "Successfully logged-out", Toast.LENGTH_SHORT).show();
    }

    public void ShowPopUpAccountSettingUpdate(View view) {
        Button cancelBtn;
        Button saveChangesBtn;
        dialog.setContentView(R.layout.account_settings_popup);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        saveChangesBtn = dialog.findViewById(R.id.saveChangesBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TPAAffiliateMainActivity.this, "Temporary", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void showQrCode(View view) {
        Button dismissBtn;
        dialog.setContentView(R.layout.qr_code_popup);
        dismissBtn = dialog.findViewById(R.id.dismissBtn);

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void ShowOrderDetails(View view) {
        dialog.setContentView(R.layout.tpa_for_order_details);
        dialog.show();
    }

    public void AddBid(View view) {
        dialog.setContentView(R.layout.tpa_bid);
        dialog.show();
    }
}
