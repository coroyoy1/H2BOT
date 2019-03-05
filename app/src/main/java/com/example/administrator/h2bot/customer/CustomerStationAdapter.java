package com.example.administrator.h2bot.customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;

import java.util.List;

public class CustomerStationAdapter extends RecyclerView.Adapter<CustomerStationAdapter.ViewHolder> {

    public static String station_id;
    private Context myContext;
    private List<UserWSBusinessInfoFile> infoList;

    public CustomerStationAdapter() {
    }

    public CustomerStationAdapter(Context myContext, List<UserWSBusinessInfoFile> infoList) {
        this.myContext = myContext;
        this.infoList = infoList;
    }

    @NonNull
    @Override
    public CustomerStationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.customer_order_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerStationAdapter.ViewHolder viewHolder, int i) {
        final UserWSBusinessInfoFile infoFile = infoList.get(i);
        String stationName = infoFile.getBusiness_name();
        String business_id = infoFile.getBusiness_id();
        viewHolder.station_name.setText(stationName);
        viewHolder.station_id.setText(business_id);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                station_id = business_id;
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container, new CustomerAllOrdersFragment())
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView station_name;
        TextView station_id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            station_name = itemView.findViewById(R.id.station_name);
            station_id = itemView.findViewById(R.id.station_id);
        }
    }
}
