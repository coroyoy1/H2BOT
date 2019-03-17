package com.example.administrator.h2bot.tpaaffiliate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
import com.example.administrator.h2bot.models.TPAModel;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.WSBusinessInfoFile;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class TPADocumentActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "WaterPeddlerDocumentActivity.class";
    String currentUser;
    ImageView driverLicenseImageView;
   // ProgressBar mProgressBar;
    String currentuser;
    Uri mImageUri;
    String image1;
    Boolean check1;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask,mUploadTask2;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    Button licenseBtn,chooseButton2,SubmitButtonWaterPeddlerHomeActivity;
    Boolean isPicked = false;
    TextView driverLicenseNo;
    ImageView driversLicense_image;
    Uri filepath;
    Button register;
    Double latitude, longitude;

    Uri mUri;
    String mFirstname, mLastname, mAddress, mContact_no, mEmail_address, mPassword, mFilepath;
    String newToken;
    String mLat, mLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpa_document);
        progressDialog = new ProgressDialog(TPADocumentActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("Water Dealer Documents");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Water Dealer Documents");
        licenseBtn = findViewById(R.id.licenseBtn);
        driversLicense_image = findViewById(R.id.driversLicense_image);
        register = findViewById(R.id.submitButton);

        Bundle bundle = getIntent().getExtras();
        String firstname = bundle.getString("firstname");
        String lastname = bundle.getString("lastname");
        String address = bundle.getString("address");
        String contact_no = bundle.getString("contactno");
        String email_address = bundle.getString("emailaddress");
        String password = bundle.getString("password");
        String filepath = bundle.getString("filepath");

        mFirstname = firstname;
        mLastname = lastname;
        mAddress = address;
        mContact_no = contact_no;
        mEmail_address = email_address;
        mPassword = password;
        mFilepath = filepath;

        register.setOnClickListener(v -> {
                CreateAccount(mEmail_address, mPassword);
        });

        licenseBtn.setOnClickListener(v -> {
                isPicked = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
              });
    }

    private void getLocationSetter()
    {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = mAddress;
        Toast.makeText(this, "locateAddress = " + locateAddress, Toast.LENGTH_SHORT).show();

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            latitude = LocationAddress.getLatitude();
            longitude = LocationAddress.getLongitude();

            mLat = String.valueOf(latitude);
            mLong = String.valueOf(longitude);


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void insertLatLong(String id, String latitude, String longitude){
        UserLocationAddress userLocationAddress = new UserLocationAddress(id, latitude, longitude);
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
        locationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocationAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TPADocumentActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //showMessage("Error to get location");
                        progressDialog.dismiss();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

                filepath = data.getData();
                if(isPicked) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getApplication(), "No text found", Toast.LENGTH_SHORT).show();
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
                            Log.d("Data: ", mFirstname+""+mLastname);
                            if(sb.toString().trim().toLowerCase().contains(mFirstname.toLowerCase())
                                    && sb.toString().trim().toLowerCase().contains(mLastname.toLowerCase())
                                    && sb.toString().toUpperCase().contains("DRIVERS LICENSE")
                                    || sb.toString().toUpperCase().contains("DRIVER'S LICENSE")
                                    || sb.toString().toUpperCase().contains("LAND")
                                    || sb.toString().toUpperCase().contains("TRANSPORTATION")
                                    || sb.toString().toUpperCase().contains("OFFICE")
                            ){
                                Picasso.get().load(filepath).into(driversLicense_image);
                                Toast.makeText(this, "Valid Driver's License", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                driversLicense_image.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(this, "Invalid Driver's License or please use a better image.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(TPADocumentActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void CreateAccount(String emailAddress, String password){
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( TPADocumentActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    newToken = instanceIdResult.getToken();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TPADocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            StorageReference mStorage = FirebaseStorage.getInstance().getReference("tpa_photos").child(userId);
                            mStorage.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String stringUri = uri.toString();
                                            UserFile userFile = new UserFile(userId,
                                                    mFilepath,
                                                    mFirstname,
                                                    mLastname,
                                                    mAddress,
                                                    mContact_no,
                                                    "Third Party Affiliate",
                                                    "active");

                                            UserAccountFile userAccountFile = new UserAccountFile(userId,
                                                    mEmail_address,
                                                    mPassword,
                                                    newToken,
                                                    "active");

                                            TPAModel tpaModel = new TPAModel(userId,
                                                    stringUri,
                                                    "active"
                                            );



                                            FirebaseDatabase.getInstance().getReference("User_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userFile)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference("User_Account_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccountFile)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            FirebaseDatabase.getInstance().getReference("User_TPA_Docs_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(tpaModel)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Toast.makeText(TPADocumentActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                                                            getLocationSetter();
                                                                                            insertLatLong(FirebaseAuth.getInstance().getCurrentUser().getUid(), mLat, mLong);
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(TPADocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(TPADocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(TPADocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    });
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TPADocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadDocument() {
        if(filepath != null){
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            Log.d("auth", userId);
            StorageReference mStorageRef = storageReference.child("tpa_documents").child(userId +"/"+"driversLicense");
            mStorageRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mFilepath = uri.toString();
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
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

    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
