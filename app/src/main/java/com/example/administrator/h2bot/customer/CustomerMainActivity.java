package com.example.administrator.h2bot.customer;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.Notification;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.objects.WaterStationOrDealer;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Objects;


public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<WaterStationOrDealer> thisList;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    TextView my_order;
    Dialog dialog;
    public GoogleMap map;
    int countInprogress;
    private ArrayList<OrderModel> adapter;
    FirebaseUser currentUser;
    String currendId, order, stationID;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_actvity_main);
        notificationManager = NotificationManagerCompat.from(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currendId = currentUser.getUid();
        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.customer_drawer);
        drawerLayout.closeDrawers();
        adapter = new ArrayList<OrderModel>();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        my_order = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.my_order));
        actionBarDrawerToggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerMapFragment()).commit();
            navigationView.setCheckedItem(R.id.map);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Find Water Merchant");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeCountDrawer();
    }

    private void initializeCountDrawer() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                my_order.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    for (DataSnapshot post : dataSnapshot1.getChildren()) {
                        for (DataSnapshot post1 : post.getChildren()) {
                            OrderModel orderModel = post1.getValue(OrderModel.class);
                            if (orderModel != null) {
                                if (orderModel.getOrder_status().equalsIgnoreCase("In-Progress")) {
                                    String text = "Your order has been accepted";
                                    sendNotification(orderModel.getOrder_no(), text);
                                } else if (orderModel.getOrder_status().equalsIgnoreCase("Dispatched") || orderModel.getOrder_status().equalsIgnoreCase("Dispatched by affiliate")) {
                                    String text = "Your order has been dispatched.";
                                    sendNotification(orderModel.getOrder_no(), text);
                                }
                                if (orderModel.getOrder_customer_id().equals(currendId)
                                        && orderModel.getOrder_status().equalsIgnoreCase("In-Progress")
                                        || orderModel.getOrder_status().equalsIgnoreCase("Dispatched")) {

                                    adapter.add(orderModel);
                                    adapter.size();
                                    my_order.setVisibility(View.VISIBLE);
                                    my_order.setVisibility(View.VISIBLE);
                                    countInprogress = adapter.size();

                                    stationID = orderModel.getOrder_merchant_id();

                                    my_order.setGravity(Gravity.CENTER_VERTICAL);
                                    my_order.setTextSize(20);
                                    my_order.setTypeface(null, Typeface.BOLD);
                                    my_order.setTextColor(getResources().getColor(R.color.colorAccent));
                                    my_order.setText("" + countInprogress);

                                }
                                else {
                                    countInprogress = adapter.size();

                                    if (countInprogress == 0) {
                                        my_order.setVisibility(View.INVISIBLE);
                                    } else {
                                        my_order.setGravity(Gravity.CENTER_VERTICAL);
                                        my_order.setTextSize(20);
                                        my_order.setTypeface(null, Typeface.BOLD);
                                        my_order.setTextColor(getResources().getColor(R.color.colorAccent));
                                        my_order.setText("" + countInprogress);
                                    }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerMapFragment()).commit();
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Find Water Merchant");
                break;

            case R.id.my_order:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerStationFragment()).commit();
                Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("My Orders");
                break;

            case R.id.account_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerAccountSettingFragment()).commit();
                Toast.makeText(this, "Account Settings", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
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
                Toast.makeText(CustomerMainActivity.this, "Temporary", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void generateQrCode(View view) {
        String get_qrCode = CustomerAllOrdersAdapter.qrCode;
        ImageView generate_qr_code;
        String qrString;
        TextView closeDialog;
        dialog.setContentView(R.layout.customer_generate_qr_code);
        closeDialog = dialog.findViewById(R.id.closeDialog);
        generate_qr_code = dialog.findViewById(R.id.generate_qr_code);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            qrString = get_qrCode;
            BitMatrix bitMatrix = multiFormatWriter.encode(qrString, BarcodeFormat.AZTEC, 279, 279);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            generate_qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        closeDialog.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void setList(ArrayList<WaterStationOrDealer> thisList) {
        this.thisList = thisList;
    }

    private void sendNotification(String orderno, String text) {
        android.app.Notification notification = new NotificationCompat.Builder(this, "notificationforpending")
                .setSmallIcon(R.drawable.ic_look1)
                .setContentTitle("H2BOT")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .build();

        notificationManager.notify(1, notification);
    }

}
