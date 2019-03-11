package com.example.administrator.h2bot;

import com.example.administrator.h2bot.customer.CustomerMainActivity;
import com.example.administrator.h2bot.deliveryman.DeliveryManDocumentActivity;
import com.example.administrator.h2bot.deliveryman.DeliveryManMainActivity;
import com.example.administrator.h2bot.models.*;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.tpaaffiliate.TPAAffiliateMainActivity;
import com.example.administrator.h2bot.waterstation.WaterStationMainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    Button addPhoto, signUp;
    ImageView imageView;

    double lat;
    double lng;


    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri uri;
    Boolean clickable=false;

    EditText firstNameRegister, lastNameRegister, addressRegister, contactRegister, emailRegister, passwordRegister;
    ProgressDialog progressDialog;
    TextView headerTitle;
    FirebaseUser currentUser;
    FirebaseAuth.AuthStateListener mAuthListener;
    String device_token_id;
    String newToken;


    private FirebaseAuth mAuth;
    private String addressLocate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        String s = getIntent().getStringExtra("TextValue");
        headerTitle = findViewById(R.id.headerRegister);
        headerTitle.setText(s);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        firstNameRegister = findViewById(R.id.RegisterFullName);
        lastNameRegister = findViewById(R.id.RegisterLastName);
        addressRegister = findViewById(R.id.RegisterAddress);
        contactRegister = findViewById(R.id.RegisterContact);
        emailRegister = (EditText) findViewById(R.id.RegisterEmailAddress);
        passwordRegister = findViewById(R.id.RegisterPassword);


        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);


        imageView = findViewById(R.id.RegisterPhoto);
        addPhoto = findViewById(R.id.addPhotoRegister);
        signUp = findViewById(R.id.RegisterSignUp);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickable=true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(RegisterActivity.this).isFinishing()) {
                    progressDialog.show();
                }
                String firstNameString = firstNameRegister.getText().toString();
                String lastNameString = lastNameRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactNoString = contactRegister.getText().toString();
                String emailAddressString = emailRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();

                if(passwordString.isEmpty() && firstNameString.isEmpty() && lastNameString.isEmpty() && addressString.isEmpty() && contactNoString.isEmpty() && emailAddressString.isEmpty() && imageView.getDrawable() == null)
                {
                    showMessage("Some fields are missing");
                    progressDialog.dismiss();
                }
                else if(imageView.getDrawable() == null)
                {
                    showMessage("Photo is not yet set!");
                    progressDialog.dismiss();
                }
                else if (contactNoString.length() > 11)
                {
                    showMessage("Contact no. must be maximum of 11 characters");
                    progressDialog.dismiss();
                }
                else
                {
                    CreateAccount(emailAddressString, passwordString);
                }
            }
        });
    }

    private void getLocationSetter()
    {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = addressRegister.getText().toString();

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
                            showMessage("Successfully registered");
                            progressDialog.dismiss();
                            passToNextActivity();
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
        finally {
            showMessage("Error the locate your address, please change again");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                uri = data.getData();

                if(clickable) {
                    Picasso.get().load(uri).into(imageView);
                }
            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }

//    private void CreateAccount(String emailString, String passwordString)
//    {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
//                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if(task.isSuccessful())
//                                        {
//                                            String userType = headerTitle.getText().toString();
//                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                                            String uidString = firebaseUser.getUid();
//
//                                            firebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
//                                                @Override
//                                                public void onSuccess(GetTokenResult getTokenResult) {
//                                                    device_token_id = getTokenResult.getToken();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                            StorageReference mStorage = FirebaseStorage.getInstance().getReference("users_photos").child(uidString);
//                                            mStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                                @Override
//                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                        @Override
//                                                        public void onSuccess(Uri uri) {
//                                                            String stringUri = uri.toString();
//                                                            UserFile userFile;
//                                                            if(userType.equals("Customer"))
//                                                            {
//                                                                userFile = new UserFile(uidString,
//                                                                        stringUri,
//                                                                        firstNameRegister.getText().toString(),
//                                                                        lastNameRegister.getText().toString(),
//                                                                        addressRegister.getText().toString(),
//                                                                        contactRegister.getText().toString(),
//                                                                        userType,
//                                                                        "active");
//                                                            }
//                                                            else
//                                                            {
//                                                                userFile = new UserFile(uidString,
//                                                                        stringUri,
//                                                                        firstNameRegister.getText().toString(),
//                                                                        lastNameRegister.getText().toString(),
//                                                                        addressRegister.getText().toString(),
//                                                                        contactRegister.getText().toString(),
//                                                                        userType,
//                                                                        "inactive");
//                                                            }
//                                                            UserAccountFile userAccountFile = new UserAccountFile(uidString,
//                                                                    emailRegister.getText().toString(),
//                                                                    passwordRegister.getText().toString(),
//                                                                    device_token_id,
//                                                                    "active");
//                                                            FirebaseDatabase.getInstance().getReference("User_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userFile)
//                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                        @Override
//                                                                        public void onSuccess(Void aVoid) {
//                                                                            progressDialog.dismiss();
//                                                                            FirebaseDatabase.getInstance().getReference("User_Account_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccountFile)
//                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                        @Override
//                                                                                        public void onSuccess(Void aVoid) {
//                                                                                            getLocationSetter();
//                                                                                        }
//                                                                                    })
//                                                                                    .addOnFailureListener(new OnFailureListener() {
//                                                                                        @Override
//                                                                                        public void onFailure(@NonNull Exception e) {
//                                                                                            progressDialog.dismiss();
//                                                                                            showMessage("Error, Connection Error");
//                                                                                            progressDialog.dismiss();
//                                                                                        }
//                                                                                    });
//                                                                        }
//                                                                    })
//                                                                    .addOnFailureListener(new OnFailureListener() {
//                                                                        @Override
//                                                                        public void onFailure(@NonNull Exception e) {
//                                                                            progressDialog.dismiss();
//                                                                            showMessage("Error, Connection Error");
//                                                                        }
//                                                                    });
//                                                            // mAuth.signOut();
//                                                        }
//                                                    });
//                                                }
//                                            });
//                                        }
//                                        else
//                                        {
//                                            showMessage("Error to save data");
//                                            progressDialog.dismiss();
//                                        }
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        showMessage("Error to save");
//                                        progressDialog.dismiss();
//                                    }
//                                });
//                        break;
//                    case DialogInterface.BUTTON_NEGATIVE:
//
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Are you sure of your fill-up information?").setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener).show();
//    }

    private void CreateAccount(String emailString, String passwordString)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            String userType = headerTitle.getText().toString();
                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            String uidString = firebaseUser.getUid();
                                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( RegisterActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                                                @Override
                                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                                    newToken = instanceIdResult.getToken();
                                                    Log.d("newToken",newToken);

                                                }
                                            });

                                            firebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                                @Override
                                                public void onSuccess(GetTokenResult getTokenResult) {
                                                    device_token_id = getTokenResult.getToken();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            StorageReference mStorage = FirebaseStorage.getInstance().getReference("users_photos").child(uidString);
                                            mStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String stringUri = uri.toString();
                                                            UserFile userFile;
                                                            if(userType.equals("Customer"))
                                                            {
                                                                userFile = new UserFile(uidString,
                                                                        stringUri,
                                                                        firstNameRegister.getText().toString(),
                                                                        lastNameRegister.getText().toString(),
                                                                        addressRegister.getText().toString(),
                                                                        contactRegister.getText().toString(),
                                                                        userType,
                                                                        "active");
                                                            }
                                                            else
                                                            {
                                                                userFile = new UserFile(uidString,
                                                                        stringUri,
                                                                        firstNameRegister.getText().toString(),
                                                                        lastNameRegister.getText().toString(),
                                                                        addressRegister.getText().toString(),
                                                                        contactRegister.getText().toString(),
                                                                        userType,
                                                                        "inactive");
                                                            }
                                                            UserAccountFile userAccountFile = new UserAccountFile(uidString,
                                                                    emailRegister.getText().toString(),
                                                                    passwordRegister.getText().toString(),
                                                                    newToken,
                                                                    "active");
                                                            FirebaseDatabase.getInstance().getReference("User_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userFile)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            progressDialog.dismiss();
                                                                            FirebaseDatabase.getInstance().getReference("User_Account_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccountFile)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            getLocationSetter();
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            progressDialog.dismiss();
                                                                                            showMessage("Error, Connection Error");
                                                                                            progressDialog.dismiss();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressDialog.dismiss();
                                                                            showMessage("Error, Connection Error");
                                                                        }
                                                                    });
                                                            // mAuth.signOut();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        else
                                        {
                                            showMessage("Error to save data");
                                            progressDialog.dismiss();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage("Error to save");
                                        progressDialog.dismiss();
                                    }
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
    }

    private void passToNextActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
