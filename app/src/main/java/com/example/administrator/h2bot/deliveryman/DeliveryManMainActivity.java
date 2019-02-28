package com.example.administrator.h2bot.deliveryman;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class DeliveryManMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAuth;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_man_main);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.dmdrawer_layout);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_dmdrawer_open, R.string.navigation_dmdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_dm);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                    new DMInProgressFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inprogress_dm);
        }

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
                        new DMTransactionsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Transactions");
                showMessages("Transactions");
                break;
            case R.id.nav_inprogress_dm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                        new DMInProgressFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("In-Progress");
                showMessages("In-Progress");
                break;
            case R.id.nav_accountsettings_dm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                        new DMAccountSettingsFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
                showMessages("Account Settings");
                break;
            case R.id.nav_feedback_dm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                        new DMFeedbackFragment()).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Feedback");
                showMessages("Feedback");
                break;
            case R.id.nav_logout_dm:
                mAuth.signOut();
                finish();
                Intent intent3 = new Intent(DeliveryManMainActivity.this, LoginActivity.class);
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
