package com.example.administrator.h2bot.waterstation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.h2bot.R;

import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WSBroadcast extends AppCompatActivity implements View.OnClickListener {

    GifImageView radioWave;
    Button activate, cancel;
    boolean isClicked = false, isClickedCancel=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsbroadcast);

        activate = findViewById(R.id.broadcastActivate);
        cancel = findViewById(R.id.broadcastCancel);

        radioWave = findViewById(R.id.radioWaveAnim);

        activate.setOnClickListener(this);
        cancel.setOnClickListener(this);
        ((GifDrawable)radioWave.getDrawable()).stop();

        cancel.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.broadcastActivate:
                isClicked = true;
                isClickedCancel = false;
                checkButtonClick();
                break;
            case R.id.broadcastCancel:
                isClicked = false;
                isClickedCancel = true;
                checkButtonClick();
                break;
        }
    }
}
