package com.example.administrator.h2bot.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;

import java.util.List;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.ImageViewholder> {

    private Context contextHolder;
    private List<TransactionHeaderFileModel> uploadHolder;

    public PendingListAdapter(Context context, List<TransactionHeaderFileModel> uploads)
    {
        contextHolder = context;
        uploadHolder = uploads;
    }

    @NonNull
    @Override
    public ImageViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.x_merchant_transaction_pendingorder, viewGroup, false);
        return new ImageViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewholder imageViewholder, int i) {
        final TransactionHeaderFileModel currentData = uploadHolder.get(i);
        String transactionNo = currentData.getTrans_no();
        String transactionStatus = currentData.getTrans_status();
        imageViewholder.transactionNoText.setText(transactionNo);
        imageViewholder.transactionStatusText.setText(transactionStatus);

        imageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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
            transactionNoText = itemView.findViewById(R.id.transactionNoPEN);
            transactionStatusText = itemView.findViewById(R.id.transactionStatusPEN);
        }
    }
}
