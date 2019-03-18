package com.example.administrator.h2bot.dealer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.objects.ReportsDealerObject;

import org.w3c.dom.Text;

import java.util.List;

public class WaterDealerReportsAdapter
        extends RecyclerView.Adapter<WaterDealerReportsAdapter.ViewHolder> {

    private Context myContext;
    private List<ReportsDealerObject> list;

    public WaterDealerReportsAdapter(Object[] transferData){
        myContext = (Context) transferData[0];
        list = (List<ReportsDealerObject>) transferData[1];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.recycler_sales_reports, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TextView waterType = viewHolder.waterType;
        TextView itemSold = viewHolder.itemSold;
        TextView income = viewHolder.income;

        if(list.size() == 0){
            waterType.setText("No item");
            itemSold.setText("-");
            income.setText("-");
        }else{
            ReportsDealerObject object = list.get(i);
            waterType.setText(object.getWaterType());
            itemSold.setText(object.getItemSold() + "");
            income.setText(object.getIncome() + "");
        }
    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(this.list.size() == 0){
                arr = 0;
            }else{
                arr=this.list.size();
            }
        }catch (Exception e){
            Toast.makeText(myContext, e + "", Toast.LENGTH_SHORT).show();
        }
        return arr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView waterType;
        public TextView itemSold;
        public TextView income;


        public ViewHolder(View itemView){
            super(itemView);
            waterType = itemView.findViewById(R.id.water_type);
            itemSold = itemView.findViewById(R.id.item_sold);
            income = itemView.findViewById(R.id.income);
        }
    }
}
