package com.example.administrator.h2bot.waterstation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WSBroadcast extends AppCompatActivity implements View.OnClickListener {
    GifImageView radioWave;
    TextView timerText;
    Button activate, cancel;
    TextView orderNoIntent, fullnameIntent;
    String orderNoText, fullNameText, customerNoText;
    FirebaseAuth firebaseAuth;
    boolean isClicked = false, isClickedCancel=false;
    int time=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsbroadcast);

        Intent intent = getIntent();
        fullNameText = intent.getStringExtra("Customer");
        orderNoText = intent.getStringExtra("OrderNo");
        customerNoText = intent.getStringExtra("CustomerNo");

        firebaseAuth = FirebaseAuth.getInstance();

        orderNoIntent = findViewById(R.id.idNumberBroadcast);
        fullnameIntent = findViewById(R.id.fullNameBroadcast);

        orderNoIntent.setText("Order No: "+orderNoText);
        fullnameIntent.setText("Customer's Name: "+fullNameText);

        activate = findViewById(R.id.broadcastActivate);
        cancel = findViewById(R.id.broadcastCancel);

        radioWave = findViewById(R.id.radioWaveAnim);
        timerText = findViewById(R.id.timer);

        if(isClicked)
        {
            holdTimerText();
        }

        activate.setOnClickListener(this);
        cancel.setOnClickListener(this);

        ((GifDrawable)radioWave.getDrawable()).stop();

        cancel.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);
    }

    public  void disableFindToAffiliate()
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer_File");
        reference
                .child(customerNoText)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(orderNoText)
                .child("order_status")
                .setValue("In-Progress")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Disable Find Affiliate");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Please check internet connection");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disableFindToAffiliate();
    }

    public void activateFindToAffiliate()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer_File");
        reference
                .child(customerNoText)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(orderNoText)
                .child("order_status")
                .setValue("Broadcasting")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Finding Affiliate");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Please check internet connection");
                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(WSBroadcast.this, s, Toast.LENGTH_LONG).show();
    }

    public void checkButtonClick()
    {
        if(isClicked)
        {
            activate.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            ((GifDrawable)radioWave.getDrawable()).start();
        }
        if(isClickedCancel)
        {
            activate.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            ((GifDrawable)radioWave.getDrawable()).stop();
        }
    }

    public void holdTimerText()
    {
        TextView textTimer = (TextView)findViewById(R.id.timer);
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                textTimer.setText("0:"+checkDigit(time));
                time--;
            }
            public void onFinish() {
                if(checkDigit(time).equals("0"))
                {
                    cancel.performClick();
                }

            }
        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.broadcastActivate:
                isClicked = true;
                isClickedCancel = false;
                holdTimerText();
                checkButtonClick();
                activateFindToAffiliate();
                break;
            case R.id.broadcastCancel:
                isClicked = false;
                isClickedCancel = true;
                checkButtonClick();
                disableFindToAffiliate();
                break;
        }
    }
}
