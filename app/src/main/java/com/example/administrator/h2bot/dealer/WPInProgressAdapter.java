package com.example.administrator.h2bot.dealer;

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
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;

import java.util.ArrayList;
import java.util.List;

public class WPInProgressAdapter extends RecyclerView.Adapter<WPInProgressAdapter.ViewHolder>{
    private Context mContext;
    private List<OrderModel> mUploads;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public WPInProgressAdapter(Context context, ArrayList<OrderModel> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public WPInProgressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.wpinprogressxml, viewGroup, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull WPInProgressAdapter.ViewHolder viewHolder, int i) {
        final OrderModel currentData = mUploads.get(i);
        viewHolder.transactionNo.setText(currentData.getOrder_no());
        viewHolder.status.setText(currentData.getOrder_status());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WPInProgressAccept detail = new WPInProgressAccept();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp, detail).addToBackStack(null).commit();
                Bundle args = new Bundle();
                args.putString("transactionno", currentData.getOrder_no());
                args.putString("transactioncustomer", currentData.getOrder_customer_id());
                args.putString("status", currentData.getOrder_status());
                detail.setArguments(args);
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

            transactionNo = itemView.findViewById(R.id.transactionNo);
            status = itemView.findViewById(R.id.status);

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
