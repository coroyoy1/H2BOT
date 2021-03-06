package com.example.administrator.h2bot.dealer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile2;
import com.example.administrator.h2bot.waterstation.WSBusinessDocumentUpdate;
import com.example.administrator.h2bot.waterstation.WSBusinessInformationUpdate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class WPBusinessInfoFragment extends Fragment implements View.OnClickListener {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReference2;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    Button updateBI, updateDocBI, updateInfo, cancelBI;
    TextView stationName, stationAddress, stationHours, stationTelNo, stationFeePerGal, stationDelivery, stationStatus,dealerdays,dealerDeliveryMethod;
    ImageView imageView;

    LinearLayout linearLayoutUp, linearLayoutUpNext;
    Dialog dialog;
    public WPBusinessInfoFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wp_businessinfo, container, false);
        dialog = new Dialog(getActivity());
        stationName = view.findViewById(R.id.stationaNameBI);
        stationAddress = view.findViewById(R.id.stationAddressBI);
        stationHours = view.findViewById(R.id.stationHoursBI);
        stationTelNo = view.findViewById(R.id.stationTelNoBI);
        stationFeePerGal = view.findViewById(R.id.stationFeePerGalBI);
        stationDelivery = view.findViewById(R.id.stationDeliveryBI);
        stationStatus = view.findViewById(R.id.stationStatusBI);
        imageView = view.findViewById(R.id.imageWaterStation);
        dealerdays = view.findViewById(R.id.dealerdays);
        linearLayoutUp = view.findViewById(R.id.linearForUpdate);
        linearLayoutUpNext = view.findViewById(R.id.linearForUpdateNext);
        linearLayoutUpNext.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("User_File").child(firebaseUser.getUid());
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stationName.setText(dataSnapshot.child("user_firstname").getValue(String.class)+" "+dataSnapshot.child("user_lastname").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = firebaseDatabase.getReference("User_WS_Business_Info_File").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WSBusinessInfoFile2 userWSBusinessInfoFile = dataSnapshot.getValue(WSBusinessInfoFile2.class);
                if(userWSBusinessInfoFile != null)
                {
                    String businessTime = userWSBusinessInfoFile.getBusiness_start_time()+" - "+userWSBusinessInfoFile.getBusiness_end_time();
                    stationAddress.setText(userWSBusinessInfoFile.getBusiness_address());
                    stationHours.setText(businessTime);
                    stationTelNo.setText(userWSBusinessInfoFile.getBusiness_tel_no());
                    stationStatus.setText(userWSBusinessInfoFile.getBusiness_min_no_of_gallons());
                    dealerdays.setText(userWSBusinessInfoFile.getBusiness_days());
                    }
                else
                {
                    showMessages("Data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Data does not exist!");
            }
        });
        databaseReference2 = firebaseDatabase.getReference("User_File").child(firebaseUser.getUid());
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserFile user = dataSnapshot.getValue(UserFile.class);
                String image = user.getUser_uri();
                Picasso.get().load(image).resize(1000,1000).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        updateBI = view.findViewById(R.id.updateButtonWSBI);
        updateDocBI = view.findViewById(R.id.updateDocumentWSBI);
        updateInfo = view.findViewById(R.id.updateInfoWSBI);
        cancelBI = view.findViewById(R.id.cancelWSBI);

        updateBI.setOnClickListener(this);
        updateDocBI.setOnClickListener(this);
        updateInfo.setOnClickListener(this);
        cancelBI.setOnClickListener(this);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
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

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateButtonWSBI:
                linearLayoutUpNext.setVisibility(View.VISIBLE);
                linearLayoutUp.setVisibility(View.GONE);
                break;
            case R.id.updateInfoWSBI:
                WPBusinessInformationUpdate additem = new WPBusinessInformationUpdate();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_wp, additem)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.updateDocumentWSBI:
                WPBusinessDocumentUpdate updateitem = new WPBusinessDocumentUpdate();
                AppCompatActivity updateActivity = (AppCompatActivity)v.getContext();
                updateActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_wp, updateitem)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.cancelWSBI:
                linearLayoutUpNext.setVisibility(View.GONE);
                linearLayoutUp.setVisibility(View.VISIBLE);
                break;
        }
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
