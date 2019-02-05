package com.example.administrator.h2bot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MerchantDataAdapter extends RecyclerView.Adapter<MerchantDataAdapter.ImageViewHolder> {

    private Context contextHolder;
    private List<MerchantGetterSetter>uploadsHolder;

    public MerchantDataAdapter(Context context, List<MerchantGetterSetter>uploads)
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
        MerchantGetterSetter currentData = uploadsHolder.get(i);
        imageViewHolder.PLItemNoHolder.setText(currentData.getmItemNo());
        imageViewHolder.PLItemNameHolder.setText(currentData.getmItemName());
    }

    @Override
    public int getItemCount() {
        return uploadsHolder.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView PLItemNoHolder, PLItemNameHolder;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            PLItemNoHolder = itemView.findViewById(R.id.PLItemNo);
            PLItemNameHolder = itemView.findViewById(R.id.PLItemName);
        }
    }
}
