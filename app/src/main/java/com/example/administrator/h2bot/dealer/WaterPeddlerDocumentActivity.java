package com.example.administrator.h2bot.dealer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.h2bot.MerchantAccessVerification;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class WaterPeddlerDocumentActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "WaterPeddlerDocumentActivity.class";
    String currentUser;
    ImageView driverLicenseImageView;
    ImageView driverPlateNumberImageView;
    String businessFreeOrNoText = "";
    String businessDeliveryService = "";
   // ProgressBar mProgressBar;
    String currentuser;
    Uri mImageUri;
    String image1;
    Boolean check1;
    EditText dealerName,dealerAddress,dealerNo,dealerBusinesshoursStart,dealerBusinesshoursEnd,dealerCapacity,dealerDeliveryFee;
    RadioButton radioYes,radioNo,radioFree,radioPerGalSD,radioFixSD;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask,mUploadTask2;
    private ProgressDialog progressDialog;
    Spinner startSpinner, endSpinner;
    private double lat;
    private double lng;
    Button chooseButton1,chooseButton2,SubmitButtonWaterPeddlerHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_peddler_document);
        progressDialog = new ProgressDialog(WaterPeddlerDocumentActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        mAuth = FirebaseAuth.getInstance();
        dealerName = findViewById(R.id.dealerName);
        dealerAddress = findViewById(R.id.dealerAddress);
        dealerNo = findViewById(R.id.dealerNo);
        dealerBusinesshoursStart = findViewById(R.id.dealerBusinesshoursStart);
        dealerBusinesshoursEnd = findViewById(R.id.dealerBusinesshoursEnd);
        dealerCapacity = findViewById(R.id.dealerCapacity);
        dealerDeliveryFee = findViewById(R.id.dealerDeliveryFee);
        currentUser = mAuth.getCurrentUser().getUid();
        radioYes = findViewById(R.id.radioYes);
        radioNo = findViewById(R.id.radioNo);
        radioFree = findViewById(R.id.radioFree);
        startSpinner= findViewById(R.id.startSpinner);
        endSpinner= findViewById(R.id.endSpinner);
        radioYes.setChecked(true);

        radioPerGalSD = findViewById(R.id.radioPerGalSD);
        radioFixSD = findViewById(R.id.radioFixSD);
        String[] arraySpinner = new String[]{
                "AM","PM"
        };
        String[] arraySpinner2 = new String[]{
                "PM","AM"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WaterPeddlerDocumentActivity.this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(WaterPeddlerDocumentActivity.this,
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter2);

    //ImageView
        driverLicenseImageView = findViewById(R.id.driverLicenseImageView);

    //Button
        chooseButton1 = findViewById(R.id.chooseButton1);
        SubmitButtonWaterPeddlerHomeActivity = findViewById(R.id.SubmitButtonWaterPeddlerHomeActivity);

    //Progress Bar
      //  mProgressBar = findViewById(R.id.progress_bar);
    //design
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Water Dealer Documents");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Water Dealer Documents");

        if(radioYes.isChecked())
        {
            businessFreeOrNoText = "not";
            businessDeliveryService = "inactive";
        }


        radioYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked();
            }
        });
        radioNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked();
            }
        });
        radioFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked();
            }
        });


        chooseButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check1= true;
                openGalery();
            }
        });
        SubmitButtonWaterPeddlerHomeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDocument();
            }
        });

    }

    private void openGalery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void getLocationSetter()
    {
        progressDialog.show();
        progressDialog.setMessage("Location Finishing");
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = dealerAddress.getText().toString();

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
                            showMessages("Submitted successfully");
                            progressDialog.dismiss();
                            Intent passIntent = new Intent(WaterPeddlerDocumentActivity.this, MerchantAccessVerification.class);
                            startActivity(passIntent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessages("Failed to get location");

                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {

            ex.printStackTrace();
            progressDialog.dismiss();
        }
        finally {
            showMessages("Failed to locate your address");
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    driverLicenseImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    private void uploadDocument() {
        String dealername = dealerName.getText().toString();
        String dealeraddress = dealerAddress.getText().toString();
        String dealerno = dealerNo.getText().toString();
        String dealerstart = dealerBusinesshoursStart.getText().toString() + startSpinner.getSelectedItem().toString();
        String dealerend = dealerBusinesshoursEnd.getText().toString() + endSpinner.getSelectedItem().toString();
        String dealercapacity = dealerCapacity.getText().toString();
        String dealerdeliveryfee = dealerDeliveryFee.getText().toString();
        if (mImageUri != null) {
            if(dealername.isEmpty()
                    && dealerstart.isEmpty()
                    && dealerend.isEmpty()
                    && dealerdeliveryfee.isEmpty()
                    && dealercapacity.isEmpty()
                    && dealerno.isEmpty()
                    && dealeraddress.isEmpty())
            {
                showMessages("Please fill up tall the fields");
                return;
            }
            else {
                StorageReference fileReference = mStorageRef.child(currentuser + "/" + "Driver License"
                        + "." + getFileExtension(mImageUri));
                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mProgressBar.setProgress(0);
                                        progressDialog.dismiss();
                                        getLocationSetter();
                                    }
                                }, 500);
                                Task<Uri> result1 = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result1.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String addOne = uri.toString();
                                        FirebaseDatabase.getInstance().getReference("User_WD_Docs_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("driver_license").setValue(addOne);
                                    }
                                });
                                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("User_File");
                                databaseReference.child(currentUser).child("user_status").setValue("unverified");

                                DatabaseReference databaseReference2= FirebaseDatabase.getInstance().getReference("User_Account_File");
                                databaseReference2.child(currentUser).child("user_status").setValue("unverified");

                                Toast.makeText(WaterPeddlerDocumentActivity.this, "Uploaded successfully" + currentuser, Toast.LENGTH_SHORT).show();
                                Log.d("capacity",""+dealerCapacity);
                                startActivity(new Intent(WaterPeddlerDocumentActivity.this, MerchantAccessVerification.class));
                                UserWSBusinessInfoFile userWSBusinessInfoFile = new UserWSBusinessInfoFile(currentUser, dealername, dealerstart, dealerend, businessDeliveryService, businessFreeOrNoText, dealerdeliveryfee, dealercapacity, dealerno, dealeraddress, "active", "");
                                FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(currentUser).setValue(userWSBusinessInfoFile);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setProgress((int) progress);
                            }
                        });
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

