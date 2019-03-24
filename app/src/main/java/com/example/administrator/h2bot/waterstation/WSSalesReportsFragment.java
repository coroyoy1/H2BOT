package com.example.administrator.h2bot.waterstation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.customer.CustomerWaterTypeAdapter;
import com.example.administrator.h2bot.dealer.WaterDealerReportsAdapter;
import com.example.administrator.h2bot.models.DateModel;
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.example.administrator.h2bot.models.WaterTypeModel;
import com.example.administrator.h2bot.objects.ReportsDealerObject;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WSSalesReportsFragment extends Fragment {
    Spinner spinner;
    FirebaseUser currentUser;
    String currendId;
    private ArrayList<OrderModel> reportsList;
    private List<WaterTypeModel> waterTypeModelList;
    private TextView totalIncome;
    private TextView totalItemSold;
    private BarChart barChart;

    private RecyclerView reportsRecycler;
    int totalItemCtr;
    double totalIncomeCtr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_report, container, false);
        refIDs(view);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currendId = currentUser.getUid();

        spinner = view.findViewById(R.id.spinner);
        String[] arraySpinner = new String[]{
                "Today", "Monthly", "Yearly"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        reportsList = new ArrayList<OrderModel>();
        waterTypeModelList = new ArrayList<>();

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        getOrderFile();
        getWaterType();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        attemptToExit();
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void getWaterType(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File").child(currentUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                waterTypeModelList.clear();
                for (DataSnapshot post1 : dataSnapshot.getChildren()){
                    WaterTypeModel waterTypeModel = post1.getValue(WaterTypeModel.class);
                    waterTypeModelList.add(waterTypeModel);
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOrderFile(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportsList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for (DataSnapshot post1 : dataSnapshot1.getChildren()){
                        for (DataSnapshot post : post1.getChildren()) {
                            OrderModel orderFileModel = post.getValue(OrderModel.class);
                            reportsList.add(orderFileModel);
                        }
                    }
                }
                getList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String convertDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date convertedDate;
        String date = "";
        String filter = spinner.getSelectedItem().toString();
        try {
            convertedDate = dateFormat.parse(dateString);
            if(filter.equalsIgnoreCase("Monthly")) {
                SimpleDateFormat postFormater = new SimpleDateFormat("MMMM");
                date = postFormater.format(convertedDate);
            }
            else if(filter.equalsIgnoreCase("Today")) {
                SimpleDateFormat postFormater = new SimpleDateFormat("dd");
                date = postFormater.format(convertedDate);
            }
            else if(filter.equalsIgnoreCase("Yearly")) {
                SimpleDateFormat postFormater = new SimpleDateFormat("yyyy");
                date = postFormater.format(convertedDate);
            }
        } catch (ParseException e) {
            String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
            try {
                convertedDate = new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(dateString);
                if(filter.equalsIgnoreCase("Monthly")) {
                    SimpleDateFormat postFormater = new SimpleDateFormat("MMMM");
                    date = postFormater.format(convertedDate);
                }
                else if(filter.equalsIgnoreCase("Today")) {
                    SimpleDateFormat postFormater = new SimpleDateFormat("dd");
                    date = postFormater.format(convertedDate);
                }
                else if(filter.equalsIgnoreCase("Yearly")) {
                    SimpleDateFormat postFormater = new SimpleDateFormat("yyyy");
                    date = postFormater.format(convertedDate);
                }
            }catch (ParseException a){
                a.printStackTrace();
            }
        }

        return date;
    }

    private void getList(){
        if(reportsList.size() == 0 || waterTypeModelList.size() == 0){
            return;
        }

        List<ReportsDealerObject> reportsObject = new ArrayList<>();
        for(WaterTypeModel water: waterTypeModelList){
            ReportsDealerObject content = new ReportsDealerObject(water.getWater_type(), 0, 0);
            reportsObject.add(content);
        }

        totalItemCtr = 0;
        totalIncomeCtr = 0;

        for(OrderModel list: reportsList){
            String deliveryDate = convertDate(list.getOrder_date_issued());
            String thisDate = convertDate(Calendar.getInstance().getTime().toString());

            if(currentUser.getUid().equalsIgnoreCase(list.getOrder_merchant_id())) {
                if (deliveryDate.equalsIgnoreCase(thisDate)) {
                    if (list.getOrder_status().equalsIgnoreCase("Completed")) {

                        for(ReportsDealerObject waterTypeModel: reportsObject){
                            String water = list.getOrder_water_type();
                            String type = waterTypeModel.getWaterType();
                            if (list.getOrder_water_type().equalsIgnoreCase(waterTypeModel.getWaterType())) {
                                double income = waterTypeModel.getIncome() + Double.parseDouble(list.getOrder_total_amt());
                                int qty = waterTypeModel.getItemSold() + Integer.parseInt(list.getOrder_qty());
                                waterTypeModel.setIncome(income);
                                waterTypeModel.setItemSold(qty);
                                totalItemCtr += Integer.parseInt(list.getOrder_qty());
                                totalIncomeCtr += Double.parseDouble(list.getOrder_total_amt());
                                break;
                            }
                        }
                    }
                }
            }
        }
        Display(reportsObject);
    }

    private void getChartValue(List<ReportsDealerObject> reportsObject){
        barChart.invalidate();
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> products = new ArrayList<>();
        int ctr = 0;
        for(ReportsDealerObject list: reportsObject){
            barEntries.add(new BarEntry(list.getItemSold(), ctr));
            products.add(list.getWaterType());
            ctr++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Products");

        BarData barData = new BarData(products, barDataSet);
        barChart.setData(barData);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }

    private void Display(List<ReportsDealerObject> reportsObject){
        getChartValue(reportsObject);
        Object[] transferObject = new Object[2];
        transferObject[0] = this.getContext();
        transferObject[1] = reportsObject;
        WaterDealerReportsAdapter adapter = new WaterDealerReportsAdapter(transferObject);
        RecyclerView.LayoutManager recycle = new GridLayoutManager(this.getContext(), 1);
        reportsRecycler.setLayoutManager(recycle);
        reportsRecycler.setAdapter(adapter);

        totalIncome.setText(totalIncomeCtr + "");
        totalItemSold.setText(totalItemCtr + "");
    }

    public void attemptToExit()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to exit the application?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void refIDs(View view){
        totalIncome = view.findViewById(R.id.totalIncome);
        totalItemSold = view.findViewById(R.id.totalItemSold);
        reportsRecycler = view.findViewById(R.id.reports_recycler);
        barChart = view.findViewById(R.id.barGraph);
        totalItemCtr = 0;
        totalIncomeCtr = 0;
    }
}