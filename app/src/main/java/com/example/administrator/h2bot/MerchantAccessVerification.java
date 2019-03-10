package com.example.administrator.h2bot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.h2bot.dealer.WaterPeddlerDocumentActivity;
import com.example.administrator.h2bot.dealer.WaterPeddlerHomeActivity;
import com.example.administrator.h2bot.deliveryman.DeliveryManDocumentActivity;
import com.example.administrator.h2bot.deliveryman.DeliveryManMainActivity;
import com.example.administrator.h2bot.waterstation.WaterStationMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MerchantAccessVerification extends AppCompatActivity implements View.OnClickListener {


    Button buttonUpdate, buttonLogout;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_access_verification);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User_File");

        buttonLogout = findViewById(R.id.logoutAV);
        buttonUpdate = findViewById(R.id.updateAV);

        buttonUpdate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        Log.d("Pasudlako","Pasudla ko");
    }

    public void checkUserType()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference referenceGet = databaseReference.child(firebaseUser.getUid());
        referenceGet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.child("user_type").getValue().toString();
                if(userType.equals("Water Station"))
                {
                    String activateStat = dataSnapshot.child("user_status").getValue().toString();
                    if(activateStat.equals("active"))
                    {
                        finish();
                        startActivity(new Intent(MerchantAccessVerification.this, WaterStationMainActivity.class));
                    }
                    else if(activateStat.equals("unverified"))
                    {
                        startActivity(new Intent(MerchantAccessVerification.this, WaterStationDocumentVersion2Activity.class));
                    }
                    else
                    {
                        Log.d("Pasudlako",""+userType);
                        showMessages("Error to pass intent");
                    }
                }
                else if(userType.equals("Delivery Man"))
                {
                    String activateStat = dataSnapshot.child("user_status").getValue().toString();
                    if(activateStat.equals("active"))
                    {
                        finish();
                        startActivity(new Intent(MerchantAccessVerification.this, DeliveryManMainActivity.class));
                    }
                    else if(activateStat.equals("unconfirmed"))
                    {
                        startActivity(new Intent(MerchantAccessVerification.this, DeliveryManDocumentActivity.class));
                    }
                    else
                    {
                        Log.d("Pasudlako",""+userType);
                        showMessages("Error to pass intent");
                    }
                }
                else if(userType.equals("Third Affiliate"))
                {
                    showMessages("Third Affiliate");
                }

                else if(userType.equals("Water Dealer"))
                {
                    String activateStat = dataSnapshot.child("user_status").getValue().toString();
                    if(activateStat.equals("active"))
                    {
                        finish();
                        startActivity(new Intent(MerchantAccessVerification.this, WaterPeddlerHomeActivity.class));
                    }
                    else if(activateStat.equals("unverified"))
                    {
                        Log.d("Pasudlako",""+userType);
                        startActivity(new Intent(MerchantAccessVerification.this, WaterPeddlerDocumentActivity.class));
                    }
                    else
                    {
                        Log.d("Pasudlako",""+userType);
                        showMessages("Error to pass intent");
                    }
                }
                else
                {
                    Log.d("Pasudlako",""+userType);
                    showMessages("No available user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.logoutAV:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MerchantAccessVerification.this, LoginActivity.class));
                break;
            case R.id.updateAV:
                checkUserType();
//                startActivity(new Intent(MerchantAccessVerification.this, WaterStationDocumentVersion2Activity.class));
                break;
        }
    }
    
}
