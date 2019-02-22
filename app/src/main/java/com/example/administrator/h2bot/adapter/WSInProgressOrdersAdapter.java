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
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.waterstation.WSPendingOrderAcceptDeclineFragment;

import java.util.List;

public class WSInProgressOrdersAdapter extends RecyclerView.Adapter<WSInProgressOrdersAdapter.ViewHolder>{
    private Context mContext;
    private List<TransactionHeaderFileModel> mUploads;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public WSInProgressOrdersAdapter(Context context, List<TransactionHeaderFileModel> uploads) {
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
        final TransactionHeaderFileModel currentData = mUploads.get(i);
                viewHolder.transactionNo.setText(currentData.getTrans_no());
                viewHolder.status.setText(currentData.getTrans_status());
//                viewHolder.customername.setText(currentData.getCustomerName());
//                viewHolder.address.setText(currentData.getAddress());
//                viewHolder.contactno.setText(currentData.getContactNo());
//                viewHolder.deliveryfee.setText(currentData.getDeliveryFee());
//                viewHolder.itemquantity.setText(currentData.getItemQuantity());
//                viewHolder.pricepergallon.setText(currentData.getPricePerGallon());
//                viewHolder.service.setText(currentData.getService());
//                viewHolder.totalprice.setText(currentData.getTotalPrice());
//                viewHolder.watertype.setText(currentData.getWaterType());
        //viewHolder.details.setText(">>");

           String transactno= currentData.getTrans_no();
            String status= currentData.getTrans_status();
//        String customername= currentData.getCustomerName();
//        String address= currentData.getAddress();
//        String contactno= currentData.getContactNo();
//        String deliveryfee= currentData.getDeliveryFee();
//        String itemquantity= currentData.getItemQuantity();
//        String pricepergallon= currentData.getPricePerGallon();
//        String service= currentData.getService();
//        String totalprice= currentData.getTotalPrice();
//        String watertype= currentData.getWaterType();


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSPendingOrderAcceptDeclineFragment detail = new WSPendingOrderAcceptDeclineFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws, detail).addToBackStack(null).commit();
                Bundle args = new Bundle();
                args.putString("transactionno", currentData.getTrans_no());
                args.putString("status", currentData.getTrans_status());
//                args.putString("customername", currentData.getCustomerName());
//                args.putString("address", currentData.getAddress());
//                args.putString("contactno", currentData.getContactNo());
//                args.putString("deliveryfee", currentData.getDeliveryFee());
//                args.putString("itemquantity", currentData.getItemQuantity());
//                args.putString("pricepergallon", currentData.getPricePerGallon());
//                args.putString("service", currentData.getService());
//                args.putString("totalprice", currentData.getTotalPrice());
//                args.putString("watertype", currentData.getWaterType());
                detail.setArguments(args);
            }
        });

                TransactionHeaderFileModel uploadCurrent = mUploads.get(i);
                viewHolder.transactionNo.setText(uploadCurrent.getTrans_no());
                viewHolder.status.setText(uploadCurrent.getTrans_status());
//                viewHolder.customername.setText(uploadCurrent.getCustomerName());
//                viewHolder.address.setText(uploadCurrent.getAddress());
//                viewHolder.contactno.setText(uploadCurrent.getContactNo());
//                viewHolder.deliveryfee.setText(uploadCurrent.getDeliveryFee());
//                viewHolder.itemquantity.setText(uploadCurrent.getItemQuantity());
//                viewHolder.pricepergallon.setText(uploadCurrent.getPricePerGallon());
//                viewHolder.service.setText(uploadCurrent.getService());
//                viewHolder.totalprice.setText(uploadCurrent.getTotalPrice());
//                viewHolder.watertype.setText(uploadCurrent.getWaterType());
//                viewHolder.details.setText(">>");

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
//            details = itemView.findViewById(R.id.details);
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
