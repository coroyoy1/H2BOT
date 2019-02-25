package com.example.administrator.h2bot.waterstation;

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

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class WaterStationMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_station_main);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.wsdrawer_layout);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_wsdrawer_open, R.string.navigation_wsdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_ws);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws,
                    new WSInProgressFragment()).commit();
            Objects.requireNonNull(getSupportActionBar()).setTitle("In-Progress");
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
                Intent intent3 = new Intent(WaterStationMainActivity.this, LoginActivity.class);
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
}
