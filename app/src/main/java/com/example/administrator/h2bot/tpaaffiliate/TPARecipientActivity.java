package com.example.administrator.h2bot.tpaaffiliate;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.h2bot.R;

public class TPARecipientActivity extends AppCompatActivity {

    Dialog dialog;
    Button mapLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tparecipient);

        dialog = new Dialog(this);
        mapLocation = findViewById(R.id.mapLocation);
        snackBar();

        mapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TPARecipientActivity.this, TPAffiliateMainActivity.class));
            }
        });
    }

    public void QrCode(View view) {
        Button cancelBtn;
        dialog.setContentView(R.layout.qr_code_popup);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void snackBar(){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Please go to the respective station for further negotiation.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Okay", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.white ))
                .show();
    }
}
