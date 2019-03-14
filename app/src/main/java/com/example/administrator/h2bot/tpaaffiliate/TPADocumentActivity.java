package com.example.administrator.h2bot.tpaaffiliate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
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
    Button licenseBtn,chooseButton2,SubmitButtonWaterPeddlerHomeActivity;
    Boolean isPicked = false;
    TextView driverLicenseNo;
    ImageView driversLicense_image;
    Uri filepath;

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
        driverLicenseNo = findViewById(R.id.driverLicenseNo);
        driversLicense_image = findViewById(R.id.driversLicense_image);

        licenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(driverLicenseNo.getText().toString())){
                    Toast.makeText(TPADocumentActivity.this, "Please fill the information needed", Toast.LENGTH_SHORT).show();
                }
                else{
                    isPicked = true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
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
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

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
                            Log.d("License No: ", driverLicenseNo.getText().toString().toLowerCase());
                            if(sb.toString().toLowerCase().contains(driverLicenseNo.getText().toString().toLowerCase())
                                    && sb.toString().toUpperCase().contains("NON-PROFESSIONAL DRIVER'S LICENSE")){
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
//                                startActivity(new Intent(TPADocumentActivity.this, MerchantAccessVerification.class));
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
