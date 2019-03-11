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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.h2bot.MerchantAccessVerification;
import com.example.administrator.h2bot.R;
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
    Button chooseButton1,chooseButton2,SubmitButtonWaterPeddlerHomeActivity;

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
        currentUser = mAuth.getCurrentUser().getUid();

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
        if (mImageUri != null) {
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
                                    }
                                }, 500);
                                Task<Uri> result1 = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result1.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String addOne = uri.toString();
                                        FirebaseDatabase.getInstance().getReference("User_TPA_Docs_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("driver_license").setValue(addOne);
                                    }
                                });
                                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("User_File");
                                databaseReference.child(currentUser).child("user_status").setValue("unverified");

                                DatabaseReference databaseReference2= FirebaseDatabase.getInstance().getReference("User_Account_File");
                                databaseReference2.child(currentUser).child("user_status").setValue("unverified");

                                Toast.makeText(TPADocumentActivity.this, "Uploaded successfully" + currentuser, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TPADocumentActivity.this, MerchantAccessVerification.class));
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

        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
