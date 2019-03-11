package com.example.administrator.h2bot.customer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    Dialog dialog;
    public GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_actvity_main);
        dialog = new Dialog(this);
        drawerLayout = findViewById(R.id.customer_drawer);
        drawerLayout.closeDrawers();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerMapFragment()).commit();
            navigationView.setCheckedItem(R.id.map);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Find Water Merchant");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        Button dismissBtn;
        dialog.setContentView(R.layout.customer_generate_qr_code);
        dismissBtn = dialog.findViewById(R.id.dismissBtn);
        generate_qr_code = dialog.findViewById(R.id.generate_qr_code);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try
        {
            qrString = get_qrCode;
            BitMatrix bitMatrix = multiFormatWriter.encode(qrString, BarcodeFormat.AZTEC,279, 279);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            generate_qr_code.setImageBitmap(bitmap);
            }
            catch(WriterException e)
            {
                e.printStackTrace();
            }
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
