package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
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
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WPBusinessInformationUpdate extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText waterStationName, waterStationAddress, waterStationPhone, waterStationStartTime,
    waterStationEndTime, waterStationMinimumGallon, waterStationDeliveryFee;
    Spinner startSpinner, endSpinner;
    RadioButton deliveryServiceYes, deliveryServiceFree, deliveryFeePerGallon, free, deliveryFeeFix;

    Button updateButton;
    String namedealer, addressdealer, numberdealer;
    ImageView imageView;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Intent intent;
    LinearLayout linearLayout1, linearLayout2, linearLayout3;

    Uri uri;
    private ProgressDialog progressDialog;
    double lat;
    double lng;


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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);


        waterStationName = view.findViewById(R.id.waterStationNameUIS);
        waterStationAddress = view.findViewById(R.id.waterStationFullAddressUIS);
        waterStationPhone = view.findViewById(R.id.waterStationPhoneUIS);
        waterStationStartTime = view.findViewById(R.id.waterStationStartTimeUIS);
        waterStationEndTime = view.findViewById(R.id.waterStationEndTimeUIS);
        waterStationMinimumGallon  = view.findViewById(R.id.waterStationMinimumGallonUIS);
        waterStationDeliveryFee = view.findViewById(R.id.waterStationDeliveryFeeUIS);

        deliveryServiceYes = view.findViewById(R.id.waterStationYesUIS);
        deliveryServiceFree = view.findViewById(R.id.waterStationFreeUIS);
        free = view.findViewById(R.id.free);
        deliveryServiceYes.setChecked(true);


        deliveryFeeFix = view.findViewById(R.id.waterStationFixUIS);
        deliveryFeePerGallon = view.findViewById(R.id.waterStationPerGallonUIS);
        deliveryFeePerGallon.setChecked(true);

        updateButton.setOnClickListener(this);
        deliveryFeePerGallon.setOnClickListener(this);
        deliveryFeeFix.setOnClickListener(this);
        deliveryServiceYes.setOnClickListener(this);
        deliveryServiceFree.setOnClickListener(this);
        String[] arraySpinner = new String[]{
                "AM","PM"
        };
        String[] arraySpinner2 = new String[]{
                "PM","AM"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter2);
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("User_WS_Business_Info_File").child(firebaseUser.getUid());
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                waterStationMinimumGallon.setText(dataSnapshot.child("business_min_no_of_gallons").getValue(String.class));
                waterStationName.setText(dataSnapshot.child("business_name").getValue(String.class));
                waterStationAddress.setText(dataSnapshot.child("business_address").getValue(String.class));
                waterStationPhone.setText(dataSnapshot.child("business_tel_no").getValue(String.class));
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



    public void updateData()
    {
        progressDialog.show();
                                String deliveryStatusIf;
                                String deliveryStatusFreeIf;
                                String deliveryFeePerGalIf;
                                String deliveryMinNoCapaIf;
                                if(deliveryServiceYes.isChecked())
                                {
                                    deliveryStatusIf = "Active";
                                    deliveryStatusFreeIf = "Not Free";
                                    deliveryFeePerGalIf = waterStationDeliveryFee.getText().toString();
                                    deliveryMinNoCapaIf = waterStationMinimumGallon.getText().toString();
                                }
                                else if(deliveryServiceFree.isChecked())
                                {
                                    deliveryStatusIf = "Active";
                                    deliveryStatusFreeIf = "Free";
                                    deliveryFeePerGalIf = "None";
                                    deliveryMinNoCapaIf = "None";
                                }
                                else
                                {
                                    showMessage("Check any radio button");
                                    return;
                                }

                                UserWSBusinessInfoFile userWSBusinessInfoFile = new UserWSBusinessInfoFile(
                                    firebaseUser.getUid(),
                                        namedealer,
                                        waterStationStartTime.getText().toString()+" "+startSpinner.getSelectedItem().toString(),
                                        waterStationEndTime.getText().toString()+" "+endSpinner.getSelectedItem().toString(),
                                        deliveryStatusIf,
                                        deliveryStatusFreeIf,
                                        deliveryFeePerGalIf,
                                        deliveryMinNoCapaIf,
                                        numberdealer,
                                        addressdealer,
                                        "Active",
                                        ""
                                );
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
                            showMessage("Successfully Submitted");
                            WPBusinessInfoFragment additem = new WPBusinessInfoFragment();
                            AppCompatActivity activity = (AppCompatActivity)getContext();
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.fragment_container_wp, additem)
                                    .addToBackStack(null)
                                    .commit();
                            progressDialog.dismiss();

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
            showMessage("Failed to locate your address");
            progressDialog.dismiss();
        }
    }

    public void getInput()
    {
            if(
                waterStationStartTime.getText().toString().isEmpty()
            && waterStationEndTime.getText().toString().isEmpty())
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.updateInfoButtonUIWS:
                getInput();
                break;
            case R.id.waterStationYesUIS:
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
                break;
//            case R.id.waterStationFreeUIS:
//                linearLayout2.setVisibility(View.GONE);
//                linearLayout3.setVisibility(View.GONE);
//                break;
            case R.id.waterStationPerGallonUIS:
                waterStationDeliveryFee.setHint("Delivery Fee Per Gallon");
                break;
            case R.id.waterStationFixUIS:
                waterStationDeliveryFee.setHint("Fixed Delivery Fee");
                break;
            case R.id.free:
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                break;
        }
    }
}
