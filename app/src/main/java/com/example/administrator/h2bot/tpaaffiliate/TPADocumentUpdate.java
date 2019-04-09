package com.example.administrator.h2bot.tpaaffiliate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.dealer.WPBusinessInfoFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class TPADocumentUpdate extends Fragment implements View.OnClickListener{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String firstname, lastname;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView1,imageUD2;
    Button button1, permitButtonUD2,button2, button3, button4, button5, button6, updateDocummentButton;
    Boolean check,check2;
    Uri uri1,uri2;
    String nbiurl, driverurl;

    boolean isClick1=false, isClick2=false, isClick3=false, isClick4=false, isClick5=false, isClick6=false;
    private ProgressDialog progressDialog;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tpa_document_update, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        imageView1 = view.findViewById(R.id.imageUD1);
        button1 = view.findViewById(R.id.permitButtonUD1);
        imageUD2 = view.findViewById(R.id.imageUD2);
        permitButtonUD2 = view.findViewById(R.id.permitButtonUD2);

        updateDocummentButton = view.findViewById(R.id.updateButtonUD);

        button1.setOnClickListener(this);
        permitButtonUD2.setOnClickListener(this);
        updateDocummentButton.setOnClickListener(this);
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User_TPA_Docs_File").child(currentUser.getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("driverLicense").getValue(String.class)).into(imageView1);
                Picasso.get().load(dataSnapshot.child("NBI_clearance").getValue(String.class)).into(imageUD2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("User_File");
        reference3.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstname = dataSnapshot.child("user_firstname").getValue(String.class);
                lastname = dataSnapshot.child("user_lastname").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            if(isClick1) {
                check2 = false;
                uri1 = data.getData();
                Bitmap bitmap = null;
                Picasso.get().load(uri1).into(imageView1);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri1);
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                    if (!textRecognizer.isOperational()) {
                        Toast.makeText(getActivity(), "No text detected", Toast.LENGTH_SHORT).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = textRecognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();

                        for (int ctr = 0; ctr < items.size(); ctr++) {
                            TextBlock myItem = items.valueAt(ctr);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                        }
                        if(sb.toString().toLowerCase().contains(firstname.toLowerCase())
                                && sb.toString().toLowerCase().contains(lastname.toLowerCase())
                                && sb.toString().toLowerCase().contains("republic of the philippines")
                                && sb.toString().toLowerCase().contains("department of transportation")
                                && sb.toString().toLowerCase().contains("land transportation office")
                                && sb.toString().toLowerCase().contains("driver's license")
                                && sb.toString().toLowerCase().contains("license no")) {
                            Picasso.get().load(uri1).into(imageView1);
                            check = true;
                            check2 = false;
                        }
                        else {
                            imageView1.setImageResource(R.drawable.ic_image_black_24dp);
                            check = false;
                            check2 = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isClick2)
            {
                check = false;
                uri2 = data.getData();
                Bitmap bitmap = null;
                Picasso.get().load(uri2).into(imageUD2);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri2);
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                    if (!textRecognizer.isOperational()) {
                        Toast.makeText(getActivity(), "No text detected", Toast.LENGTH_SHORT).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = textRecognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();

                        for (int ctr = 0; ctr < items.size(); ctr++) {
                            TextBlock myItem = items.valueAt(ctr);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                        }
                        if (sb.toString().toLowerCase().contains(firstname.toLowerCase().trim())
                                && sb.toString().toLowerCase().contains(lastname.toLowerCase().trim())
                                && sb.toString().toLowerCase().contains("republic")
                                && sb.toString().toLowerCase().contains("philippines")
                                && sb.toString().toLowerCase().contains("department of justice")
                                && sb.toString().toLowerCase().contains("national bureau of investigation")) {
                            Picasso.get().load(uri2).into(imageUD2);
                            check2 = true;
                        } else {
                            imageUD2.setImageResource(R.drawable.ic_image_black_24dp);
                            Toast.makeText(getActivity(), "Invalid NBI Clearance. Choose a clear image", Toast.LENGTH_SHORT).show();
                            check2 = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            String text = "Choose an image";
            snackBar(text);
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
        if(uri1 == null && uri2!=null)
        {
            uploadPhotoWithoutDriversLicense();
            String text = "Updated successfully";
            snackBar(text);
        }
        else if(uri1 != null && uri2 == null)
        {
            uploadPhotoWithoutNBIClearance();
            String text = "Updated successfully";
            snackBar(text);
        }
        else if (uri1 != null && uri2 != null)
        {
            updatePhoto();
            String text = "Updated successfully";
            snackBar(text);
        }
        else
        {
            String text = "Choose an image";
            snackBar(text);
        }
    }

    public void updatePhoto()
    {
        progressDialog.dismiss();
        if(uri1 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("tpa_documents")
                    .child(currentUser.getUid()+"/"+"driversLicense");
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
                                            .getReference("User_TPA_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("driverLicense").setValue(uriString);

                                    if(uri2 != null)
                                    {
                                        StorageReference storageReference
                                                = FirebaseStorage.getInstance()
                                                .getReference("tpa_documents")
                                                .child(currentUser.getUid()+"/"+"NBI_clearance");
                                        storageReference.putFile(uri2)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri2) {
                                                                String uriString2 = uri2.toString();
                                                                FirebaseDatabase.getInstance()
                                                                        .getReference("User_TPA_Docs_File")
                                                                        .child(currentUser.getUid())
                                                                        .child("NBI_clearance").setValue(uriString2);

                                                                TPAAccountSettingFragment additem = new TPAAccountSettingFragment();
                                                                AppCompatActivity activity = (AppCompatActivity)getContext();
                                                                activity.getSupportFragmentManager()
                                                                        .beginTransaction()
                                                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                                        .replace(R.id.fragment_container, additem)
                                                                        .addToBackStack(null)
                                                                        .commit();
                                                            }
                                                        });
                                                    }
                                                });

                                    }
                                }
                            });
                        }
                    });

        }
    }
    public void uploadPhotoWithoutDriversLicense()
    {
        progressDialog.dismiss();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_TPA_Docs_File").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nbiurl = dataSnapshot.child("NBI_clearance").getValue(String.class);
                driverurl = dataSnapshot.child("driverLicense").getValue(String.class);

                FirebaseDatabase.getInstance()
                        .getReference("User_TPA_Docs_File")
                        .child(currentUser.getUid())
                        .child("driverLicense").setValue(driverurl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(uri2 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("tpa_documents")
                    .child(currentUser.getUid()+"/"+"NBI_clearance");
            storageReference.putFile(uri2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri2) {
                                    String uriString2 = uri2.toString();
                                    FirebaseDatabase.getInstance()
                                            .getReference("User_TPA_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("NBI_clearance").setValue(uriString2);

                                    TPAAccountSettingFragment additem = new TPAAccountSettingFragment();
                                    AppCompatActivity activity = (AppCompatActivity)getContext();
                                    activity.getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                            .replace(R.id.fragment_container, additem)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            });
                        }
                    });
        }
    }
    public void uploadPhotoWithoutNBIClearance()
    {
        progressDialog.dismiss();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_TPA_Docs_File").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nbiurl = dataSnapshot.child("NBI_clearance").getValue(String.class);
                driverurl = dataSnapshot.child("driverLicense").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(uri1 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("tpa_documents")
                    .child(currentUser.getUid()+"/"+"driversLicense");
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
                                            .getReference("User_TPA_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("driverLicense").setValue(uriString);

                                    FirebaseDatabase.getInstance()
                                            .getReference("User_TPA_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("NBI_clearance").setValue(nbiurl);

                                    TPAAccountSettingFragment additem = new TPAAccountSettingFragment();
                                    AppCompatActivity activity = (AppCompatActivity)getContext();
                                    activity.getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                            .replace(R.id.fragment_container, additem)
                                            .addToBackStack(null)
                                            .commit();
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
                isClick2=false;
                isClick1=true;
                openGallery();
                break;
            case R.id.permitButtonUD2:
                isClick1=false;
                isClick2=true;
                openGallery();
                break;

            case R.id.updateButtonUD:
                getInput();
                break;
        }
    }
    public void snackBar(String text){
        View parentLayout = getActivity().findViewById(android.R.id.content);
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
