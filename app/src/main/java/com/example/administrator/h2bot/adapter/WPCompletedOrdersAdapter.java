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
import com.example.administrator.h2bot.dealer.WPCompletedAccept;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;


import java.util.List;

public class WPCompletedOrdersAdapter extends RecyclerView.Adapter<WPCompletedOrdersAdapter.ViewHolder>{
    private Context mContext;
    private List<TransactionHeaderFileModel> mUploads;
    private WPCompletedOrdersAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(WPCompletedOrdersAdapter.OnItemClickListener listener) {
        mListener = (WPCompletedOrdersAdapter.OnItemClickListener) listener;
    }
    public WPCompletedOrdersAdapter(Context context, List<TransactionHeaderFileModel> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @NonNull
    @Override
    public WPCompletedOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.x_merchant_transaction_completedtransaction, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull WPCompletedOrdersAdapter.ViewHolder viewHolder, int i) {
        final TransactionHeaderFileModel currentData = mUploads.get(i);
        viewHolder.transactionNo.setText(currentData.getTrans_no());
        viewHolder.status.setText(currentData.getTrans_status());
        String transactno= currentData.getTrans_no();
        String status= currentData.getTrans_status();

       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               WPCompletedAccept additem = new WPCompletedAccept();
               AppCompatActivity activity = (AppCompatActivity)v.getContext();
               activity.getSupportFragmentManager()
                       .beginTransaction()
                       .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                       .replace(R.id.fragment_container_wp, additem)
                       .addToBackStack(null)
                       .commit();
               Bundle args = new Bundle();
               args.putString("transactionno", currentData.getTrans_no());
               args.putString("status", currentData.getTrans_status());
               additem.setArguments(args);
           }
       });
        TransactionHeaderFileModel uploadCurrent = mUploads.get(i);
        viewHolder.transactionNo.setText(uploadCurrent.getTrans_no());
        viewHolder.status.setText(uploadCurrent.getTrans_status());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionNo, status, details,address, customername, contactno, deliveryfee,itemquantity, pricepergallon,service,totalprice,watertype;
        public ViewHolder(View itemView) {

            super(itemView);
            transactionNo = itemView.findViewById(R.id.transactionNoCOM);
            status = itemView.findViewById(R.id.transactionStatusCOM);
//            details = itemView.findViewById(R.id.t);
//            address = itemView.findViewById(R.id.address);
//            customername = itemView.findViewById(R.id.customername);
//            contactno = itemView.findViewById(R.id.contactno);
//            deliveryfee = itemView.findViewById(R.id.deliveryfee);
//            itemquantity = itemView.findViewById(R.id.itemquantity);
//            pricepergallon = itemView.findViewById(R.id.pricepergallon);
//            service = itemView.findViewById(R.id.service);
//            totalprice = itemView.findViewById(R.id.totalprice);
//            watertype = itemView.findViewById(R.id.watertype);


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
