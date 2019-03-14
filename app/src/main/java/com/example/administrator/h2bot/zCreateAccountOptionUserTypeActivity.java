package com.example.administrator.h2bot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class zCreateAccountOptionUserTypeActivity extends AppCompatActivity implements View.OnClickListener {

    Button custCA, stationCA, dealerCA, affiliateCA;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_create_account_option_user_type);

        intent = new Intent();

        custCA = findViewById(R.id.cCA);
        stationCA = findViewById(R.id.soCA);
        dealerCA = findViewById(R.id.wdCA);
        affiliateCA = findViewById(R.id.tpaCA);

        custCA.setOnClickListener(this);
        stationCA.setOnClickListener(this);
        dealerCA.setOnClickListener(this);
        affiliateCA.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cCA:
                intent.putExtra("TextValue", "Customer");
                intent.setClass(zCreateAccountOptionUserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.soCA:
                intent.putExtra("TextValue", "Water Station");
                intent.setClass(zCreateAccountOptionUserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.wdCA:
                intent.putExtra("TextValue", "Water Dealer");
                intent.setClass(zCreateAccountOptionUserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tpaCA:
                intent.putExtra("TextValue", "Third Party Affiliate");
                intent.setClass(zCreateAccountOptionUserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
