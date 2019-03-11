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
import com.example.administrator.h2bot.deliveryman.DMCompletedAcception;
import com.example.administrator.h2bot.deliveryman.DMInProgressAcception;
import com.example.administrator.h2bot.models.DeliveryManListAdapter;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.waterstation.WSPendingOrderAcceptDeclineFragment;

import java.util.List;

public class DMCompletedOrderAdapter extends RecyclerView.Adapter<DMCompletedOrderAdapter.ImageViewHolder> {

    private Context contextHolder;
    private List<OrderModel> uploadHolder;

    public DMCompletedOrderAdapter(Context context, List<OrderModel> uploads)
    {
        contextHolder = context;
        uploadHolder = uploads;
    }

    @NonNull
    @Override
    public DMCompletedOrderAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.x_merchant_transaction_completedtransaction, viewGroup, false);
        return new DMCompletedOrderAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DMCompletedOrderAdapter.ImageViewHolder imageViewHolder, int i) {
        final OrderModel currentData = uploadHolder.get(i);
        String transactionNo = currentData.getOrder_no();
        String transactionStatus = currentData.getOrder_status();
        imageViewHolder.transactionNoText.setText(transactionNo);
        imageViewHolder.transactionStatusText.setText(transactionStatus);

        imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMCompletedAcception additem = new DMCompletedAcception();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_dm, additem)
                        .addToBackStack(null)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("transactionno", transactionNo);
                additem.setArguments(bundle);
//                WSPendingOrderAcceptDeclineFragment additem = new WSPendingOrderAcceptDeclineFragment();
//                AppCompatActivity activity = (AppCompatActivity)v.getContext();
//                activity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
//                        .replace(R.id.fragment_container_ws, additem)
//                        .addToBackStack(null)
//                        .commit();
//
//                Bundle bundle = new Bundle();
//                bundle.putString("transactNoString", transactionNo);
//                additem.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadHolder.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView transactionNoText, transactionStatusText;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionNoText = itemView.findViewById(R.id.transactionNoCOM);
            transactionStatusText = itemView.findViewById(R.id.transactionStatusCOM);
        }
    }
}
