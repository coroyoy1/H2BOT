package com.example.administrator.h2bot.deliveryman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSDMFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class DMRegisterAccount extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText firstNameDM, lastNameDM, addressDM, contactNoDM, emailDM, passwordDM, confirmPassDM, licenseIdDM;
    FirebaseAnalytics firebaseAnalytics;
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
    String device_token_id;

    String emailPass, passPass;
    double lat, lng;

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
        licenseIdDM = view.findViewById(R.id.licenseTextDM);
        licenseIdDM.setVisibility(View.GONE);

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
        DatabaseReference databaseReferenceThis = FirebaseDatabase.getInstance().getReference("User_Account_File").child(GetAuth);
        databaseReferenceThis.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                    if (userAccountFile != null)
                    {
                        emailPass = userAccountFile.getUser_email_address();
                        passPass = userAccountFile.getUser_password();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Error to intent");
            }
        });

        addDocumentPhoto.setOnClickListener(this);
        addPhotoBDM.setOnClickListener(this);
        registerDM.setOnClickListener(this);
        return view;
    }

    private void performLogin(String emailString, String passwordString)
    {
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showMessages("Success Login");
                        WSDMFragment wsdmFragment = new WSDMFragment();
                        AppCompatActivity activity = (AppCompatActivity)getContext();
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(R.id.fragment_container_ws, wsdmFragment)
                                .addToBackStack(null)
                                .commit();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessages("Fail to login");
                    }
                });
    }


    public void getStationParent(String childDeliveryMan)
    {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User_File");
        databaseReference2.child(firebaseUser.getUid()).child("station_parent").setValue(GetAuth)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessages("Successfully Added");
                        performLogin(emailPass, passPass);
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

    public void registerDeliveryMan()
    {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailDM.getText().toString(), passwordDM.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                       String userd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                       FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                       currentUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                           @Override
                           public void onSuccess(GetTokenResult getTokenResult) {
                               device_token_id = getTokenResult.getToken();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
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
                                                addressDM.getText().toString(),
                                                contactNoDM.getText().toString(),
                                                "Delivery Man",
                                                "active"
                                        );
                                        databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                        databaseReference.child(userd).setValue(userFile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("users_documents");
                                                    storageReference1.child(userd)
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
                                                                                    device_token_id,
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
                                                                                        databaseReference2.child(GetAuth).child(userd).setValue(userWSDMFile)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    //showMessages("Successfully Added");
                                                                                                    getLocationSetter();
                                                                                                    //getStationParent(userd);
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
    private void getLocationSetter()
    {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = addressDM.getText().toString();

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(FirebaseAuth.getInstance().getCurrentUser().getUid(), getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessages("Successfully registered");
                            getStationParent(firebaseUser.getUid());
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //showMessage("Error to get location");
                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
            if(fClick)
            {
                uri = data.getData();
                Picasso.get().load(uri).into(imageView);
            }

            if(eClick)
            {
                uriv = data.getData();
                Bitmap bitmap = null;
                try
                {

                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriv);
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder sb= new StringBuilder();

                    for(int ctr=0;ctr<items.size();ctr++)
                    {
                        TextBlock myItem = items.valueAt(ctr);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    if(sb.toString().trim().toLowerCase().contains("Department of Transportation".toLowerCase()))
                    {
                        if (sb.toString().toLowerCase().contains(firstNameDM.getText().toString()))
                        {
                            if (sb.toString().toLowerCase().contains(lastNameDM.getText().toString()))
                            {
                                Picasso.get().load(uriv).into(imageDocument);
                                Toast.makeText(getActivity(), "Valid business permit", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                imageDocument.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(getActivity(), "Invalid driver's license", Toast.LENGTH_SHORT).show();
                                uriv = Uri.parse("");
                            }
                        }
                        else
                        {
                            imageDocument.setImageResource(R.drawable.ic_image_black_24dp);
                            Toast.makeText(getActivity(), "Invalid driver's license", Toast.LENGTH_SHORT).show();
                            uriv = Uri.parse("");
                        }
                    }
                    else{
                        imageDocument.setImageResource(R.drawable.ic_image_black_24dp);
                        Toast.makeText(getActivity(), "Invalid driver's license", Toast.LENGTH_SHORT).show();
                        uriv = Uri.parse("");
                    }
//                    if (!licenseIdDM.getText().toString().isEmpty() || !licenseIdDM.getText().toString().equals(""))
//                    {
//                        String license = licenseIdDM.getText().toString();
//                    }
//                    else
//                    {
//                        showMessages("License number does not set");
//                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
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
                break;
        }
    }
}
