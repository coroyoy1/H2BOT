package com.example.administrator.h2bot.dealer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.DateModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WPSalesReportFragment extends Fragment {
    Spinner spinner;
    FirebaseUser currentUser;
    String currendId;
    private ArrayList<OrderModel> adapter3;
    private ArrayList<DateModel> adapter2;
    TextView purifiedWaterItemSoldTextView,incomePurifiedWater ,itemSoldMineral,incomeMineral;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currendId = currentUser.getUid();
        View view = inflater.inflate(R.layout.fragment_sales_report, container, false);
        spinner = view.findViewById(R.id.spinner);
        itemSoldMineral = view.findViewById(R.id.itemSoldMineral);
        incomeMineral = view.findViewById(R.id.incomeMineral);
        String[] arraySpinner = new String[]{
                "Today", "Weekly", "Monthly", "Yearly"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter3 = new ArrayList<OrderModel>();
        adapter2 = new ArrayList<DateModel>();
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("hihi","Hi");
                adapter3.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Log.d("hiho","Hi");
                    for (DataSnapshot post : dataSnapshot1.child(currendId).getChildren())
                    {
                        Log.d("Hoko","Hi");
                        OrderModel orderModel = post.getValue(OrderModel.class);
                        DateModel dateModel = post.getValue(DateModel.class);
                        if(orderModel != null)
                        {
                            Log.d("Hiko","Hi");
//                            String date = (String) DateFormat.format("MMM",  dateModel.getOrder_delivery_date());
                            String dateString = orderModel.getOrder_delivery_date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                            Date convertedDate = new Date();
                            try {
                                convertedDate = dateFormat.parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String date = (String) DateFormat.format("MMM",  convertedDate);
                            Log.d("Hiko","Hi"+date);
                            String s = orderModel.getOrder_water_type();
                            String[] words = s.split("\\s+");
                            for (int i = 0; i < words.length; i++) {
                                // You may want to check for a non-word character before blindly
                                // performing a replacement
                                // It may also be necessary to adjust the character class
                                words[i] = words[i].replaceAll("[^\\w]", "");
                                Log.d("Words",""+words[i]);
                                if(words[i].equalsIgnoreCase("Mineral") )
                                {
                                    Log.d("Hiko","Hi"+orderModel.getOrder_status());
                                    adapter3.add(orderModel);
                                    adapter3.size();
                                    itemSoldMineral.setText(""+adapter3.size());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}
