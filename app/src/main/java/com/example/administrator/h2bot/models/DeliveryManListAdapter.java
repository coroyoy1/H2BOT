package com.example.administrator.h2bot.models;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.waterstation.WSDMInformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeliveryManListAdapter extends RecyclerView.Adapter<DeliveryManListAdapter.ImageViewHolder>
{
    private Context contextHolder;
    private List<UserFile> uploadHolder;

    public DeliveryManListAdapter(Context context, List<UserFile> uploads)
    {
        contextHolder = context;
        uploadHolder = uploads;
    }

    @NonNull
    @Override
    public DeliveryManListAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(contextHolder).inflate(R.layout.x_ws_dm_list, viewGroup,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryManListAdapter.ImageViewHolder imageViewHolder, int i) {
        final UserFile currentData = uploadHolder.get(i);
        String fullName = currentData.getUser_firtname() +" "+ currentData.getUser_lastname();
        imageViewHolder.DeliveryName.setText(fullName);
        String dmImage = currentData.getUser_uri();
        Picasso.get().load(dmImage).fit().centerCrop().into(imageViewHolder.DeliveryImage);

        imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSDMInformation addInformation = new WSDMInformation();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                        .replace(R.id.fragment_container_ws, addInformation)
                        .addToBackStack(null)
                        .commit();

                String fullNameString = currentData.getUser_firtname() +" "+ currentData.getUser_lastname();
                Bundle args = new Bundle();
                args.putString("NameDM", fullNameString);
                args.putString("AddressDM", currentData.getUser_address());
                args.putString("ContactNoDM", currentData.getUser_phone_no());
                args.putString("StatusDM", currentData.getUser_status());
                args.putString("ImageDM", currentData.getUser_uri());
                args.putString("UIDDM", currentData.getUser_getUID());
                addInformation.setArguments(args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadHolder.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView DeliveryName;
        public CircleImageView DeliveryImage;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            DeliveryImage = itemView.findViewById(R.id.imageCircleDelM);
            DeliveryName = itemView.findViewById(R.id.deliveryOfName);
        }
    }
}