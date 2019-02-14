package com.example.administrator.h2bot.waterstation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MerchantDataAdapter extends RecyclerView.Adapter<MerchantDataAdapter.ImageViewHolder> {

    private Context contextHolder;
    private List<ProductGetterSetter>uploadsHolder;

    public MerchantDataAdapter(Context context, List<ProductGetterSetter>uploads)
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
        final ProductGetterSetter currentData = uploadsHolder.get(i);
        imageViewHolder.PLItemNameHolder.setText(currentData.getmItemName());
        final String itemName = currentData.getmItemName();
        final String itemPrice = currentData.getmItemPrice();
        final String itemQuantity = currentData.getmItemQuantity();
        final String itemType = currentData.getmWaterType();
        final String itemUid = currentData.getmItemUID();

        Picasso.get()
                .load(currentData.getmItemImage())
                .fit()
                .centerCrop()
                .into(imageViewHolder.PLImage);
        imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSProductListIntent additem = new WSProductListIntent();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("ItemNameMDA", itemName);
                args.putString("ItemPriceMDA", itemPrice);
                args.putString("ItemQuantityMDA", itemQuantity);
                args.putString("ItemTypeMDA", itemType);
                args.putString("ItemImageMDA", currentData.getmItemImage());
                args.putString("ItemUidMDA", itemUid);
                additem.setArguments(args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadsHolder.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView PLItemNoHolder, PLItemNameHolder;
        public ImageView PLImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
//            PLItemNoHolder = itemView.findViewById(R.id.PLItemNo);
            PLItemNameHolder = itemView.findViewById(R.id.PLItemName);
            PLImage = itemView.findViewById(R.id.imageViewPL);
        }
    }
}
