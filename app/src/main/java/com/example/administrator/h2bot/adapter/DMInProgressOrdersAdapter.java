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
import com.example.administrator.h2bot.models.UserFile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DMInProgressOrdersAdapter extends RecyclerView.Adapter<DMInProgressOrdersAdapter.ImageViewholder> {

    private Context contextHolder;
    private List<OrderModel> uploadHolder;


    public DMInProgressOrdersAdapter(Context context, List<OrderModel>data)
    {
        contextHolder = context;
        uploadHolder = data;
    }

    public ImageViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.x_merchant_dm_transaction_inprogressorder, viewGroup, false);
        return new ImageViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DMInProgressOrdersAdapter.ImageViewholder imageViewholder, int i) {
        final OrderModel currentData = uploadHolder.get(i);
        String transactionNo = currentData.getOrder_no();
        String transactionStatus = currentData.getOrder_status();
        String transactionCustomer = currentData.getOrder_customer_id();
        String transactionStation = currentData.getOrder_merchant_id();
        imageViewholder.transactionNoText.setText(transactionNo);
        imageViewholder.transactionStatusText.setText(transactionStatus);
        String customerNo = currentData.getOrder_customer_id();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
        reference.child(customerNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserFile userFile = dataSnapshot.getValue(UserFile.class);
                if (userFile != null)
                {
                    String customerName = userFile.getUser_lastname() +", "+ userFile.getUser_firstname();
                    imageViewholder.transactionCustomerText.setText(customerName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapMerchantFragment additem = new MapMerchantFragment();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_dm, additem)
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
        });
    }

    @Override
    public int getItemCount() {
        return uploadHolder.size();
    }

    public class ImageViewholder extends RecyclerView.ViewHolder{
        TextView transactionNoText, transactionStatusText, transactionCustomerText;
        public ImageViewholder(@NonNull View itemView) {
            super(itemView);
            transactionCustomerText = itemView.findViewById(R.id.customerNameDMCOM);
            transactionNoText = itemView.findViewById(R.id.transactionNoINDM);
            transactionStatusText = itemView.findViewById(R.id.transactionStatusINDM);
        }
    }
}
