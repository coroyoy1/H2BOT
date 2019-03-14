package com.example.administrator.h2bot.deliveryman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class DeliveryManDocumentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView1, imageView2;
    EditText editText1, editText2;
    Button submitDoc, logoutDoc, imageButton1DM, imageButton2DM;

    Boolean isClick1=false, isClick2=false;
    Intent intent;
    Uri uri1, uri2;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_man_document);

        intent = new Intent();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        editText1 = findViewById(R.id.stationNameDM);
        editText2 = findViewById(R.id.stationRelateDM);

        imageView1 = findViewById(R.id.permit1DM);
        imageView2 = findViewById(R.id.permit2DM);

        imageButton1DM = findViewById(R.id.permitButton1DM);
        imageButton2DM = findViewById(R.id.permitButton2DM);
        submitDoc = findViewById(R.id.submitButtonDM);
        logoutDoc = findViewById(R.id.logoutButtonDM);


        imageButton1DM.setOnClickListener(this);
        imageButton2DM.setOnClickListener(this);

        submitDoc.setOnClickListener(this);
        logoutDoc.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            uri1 = data.getData();uri2 = data.getData();
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                if(isClick1)
                {
                    try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri1);
                        imageView1.setImageBitmap(bitmap);
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (isClick2)
                {
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri1);
                        imageView2.setImageBitmap(bitmap);
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else {
                showMessages("You haven't select any photo");
            }
        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        if(uri1 != null && uri2 != null)
        {
            String currentUser = mAuth.getCurrentUser().getUid();
            StorageReference refFirst = storageReference.child("users_documents").child(currentUser+"/"+"documentFirst");
            refFirst.putFile(uri1);
            StorageReference refSecond = storageReference.child("users_documents").child(currentUser+"/"+"documentSecond");
            refSecond.putFile(uri2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseDatabase databaseStore = FirebaseDatabase.getInstance();
                            DatabaseReference referenceStore = databaseStore.getReference("Users");
                            referenceStore.child(mAuth.getCurrentUser().getUid()).child("status").setValue("unconfirmed")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                            referenceStore.child(mAuth.getCurrentUser().getUid()).child("stationName").setValue(editText1.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                            referenceStore.child(mAuth.getCurrentUser().getUid()).child("stationRelatedNo").setValue(editText2.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                          showMessages("Successfully submitted");
                                        }
                                    });
                            progressDialog.dismiss();
                            showMessages("Requirements still on process for the admin");
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    showMessages("Failed to Pass or Submit");
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
        }
    }

    public boolean checkingAddPhoto()
    {
        if(imageView1.getDrawable() == null && imageView2.getDrawable() == null)
        {
            showMessages("All document should be attached!");
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.permitButton1DM:
                isClick1=true;
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;
            case R.id.permitButton2DM:
                isClick2=true;
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;
            case R.id.submitButtonDM:
                if(checkingAddPhoto())
                {
                    return;
                }
                uploadImage();
                break;
            case R.id.logoutButtonDM:
                mAuth.signOut();
                finish();
                startActivity(new Intent(DeliveryManDocumentActivity.this, LoginActivity.class));
                break;
                default:
                    showMessages("Does not available in the option");
                    break;
        }
    }


    public void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
