package com.example.administrator.h2bot;

import com.example.administrator.h2bot.models.*;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaterStationDocumentVersion2Activity extends AppCompatActivity implements CheckBox.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_station_document_version2);
        week = new ArrayList<>();

        progressDialog = new ProgressDialog(WaterStationDocumentVersion2Activity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Creating account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        startSpinner= findViewById(R.id.startSpinner);
        endSpinner= findViewById(R.id.endSpinner);

        // Button
        businessPermitBtn = findViewById(R.id.businessPermitBtn);
        sanitaryPermitBtn = findViewById(R.id.sanitaryPermitBtn);
        physicochemicalbutton = findViewById(R.id.physicochemicalPermitBtn);
        submitButton = findViewById(R.id.submitButton);
        startTime = findViewById(R.id.startTimeButton);
        endTime = findViewById(R.id.endTimeButton);


        //TextView
        startTimeView = findViewById(R.id.startTimeTextView);
        endTimeView = findViewById(R.id.endTimeTextView);


        //Imageview
        businessPermit_image = findViewById(R.id.businessPermit_image);
        sanitaryPermit_image = findViewById(R.id.sanitaryPermit_image);
        physicochemicalPermit_Image = findViewById(R.id.physicochemicalPermit_image);;

        //EditText
        stationName = findViewById(R.id.stationName);
        stationAddress = findViewById(R.id.stationAddress);
        telNo = findViewById(R.id.telNo);
        min_no_of_gallons = findViewById(R.id.min_no_of_gallons);
        priceOfGallonEdit = findViewById(R.id.priceOfGallon);
        currentNoGallonEdit = findViewById(R.id.noOfGallons);

        //Radiogroup
        haveGallonGroup = findViewById(R.id.doYouHaveGallonGroup);

        //CheckBox
        mon = findViewById(R.id.monBox);
        tue = findViewById(R.id.tueBox);
        wed = findViewById(R.id.wedBox);
        thurs = findViewById(R.id.thursBox);
        fri = findViewById(R.id.friBox);
        sat = findViewById(R.id.satBox);
        sun = findViewById(R.id.sunBox);

        Bundle bundle = getIntent().getExtras();
        String firstname = bundle.getString("firstname");
        String lastname = bundle.getString("lastname");
        String address = bundle.getString("address");
        String contact_no = bundle.getString("contactno");
        String email_address = bundle.getString("emailaddress");
        String password = bundle.getString("password");
        String filepath = bundle.getString("filepath");
        filePathUri = Uri.parse(filepath);

        mFirstname = firstname;
        mLastname = lastname;
        mAddress = address;
        mContact_no = contact_no;
        mEmail_address = email_address;
        mPassword = password;
        mFilepath = filepath;

        //Button Listener
        businessPermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(stationName.getEditText().getText().toString()) || TextUtils.isEmpty(stationAddress.getEditText().getText().toString())){
                    Toast.makeText(WaterStationDocumentVersion2Activity.this, "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(WaterStationDocumentVersion2Activity.this, "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(WaterStationDocumentVersion2Activity.this, "Plesae fill the needed information above", Toast.LENGTH_SHORT).show();
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
                showMessages(week.toString());
                CreateAccount(mEmail_address, mPassword);
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
                    case R.id.no:
                        currentNoGallonEdit.setVisibility(View.GONE);
                        priceOfGallonEdit.setVisibility(View.GONE);
                        currentNoGallonEdit.getEditText().setText("NONE");
                        priceOfGallonEdit.getEditText().setText("NONE");
                        break;
                    case R.id.yes:
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
                DialogFragment dFragment = new TimePickerStartingTimeFragment();
                dFragment.show(getFragmentManager(),"Time Picker");
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new TimePickerEndingTimeFragment();
                dFragment.show(getFragmentManager(),"Time Picker");
            }
        });
    }

    private boolean checkAddress(String mAddress)
    {
        Geocoder coder = new Geocoder(this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                if(isPicked) {
                    filepath = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
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
                            Log.d("Data: ", sb.toString());
                            Log.d("Station name: ", stationName.getEditText().getText().toString().toLowerCase());
                            if(sb.toString().toLowerCase().contains(stationName.getEditText().getText().toString().toLowerCase())){
                            Picasso.get().load(filepath).fit().centerCrop().into(businessPermit_image);
                            Toast.makeText(this, "Valid business permit", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(this, "Invalid business permit", Toast.LENGTH_SHORT).show();
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
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath2);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(this, "Valid sanitary permit", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(this, "Invalid sanitary permit", Toast.LENGTH_SHORT).show();
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
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath3);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(this, "Valid Physicochemical permit", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                businessPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(this, "Invalid Physicochemical permit", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
        else
        {
            Toast.makeText(WaterStationDocumentVersion2Activity.this, "You haven't picked an image",Toast.LENGTH_LONG).show();
        }
    }
    private void CreateAccount(String emailAddress, String password){
        if (checkAddress(mAddress))
        {
            showMessages("Address is not valid, Please make sure your input are correct!");
            return;
        }
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( WaterStationDocumentVersion2Activity.this,  new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    newToken = instanceIdResult.getToken();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            StorageReference mStorage = FirebaseStorage.getInstance().getReference("station_photos").child(userId);
                            mStorage.putFile(filePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String stringUri = uri.toString();
                                            UserFile userFile = new UserFile(userId,
                                                    stringUri,
                                                    mFirstname,
                                                    mLastname,
                                                    mAddress,
                                                    mContact_no,
                                                    "Water Station",
                                                    "active");

                                            UserAccountFile userAccountFile = new UserAccountFile(userId,
                                                    mEmail_address,
                                                    mPassword,
                                                    newToken,
                                                    "active");

                                            StationBusinessInfo stationBusinessInfo = new StationBusinessInfo(
                                                firebaseUser.getUid(),
                                                    stationName.getEditText().getText().toString(),
                                                    stationAddress.getEditText().getText().toString(),
                                                    telNo.getEditText().getText().toString(),
                                                    startTimeView.getText().toString(),
                                                    endTimeView.getText().toString(),
                                                    week.toString().substring(0, week.size()-1),
                                                    min_no_of_gallons.getEditText().getText().toString(),
                                                    priceOfGallonEdit.getEditText().getText().toString(),
                                                    currentNoGallonEdit.getEditText().getText().toString(),
                                                    "active"
                                            );

                                            UserWallet userWallet = new UserWallet(
                                                    firebaseUser.getUid(),
                                                    "0",
                                                    "active"
                                            );

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Wallet");
                                            databaseReference.child(firebaseUser.getUid()).setValue(userWallet)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference("User_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userFile)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            FirebaseDatabase.getInstance().getReference("User_Account_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccountFile)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(stationBusinessInfo)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            progressDialog.dismiss();
                                                                                                            getLocationSetter();
                                                                                                            Toast.makeText(WaterStationDocumentVersion2Activity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            showMessages("Load cannot be store");
                                                        }
                                                    });

                                        }
                                    });
                                }
                            });
                            uploadAllImage();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getLocationSetter()
    {
        progressDialog.show();
        progressDialog.setMessage("Location Finishing");
        Geocoder coder = new Geocoder(this);
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
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    public void checkDocuments(){
        if(businessPermit_image.getDrawable() == null
                || sanitaryPermit_image.getDrawable() == null){
            Toast.makeText(this, "Please fill all the requirments", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void uploadAllImage(){
        if(filepath != null){
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            Log.d("auth", userId);
            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"businessPermitDocument");
            mStorageRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            business = uri.toString();
                        }
                    });
                }
            });
        }

        if(filepath3 != null){
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            Log.d("auth", userId);
            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"physicochemicalDocument");
            mStorageRef.putFile(filepath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            physicochemical = uri.toString();
                        }
                    });
                }
            });
        }


        if(filepath2 != null){
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
                            String stringUri = uri.toString();
                            WSDocFile wsDocFile = new WSDocFile(userId,
                                    business,
                                    stringUri,
                                    physicochemical,
                                    bir,
                                    "active");

                            FirebaseDatabase.getInstance().getReference("User_WS_Docs_File").child(userId).setValue(wsDocFile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(WaterStationDocumentVersion2Activity.this, LoginActivity.class));
                                            mAuth.signOut();
                                            Toast.makeText(WaterStationDocumentVersion2Activity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WaterStationDocumentVersion2Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    progressDialog.show();
                }
            });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.monBox:
                String monday = "Monday";
                String monSplit = TextUtils.join(",", Collections.singleton(monday));
                if (mon.isChecked()) {
                    week.add(monSplit);
                }
                else {
                    week.remove(monSplit);
                }
                break;
            case R.id.tueBox:
                String tuesday = "Tuesday";
                String tuesdaySplit = TextUtils.join(",", Collections.singleton(tuesday));
                if (tue.isChecked()) {
                    week.add(tuesdaySplit);
                }
                else {
                    week.remove(tuesdaySplit);
                }
                break;
            case R.id.wedBox:
                String wednesday = "Wednesday";
                String wednesdaySplit = TextUtils.join(",", Collections.singleton(wednesday));
                if (wed.isChecked()) {
                    week.add(wednesdaySplit);
                }
                else {
                    week.remove(wednesdaySplit);
                }
                break;
            case R.id.thursBox:
                String thursday = "Thursday";
                String thursdaySplit = TextUtils.join(",", Collections.singleton(thursday));
                if (thurs.isChecked()) {
                    week.add(thursdaySplit);
                }
                else {
                    week.remove(thursdaySplit);
                }
                break;
            case R.id.friBox:
                String friday = "Friday";
                String fridaySplit = TextUtils.join(",", Collections.singleton(friday));
                if (fri.isChecked()) {
                    week.add(fridaySplit);
                }
                else {
                    week.remove(fridaySplit);
                }
                break;
            case R.id.satBox:
                String saturday = "Saturday";
                String saturdaySplit = TextUtils.join(",", Collections.singleton(saturday));
                if (sat.isChecked()) {
                    week.add(saturdaySplit);
                }
                else {
                    week.remove(saturdaySplit);
                }
                break;
            case R.id.sunBox:
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