public void setChecked()
{
    if (radioNo.isChecked())
    {
        dealerCapacity.setVisibility(View.INVISIBLE);
        dealerCapacity.setText("");
        radioPerGalSD.setVisibility(View.INVISIBLE);
        radioFixSD.setVisibility(View.INVISIBLE);
        dealerDeliveryFee.setVisibility(View.INVISIBLE);
        businessFreeOrNoText = "not";
        businessDeliveryService = "inactive";
    }
    else if(radioYes.isChecked())
    {
        dealerCapacity.setVisibility(View.VISIBLE);
        dealerCapacity.setText("");
        radioPerGalSD.setVisibility(View.VISIBLE);
        radioFixSD.setVisibility(View.VISIBLE);
        dealerDeliveryFee.setVisibility(View.VISIBLE);
        businessFreeOrNoText = "not";
        businessDeliveryService = "active";
    }
    else if(radioFree.isChecked())
    {
        dealerCapacity.setVisibility(View.INVISIBLE);
        radioPerGalSD.setVisibility(View.INVISIBLE);
        radioFixSD.setVisibility(View.INVISIBLE);
        dealerDeliveryFee.setVisibility(View.INVISIBLE);
        businessFreeOrNoText = "free";
        businessDeliveryService = "active";
    }
    else
    {
        showMessages("Check any radio button first");
    }
}
    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
