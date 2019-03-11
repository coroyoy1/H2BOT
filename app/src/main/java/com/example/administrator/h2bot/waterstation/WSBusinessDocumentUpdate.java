package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
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

public class WSBusinessDocumentUpdate extends Fragment implements View.OnClickListener{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    Button button1, button2, button3, button4, button5, button6, updateDocummentButton;
    Spinner startSpinner, endSpinner;
    EditText permitString;

    Uri uri1,uri2,uri3,uri4,uri5,uri6;

    boolean isClick1=false, isClick2=false, isClick3=false, isClick4=false, isClick5=false, isClick6=false;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_merchant_ws_updatedocument, container, false);

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

        startSpinner= view.findViewById(R.id.startSpinner);
        endSpinner= view.findViewById(R.id.endSpinner);

        String[] arraySpinner = new String[]{
                "AM","PM"
        };
        String[] arraySpinner2 = new String[]{
                "PM","AM"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter2);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            if(isClick1)
            {
                uri1 = data.getData();
                Picasso.get().load(uri1).into(imageView1);
            }
            if(isClick2)
            {
                uri2 = data.getData();
                Picasso.get().load(uri2).into(imageView2);
            }
            if(isClick3)
            {
                uri3 = data.getData();
                Picasso.get().load(uri3).into(imageView3);
            }
            if(isClick4)
            {
                uri4 = data.getData();
                Picasso.get().load(uri4).into(imageView4);
            }
            if(isClick5)
            {
                uri5 = data.getData();
                Picasso.get().load(uri5).into(imageView5);
            }
            if(isClick6)
            {
                uri6 = data.getData();
                Picasso.get().load(uri6).into(imageView6);
            }

        }
        else
        {
            showMessage("Choose an image");
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST);
    }

    public void getInput()
    {
        if(uri1 == null && uri2 == null && uri3 == null && uri4 == null && uri5 == null && uri6 == null)
        {
            showMessage("Please an image before you proceed");
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
                    .getReference("users_documents")
                    .child(currentUser.getUid()+"/"+"business_permit_picture");
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
                                            .getReference("User_WS_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_business_permit").setValue(uriString);
                                }
                            });
                        }
                    });

        }
        if(uri2 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("users_documents")
                    .child(currentUser.getUid()+"/"+"station_sanitary_permit_picture");
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
                                            .getReference("User_WS_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_sanitary_permit").setValue(uriString);
                                }
                            });
                        }
                    });

        }
        if(uri3 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("users_documents")
                    .child(currentUser.getUid()+"/"+"station_fire_protection_picture");
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
                                            .getReference("User_WS_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_fire_protection").setValue(uriString);
                                }
                            });
                        }
                    });

        }
        if(uri4 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("users_documents")
                    .child(currentUser.getUid()+"/"+"station_real_property_taxes_picture");
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
                                            .getReference("User_WS_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_real_property_taxes").setValue(uriString);
                                }
                            });
                        }
                    });

        }
        if(uri5 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("users_documents")
                    .child(currentUser.getUid()+"/"+"station_occupancy_permit_picture");
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
                                            .getReference("User_WS_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_occupancy_permit").setValue(uriString);
                                }
                            });
                        }
                    });

        }
        if(uri6 != null)
        {
            StorageReference storageReference
                    = FirebaseStorage.getInstance()
                    .getReference("users_documents")
                    .child(currentUser.getUid()+"/"+"station_physico_chem_permit_picture");
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
                                            .getReference("User_WS_Docs_File")
                                            .child(currentUser.getUid())
                                            .child("station_physico_chem_permit").setValue(uriString)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            UserWSBusinessInfoFile userWSBusinessInfoFile = new UserWSBusinessInfoFile();
                                            userWSBusinessInfoFile.setBusiness_status("inactive");
                                            DatabaseReference database1
                                                    = FirebaseDatabase
                                                    .getInstance()
                                                    .getReference("User_WS_Business_Info_File")
                                                    .child(currentUser.getUid());
                                            database1.setValue(userWSBusinessInfoFile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    showMessage("All requirement are successfully updated!");
                                                    progressDialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    showMessage("Unsuccessful updating the image");
                                                    progressDialog.dismiss();
                                                }
                                            });

                                        }
                                    });
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
                isClick2=false;
                isClick3=false;
                isClick4=false;
                isClick5=false;
                isClick6=false;
                openGallery();
                break;
            case R.id.permitButtonUD2:
                isClick2=true;
                isClick1=false;
                isClick3=false;
                isClick4=false;
                isClick5=false;
                isClick6=false;
                openGallery();
                break;
            case R.id.permitButtonUD3:
                isClick3=true;
                isClick2=false;
                isClick1=false;
                isClick4=false;
                isClick5=false;
                isClick6=false;
                openGallery();
                break;
            case R.id.permitButtonUD4:
                isClick4=true;
                isClick2=false;
                isClick3=false;
                isClick1=false;
                isClick5=false;
                isClick6=false;
                openGallery();
                break;
            case R.id.permitButtonUD5:
                isClick5=true;
                isClick2=false;
                isClick3=false;
                isClick4=false;
                isClick1=false;
                isClick6=false;
                openGallery();
                break;
            case R.id.permitButtonUD6:
                isClick6=true;
                isClick2=false;
                isClick3=false;
                isClick4=false;
                isClick5=false;
                isClick1=false;
                openGallery();
                break;
            case R.id.updateButtonUD:
                getInput();
                break;
        }
    }
}
