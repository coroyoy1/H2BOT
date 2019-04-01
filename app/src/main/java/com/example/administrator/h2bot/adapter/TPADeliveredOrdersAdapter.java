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
import com.example.administrator.h2bot.dealer.WPInProgressAccept;
import com.example.administrator.h2bot.models.AffiliateStationOrderModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.tpaaffiliate.TPAAcceptedFragment;
import com.example.administrator.h2bot.tpaaffiliate.TPAAcceptedOrdersInfo;
import com.example.administrator.h2bot.tpaaffiliate.TPADeliveredInfoFragment;
import com.example.administrator.h2bot.tpaaffiliate.TPAMapDestinationFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TPADeliveredOrdersAdapter extends RecyclerView.Adapter<TPADeliveredOrdersAdapter.ViewHolder>{
    private Context mContext;
    private List<AffiliateStationOrderModel> mUploads;
    private TPADeliveredOrdersAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(TPADeliveredOrdersAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public TPADeliveredOrdersAdapter(Context context, List<AffiliateStationOrderModel> uploads) {
        mContext = context;
        mUploads = uploads;

    }
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.x_merchant_transaction_inprogressorder, viewGroup, false);
        return new TPADeliveredOrdersAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull TPADeliveredOrdersAdapter.ViewHolder viewHolder, int i) {
        final AffiliateStationOrderModel currentData = mUploads.get(i);
        viewHolder.transactionNo.setText(currentData.getOrderNo());
        viewHolder.status.setText(currentData.getStatus());

        DatabaseReference customerFile = FirebaseDatabase.getInstance().getReference("User_File").child(currentData.getCustomerId());
        customerFile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.customerNameIN.setText("Customer Name: "+dataSnapshot.child("user_firstname").getValue(String.class)+" "+dataSnapshot.child("user_lastname").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TPADeliveredInfoFragment additem = new TPADeliveredInfoFragment();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, additem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("orderno", currentData.getOrderNo());
                args.putString("stationid", currentData.getStationid());
                args.putString("customerid", currentData.getCustomerId());
                args.putString("status", currentData.getStatus());
                additem.setArguments(args);
            }
        });

        AffiliateStationOrderModel uploadCurrent = mUploads.get(i);
        viewHolder.transactionNo.setText(uploadCurrent.getOrderNo());
        viewHolder.status.setText(uploadCurrent.getStatus());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView customerNameIN,transactionNo, status, details,address, customername, contactno, deliveryfee,itemquantity, pricepergallon,service,totalprice,watertype;

        public ViewHolder(View itemView) {
            super(itemView);

            transactionNo = itemView.findViewById(R.id.transactionNoIN);
            status = itemView.findViewById(R.id.transactionStatusIN);
            customerNameIN = itemView.findViewById(R.id.customerNameIN);
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
