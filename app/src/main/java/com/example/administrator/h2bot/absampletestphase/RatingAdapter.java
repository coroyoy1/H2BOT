package com.example.administrator.h2bot.absampletestphase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.PendingListAdapter;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.waterstation.WSPendingOrderAcceptDeclineFragment;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ImageViewholder>{

    private Context contextHolder;
    private List<RatingModel> uploadHolder;

    public RatingAdapter(Context context, List<RatingModel> uploads)
    {
        contextHolder = context;
        uploadHolder = uploads;
    }

    @NonNull
    @Override
    public RatingAdapter.ImageViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.z_feedback_comment, viewGroup, false);
        return new RatingAdapter.ImageViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.ImageViewholder imageViewholder, int i) {
        final RatingModel currentData = uploadHolder.get(i);
        String comment = currentData.getRating_comment();
        String rate = currentData.getRating_number();

        imageViewholder.ratingBar.setEnabled(false);
        imageViewholder.ratingBar.setMax(5);
        imageViewholder.ratingBar.setStepSize(0.01f);
        imageViewholder.ratingBar.setRating(Float.parseFloat(rate));
        imageViewholder.ratingBar.invalidate();

        imageViewholder.textView.setText(comment);
    }

    @Override
    public int getItemCount() {
        return uploadHolder.size();
    }

    public class ImageViewholder extends RecyclerView.ViewHolder{
        RatingBar ratingBar;
        TextView textView;
        public ImageViewholder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBarFEED);
            textView = itemView.findViewById(R.id.commentFEED);
        }
    }
}
