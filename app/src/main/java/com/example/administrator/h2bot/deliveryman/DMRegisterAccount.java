package com.example.administrator.h2bot.deliveryman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.RegisterActivity;
import com.example.administrator.h2bot.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class DMRegisterAccount extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText firstNameDM, lastNameDM, addressDM, contactNoDM, emailDM, passwordDM, confirmPassDM;
    Button registerDM, addPhotoBDM, addDocumentPhoto;
    CircleImageView imageView;
    ImageView imageDocument;
    Uri uri, uriv;
    Boolean fClick=false, eClick=false;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String GetAuth;

    StorageTask uploadTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_dm_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("user_document");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firstNameDM = view.findViewById(R.id.RegisterFullNameDM);
        lastNameDM = view.findViewById(R.id.RegisterLastNameDM);
        contactNoDM = view.findViewById(R.id.RegisterContactDM);
        emailDM = view.findViewById(R.id.RegisterEmailAddressDM);
        passwordDM = view.findViewById(R.id.RegisterPasswordDM);
        confirmPassDM = view.findViewById(R.id.ConfirmPasswordDM);
        addressDM = view.findViewById(R.id.RegisterAddressDM);
        addDocumentPhoto = view.findViewById(R.id.addDocumentButton);
        imageDocument = view.findViewById(R.id.documentImage);

        addPhotoBDM = view.findViewById(R.id.addPhotoDM);
        registerDM = view.findViewById(R.id.RegisterSignUpDM);
        imageView = view.findViewById(R.id.imageDM);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);


        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            GetAuth = bundle.getString("AuthenFirebase");
        }

        addDocumentPhoto.setOnClickListener(this);
        addPhotoBDM.setOnClickListener(this);
        registerDM.setOnClickListener(this);
        return view;
    }

    public void registerDeliveryMan()
    {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailDM.getText().toString(), passwordDM.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                       String userd = FirebaseAuth.getInstance().getUid();

                       storageReference.child(userd)
                               .putFile(uri)
                               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                               result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                        String profilePic = uri.toString();
                                        UserFile userFile = new UserFile(
                                                userd,
                                                profilePic,
                                                firstNameDM.getText().toString(),
                                                lastNameDM.getText().toString(),
                                                contactNoDM.getText().toString(),
                                                addressDM.getText().toString(),
                                                "Delivery Man",
                                                "active"
                                        );
                                        databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                        databaseReference.child(userd).setValue(userFile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    showMessages("Successfully Registered");
                                                    progressDialog.dismiss();
                                                    storageReference.child(userd)
                                                            .putFile(uriv)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    Task<Uri> resultOn = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                                    resultOn.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            String docDM = uri.toString();
                                                                            UserAccountFile userAccountFile = new UserAccountFile(
                                                                                    userd,
                                                                                    emailDM.getText().toString(),
                                                                                    passwordDM.getText().toString(),
                                                                                    "active"
                                                                            );
                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                            databaseReference1.child(userd).setValue(userAccountFile)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        UserWSDMFile userWSDMFile = new UserWSDMFile(
                                                                                                GetAuth,
                                                                                                userd,
                                                                                                docDM
                                                                                        );
                                                                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
                                                                                        databaseReference2.child(userd).setValue(userWSDMFile)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    showMessages("Successfully Added");
                                                                                                    progressDialog.dismiss();
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    showMessages("Fail to submit");
                                                                                                    progressDialog.dismiss();
                                                                                                }
                                                                                            });
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        showMessages("Fail to submit");
                                                                                        progressDialog.dismiss();
                                                                                    }
                                                                                });
                                                                        }
                                                                    })
                                                                     .addOnFailureListener(new OnFailureListener() {
                                                                         @Override
                                                                         public void onFailure(@NonNull Exception e) {
                                                                            showMessages("Fail to submit");
                                                                            progressDialog.dismiss();
                                                                         }
                                                                     });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    showMessages("Fail to submit");
                                                                    progressDialog.dismiss();
                                                                }
                                                            });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    showMessages("Error to save, Please check internet");
                                                    progressDialog.dismiss();
                                                }
                                            });

                                    }
                               })
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       showMessages("Error to submit, Please check internet connection");
                                       progressDialog.dismiss();
                                   }
                               });
                           }
                       });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessages("Email already exists");
                        progressDialog.dismiss();
                    }
                });
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


    public void GettingData()
    {
        final String firstNameString = firstNameDM.getText().toString();
        final String lastNameString = lastNameDM.getText().toString();
        final String contactString = contactNoDM.getText().toString();
        final String emailString = emailDM.getText().toString();
        final String passwordString = passwordDM.getText().toString();
        final String confirmString = confirmPassDM.getText().toString();
        final String addressString = addressDM.getText().toString();



        //final String uriString = uri.toString();

//        Bundle args = new Bundle();
//        args.putString("DMFirstName", firstNameString);

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
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null && data.getData() != null)
        {
            uri = data.getData(); uriv = data.getData();
            if(fClick)
            {
                Picasso.get().load(uri).into(imageView);
            }
            if(eClick)
            {
                Picasso.get().load(uriv).into(imageDocument);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addPhotoDM:
                fClick = true; eClick=false;
                openGallery();
                break;
            case R.id.addDocumentButton:
                fClick=false; eClick=true;
                openGallery();
                break;
            case R.id.RegisterSignUpDM:
                registerDeliveryMan();
//                GettingData();

//                DMRegisterDocument dmRegisterDocument = new DMRegisterDocument();
//                AppCompatActivity activity = (AppCompatActivity)v.getContext();
//                activity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .replace(R.id.fragment_container_ws, dmRegisterDocument)
//                        .addToBackStack(null)
//                        .commit();
                break;
        }
    }
}
