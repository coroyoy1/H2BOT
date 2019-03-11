package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class WPBusinessDocumentUpdate extends Fragment implements View.OnClickListener{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    Button button1, button2, button3, button4, button5, button6, updateDocummentButton;
    EditText permitString;

    Uri uri1,uri2,uri3,uri4,uri5,uri6;

    boolean isClick1=false, isClick2=false, isClick3=false, isClick4=false, isClick5=false, isClick6=false;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_merchant_wp_updatedocument, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        imageView1 = view.findViewById(R.id.imageUD1);
        imageView2 = view.findViewById(R.id.imageUD2);
        imageView3 = view.findViewById(R.id.imageUD3);
        imageView4 = view.findViewById(R.id.imageUD4);
        imageView5 = view.findViewById(R.id.imageUD5);
        imageView6 = view.findViewById(R.id.imageUD6);

        button1 = view.findViewById(R.id.permitButtonUD1);
        button2 = view.findViewById(R.id.permitButtonUD2);
        button3 = view.findViewById(R.id.permitButtonUD3);
        button4 = view.findViewById(R.id.permitButtonUD4);
        button5 = view.findViewById(R.id.permitButtonUD5);
        button6 = view.findViewById(R.id.permitButtonUD6);

        permitString = view.findViewById(R.id.businessPermitNoUD);

        updateDocummentButton = view.findViewById(R.id.updateButtonUD);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        updateDocummentButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            uri1 = data.getData(); uri2 = data.getData(); uri3 = data.getData(); uri4 = data.getData(); uri5 = data.getData(); uri6 = data.getData();
            if(isClick1)
            {
                Picasso.get().load(uri1).into(imageView1);
            }

        }
        else
        {
            showMessage("Choose an image!");
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    public void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
    }

    public void getInput()
    {
        if(uri1 == null && uri2 == null && uri3 == null && uri4 == null && uri5 == null && uri6 == null)
        {
            showMessage("Choose an image");
        }
        else
        {
            updatePhoto();
        }
    }

    public void updatePhoto()
    {
        progressDialog.dismiss();
        if(uri1 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("Water Dealer Documents")
                    .child(currentUser.getUid()+"/"+"business_driver_license");
            storageReference.putFile(uri1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uriString = uri.toString();
                                    FirebaseDatabase.getInstance()
                                            .getReference("User_WD_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_driver_license").setValue(uriString);
                                }
                            });
                        }
                    });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.permitButtonUD1:
                isClick1=true;
                openGallery();
                break;

            case R.id.updateButtonUD:
                getInput();
                break;
        }
    }
}
