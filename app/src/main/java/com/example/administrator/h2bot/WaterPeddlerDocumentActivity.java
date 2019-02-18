package com.example.administrator.h2bot;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class WaterPeddlerDocumentActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "WaterPeddlerDocumentActivity.class";

    ImageView driverLicenseImageView;
    ImageView driverPlateNumberImageView;

    ProgressBar mProgressBar;
    String currentuser;
    Uri mImageUri,mImageUri2;
    String image1;
    Boolean check1, check2;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask,mUploadTask2;

    Button chooseButton1,chooseButton2,SubmitButtonWaterPeddlerHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_peddler_document);

//        drawerLayout = findViewById(R.id.customer_drawer);
//        drawerLayout.closeDrawers();
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        actionBarDrawerToggle.syncState();
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerMapFragment()).commit();
//            navigationView.setCheckedItem(R.id.map);
//            Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
//        }
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //ImageView
        driverLicenseImageView = findViewById(R.id.driverLicenseImageView);
        driverPlateNumberImageView = findViewById(R.id.driverPlateNumberImageView);

    //Button
        chooseButton1 = findViewById(R.id.chooseButton1);
        chooseButton2 = findViewById(R.id.chooseButton2);
        SubmitButtonWaterPeddlerHomeActivity = findViewById(R.id.SubmitButtonWaterPeddlerHomeActivity);

    //Progress Bar
        mProgressBar = findViewById(R.id.progress_bar);
    //design
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Water Dealer Documents");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Water Dealer Documents");

        chooseButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check1= true;
                check2 = false;
                openGalery();
            }
        });
        chooseButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check2 = true;
                check1 = false;
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
            if(check1==true) {
            mImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    driverLicenseImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(check2==true)
            {
                mImageUri2 = data.getData();
                Picasso.get().load(mImageUri2).into(driverPlateNumberImageView);
            }
        }
    }
    private void uploadDocument() {
        if (mImageUri != null && mImageUri2 != null) {
            StorageReference fileReference = mStorageRef.child(currentuser+"/"+"Driver License"
                    + "." + getFileExtension(mImageUri));
            StorageReference fileReference2 = mStorageRef.child(currentuser+"/"+"Driver Plate Number"
                    + "." + getFileExtension(mImageUri2));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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

                        }
                    });
            mUploadTask2 = fileReference2.putFile(mImageUri2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(WaterPeddlerDocumentActivity.this, "Upload successful" +currentuser, Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WaterPeddlerDocumentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.map:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerMapFragment()).commit();
//                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
//                break;
//
//            case R.id.my_order:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerOrdersFragment()).commit();
//                Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("My Orders");
//                break;
//
//            case R.id.account_settings:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerAccountSettingFragment()).commit();
//                Toast.makeText(this, "Account Settings", Toast.LENGTH_SHORT).show();
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Account Settings");
//                break;
//
//            case R.id.feedback:
//                final Dialog dialog = new Dialog(this);
//                dialog.setContentView(R.layout.feedback_popup);
//                dialog.show();
//                break;
//
//            case R.id.receipts:
//                Objects.requireNonNull(getSupportActionBar()).setTitle("Receipts");
//                break;
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    private void logout() {
//        mAuth.getInstance().signOut();
//        finish();
//        startActivity(new Intent(this, LoginActivity.class));
//        Toast.makeText(this, "Successfully logged-out", Toast.LENGTH_SHORT).show();
//    }
}
