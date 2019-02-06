package com.example.administrator.h2bot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        editText1 = findViewById(R.id.stationNameDM);
        editText2 = findViewById(R.id.stationRelateDM);

        imageView1 = findViewById(R.id.permit1DM);
        imageView2 = findViewById(R.id.permit2DM);

        imageButton1DM = findViewById(R.id.permitButton1DM);
        imageButton2DM = findViewById(R.id.permitButton2DM);

        imageButton1DM.setOnClickListener(this);
        imageButton2DM.setOnClickListener(this);
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
        if(uri1 != null && uri2 != null)
        {

        }
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

                break;
            case R.id.logoutButtonDM:

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
