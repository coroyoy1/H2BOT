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
        final UserWSWDWaterTypeFile userWSWDWaterTypeFile = waterTypeList.get(i);
        String water_type = userWSWDWaterTypeFile.getWater_type();
        String pickup_price = userWSWDWaterTypeFile.getPickup_price_per_gallon();
        String delivery_price = userWSWDWaterTypeFile.getDelivery_price_per_gallon();

        viewHolder.waterType.setText(water_type);
        viewHolder.pickupPrice.setText(String.format("%.2f", Double.parseDouble(pickup_price)));
        viewHolder.deliveryPrice.setText(String.format("%.2f", Double.parseDouble(delivery_price)));
    }

    @Override
    public int getItemCount() {
        return waterTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView waterType;
        TextView pickupPrice;
        TextView deliveryPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            waterType = itemView.findViewById(R.id.waterType);
            pickupPrice = itemView.findViewById(R.id.pickupPrice);
            deliveryPrice = itemView.findViewById(R.id.deliveryPrice);
        }
    }
}
