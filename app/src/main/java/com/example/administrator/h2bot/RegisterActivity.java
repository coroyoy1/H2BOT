package com.example.administrator.h2bot;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Button addPhoto, signUp;
    ImageView imageView;

    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri imageUri;
    Boolean clickable=false;

    Spinner spinnerRegister;
    EditText fullNameRegister, ageRegister, addressRegister, contactRegister, emailRegister, passwordRegister;
    ProgressBar loadingProgressBar;
    ProgressDialog progressDialog;
    private StorageTask mUploadTask;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        storageReference = FirebaseStorage.getInstance().getReference("users_profile_photo");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        mAuth = FirebaseAuth.getInstance();

        fullNameRegister = (EditText) findViewById(R.id.RegisterFullName);
        ageRegister = (EditText) findViewById(R.id.RegisterAge);
        addressRegister = (EditText) findViewById(R.id.RegisterAddress);
        contactRegister = (EditText) findViewById(R.id.RegisterContact);
        emailRegister = (EditText) findViewById(R.id.RegisterEmailAddress);
        passwordRegister = (EditText) findViewById(R.id.RegisterPassword);

        loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        spinnerRegister = (Spinner)findViewById(R.id.RegisterSpinner);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        loadingProgressBar.setVisibility(View.INVISIBLE);

        imageView = (ImageView)findViewById(R.id.RegisterPhoto);
        addPhoto = (Button) findViewById(R.id.addPhotoRegister);
        signUp = (Button) findViewById(R.id.RegisterSignUp);

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
                if(!(RegisterActivity.this).isFinishing())
                {
                    progressDialog.show();
                }
                String spinnerString = spinnerRegister.getSelectedItem().toString();
                String fullnameString = fullNameRegister.getText().toString();
                String ageString = ageRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactString = contactRegister.getText().toString();
                String emailString = emailRegister.getText().toString().trim();
                String passwordString = passwordRegister.getText().toString().trim();

                if(passwordString.isEmpty() || fullnameString.isEmpty() || ageString.isEmpty() || addressString.isEmpty() || contactString.isEmpty() || emailString.isEmpty())
                {
                    showMessage("Some fields are missing");
                    progressDialog.dismiss();
                }
                else
                {
                    CreateAccount();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void CreateAccount()
    {
        mAuth.createUserWithEmailAndPassword(emailRegister.getText().toString().trim(),passwordRegister.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 500);
                        StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
                        mUploadTask = fileReference.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Random random = new Random();
                                                int number = random.nextInt(1000000000) + 1;
                                                String stringNumber = Integer.toString(number);
                                                String stringPhoto = uri.toString();
                                                String userType = spinnerRegister.getSelectedItem().toString();
                                                if(userType.equals("Customer"))
                                                {
                                                    Users user = new Users(userType, fullNameRegister.getText().toString(), emailRegister.getText().toString().trim(), ageRegister.getText().toString(), addressRegister.getText().toString(), contactRegister.getText().toString(), passwordRegister.getText().toString().trim(), "none", "active", stringPhoto);
                                                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        showMessage("Successfully Register");
                                                                        progressDialog.dismiss();
                                                                        finish();
                                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    }
                                                                    else {
                                                                        showMessage("Error to Register");
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else if(userType.equals("Station Owner"))
                                                {
                                                    Users user = new Users(userType, fullNameRegister.getText().toString(), emailRegister.getText().toString().trim(), ageRegister.getText().toString(), addressRegister.getText().toString(), contactRegister.getText().toString(), passwordRegister.getText().toString().trim(), stringNumber, "inactive", stringPhoto);
                                                    String uploadId = databaseReference.push().getKey();
                                                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        showMessage("Successfully Register");
                                                                        progressDialog.dismiss();
                                                                        finish();
                                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    }
                                                                    else {
                                                                        showMessage("Error to Register");
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else if(userType.equals("Delivery Man"))
                                                {
                                                    Users user = new Users(userType, fullNameRegister.getText().toString(), emailRegister.getText().toString().trim(), ageRegister.getText().toString(), addressRegister.getText().toString(), contactRegister.getText().toString(), passwordRegister.getText().toString().trim(), "none", "inactive", stringPhoto);
                                                    String uploadId = databaseReference.push().getKey();
                                                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        showMessage("Successfully Register");
                                                                        progressDialog.dismiss();
                                                                        finish();
                                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    }
                                                                    else {
                                                                        showMessage("Error to Register");
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else if(userType.equals("Water Dealer"))
                                                {
                                                    Users user = new Users(userType, fullNameRegister.getText().toString(), emailRegister.getText().toString().trim(), ageRegister.getText().toString(), addressRegister.getText().toString(), contactRegister.getText().toString(), passwordRegister.getText().toString().trim(), "none", "inactive", stringPhoto);
                                                    String uploadId = databaseReference.push().getKey();
                                                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        showMessage("Successfully Register");
                                                                        progressDialog.dismiss();
                                                                        finish();
                                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    }
                                                                    else {
                                                                        showMessage("Error to Register");
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else if(userType.equals("Third Party Affiliate"))
                                                {
                                                    Users user = new Users(userType, fullNameRegister.getText().toString(), emailRegister.getText().toString().trim(), ageRegister.getText().toString(), addressRegister.getText().toString(), contactRegister.getText().toString(), passwordRegister.getText().toString().trim(), "none", "inactive", stringPhoto);
                                                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        showMessage("Successfully Register");
                                                                        progressDialog.dismiss();
                                                                        finish();
                                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    }
                                                                    else {
                                                                        showMessage("Error to Register");
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else
                                                {
                                                    showMessage("No available users");
                                                    progressDialog.dismiss();
                                                }

                                            }
                                        });
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(imageView);
            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
