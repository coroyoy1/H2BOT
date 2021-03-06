package com.example.administrator.h2bot.dealer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.PendingListAdapter;
import com.example.administrator.h2bot.adapter.WPInProgressOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.waterstation.WSPendingOrderAcceptDeclineFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class WPPendingListAdapter extends RecyclerView.Adapter<WPPendingListAdapter.ImageViewholder> {

    private Context contextHolder;
    private List<OrderModel> uploadHolder;

    private WPInProgressOrdersAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(WPInProgressOrdersAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public WPPendingListAdapter(Context context, List<OrderModel> uploads)
    {
        contextHolder = context;
        uploadHolder = uploads;
    }

    @NonNull
    @Override
    public WPPendingListAdapter.ImageViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.x_merchant_transaction_pendingorder, viewGroup, false);
        return new WPPendingListAdapter.ImageViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WPPendingListAdapter.ImageViewholder imageViewholder, int i) {
        final OrderModel currentData = uploadHolder.get(i);
        String transactionNo = currentData.getOrder_no();
        String transactionStatus = currentData.getOrder_status();
        imageViewholder.transactionNoText.setText(transactionNo);
        imageViewholder.transactionStatusText.setText(transactionStatus);

        DatabaseReference customerFile = FirebaseDatabase.getInstance().getReference("User_File").child(currentData.getOrder_customer_id());
        customerFile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageViewholder.customerNamePEN.setText("Customer Name: "+dataSnapshot.child("user_firstname").getValue(String.class)+" "+dataSnapshot.child("user_lastname").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WPPendingOrderAcceptDeclineFragment additem = new WPPendingOrderAcceptDeclineFragment();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_wp, additem)
                        .addToBackStack(null)
                        .commit();

                Bundle bundle = new Bundle();
                bundle.putString("transactionno", transactionNo);
                Log.d("transactionno",""+transactionNo);
                bundle.putString("transactioncustomer", currentData.getOrder_customer_id());
                additem.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadHolder.size();
    }

    public class ImageViewholder extends RecyclerView.ViewHolder{
        TextView transactionNoText, transactionStatusText,customerNamePEN;
        public ImageViewholder(@NonNull View itemView) {
            super(itemView);
            transactionNoText = itemView.findViewById(R.id.transactionNoPEN);
            transactionStatusText = itemView.findViewById(R.id.transactionStatusPEN);
            customerNamePEN = itemView.findViewById(R.id.customerNamePEN);
            itemView.setOnClickListener(new View.OnClickListener() {
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
