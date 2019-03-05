package com.example.administrator.h2bot.tpaaffiliate;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

public class TPAScanCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpascan_code);

        Objects.requireNonNull(getSupportActionBar()).setTitle("QR Code Scanner");
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
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
                Toast.makeText(this,result.getContents(), Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
