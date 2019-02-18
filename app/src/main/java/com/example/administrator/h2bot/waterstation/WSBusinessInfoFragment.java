package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WSBusinessInfoFragment extends Fragment implements View.OnClickListener
{
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    Button updateBI;
    TextView stationName, stationAddress, stationHours, stationTelNo, stationFeePerGal, stationDelivery, stationStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_ws_businessinfo, container, false);

       stationName = view.findViewById(R.id.stationaNameBI);
       stationAddress = view.findViewById(R.id.stationAddressBI);
       stationHours = view.findViewById(R.id.stationHoursBI);
       stationTelNo = view.findViewById(R.id.stationTelNoBI);
       stationFeePerGal = view.findViewById(R.id.stationFeePerGalBI);
       stationDelivery = view.findViewById(R.id.stationDeliveryBI);
       stationStatus = view.findViewById(R.id.stationStatusBI);

       mAuth = FirebaseAuth.getInstance();
       firebaseUser = mAuth.getCurrentUser();
       firebaseDatabase = FirebaseDatabase.getInstance();
       databaseReference = firebaseDatabase.getReference("User_WS_Business_Info_File").child(firebaseUser.getUid());
       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   UserWSBusinessInfoFile userWSBusinessInfoFile = dataSnapshot.getValue(UserWSBusinessInfoFile.class);
                   if(userWSBusinessInfoFile != null)
                   {
                       String businessTime = userWSBusinessInfoFile.getBusiness_start_time()+" - "+userWSBusinessInfoFile.getBusiness_end_time();
                        stationName.setText("Station Name: "+userWSBusinessInfoFile.getBusiness_name());
                        stationAddress.setText("Full Address: "+userWSBusinessInfoFile.getBusiness_address());
                        stationHours.setText("Business Hours: "+businessTime);
                        stationTelNo.setText("Contact No.: "+userWSBusinessInfoFile.getBusiness_tel_no());
                        stationFeePerGal.setText("Fee per Gallon: "+userWSBusinessInfoFile.getBusiness_delivery_fee_per_gal());
                        stationDelivery.setText("Delivery Status: "+userWSBusinessInfoFile.getBusiness_delivery_service_status());
                        stationStatus.setText("Station Status: "+userWSBusinessInfoFile.getBusiness_status());
                   }
                   else
                   {
                       showMessages("Data does not exists");
                   }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Does not exists!");
           }
       });

       updateBI = view.findViewById(R.id.updateButtonWSBI);
       updateBI.setOnClickListener(this);

       return view;
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateButtonWSBI:

                break;
        }
    }
}
