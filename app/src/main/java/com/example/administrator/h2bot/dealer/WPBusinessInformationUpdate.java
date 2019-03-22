package com.example.administrator.h2bot.dealer;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WPBusinessInformationUpdate extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText waterStationStartTime,
    waterStationEndTime, waterStationMinimumGallon, waterStationDeliveryFee;
    Spinner startSpinner, endSpinner;
    RadioButton deliveryServiceYes, deliveryServiceFree, deliveryFeePerGallon, free, deliveryFeeFix;
    LinearLayout linearDelFeeNext;
    Button updateButton;
    String namedealer, addressdealer, numberdealer;
    ImageView imageView;
    String name, address, contact;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Intent intent;
    LinearLayout linearLayout1, linearLayout2, linearLayout3;
    TimePicker simpleTimePicker,simpleTimePicker1;
    Uri uri;
    CheckBox mon, tue, wed, thurs, fri, sat, sun;
    private ProgressDialog progressDialog;
    double lat;
    double lng;
    List<String> week;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_merchant_wp_updateinformationxml, container, false);
        intent = new Intent();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("user_station_photo_display");
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        updateButton = view.findViewById(R.id.updateInfoButtonUIWS);
        startSpinner= view.findViewById(R.id.startSpinner);
        endSpinner= view.findViewById(R.id.endSpinner);
        linearLayout1 = view.findViewById(R.id.linearMinGal);
        linearLayout2 = view.findViewById(R.id.linearDelFee);
        linearLayout3 = view.findViewById(R.id.linearDelFeeNext);
        week = new ArrayList<String>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        simpleTimePicker = view.findViewById(R.id.simpleTimePicker);
        simpleTimePicker.setIs24HourView(true);
        simpleTimePicker1 = view.findViewById(R.id.simpleTimePicker1);
        simpleTimePicker1.setIs24HourView(true);

        waterStationMinimumGallon  = view.findViewById(R.id.waterStationMinimumGallonUIS);
        waterStationDeliveryFee = view.findViewById(R.id.waterStationDeliveryFeeUIS);
        mon = view.findViewById(R.id.monBox);
        tue = view.findViewById(R.id.tueBox);
        wed = view.findViewById(R.id.wedBox);
        thurs = view.findViewById(R.id.thursBox);
        fri = view.findViewById(R.id.friBox);
        sat = view.findViewById(R.id.satBox);
        sun = view.findViewById(R.id.sunBox);
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thurs.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        sun.setOnClickListener(this);
        free = view.findViewById(R.id.freefree);

        deliveryFeeFix = view.findViewById(R.id.waterStationFixUIS);
        deliveryFeePerGallon = view.findViewById(R.id.waterStationPerGallonUIS);

        deliveryFeePerGallon.setChecked(true);
        free.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        deliveryFeePerGallon.setOnClickListener(this);
        deliveryFeeFix.setOnClickListener(this);
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("User_WS_Business_Info_File").child(firebaseUser.getUid());
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                waterStationMinimumGallon.setText(dataSnapshot.child("business_min_no_of_gallons").getValue(String.class));
                namedealer = (dataSnapshot.child("business_name").getValue(String.class));
                addressdealer = (dataSnapshot.child("business_address").getValue(String.class));
                numberdealer = (dataSnapshot.child("business_tel_no").getValue(String.class));
                //waterStationStartTime.setText(dataSnapshot.child("business_start_time").getValue(String.class));
                //waterStationStartTime.setText(dataSnapshot.child("business_end_time").getValue(String.class));
                waterStationMinimumGallon.setText(dataSnapshot.child("business_min_no_of_gallons").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateData()
    {
        progressDialog.show();
            String deliveryStatusFreeIf="";
            if(free.isChecked())
            {
                deliveryStatusFreeIf = "Free";
                waterStationDeliveryFee.setVisibility(View.GONE);
            }
            else if (deliveryFeePerGallon.isChecked())
            {
                deliveryStatusFreeIf = "Per gallon";
            }
            else if (deliveryFeeFix.isChecked())
            {
                deliveryStatusFreeIf = "Fixed price";
            }
            else if(!free.isChecked() && !deliveryFeePerGallon.isChecked() && !deliveryFeeFix.isChecked())
            {
                showMessage("Check any radio button");
                progressDialog.dismiss();
                return;
            }
              WSBusinessInfoFile2 userWSBusinessInfoFile = new WSBusinessInfoFile2(
                firebaseUser.getUid(),
                namedealer,
                addressdealer,
                numberdealer,
                String.valueOf(simpleTimePicker.getHour())+":"+String.valueOf(simpleTimePicker.getMinute()),
                String.valueOf(simpleTimePicker1.getHour())+":"+String.valueOf(simpleTimePicker1.getMinute()),
                      week.toString(),
                deliveryStatusFreeIf,
                waterStationDeliveryFee.getText().toString(),
                waterStationMinimumGallon.getText().toString(),
                "active");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
            databaseReference.child(firebaseUser.getUid()).setValue(userWSBusinessInfoFile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getLocationSetter();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
    }

    private void showMessage(String wordMessage) {
        Toast.makeText(getActivity(), wordMessage, Toast.LENGTH_LONG).show();
    }

    private void getLocationSetter()
    {
        progressDialog.show();
        progressDialog.setMessage("Location Finishing");
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = addressdealer;

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(firebaseUser.getUid(), getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(firebaseUser.getUid()).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            WPBusinessInfoFragment additem = new WPBusinessInfoFragment();
                            AppCompatActivity activity = (AppCompatActivity)getContext();
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.fragment_container_wp, additem)
                                    .addToBackStack(null)
                                    .commit();
                            progressDialog.dismiss();
                            String text = "Successfully updated";
                            snackBar(text);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {

            ex.printStackTrace();
            progressDialog.dismiss();
        }
        finally {
            //showMessage("Failed to locate your address");
            progressDialog.dismiss();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getInput()
    {
            if(
                    deliveryFeePerGallon.isChecked()
            && waterStationDeliveryFee.getText().toString().trim().isEmpty()
            || deliveryFeeFix.isChecked() && waterStationDeliveryFee.getText().toString().trim().isEmpty())
            {
                showMessage("Please fill all the fields!");
            }
            else
            {
                updateData();
            }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void openGallery()
    {
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateInfoButtonUIWS:
                getInput();
                break;
            case R.id.waterStationPerGallonUIS:
                waterStationDeliveryFee.setText("");
                waterStationDeliveryFee.setHint("Delivery Fee Per Gallon");
                waterStationDeliveryFee.setVisibility(View.VISIBLE);
                break;
            case R.id.waterStationFixUIS:
                waterStationDeliveryFee.setText("");
                waterStationDeliveryFee.setHint("Fixed Delivery Fee");
                waterStationDeliveryFee.setVisibility(View.VISIBLE);
                break;
            case R.id.freefree:
                waterStationDeliveryFee.setText("0");
                waterStationDeliveryFee.setHint("Free Delivery");
                waterStationDeliveryFee.setVisibility(View.GONE);
                break;
                case R.id.monBox:
                    String monday = "Monday";
                    String monSplit = String.join(",", monday);
                    if (mon.isChecked()) {
                        week.add(monSplit);
                    }
                    else {
                        week.remove(monSplit);
                    }
                    break;
                case R.id.tueBox:
                    String tuesday = "Tuesday";
                    String tuesdaySplit = String.join(",", tuesday);
                    if (tue.isChecked()) {
                        week.add(tuesdaySplit);
                    }
                    else {
                        week.remove(tuesdaySplit);
                    }
                    break;
                case R.id.wedBox:
                    String wednesday = "Wednesday";
                    String wednesdaySplit = String.join(",", wednesday);
                    if (wed.isChecked()) {
                        week.add(wednesdaySplit);
                    }
                    else {
                        week.remove(wednesdaySplit);
                    }
                    break;
                case R.id.thursBox:
                    String thursday = "Thursday";
                    String thursdaySplit = String.join(",", thursday);
                    if (thurs.isChecked()) {
                        week.add(thursdaySplit);
                    }
                    else {
                        week.remove(thursdaySplit);
                    }
                    break;
                case R.id.friBox:
                    String friday = "Friday";
                    String fridaySplit = String.join(",", friday);
                    if (fri.isChecked()) {
                        week.add(fridaySplit);
                    }
                    else {
                        week.remove(fridaySplit);
                    }
                    break;
                case R.id.satBox:
                    String saturday = "Saturday";
                    String saturdaySplit = String.join(",", saturday);
                    if (sat.isChecked()) {
                        week.add(saturdaySplit);
                    }
                    else {
                        week.remove(saturdaySplit);
                    }
                    break;
                case R.id.sunBox:
                    String sunday = "Sunday";
                    String sundaySplit = String.join(",", sunday);
                    if (sun.isChecked()) {
                        week.add(sundaySplit);
                    }
                    else {
                        week.remove(sundaySplit);
                    }
                    break;

        }
    }
    public void snackBar(String text){
        View parentLayout = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, ""+text, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        snackbar.setAction("Okay", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(getResources().getColor(android.R.color.white ));
        snackbar.show();
    }
}
