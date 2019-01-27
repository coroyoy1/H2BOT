package com.example.administrator.h2bot;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class AddItemMerchant extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddItemMerchant.class";
    Button UploadAPhotoButton,AddItemButton;
    ImageView ItemImage;
    EditText ItemNameEditText,PriceEditText,QualityEditText;
    Spinner waterTypeSpinner;
    private ProgressBar mProgressBar;
    String currentuser;
    Uri mImageUri;
    private String imageUrl;


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_merchant);

        //Button
        UploadAPhotoButton = findViewById(R.id.UploadAPhotoButton);
        AddItemButton=findViewById(R.id.AddItemButton);

        //ImageView
        ItemImage=findViewById(R.id.ItemImage);

        //EditView
        ItemNameEditText =findViewById(R.id.ItemNameEditText);
        PriceEditText = findViewById(R.id.PriceEditText);
        QualityEditText = findViewById(R.id.QualityEditText);

        //Spinner
        waterTypeSpinner=findViewById(R.id.waterTypeSpinner);
        String[] arraySpinner = new String[] {
                "Mineral", "Distilled", "Purified", "Alkaline"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterTypeSpinner.setAdapter(adapter);


        //ProgressBar
        mProgressBar = findViewById(R.id.progress_bar);

        //design
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("sampleItemPhotos");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User Items");

        PriceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        QualityEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        PriceEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        QualityEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

        UploadAPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalery();
            }
        });
        AddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddItemMerchant.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadItem();
                }
            }
        });
    }

    private void uploadItem() {
        String itemname = ItemNameEditText.getText().toString().trim();
        String itemprice = PriceEditText.getText().toString().trim();
        String itemquality = QualityEditText.getText().toString().trim();
        if (mImageUri != null) {
            if(itemname.isEmpty()){
                ItemNameEditText.setError("Required!");
                ItemNameEditText.requestFocus();
                return;
            }
            if(itemprice.isEmpty()){
                PriceEditText.setError("Required!");
                PriceEditText.requestFocus();
                return;
            }
            if(itemquality.isEmpty()){
                QualityEditText.setError("Required!");
                QualityEditText.requestFocus();
                return;
            }
            final String uuid = UUID.randomUUID().toString();
            StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uuid).child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
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
                            Toast.makeText(AddItemMerchant.this, "Upload successful" +currentuser, Toast.LENGTH_LONG).show();
                            WaterPeddlerGetterSetter upload = new WaterPeddlerGetterSetter(
                                    taskSnapshot.getMetadata().getPath(),
                                    ItemNameEditText.getText().toString().trim(),
                                   waterTypeSpinner.getSelectedItem().toString(),
                                    PriceEditText.getText().toString().trim(),
                                    QualityEditText.getText().toString().trim(),
                                    currentuser
                            );
                            Picasso.get().load(R.drawable.ic_menu_camera).into(ItemImage);
                            ItemNameEditText.setText("");
                            PriceEditText.setText("");
                            QualityEditText.setText("");
                            String uploadId = currentuser;
                            mDatabaseRef.child(uploadId).child(uuid).setValue(upload);

//                            mDatabaseRef.child(uploadId).child(System.currentTimeMillis()
//                                    + "." + getFileExtension(mImageUri)).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddItemMerchant.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Picasso.get().load(mImageUri).into(ItemImage);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
