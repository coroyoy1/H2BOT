package com.example.administrator.h2bot.models;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.waterstation.WSProductListIntent;
import com.example.administrator.h2bot.waterstation.WSProductListUpdate;

import java.util.List;

public class MerchantDataAdapter extends RecyclerView.Adapter<MerchantDataAdapter.ImageViewHolder> {

    private Context contextHolder;
    private List<WSWDWaterTypeFile>uploadsHolder;

    public MerchantDataAdapter(Context context, List<WSWDWaterTypeFile>uploads)
    {
        contextHolder = context;
        uploadsHolder = uploads;
    }
    @NonNull
    @Override
    public MerchantDataAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.x_merchant_productlist, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantDataAdapter.ImageViewHolder imageViewHolder, int i) {
        final WSWDWaterTypeFile currentData = uploadsHolder.get(i);
        imageViewHolder.PLItemNameHolder.setText(currentData.getWater_type());
        imageViewHolder.PLItemNoHolder.setText(currentData.getWater_name());

        final String itemPrice = currentData.getPickup_price_per_gallon();
        final String itemDelivery = currentData.getDelivery_price_per_gallon();
        final String itemType = currentData.getWater_type();
        final String itemUid = currentData.getWater_seller_id();
        final String itemStatus = currentData.getWater_status();
        final String itemDescription = currentData.getWater_description();
        final String itemName = currentData.getWater_name();

        imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSProductListIntent additem = new WSProductListIntent();
                WSProductListUpdate updateitem = new WSProductListUpdate();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("ItemPriceMDA", itemPrice);
                args.putString("ItemTypeMDA", itemType);
                args.putString("ItemUidMDA", itemUid);
                args.putString("ItemNameMDA", itemName);
                args.putString("ItemStatusMDA", itemStatus);
                args.putString("ItemDeliveryMDA", itemDelivery);
                args.putString("ItemDescriptionMDA", itemDescription);
                additem.setArguments(args);
                updateitem.setArguments(args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadsHolder.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView PLItemNoHolder, PLItemNameHolder;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            PLItemNoHolder = itemView.findViewById(R.id.PLIname);
            PLItemNameHolder = itemView.findViewById(R.id.PLItemName);
        }
    }
}
