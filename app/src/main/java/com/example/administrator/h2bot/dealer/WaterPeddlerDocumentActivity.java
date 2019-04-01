package com.example.administrator.h2bot.dealer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.WDDocFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile2;
import com.example.administrator.h2bot.models.WSDocFile;
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
import java.util.Calendar;
import java.util.List;

public class WaterPeddlerDocumentActivity extends AppCompatActivity implements CheckBox.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    String userId;
    TextView startTimeTextView, endTimeTextView;
    String startHour, startMinute, endHour, endMinute, startAMPM, endAMPM;
    ImageView NBIImageView, driversLicense_image;
    Button driversLicenseBtn, submitButton, NBIButton;
    RadioGroup deliveryFeeGroup;
    boolean check,check1;
    EditText stationName, stationAddress, endingHour, businessDeliveryFeePerGal, businessMinNoCapacity, telNo, deliveryFee, min_no_of_gallons,startingHour,endHour2;
    String deliveryMethod, mUri;
    Spinner simpleTimePicker;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private double lat;
    private double lng;
    String newToken;
    CheckBox mon, tue, wed, thurs, fri, sat, sun;
    Uri filepath3, filepath2,mFilepath;
    Boolean isPicked = false;
    Boolean isPicked2 = false;
    String mFirstname, mLastname, mAddress, mContact_no, mEmail_address, mPassword;
    Spinner startSpinner, endSpinner;
    List<String> week;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_peddler_document);

        progressDialog = new ProgressDialog(WaterPeddlerDocumentActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Creating account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        week = new ArrayList<String>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        endHour2 = findViewById(R.id.endHour2);
        startingHour = findViewById(R.id.startingHour);
        startSpinner= findViewById(R.id.startSpinner);
        endSpinner= findViewById(R.id.endSpinner);
        // Button
        driversLicenseBtn = findViewById(R.id.driversLicenseBtn);
        submitButton = findViewById(R.id.submitButton);
        NBIButton = findViewById(R.id.NBIButton);

        //Imageview
        driversLicense_image = findViewById(R.id.driversLicense_image);
        NBIImageView = findViewById(R.id.NBIImageView);
        //EditText
        stationName = findViewById(R.id.stationName);
        stationAddress = findViewById(R.id.stationAddress);
        telNo = findViewById(R.id.telNo);
        deliveryFee = findViewById(R.id.deliveryFee);
        min_no_of_gallons = findViewById(R.id.min_no_of_gallons);

        Bundle bundle = getIntent().getExtras();
        String firstname = bundle.getString("firstname");
        String lastname = bundle.getString("lastname");
        String address = bundle.getString("address");
        String contact_no = bundle.getString("contactno");
        String email_address = bundle.getString("emailaddress");
        String password = bundle.getString("password");
        String filepath = bundle.getString("filepath");
        mFilepath = Uri.parse(filepath);

        startSpinner= findViewById(R.id.startSpinner);
        endSpinner= findViewById(R.id.endSpinner);
        mon = findViewById(R.id.monBox);
        tue = findViewById(R.id.tueBox);
        wed = findViewById(R.id.wedBox);
        thurs = findViewById(R.id.thursBox);
        fri = findViewById(R.id.friBox);
        sat = findViewById(R.id.satBox);
        sun = findViewById(R.id.sunBox);
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thurs.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        sun.setOnClickListener(this);
        mFirstname = firstname;
        mLastname = lastname;
        mAddress = address;
        mContact_no = contact_no;
        mEmail_address = email_address;
        mPassword = password;

        String[] arraySpinner = new String[]{
                "AM", "PM"
        };
        String[] arraySpinner2 = new String[]{
                "PM", "AM"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WaterPeddlerDocumentActivity.this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(WaterPeddlerDocumentActivity.this,
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter2);

        driversLicenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    isPicked = false;
                    isPicked2 = true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });
        NBIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPicked2 = false;
                isPicked = true;
                Log.d("CheckPick",""+isPicked);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        startingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(WaterPeddlerDocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startingHour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        endHour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(WaterPeddlerDocumentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endHour2.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check==true && check1==true) {
                    CreateAccount(mEmail_address, mPassword);
                }
                else if(filepath2 == null || filepath3==null)
                {
                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Please choose an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                                Log.d("Strings",""+sb.append(myItem.getValue()));
                            }
                            if(sb.toString().toLowerCase().contains(mFirstname.toLowerCase())
                                && sb.toString().toLowerCase().contains(mLastname.toLowerCase())
                                && sb.toString().toLowerCase().contains("republic of the philippines")
                                && sb.toString().toLowerCase().contains("department of transportation")
                                && sb.toString().toLowerCase().contains("land transportation office")
                                && sb.toString().toLowerCase().contains("driver's license")
                                && sb.toString().toLowerCase().contains("license no"))
                                {
                                Picasso.get().load(filepath2).into(driversLicense_image);
                                String text = "Valid driver's license";
                                snackBar(text);
                                check1 = true;
                            }
                            else{
                                driversLicense_image.setImageResource(R.drawable.ic_image_black_24dp);
                                String text = "Invalid driver's license. Please capture the license clearly";
                                snackBar(text);
                                check1 = false;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(isPicked)
                {
                       filepath3 = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath3);
                            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                            if (!textRecognizer.isOperational()) {
                                Toast.makeText(getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
                            } else {
                                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                                StringBuilder sb = new StringBuilder();

                                for (int ctr = 0; ctr < items.size(); ctr++) {
                                    TextBlock myItem = items.valueAt(ctr);
                                    sb.append(myItem.getValue());
                                    sb.append("\n");
                                    Log.d("Strings",""+sb.append(myItem.getValue()));
                                }
                                if (sb.toString().toLowerCase().contains(mFirstname.toLowerCase().trim())
                                    && sb.toString().toLowerCase().contains(mLastname.toLowerCase().trim())
                                    && sb.toString().toLowerCase().contains("republic")
                                        && sb.toString().toLowerCase().contains("philippines")
                                    && sb.toString().toLowerCase().contains("department of justice")
                                    && sb.toString().toLowerCase().contains("national bureau of investigation"))
                                {
                                    Picasso.get().load(filepath3).into(NBIImageView);
                                    String text = "Valid NBI clearance";
                                    snackBar(text);
                                    check = true;
                                }
                                else {
                                    NBIImageView.setImageResource(R.drawable.ic_image_black_24dp);
                                    String text = "Invalid NBI Clearance. Please capture it clearly";
                                    snackBar(text);
                                    check = false;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
        else
        {
            Toast.makeText(WaterPeddlerDocumentActivity.this, "You haven't picked an image",Toast.LENGTH_LONG).show();
        }
    }

    private void CreateAccount(String emailAddress, String password){
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    userId = user.getUid();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = firebaseUser.getUid();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( WaterPeddlerDocumentActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            newToken = instanceIdResult.getToken();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    StorageReference mStorage1 = FirebaseStorage.getInstance().getReference("user_photo").child(userId);
                    mStorage1.putFile(mFilepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                }
                            });
                        }
                    });
                    StorageReference mStorage = FirebaseStorage.getInstance().getReference("dealer_photos").child(userId);
                    mStorage.putFile(mFilepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            String name = mFirstname+" "+mLastname;
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
                                            "Water Dealer",
                                            "active");

                                    UserAccountFile userAccountFile = new UserAccountFile(userId,
                                            mEmail_address,
                                            mPassword,
                                            newToken,
                                            "active");

                                    String startHour = startingHour.getText().toString() + " " + startSpinner.getSelectedItem();
                                    String endHour = endHour2.getText().toString() + " " + endSpinner.getSelectedItem();

                                    WSBusinessInfoFile2 businessInfoFile = new WSBusinessInfoFile2(
                                            userId,
                                            name,
                                            mAddress,
                                            mContact_no,
                                            startHour,
                                            endHour,
                                            week.toString(),
                                            min_no_of_gallons.getText().toString(),
                                            "active");

                                    FirebaseDatabase.getInstance().getReference("User_File").child(userId).setValue(userFile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseDatabase.getInstance().getReference("User_Account_File").child(userId).setValue(userAccountFile)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                           FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(userId).setValue(businessInfoFile)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    progressDialog.dismiss();
                                                                    getLocationSetter();
                                                                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                                    uploadAllImage();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        });
                                }
                            });
                        }
                    });
                  //  uploadAllImage();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
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
        String locateAddress = mAddress;

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(userId, getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(userId).setValue(userLocationAddress)
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
            progressDialog.dismiss();
        }
    }

    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }




    public void uploadAllImage(){
        if(filepath2 != null){
            StorageReference mStorageRef = storageReference.child("dealer_documents").child(userId +"/"+"DriversLicenseDocument");
            mStorageRef.putFile(filepath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String stringUri = uri.toString();
                            WDDocFile wsDocFile = new WDDocFile(userId,
                                "active",
                                    stringUri);

                            FirebaseDatabase.getInstance().getReference("User_WD_Docs_File").child(userId).setValue(wsDocFile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if(filepath3 != null){
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            userId = user.getUid();
                                            StorageReference mStorageRef = storageReference.child("dealer_documents").child(userId +"/"+"NBIClearanceDocument");
                                            mStorageRef.putFile(filepath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            FirebaseDatabase.getInstance().getReference("User_WD_Docs_File").child(userId).child("NBI_clearance").setValue(uri.toString());
                                                            startActivity(new Intent(WaterPeddlerDocumentActivity.this, LoginActivity.class));
                                                            mAuth.signOut();
                                                            finish();
                                                            Toast.makeText(WaterPeddlerDocumentActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void starttimeData()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WaterPeddlerDocumentActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.timepicker, null);

        TimePicker simpleTimePicker = dialogView.findViewById(R.id.simpleTimePicker);
        Button setButton = dialogView.findViewById(R.id.setButton);
        simpleTimePicker.setIs24HourView(true);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHour = String.valueOf(simpleTimePicker.getHour());
                startMinute = String.valueOf(simpleTimePicker.getMinute());
                String AM_PM ;
                Toast.makeText(WaterPeddlerDocumentActivity.this, startHour+","+startMinute,Toast.LENGTH_SHORT).show();
                startTimeTextView.setText(startHour+":"+startMinute);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public void starttimeData2()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WaterPeddlerDocumentActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.timepicker, null);

        TimePicker simpleTimePicker = dialogView.findViewById(R.id.simpleTimePicker);
        Button setButton = dialogView.findViewById(R.id.setButton);
        simpleTimePicker.setIs24HourView(true);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHour = String.valueOf(simpleTimePicker.getHour());
                startMinute = String.valueOf(simpleTimePicker.getMinute());
                String AM_PM ;
                Toast.makeText(WaterPeddlerDocumentActivity.this, startHour+","+startMinute,Toast.LENGTH_SHORT).show();
                endTimeTextView.setText(startHour+":"+startMinute);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
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
                    showMessages(week.toString());
                }
                else {
                    week.remove(sundaySplit);
                    showMessages(week.toString());
                }
                break;
        }
    }
    public void snackBar(String text){
        View parentLayout = findViewById(android.R.id.content);
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
