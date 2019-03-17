package com.example.administrator.h2bot.dealer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.example.administrator.h2bot.models.DateModel;
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
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

public class WPSalesReportFragment extends Fragment {
    Spinner spinner;
    FirebaseUser currentUser;
    String currendId;
    private ArrayList<OrderModel> reportsList;

    TextView itemSoldMineral,incomeMineral;
    TextView purifiedWaterItemSoldTextView,incomePurifiedWater;
    TextView distilledSold,distilledIncome;
    TextView alkalineSold,alkalineIncome;
    TextView totalItemSold, totalIncome;
    int purifiedSoldCtr, alkalineSoldCtr, distilledSoldCtr, mineralSoldCtr, totalItemCtr;
    double purifiedIncomeCtr, alkalineIncomeCtr, distilledIncomeCtr, mineralIncomeCtr, totalIncomeCtr;

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

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        getOrderFile();

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
            String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
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
        if(reportsList.size() == 0){
            return;
        }

        purifiedSoldCtr = alkalineSoldCtr = distilledSoldCtr = mineralSoldCtr = totalItemCtr = 0;
        purifiedIncomeCtr = alkalineIncomeCtr = distilledIncomeCtr = mineralIncomeCtr = totalIncomeCtr = 0;

        for(OrderModel list: reportsList){
            String deliveryDate = convertDate(list.getOrder_delivery_date());
            String thisDate = convertDate(Calendar.getInstance().getTime().toString());

            if(currentUser.getUid().equalsIgnoreCase(list.getOrder_merchant_id())) {
                if (deliveryDate.equalsIgnoreCase(thisDate)) {
                    if (list.getOrder_status().equalsIgnoreCase("Completed")) {

                        if (list.getOrder_water_type().equalsIgnoreCase("Purified")) {
                            purifiedIncomeCtr += Double.parseDouble(list.getOrder_total_amt());
                            purifiedSoldCtr += Integer.parseInt(list.getOrder_qty());
                            totalItemCtr += purifiedSoldCtr;
                            totalIncomeCtr += purifiedIncomeCtr;
                        } else if (list.getOrder_water_type().equalsIgnoreCase("Distilled")) {
                            distilledIncomeCtr += Double.parseDouble(list.getOrder_total_amt());
                            distilledSoldCtr += Integer.parseInt(list.getOrder_qty());
                            totalItemCtr += distilledSoldCtr;
                            totalIncomeCtr += distilledIncomeCtr;
                        } else if (list.getOrder_water_type().equalsIgnoreCase("Mineral")) {
                            mineralIncomeCtr += Double.parseDouble(list.getOrder_total_amt());
                            mineralSoldCtr += Integer.parseInt(list.getOrder_qty());
                            totalItemCtr += mineralSoldCtr;
                            totalIncomeCtr += mineralIncomeCtr;
                        } else if (list.getOrder_water_type().equalsIgnoreCase("Alkaline")) {
                            alkalineIncomeCtr += Double.parseDouble(list.getOrder_total_amt());
                            alkalineSoldCtr += Integer.parseInt(list.getOrder_qty());
                            totalItemCtr += alkalineSoldCtr;
                            totalIncomeCtr += alkalineIncomeCtr;
                        }
                    }
                }
            }
        }
        Display();
    }

    private void Display(){

        itemSoldMineral.setText(mineralSoldCtr + "");
        incomeMineral.setText(mineralIncomeCtr + "");

        purifiedWaterItemSoldTextView.setText(purifiedSoldCtr + "");
        incomePurifiedWater.setText(purifiedIncomeCtr + "");

        distilledSold.setText(distilledSoldCtr + "");
        distilledIncome.setText(distilledIncomeCtr + "");

        alkalineSold.setText(alkalineSoldCtr + "");
        alkalineIncome.setText(alkalineIncomeCtr + "");

        totalItemSold.setText(totalItemCtr + "");
        totalIncome.setText(totalIncomeCtr + "");
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
        itemSoldMineral = view.findViewById(R.id.itemSoldMineral);
        incomeMineral = view.findViewById(R.id.incomeMineral);
        purifiedWaterItemSoldTextView = view.findViewById(R.id.purifiedWaterItemSoldTextView);
        incomePurifiedWater = view.findViewById(R.id.incomePurifiedWater);
        distilledSold = view.findViewById(R.id.distilledSoldTextView);
        distilledIncome = view.findViewById(R.id.distilledIncomeTextView);
        alkalineSold = view.findViewById(R.id.alkalineWaterItemSoldTextView);
        alkalineIncome = view.findViewById(R.id.alkalineIncomeTextView);
        totalItemSold = view.findViewById(R.id.totalItemSold);
        totalIncome = view.findViewById(R.id.totalIncome);

        purifiedSoldCtr = alkalineSoldCtr = distilledSoldCtr = mineralSoldCtr = totalItemCtr = 0;
        purifiedIncomeCtr = alkalineIncomeCtr = distilledIncomeCtr = mineralIncomeCtr = totalIncomeCtr = 0;
    }
}