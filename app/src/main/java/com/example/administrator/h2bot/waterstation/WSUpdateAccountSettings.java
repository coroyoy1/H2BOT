package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class WSUpdateAccountSettings extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText firstNameWU, lastNameWU, addressWU, contactNoWU, emailAddressWU, passwordWU, confirmPasswordWU;
    private Button updateButton, addPhotobutton;
    private CircleImageView imageView;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    Uri uri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_waterstationaccountsettings, container, false);
        firstNameWU = view.findViewById(R.id.RegisterFullNameUAS);
        lastNameWU = view.findViewById(R.id.RegisterLastNameUAS);
        addressWU = view.findViewById(R.id.RegisterAddressUAS);
        contactNoWU = view.findViewById(R.id.RegisterContactUAS);
        emailAddressWU = view.findViewById(R.id.RegisterEmailAddressUAS);
        passwordWU = view.findViewById(R.id.RegisterPasswordUAS);
        confirmPasswordWU = view.findViewById(R.id.ConfirmPasswordUAS);
        updateButton = view.findViewById(R.id.RegisterSignUpUAS);
        imageView = view.findViewById(R.id.imageUAS);
        addPhotobutton = view.findViewById(R.id.addPhotoUAS);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        updateButton.setOnClickListener(this);
        addPhotobutton.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        return view;
    }

    public void updateDataAccount()
    {
        String firstNameString = firstNameWU.getText().toString();
        String lastNameString = lastNameWU.getText().toString();
        String addressString = addressWU.getText().toString();
        String contactNoString = contactNoWU.getText().toString();
        String emailAddressString = emailAddressWU.getText().toString();
        String passwordString = passwordWU.getText().toString();
        String confirmPasswordString = confirmPasswordWU.getText().toString();

        if(firstNameString.isEmpty()
            && lastNameString.isEmpty()
            && addressString.isEmpty()
            && contactNoString.isEmpty()
            && emailAddressString.isEmpty()
            && passwordString.isEmpty()
            && confirmPasswordString.isEmpty())
        {
            showMessages("Please fill up the data before update!");
        }
        else
        {
            thisGetUpdateData(firstNameString, lastNameString, addressString, contactNoString,
                    emailAddressString,passwordString, confirmPasswordString);
        }
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    public void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            uri = data.getData();
            Picasso.get().load(uri).into(imageView);
        }
        else
        {
            showMessages("Empty image");
        }
    }



    private void thisGetUpdateData(String firstNameString, String lastNameString, String addressString, String contactNoString, String emailAddressString, String passwordString, String confirmPasswordString) {
        progressDialog.show();
        firebaseUser.updateEmail(emailAddressString)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseUser.updatePassword(passwordString)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("users_photo");
                                        storageReference.putFile(uri)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String uriString = uri.toString();
                                                                UserFile userFile = new UserFile(
                                                                        firebaseUser.getUid(),
                                                                        uriString,
                                                                        firstNameString,
                                                                        lastNameString,
                                                                        addressString,
                                                                        contactNoString,
                                                                        "Water Station",
                                                                        "active"
                                                                );
                                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                                                databaseReference.child(firebaseUser.getUid()).setValue(userFile)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                UserAccountFile userAccountFile = new UserAccountFile(
                                                                                        firebaseUser.getUid(),
                                                                                        emailAddressString,
                                                                                        passwordString,
                                                                                        "active"
                                                                                );
                                                                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                successMessages();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                showMessages("User Account does not successfully save");
                                                                                            }
                                                                                        });

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                showMessages("Data does not saved");
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        showMessages("Image is not selected!");
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessages("Fail to update the information");
                    }
                });
    }

    private void successMessages() {
        showMessages("Successfully Updated");
        WSAccountSettingsFragment wsdmFragment = new WSAccountSettingsFragment();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, wsdmFragment)
                .addToBackStack(null)
                .commit();
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.RegisterSignUpUAS:
                updateDataAccount();
                break;
            case R.id.addPhotoUAS:
                openGallery();
                break;
        }
    }
}