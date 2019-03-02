package com.example.administrator.h2bot.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.deliveryman.DMInProgressAcception;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.waterstation.WSPendingOrderAcceptDeclineFragment;

import java.util.List;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class DMInProgressOrdersAdapter extends RecyclerView.Adapter<DMInProgressOrdersAdapter.ImageViewholder> {

    private Context contextHolder;
    private List<TransactionHeaderFileModel> uploadHolder;


    public DMInProgressOrdersAdapter(Context context, List<TransactionHeaderFileModel>data)
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
        final TransactionHeaderFileModel currentData = uploadHolder.get(i);
        String transactionNo = currentData.getTrans_no();
        String transactionStatus = currentData.getTrans_status();
        imageViewholder.transactionNoText.setText(transactionNo);
        imageViewholder.transactionStatusText.setText(transactionStatus);

        imageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMInProgressAcception additem = new DMInProgressAcception();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_dm, additem)
                        .addToBackStack(null)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("transactionno", transactionNo);
                additem.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadHolder.size();
    }

    public class ImageViewholder extends RecyclerView.ViewHolder{
        TextView transactionNoText, transactionStatusText;
        public ImageViewholder(@NonNull View itemView) {
            super(itemView);
            transactionNoText = itemView.findViewById(R.id.transactionNoINDM);
            transactionStatusText = itemView.findViewById(R.id.transactionStatusINDM);
        }
    }
}
