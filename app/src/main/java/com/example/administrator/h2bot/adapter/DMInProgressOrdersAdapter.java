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

import java.util.List;

public class DMInProgressOrdersAdapter extends RecyclerView.Adapter<DMInProgressOrdersAdapter.ViewHolder> {

    private Context mContext;
    private List<TransactionHeaderFileModel> mUploads;
    private DMInProgressOrdersAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(DMInProgressOrdersAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public DMInProgressOrdersAdapter(Context context, List<TransactionHeaderFileModel> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public DMInProgressOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(mContext).inflate(R.layout.x_merchant_dm_transaction_inprogressorder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DMInProgressOrdersAdapter.ViewHolder viewHolder, int i) {
        final TransactionHeaderFileModel currentData = mUploads.get(i);
        viewHolder.transactionNo.setText(currentData.getTrans_no());
        viewHolder.status.setText(currentData.getTrans_status());

        String transactno= currentData.getTrans_no();
        String status= currentData.getTrans_status();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WSInProgressAccept additem = new WSInProgressAccept();
//                AppCompatActivity activity = (AppCompatActivity)v.getContext();
//                activity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .replace(R.id.fragment_container_ws, additem)
//                        .addToBackStack(null)
//                        .commit();
//                Bundle args = new Bundle();
//                args.putString("transactionno", currentData.getTrans_no());
//                args.putString("status", currentData.getTrans_status());
////                args.putString("customername", currentData.getCustomerName());
////                args.putString("address", currentData.getAddress());
////                args.putString("contactno", currentData.getContactNo());
////                args.putString("deliveryfee", currentData.getDeliveryFee());
////                args.putString("itemquantity", currentData.getItemQuantity());
////                args.putString("pricepergallon", currentData.getPricePerGallon());
////                args.putString("service", currentData.getService());
////                args.putString("totalprice", currentData.getTotalPrice());
////                args.putString("watertype", currentData.getWaterType());
//                additem.setArguments(args);
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
        public ViewHolder(@NonNull View rootView) {
            super(rootView);
            transactionNo = rootView.findViewById(R.id.transactionStatusINDM);
            status = rootView.findViewById(R.id.transactionStatusINDM);

            rootView.setOnClickListener(new View.OnClickListener() {
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
