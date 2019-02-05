package com.example.administrator.h2bot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class WSAccessVerification extends AppCompatActivity implements View.OnClickListener {

    Button buttonUpdate, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsaccess_verification);

        buttonLogout = findViewById(R.id.logoutAV);
        buttonUpdate = findViewById(R.id.updateAV);

        buttonUpdate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.logoutAV:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(WSAccessVerification.this, LoginActivity.class));
                break;
            case R.id.updateAV:
                //Temp
                startActivity(new Intent(WSAccessVerification.this, WaterStationDocumentVersion2Activity.class));
                break;
        }
    }
}
