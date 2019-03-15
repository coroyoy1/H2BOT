package com.example.administrator.h2bot.waterstation;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
import com.example.administrator.h2bot.dealer.WaterPeddlerDocumentActivity;
import com.example.administrator.h2bot.models.*;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class WSBusinessInfoFinal extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView businessPermit_image, sanitaryPermit_image, physicochemicalPermit_Image, birPermit_Image;
    Button businessPermitBtn, sanitaryPermitBtn,
            physicochemicalbutton, birbutton, submitButton;
    RadioGroup deliveryFeeGroup;

    EditText stationName, stationAddress, endingHour, startingHour, businessDeliveryFeePerGal, businessMinNoCapacity, telNo, deliveryFee, min_no_of_gallons;
    Spinner startSpinner, endSpinner;
    String deliveryMethod, business, sanitary, physicochemical, bir;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private double lat;
    private double lng;

    String newToken;
    Uri filepath, filepath2, filepath3, filepath4;
    Boolean isPicked = false;
    Boolean isPicked2 = false;
    Boolean isPicked3 = false;
    Boolean isPicked4 = false;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;

    String mFirstname, mLastname, mAddress, mContact_no, mEmail_address, mPassword, mFilepath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_water_station_document_update, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Creating account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        RadioButton free,fixPrice,perGallon;
        startSpinner= view.findViewById(R.id.startSpinnerDU);
        endSpinner= view.findViewById(R.id.endSpinnerDU);

        // Button
        businessPermitBtn = view.findViewById(R.id.businessPermitBtnDU);
        sanitaryPermitBtn = view.findViewById(R.id.sanitaryPermitBtnDU);
        physicochemicalbutton = view.findViewById(R.id.physicochemicalPermitBtnDU);
        birbutton = view.findViewById(R.id.birPermitBtnDU);
        submitButton = view.findViewById(R.id.submitButtonDU);
        free = view.findViewById(R.id.freeDU);
        fixPrice = view.findViewById(R.id.fixPriceDU);
        perGallon = view.findViewById(R.id.perGallonDU);
        //Imageview
        businessPermit_image = view.findViewById(R.id.businessPermit_imageDU);
        sanitaryPermit_image = view.findViewById(R.id.sanitaryPermit_imageDU);
        physicochemicalPermit_Image = view.findViewById(R.id.physicochemicalPermit_imageDU);
        birPermit_Image = view.findViewById(R.id.birPermit_imageDU);

        //EditText
        stationName = view.findViewById(R.id.stationNameDU);
        stationAddress = view.findViewById(R.id.stationAddressDU);
        telNo = view.findViewById(R.id.telNoDU);
        endingHour = view.findViewById(R.id.endingHourDU);
        startingHour = view.findViewById(R.id.startingHourDU);
        deliveryFee = view.findViewById(R.id.deliveryFeeDU);
        min_no_of_gallons = view.findViewById(R.id.min_no_of_gallonsDU);

        //Radiogroup
        deliveryFeeGroup = view.findViewById(R.id.deliveryFeeGroupDU);


        String[] arraySpinner = new String[]{
                "AM", "PM"
        };
        String[] arraySpinner2 = new String[]{
                "PM", "AM"
        };
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter2);


        businessPermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(stationName.getText().toString()) || TextUtils.isEmpty(stationAddress.getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = true;
                    isPicked2 = false;
                    isPicked3 = false;
                    isPicked4 = false;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            }
        });

        sanitaryPermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(stationName.getText().toString()) || TextUtils.isEmpty(stationAddress.getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = false;
                    isPicked2 = true;
                    isPicked3 = false;
                    isPicked4 = false;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            }
        });

        physicochemicalbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(stationName.getText().toString()) || TextUtils.isEmpty(stationAddress.getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = false;
                    isPicked2 = false;
                    isPicked3 = true;
                    isPicked4 = false;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            }
        });

        birbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(stationName.getText().toString()) || TextUtils.isEmpty(stationAddress.getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = false;
                    isPicked2 = false;
                    isPicked3 = false;
                    isPicked4 = true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDocuments();
//                uploadAllImage();
            }
        });

        deliveryFeeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.perGallon:
                        deliveryMethod = "Per Gallon";
                        deliveryFee.setVisibility(View.VISIBLE);
                        deliveryFee.setHint("Delivery fee per gallon");
                        break;

                    case R.id.fixPrice:
                        deliveryMethod = "Fix Price";
                        deliveryFee.setVisibility(View.VISIBLE);
                        deliveryFee.setHint("Fixed delivery fee");
                        break;

                    case R.id.free:
                        deliveryMethod = "Free";
                        deliveryFee.setVisibility(View.GONE);
                        break;
                }
            }
        });

        startingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startingHour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endingHour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        retrieveData();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                if(isPicked) {
                    filepath = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getActivity(), "No text detected", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb= new StringBuilder();

                            for(int ctr=0;ctr<items.size();ctr++)
                            {
                                TextBlock myItem = items.valueAt(ctr);
                                sb.append(myItem.getValue());
                                sb.append("\n");
                            }
                            if(sb.toString().toLowerCase().contains(stationName.getText().toString().toLowerCase())){
                                Picasso.get().load(filepath).into(businessPermit_image);
                                Toast.makeText(getActivity(), "Valid business permit", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(getActivity(), "Invalid business permit", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(isPicked2) {
                    filepath2 = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath2);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getActivity(), "No text detected", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb= new StringBuilder();

                            for(int ctr=0;ctr<items.size();ctr++)
                            {
                                TextBlock myItem = items.valueAt(ctr);
                                sb.append(myItem.getValue());
                                sb.append("\n");
                            }
//                            if(sb.toString().toLowerCase().contains(stationName.getText().toString().toLowerCase())){
                            Picasso.get().load(filepath2).into(sanitaryPermit_image);
                            Toast.makeText(getActivity(), "Valid sanitary permit", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
//                                Toast.makeText(this, "Invalid sanitary permit", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isPicked3)
                {
                    filepath3 = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath3);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getActivity(), "No text detected", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb= new StringBuilder();

                            for(int ctr=0;ctr<items.size();ctr++)
                            {
                                TextBlock myItem = items.valueAt(ctr);
                                sb.append(myItem.getValue());
                                sb.append("\n");
                            }
//                            if(sb.toString().toLowerCase().contains(stationName.getText().toString().toLowerCase())){
                            Picasso.get().load(filepath3).into(physicochemicalPermit_Image);
                            Toast.makeText(getActivity(), "Valid sanitary permit", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
//                                Toast.makeText(this, "Invalid sanitary permit", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isPicked4)
                {
                    filepath4 = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath4);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getActivity(), "No text detected", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb= new StringBuilder();

                            for(int ctr=0;ctr<items.size();ctr++)
                            {
                                TextBlock myItem = items.valueAt(ctr);
                                sb.append(myItem.getValue());
                                sb.append("\n");
                            }
//                            if(sb.toString().toLowerCase().contains(stationName.getText().toString().toLowerCase())){
                            Picasso.get().load(filepath4).into(birPermit_Image);
                            Toast.makeText(getActivity(), "Valid sanitary permit", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
//                                Toast.makeText(this, "Invalid sanitary permit", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            Toast.makeText(getActivity(), "You haven't picked an image",Toast.LENGTH_LONG).show();
        }
    }

    private void getLocationSetter()
    {
        progressDialog.show();
        progressDialog.setMessage("Location Finishing");
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = stationAddress.getText().toString();

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(FirebaseAuth.getInstance().getCurrentUser().getUid(), getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessages("Successfully Submitted");
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessages("Error: " + e.getMessage());
                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {

            ex.printStackTrace();
            progressDialog.dismiss();
        }
        finally {
            showMessages("Error to locate your address, please change again");
            progressDialog.dismiss();
        }
    }

    private void showMessages(String s)
    {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    public void checkDocuments(){
        if(businessPermit_image.getDrawable() == null
                || sanitaryPermit_image.getDrawable() == null || physicochemicalPermit_Image.getDrawable() == null
        || birPermit_Image.getDrawable() == null){
            Toast.makeText(getActivity(), "Please fill all the requirments", Toast.LENGTH_SHORT).show();
            return;
        }
    }


//    public void updateInformation()
//    {
//
//        WSBusinessInfoFile wsBusinessInfoFile = new WSBusinessInfoFile(
//                mAuth.getCurrentUser().getUid(),
//                stationAddress.getText().toString(),
//                telNo.getText().toString(),
//
//        )
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
//        databaseReference.child(mAuth.getCurrentUser().getUid())
//    }

//            this.business_id = business_id;
//        this.business_name = business_name;
//        this.business_address = business_address;
//        this.business_tel_no = business_tel_no;
//        this.business_start_time = business_start_time;
//        this.business_end_time = business_end_time;
//        this.business_delivery_fee_method = business_delivery_fee_method;
//        this.business_delivery_fee = business_delivery_fee;
//        this.business_min_no_of_gallons = business_min_no_of_gallons;
//        this.business_status = business_status;

//    public void uploadAllImage(){
//        if(filepath != null){
//            FirebaseUser user = mAuth.getCurrentUser();
//            String userId = user.getUid();
//            Log.d("auth", userId);
//            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"businessPermitDocument");
//            mStorageRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            mUri = uri.toString();
//                        }
//                    });
//                    progressDialog.dismiss();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    progressDialog.dismiss();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
//                    progressDialog.show();
//                }
//            });
//        }
//
//        if(filepath2 != null){
//            FirebaseUser user = mAuth.getCurrentUser();
//            String userId = user.getUid();
//            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"sanitaryPermitDocument");
//            mStorageRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String stringUri = uri.toString();
//                            WSDocFile wsDocFile = new WSDocFile(userId,
//                                    stringUri,
//                                    mUri,
//                                    "active");
//
//                            FirebaseDatabase.getInstance().getReference("User_WS_Docs_File").child(userId).setValue(wsDocFile)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    });
//                    progressDialog.dismiss();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
//                    progressDialog.show();
//                }
//            });
//        }
//    }

    public String getTime(String clock)
    {
        String clockString = clock;
        String newDate = clockString.substring(0, clockString.length() - 3);
        return newDate;
    }

    public String getAMPM(String midday)
    {
        String mid = midday;
        String midline = mid.substring(mid.length() - 2, mid.length());
        return midline;
    }

    public void retrieveData()
    {
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        databaseReference.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        WSBusinessInfoFile wsBusinessInfoFile = dataSnapshot.getValue(WSBusinessInfoFile.class);
                        if (wsBusinessInfoFile != null)
                        {
                            stationName.setText(wsBusinessInfoFile.getBusiness_name());
                            stationAddress.setText(wsBusinessInfoFile.getBusiness_address());
                            String startclockWS = wsBusinessInfoFile.getBusiness_start_time();
                            String endclockWS = wsBusinessInfoFile.getBusiness_end_time();
                            startingHour.setText(getTime(startclockWS));
                            endingHour.setText(getTime(endclockWS));

                            String middayAM = getAMPM(wsBusinessInfoFile.getBusiness_start_time());
                            String middayPM = getAMPM(wsBusinessInfoFile.getBusiness_end_time());
                            if (middayAM != null)
                            {
                                int spinnerPos = adapter.getPosition(middayAM);
                                startSpinner.setSelection(spinnerPos);
                            }
                            if (middayPM != null)
                            {
                                int spinnerPos1 = adapter2.getPosition(middayPM);
                                endSpinner.setSelection(spinnerPos1);
                            }
                            if ("Fix Price".equals(wsBusinessInfoFile.getBusiness_delivery_fee_method()))
                            {
                                deliveryFeeGroup.check(R.id.fixPriceDU);
                                deliveryFee.setText(wsBusinessInfoFile.getBusiness_delivery_fee());
                            }
                            else if ("Per Gallon".equals(wsBusinessInfoFile.getBusiness_delivery_fee_method()))
                            {
                                deliveryFeeGroup.check(R.id.perGallonDU);
                                deliveryFee.setText(wsBusinessInfoFile.getBusiness_delivery_fee());
                            }
                            else if ("Free Delivery Fee".equals(wsBusinessInfoFile.getBusiness_delivery_fee_method()))
                            {
                                deliveryFeeGroup.check(R.id.freeDU);
                            }
                            telNo.setText(wsBusinessInfoFile.getBusiness_tel_no());
                            min_no_of_gallons.setText(wsBusinessInfoFile.getBusiness_min_no_of_gallons());

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_WS_Docs_File");
                            databaseReference1.child(mAuth.getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            WSDocFile wsDocFile = dataSnapshot.getValue(WSDocFile.class);
                                            if (wsDocFile != null)
                                            {
                                                Picasso.get().load(wsDocFile.getStation_bir_permit()).centerCrop().fit().into(businessPermit_image);
                                                Picasso.get().load(wsDocFile.getStation_sanitary_permit()).fit().centerCrop().into(sanitaryPermit_image);
                                                Picasso.get().load(wsDocFile.getStation_physicochemical_permit()).fit().centerCrop().into(physicochemicalPermit_Image);
                                                Picasso.get().load(wsDocFile.getStation_bir_permit()).fit().centerCrop().into(birPermit_Image);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            showMessages("No documents submitted");
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessages("Your information is not available");
                    }
                });
    }

}
