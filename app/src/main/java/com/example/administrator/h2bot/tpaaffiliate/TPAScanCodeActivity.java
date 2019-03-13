package com.example.administrator.h2bot.tpaaffiliate;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

public class TPAScanCodeActivity extends AppCompatActivity {

    private DatabaseReference updateOrderRef;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpascan_code);

        updateOrderRef = db.getReference("Customer_Order_File");
        Objects.requireNonNull(getSupportActionBar()).setTitle("QR Code Scanner");
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null)
        {
            if(result.getContents() == null){
                Toast.makeText(this,"Scan cancelled.", Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                String [] ids = result.getContents().split("\\s+");
                updateOrderRef.child(ids[0])
                        .child(ids[1])
                        .child(ids[2])
                        .child("order_status")
                        .setValue("Completed");
                Toast.makeText(this, "Transaction successful", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
