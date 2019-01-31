package com.example.administrator.h2bot;

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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class TPAffiliateMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAGoogleMapFragment()).commit();
            navigationView.setCheckedItem(R.id.map);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAGoogleMapFragment()).commit();
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
                break;

//            case R.id.accepted:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAHistoryFragment()).commit();
//                Toast.makeText(this, "Transactions", Toast.LENGTH_SHORT).show();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("TransactionsDelivered");
//                break;

            case R.id.history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TPAHistoryFragment()).commit();
                Toast.makeText(this, "Transactions", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("TransactionsDelivered");
                break;

            case R.id.account_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountSettingFragment()).commit();
                Toast.makeText(this, "Account Settings", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
                break;

            case R.id.rate:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.ratings_popup);
                dialog.show();
                break;

            case R.id.transactions:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerTransactionsFragment()).commit();
                Toast.makeText(this, "Transactions", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Transactions");
                break;
        }
        if (menuItem.getItemId() == R.id.logout) {
            logout();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
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
}
