package com.example.administrator.h2bot.customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.objects.WaterStationOrDealer;

import java.util.ArrayList;

public class SearchMerchantAdapter extends RecyclerView.Adapter<SearchMerchantAdapter.ViewHolder> {
    private ArrayList<WaterStationOrDealer> thisList;
    private Context myContext;

    public SearchMerchantAdapter() {
    }

    public SearchMerchantAdapter(Context myContext, ArrayList<WaterStationOrDealer> thisList) {
        this.myContext = myContext;
        this.thisList = thisList;
    }

    @NonNull
    @Override


    public SearchMerchantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.search_merchant_popup_layout, viewGroup, false);
        return new SearchMerchantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchMerchantAdapter.ViewHolder viewHolder, int i) {
        final WaterStationOrDealer merchants = thisList.get(i);
        String distance = merchants.getDistance();

        viewHolder.distance.setText(distance);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView merchantName, address, distance, travelTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            merchantName = itemView.findViewById(R.id.merchantName);
            address = itemView.findViewById(R.id.address);
            distance = itemView.findViewById(R.id.distance);
            travelTime = itemView.findViewById(R.id.travelTime);
        }
    }

    public void setList(ArrayList<WaterStationOrDealer> thisList){
        this.thisList = thisList;
    }
}
