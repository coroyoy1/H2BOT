package com.example.administrator.h2bot.adapter;

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
import com.example.administrator.h2bot.mapmerchant.MapMerchantFragment;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.waterstation.WSStationToAffiliate;

import java.util.List;

public class WSInProgressOrdersAdapter extends RecyclerView.Adapter<WSInProgressOrdersAdapter.ViewHolder>{
    private Context mContext;
    private List<OrderModel> mUploads;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public WSInProgressOrdersAdapter(Context context, List<OrderModel> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public WSInProgressOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.x_merchant_transaction_inprogressorder, viewGroup, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull WSInProgressOrdersAdapter.ViewHolder viewHolder, int i) {
        final OrderModel currentData = mUploads.get(i);
                viewHolder.transactionNo.setText(currentData.getOrder_no());
                viewHolder.status.setText(currentData.getOrder_status());


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentData.getOrder_status().equals("Accepted"))
                {
                    WSStationToAffiliate additem = new WSStationToAffiliate();
                    AppCompatActivity activity = (AppCompatActivity)v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container_ws, additem)
                            .addToBackStack(null)
                            .commit();
                    Bundle args = new Bundle();
                    args.putString("transactionno", currentData.getOrder_no());
                    args.putString("transactioncustomer", currentData.getOrder_customer_id());
                    args.putString("status", currentData.getOrder_status());
                    args.putString("transactionusertype", currentData.getOrder_status());
                    args.putString("transactionmerchant", currentData.getOrder_merchant_id());
                    additem.setArguments(args);
                }
                else
                {
                    MapMerchantFragment additem = new MapMerchantFragment();
                    AppCompatActivity activity = (AppCompatActivity)v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .replace(R.id.fragment_container_ws, additem)
                            .addToBackStack(null)
                            .commit();
                    Bundle args = new Bundle();
                    args.putString("transactionno", currentData.getOrder_no());
                    args.putString("transactioncustomer", currentData.getOrder_customer_id());
                    args.putString("status", currentData.getOrder_status());
                    args.putString("transactionusertype", currentData.getOrder_status());
                    args.putString("transactionmerchant", currentData.getOrder_merchant_id());
                    additem.setArguments(args);
                }
            }
        });

                OrderModel uploadCurrent = mUploads.get(i);
                viewHolder.transactionNo.setText(uploadCurrent.getOrder_no());
                viewHolder.status.setText(uploadCurrent.getOrder_status());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionNo, status, details,address, customername, contactno, deliveryfee,itemquantity, pricepergallon,service,totalprice,watertype;

        public ViewHolder(View itemView) {
            super(itemView);

            transactionNo = itemView.findViewById(R.id.transactionNoIN);
            status = itemView.findViewById(R.id.transactionStatusIN);
            itemView.setOnClickListener(new View    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
