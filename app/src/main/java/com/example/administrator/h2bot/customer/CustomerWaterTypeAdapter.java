package com.example.administrator.h2bot.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.TransactionNoModel;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;

import java.util.List;

public class CustomerWaterTypeAdapter extends RecyclerView.Adapter<CustomerWaterTypeAdapter.ViewHolder>{

    private Context myContext;
    private List<UserWSWDWaterTypeFile> waterTypeList;

    public CustomerWaterTypeAdapter() {
    }

    public CustomerWaterTypeAdapter(Context myContext, List<UserWSWDWaterTypeFile> waterTypeList) {
        this.myContext = myContext;
        this.waterTypeList = waterTypeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.station_water_types, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.d("isExist: ", CustomerMapFragment.isExist.toString());
        if(!CustomerMapFragment.isExist){
            viewHolder.waterType.setText("No water type yet");
        }
        else{
            final UserWSWDWaterTypeFile userWSWDWaterTypeFile = waterTypeList.get(i);
            String water_type = userWSWDWaterTypeFile.getWater_type();
            String water_price = userWSWDWaterTypeFile.getWater_price_per_gallon();

            viewHolder.waterType.setText(water_type);
            viewHolder.waterPrice.setText("- " + String.format("%.2f", Double.parseDouble(water_price)));
        }
    }

    @Override
    public int getItemCount() {
        return waterTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView waterType;
        TextView waterPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            waterType = itemView.findViewById(R.id.waterType);
            waterPrice = itemView.findViewById(R.id.waterPrice);
            waterType.setText("HAHAHA");
        }
    }
}
