package com.example.administrator.h2bot.waterstation;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
import com.example.administrator.h2bot.dealer.WaterPeddlerDocumentActivity;
import com.example.administrator.h2bot.models.*;

import android.app.DialogFragment;
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
import android.support.design.widget.TextInputLayout;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class WSBusinessInfoFinal extends Fragment implements CheckBox.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView businessPermit_image, sanitaryPermit_image, physicochemicalPermit_Image, birPermit_Image;
    Button businessPermitBtn, sanitaryPermitBtn,
            physicochemicalbutton, submitButton;
    RadioGroup haveGallonGroup;

    TextInputLayout stationName, stationAddress, telNo,min_no_of_gallons, priceOfGallonEdit, currentNoGallonEdit;
    Spinner startSpinner, endSpinner;
    String deliveryMethod, business, sanitary, physicochemical, bir;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private double lat;
    private double lng;
    Uri filePathUri;

    String newToken;
    Uri filepath, filepath2, filepath3, filepath4;
    Boolean isPicked = false;
    Boolean isPicked2 = false;
    Boolean isPicked3 = false;

    CheckBox mon, tue, wed, thurs, fri, sat, sun;

    List<String> week;

    String mFirstname, mLastname, mAddress, mContact_no, mEmail_address, mPassword, mFilepath;

    Button startTime, endTime;
    TextView startTimeView, endTimeView;
    private boolean isAddressExist;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_water_station_document_update2, container, false);

        week = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Updating Document and Information...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        // Button
        businessPermitBtn = view.findViewById(R.id.businessPermitBtnUSD);
        sanitaryPermitBtn = view.findViewById(R.id.sanitaryPermitBtnUSD);
        physicochemicalbutton = view.findViewById(R.id.physicochemicalPermitBtnUSD);
        submitButton = view.findViewById(R.id.submitButtonUSD);
        startTime = view.findViewById(R.id.startTimeButtonUSD);
        endTime = view.findViewById(R.id.endTimeButtonUSD);


        //TextView
        startTimeView = view.findViewById(R.id.startTimeTextViewUSD);
        endTimeView = view.findViewById(R.id.endTimeTextViewUSD);


        //Imageview
        businessPermit_image = view.findViewById(R.id.businessPermit_imageUSD);
        sanitaryPermit_image = view.findViewById(R.id.sanitaryPermit_imageUSD);
        physicochemicalPermit_Image = view.findViewById(R.id.physicochemicalPermit_imageUSD);;

        //EditText
        stationName = view.findViewById(R.id.stationNameUSD);
        stationAddress = view.findViewById(R.id.stationAddressUSD);
        telNo = view.findViewById(R.id.telNoUSD);
        min_no_of_gallons = view.findViewById(R.id.min_no_of_gallonsUSD);
        priceOfGallonEdit = view.findViewById(R.id.priceOfGallonUSD);
        currentNoGallonEdit = view.findViewById(R.id.noOfGallonsUSD);

        //Radiogroup
        haveGallonGroup = view.findViewById(R.id.doYouHaveGallonGroupUSD);

        //CheckBox
        mon = view.findViewById(R.id.monBoxUSD);
        tue = view.findViewById(R.id.tueBoxUSD);
        wed = view.findViewById(R.id.wedBoxUSD);
        thurs = view.findViewById(R.id.thursBoxUSD);
        fri = view.findViewById(R.id.friBoxUSD);
        sat = view.findViewById(R.id.satBoxUSD);
        sun = view.findViewById(R.id.sunBoxUSD);


        //Button Listener
        businessPermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(stationName.getEditText().getText().toString()) || TextUtils.isEmpty(stationAddress.getEditText().getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = true;
                    isPicked2 = false;
                    isPicked3 = false;
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
                if(TextUtils.isEmpty(stationName.getEditText().getText().toString()) || TextUtils.isEmpty(stationAddress.getEditText().getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = false;
                    isPicked2 = true;
                    isPicked3 = false;
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
                if(TextUtils.isEmpty(stationName.getEditText().getText().toString()) || TextUtils.isEmpty(stationAddress.getEditText().getText().toString())){
                    Toast.makeText(getActivity(), "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = false;
                    isPicked2 = false;
                    isPicked3 = true;
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
            }
        });

        //Checkboxes Listner
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thurs.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        sun.setOnClickListener(this);

        haveGallonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.noUSD:
                        currentNoGallonEdit.setVisibility(View.GONE);
                        priceOfGallonEdit.setVisibility(View.GONE);
                        currentNoGallonEdit.getEditText().setText("NONE");
                        priceOfGallonEdit.getEditText().setText("NONE");
                        break;
                    case R.id.yesUSD:
                        currentNoGallonEdit.setVisibility(View.VISIBLE);
                        priceOfGallonEdit.setVisibility(View.VISIBLE);
                        currentNoGallonEdit.getEditText().setText("");
                        priceOfGallonEdit.getEditText().setText("");
                        break;
                }
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new TimePickerStartingFragmentForUpdate();
                dFragment.show(getActivity().getFragmentManager(),"Time Picker");
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new TimePickerEndingFragmentForUpdate();
                dFragment.show(getActivity().getFragmentManager(),"Time Picker");
            }
        });

        retrieveBusinessInfo();

        return view;
    }

    private void retrieveBusinessInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        reference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StationBusinessInfo stationBusiness = dataSnapshot.getValue(StationBusinessInfo.class);
                if (stationBusiness != null)
                {
                    stationName.getEditText().setText(stationBusiness.getBusiness_name());
                    stationAddress.getEditText().setText(stationBusiness.getBusiness_address());
                    telNo.getEditText().setText(stationBusiness.getBusiness_tel_no());
                    startTimeView.setText(stationBusiness.getBusiness_start_time());
                    endTimeView.setText(stationBusiness.getBusiness_end_time());
                    min_no_of_gallons.getEditText().setText(stationBusiness.getBusiness_min_no_of_gallons());
                    currentNoGallonEdit.getEditText().setText(stationBusiness.getBusiness_current_no_gallons());
                    priceOfGallonEdit.getEditText().setText(stationBusiness.getBusiness_price_of_gallon());
                    if (stationBusiness.getBusiness_current_no_gallons().toLowerCase().equals("none".toLowerCase())
                            && stationBusiness.getBusiness_price_of_gallon().toLowerCase().equals("none".toLowerCase()))
                    {
                        haveGallonGroup.check(R.id.noUSD);
                    }

                    String array =  stationBusiness.getBusiness_days();
                    List<String> list = new ArrayList<String>(Collections.singleton(array));

                    String removeBracket = list.toString();
                    removeBracket = removeBracket.substring(1, removeBracket.length() - 1);

                    List<String> stringList = new ArrayList<String>(Arrays.asList(removeBracket.split(",")));
                    String str = "";
                    for (int count = 0; count < stringList.size(); count++)
                    {
                        str = stringList.get(count);
                        str = str.replace(" ", "");
                        if (str.toLowerCase().equals("Monday".toLowerCase()))
                        {
                            mon.setChecked(true);
                        }
                        if (str.toLowerCase().equals("Tuesday".toLowerCase()))
                        {
                            tue.setChecked(true);
                        }
                        if (str.toLowerCase().equals("Wednesday".toLowerCase()))
                        {
                            wed.setChecked(true);
                        }
                        if (str.toLowerCase().equals("Thursday".toLowerCase()))
                        {
                            thurs.setChecked(true);

                        }
                        if (str.toLowerCase().equals("Friday".toLowerCase()))
                        {
                            fri.setChecked(true);
                        }
                        if (str.toLowerCase().equals("Saturday".toLowerCase()))
                        {
                            sat.setChecked(true);
                        }
                        if (str.toLowerCase().equals("Sunday".toLowerCase()))
                        {
                            sun.setChecked(true);
                        }
                    }
                    String split;
                    if (mon.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Monday"));
                        week.add(split);
                    }
                    if (tue.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Tuesday"));
                        week.add(split);
                    }
                    if (wed.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Wednesday"));
                        week.add(split);
                    }
                    if (thurs.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Thursday"));
                        week.add(split);
                    }
                    if (fri.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Friday"));
                        week.add(split);
                    }
                    if (sat.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Saturday"));
                        week.add(split);
                    }
                    if (sun.isChecked())
                    {
                        split = TextUtils.join(",", Collections.singleton("Sunday"));
                        week.add(split);
                    }
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_Docs_File");
                    databaseReference.child(mAuth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    WSDocFile wsDocFile = dataSnapshot.getValue(WSDocFile.class);
                                    if (wsDocFile != null)
                                    {
                                        String businessPermit = wsDocFile.getStation_business_permit();
                                        String sanitaryPermit = wsDocFile.getStation_sanitary_permit();
                                        String physicochemicalPermit = wsDocFile.getStation_physicochemical_permit();
                                        Picasso.get().load(businessPermit).fit().centerCrop().into(businessPermit_image);
                                        Picasso.get().load(sanitaryPermit).fit().centerCrop().into(sanitaryPermit_image);
                                        Picasso.get().load(physicochemicalPermit).fit().centerCrop().into(physicochemicalPermit_Image);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void updateBusinessInfo()
    {
        progressDialog.show();
        mAddress = stationAddress.getEditText().getText().toString();
        if (checkAddress(mAddress))
        {
            showMessages("Address is not valid, Please make sure your input are correct!");
            return;
        }
        StationBusinessInfo stationBusinessInfo = new StationBusinessInfo(
                mAuth.getCurrentUser().getUid(),
                stationName.getEditText().getText().toString(),
                stationAddress.getEditText().getText().toString(),
                telNo.getEditText().getText().toString(),
                startTimeView.getText().toString(),
                endTimeView.getText().toString(),
                week.toString(),
                min_no_of_gallons.getEditText().getText().toString(),
                priceOfGallonEdit.getEditText().getText().toString(),
                currentNoGallonEdit.getEditText().getText().toString(),
                "active"
        );
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
        databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(stationBusinessInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            getLocationSetter();
                            uploadAllImage();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessages("Failed to update your business info, please check internet connection!");
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean checkAddress(String mAddress)
    {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        String locateAddress = mAddress;
        try {
            address = coder.getFromLocationName(locateAddress, 5);
            if(address.size() == 0){
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if(isPicked) {
                filepath = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                    if(!textRecognizer.isOperational())
                    {
                        Toast.makeText(getActivity().getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
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
                        if(sb.toString().toLowerCase().contains(stationName.getEditText().getText().toString().toLowerCase())){
                            Picasso.get().load(filepath).fit().centerCrop().into(businessPermit_image);
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
                        Toast.makeText(getActivity().getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
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
                        if(sb.toString().toLowerCase().contains(stationName.getEditText().getText().toString().toLowerCase())){
                            Picasso.get().load(filepath2).fit().centerCrop().into(sanitaryPermit_image);
                            Toast.makeText(getActivity(), "Valid sanitary permit", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                            Toast.makeText(getActivity(), "Invalid sanitary permit", Toast.LENGTH_SHORT).show();
                        }
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
                        Toast.makeText(getActivity().getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
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
                        if(sb.toString().toLowerCase().contains(stationName.getEditText().getText().toString().toLowerCase())){
                            Picasso.get().load(filepath3).fit().centerCrop().into(physicochemicalPermit_Image);
                            Toast.makeText(getActivity(), "Valid Physicochemical permit", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                            Toast.makeText(getActivity(), "Invalid Physicochemical permit", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
        progressDialog.setMessage("Finishing the Business Information");
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = stationAddress.getEditText().getText().toString();

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(mAuth.getCurrentUser().getUid() , getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessages("Successfully Update Information");
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
    }
    private void showMessages(String s)
    {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
    public void checkDocuments(){
        if(businessPermit_image.getDrawable() == null
                || sanitaryPermit_image.getDrawable() == null
                || physicochemicalPermit_Image.getDrawable() == null){
            Toast.makeText(getActivity(), "Please upload document", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            updateBusinessInfo();
        }
    }
    public void uploadAllImage(){
        if (filepath != null || filepath2 != null || filepath3 != null)
        {
            progressDialog.setMessage("Finishing Update Documents");
            progressDialog.show();
        }
        if(filepath != null){
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"businessPermitDocument");
            mStorageRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            business = uri.toString();
                            uploadBusinessPermit(business);
                        }
                    });
                }
            });
        }

        if(filepath3 != null){
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"physicochemicalDocument");
            mStorageRef.putFile(filepath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            physicochemical = uri.toString();
                            uploadPhysicoChemicalPermit(physicochemical);
                        }
                    });
                }
            });
        }
        if(filepath2 != null){
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"sanitaryPermitDocument");
            mStorageRef.putFile(filepath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sanitary = uri.toString();
                            uploadSanitaryPermit(sanitary);
                        }
                    });
                }
            });
        }
    }
    public void uploadSanitaryPermit(String sanitary)
    {
        if (!sanitary.isEmpty())
        {
            FirebaseDatabase.getInstance().getReference("User_WS_Docs_File").child(mAuth.getCurrentUser().getUid())
                    .child("station_sanitary_permit")
                    .setValue(sanitary)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(getActivity(), "Uploaded ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
    public void uploadBusinessPermit(String business)
    {
        if (!business.isEmpty())
        {
            FirebaseDatabase.getInstance().getReference("User_WS_Docs_File").child(mAuth.getCurrentUser().getUid())
                    .child("station_business_permit")
                    .setValue(business)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(getActivity(), "Uploaded ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
    public void uploadPhysicoChemicalPermit(String physicochemical)
    {
        if (!physicochemical.isEmpty())
        {
            FirebaseDatabase.getInstance().getReference("User_WS_Docs_File").child(mAuth.getCurrentUser().getUid())
                    .child("station_physicochemical_permit")
                    .setValue(physicochemical)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(getActivity(), "Uploaded ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.monBoxUSD:
                String monday = "Monday";
                String monSplit = TextUtils.join(",", Collections.singleton(monday));
                if (mon.isChecked()) {
                    week.add(monSplit);
                }
                else {
                    week.remove(monSplit);
                }
                break;
            case R.id.tueBoxUSD:
                String tuesday = "Tuesday";
                String tuesdaySplit = TextUtils.join(",", Collections.singleton(tuesday));
                if (tue.isChecked()) {
                    week.add(tuesdaySplit);
                }
                else {
                    week.remove(tuesdaySplit);
                }
                break;
            case R.id.wedBoxUSD:
                String wednesday = "Wednesday";
                String wednesdaySplit = TextUtils.join(",", Collections.singleton(wednesday));
                if (wed.isChecked()) {
                    week.add(wednesdaySplit);
                }
                else {
                    week.remove(wednesdaySplit);
                }
                break;
            case R.id.thursBoxUSD:
                String thursday = "Thursday";
                String thursdaySplit = TextUtils.join(",", Collections.singleton(thursday));
                if (thurs.isChecked()) {
                    week.add(thursdaySplit);
                }
                else {
                    week.remove(thursdaySplit);
                }
                break;
            case R.id.friBoxUSD:
                String friday = "Friday";
                String fridaySplit = TextUtils.join(",", Collections.singleton(friday));
                if (fri.isChecked()) {
                    week.add(fridaySplit);
                }
                else {
                    week.remove(fridaySplit);
                }
                break;
            case R.id.satBoxUSD:
                String saturday = "Saturday";
                String saturdaySplit = TextUtils.join(",", Collections.singleton(saturday));
                if (sat.isChecked()) {
                    week.add(saturdaySplit);
                }
                else {
                    week.remove(saturdaySplit);
                }
                break;
            case R.id.sunBoxUSD:
                String sunday = "Sunday";
                String sundaySplit = TextUtils.join(",", Collections.singleton(sunday));
                if (sun.isChecked()) {
                    week.add(sundaySplit);
                    showMessages(week.toString());
                }
                else {
                    week.remove(sundaySplit);
                    showMessages(week.toString());
                }
                break;
        }
    }
}
