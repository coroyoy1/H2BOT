package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.administrator.h2bot.R;

import java.util.Objects;

public class WSDashboard extends Fragment implements View.OnClickListener {

    Button updateInfo, addProduct;
    CardView currentIN, currentPEN, currentCOM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_dashboard, container, false);

        cardViews(view);
        onClickers();

        return view;
    }

    private void cardViews(View view)
    {
        //Button
        updateInfo = view.findViewById(R.id.updateStationInfo);
        addProduct = view.findViewById(R.id.addProduct);

        //CardView
        currentIN = view.findViewById(R.id.currentInProgress);
        currentPEN = view.findViewById(R.id.currentPending);
        currentCOM = view.findViewById(R.id.currentCompleted);
    }

    private void onClickers()
    {
        updateInfo.setOnClickListener(this);
        addProduct.setOnClickListener(this);
        currentCOM.setOnClickListener(this);
        currentIN.setOnClickListener(this);
        currentPEN.setOnClickListener(this);
    }


    //Card View Intent
    private void clickCompletedOrders(View v)
    {
        WSTransactionsFragment intent = new WSTransactionsFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
        Objects.requireNonNull(getActivity()).setTitle("Completed Orders");
    }

    private void clickPendingdOrders(View v)
    {
        WSPendingOrdersFragment intent = new WSPendingOrdersFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
        Objects.requireNonNull(getActivity()).setTitle("Pending Orders");
    }

    private void clickInProgressOrders(View v)
    {
        WSInProgressFragment intent = new WSInProgressFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
        Objects.requireNonNull(getActivity()).setTitle("In-Progress Orders");
    }

    //Button Intent
    private void clickUpdateStationInfo(View v)
    {
        WSBusinessInfoFragment intent = new WSBusinessInfoFragment();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
    }

    private void clickAddProduct(View v)
    {
        WSProductAdd intent = new WSProductAdd();
        AppCompatActivity activity = (AppCompatActivity)v.getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, intent)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateStationInfo:
                clickUpdateStationInfo(v);
                break;
            case R.id.addProduct:
                clickAddProduct(v);
                break;
            case R.id.currentCompleted:
                clickCompletedOrders(v);
                break;
            case R.id.currentPending:
                clickPendingdOrders(v);
                break;
            case R.id.currentInProgress:
                clickInProgressOrders(v);
                break;
        }
    }
}
