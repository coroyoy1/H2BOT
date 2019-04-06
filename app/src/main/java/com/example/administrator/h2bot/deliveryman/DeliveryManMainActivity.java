package com.example.administrator.h2bot.deliveryman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.google.android.gms.maps.GoogleMap;
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
    FirebaseAuth mAuth;
    GoogleMap map;
    FirebaseUser currentUser;
    TextView nav_inprogress_dm;
    private ArrayList<OrderModel> adapter2;
    String currendId;
    int countInprogress;
    private ProgressDialog progressDialog;
    private NotificationManagerCompat notificationManager;
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

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.dmdrawer_layout);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_dmdrawer_open, R.string.navigation_dmdrawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view_dm);
        navigationView.setNavigationItemSelectedListener(this);
        nav_inprogress_dm=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_inprogress_dm));
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeCountDrawer();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
                    new DMInProgressFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inprogress_dm);
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
//            case R.id.nav_feedback_dm:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_dm,
//                        new DMFeedbackFragment()).commit();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Feedback");
//                break;
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
    private void sendNotification() {
        android.app.Notification notification = new NotificationCompat.Builder(this, "notificationforpending")
                .setSmallIcon(R.drawable.ic_look1)
                .setContentTitle("H2BOT")
                .setContentText("You have in-progress order(s).")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .build();

        notificationManager.notify(1, notification);
    }
}
