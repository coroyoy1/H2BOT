package com.example.administrator.h2bot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Button addPhoto, signUp;
    ImageView imageView;

    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri uri;

    Spinner spinnerRegister;
    EditText fullNameRegister, ageRegister, addressRegister, contactRegister, emailRegister, passwordRegister;
    ProgressBar loadingProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        fullNameRegister = (EditText) findViewById(R.id.RegisterFullName);
        ageRegister = (EditText) findViewById(R.id.RegisterAge);
        addressRegister = (EditText) findViewById(R.id.RegisterAddress);
        contactRegister = (EditText) findViewById(R.id.RegisterContact);
        emailRegister = (EditText) findViewById(R.id.RegisterEmailAddress);
        passwordRegister = (EditText) findViewById(R.id.RegisterPassword);

        loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        spinnerRegister = (Spinner)findViewById(R.id.RegisterSpinner);

        loadingProgressBar.setVisibility(View.INVISIBLE);

        imageView = (ImageView)findViewById(R.id.RegisterPhoto);
        addPhoto = (Button) findViewById(R.id.addPhotoRegister);
        signUp = (Button) findViewById(R.id.RegisterSignUp);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                signUp.setVisibility(View.INVISIBLE);
                String spinnerString = spinnerRegister.getSelectedItem().toString();
                String fullnameString = fullNameRegister.getText().toString();
                String ageString = ageRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactString = contactRegister.getText().toString();
                String emailString = emailRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();


                if(passwordString.isEmpty() || fullnameString.isEmpty() || ageString.isEmpty() || addressString.isEmpty() || contactString.isEmpty() || emailString.isEmpty())
                {
                    showMessage("Please fill up the requirements!");
                    signUp.setVisibility(View.VISIBLE);
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    CreateAccountUser(passwordString, spinnerString, fullnameString, ageString, addressString, contactString, emailString);
                }
            }
        });
    }

    private void CreateAccountUser(final String passwordString, final String spinnerString, final String fullnameString, final String ageString, final String addressString, final String contactString, final String emailString)
    {
        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Users user = new Users(spinnerString, fullnameString, emailString, ageString, addressString, contactString, passwordString);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful()) {
                                        showMessage("Successfully Register");
                                    }
                                    else {
                                        showMessage("Error to Register");
                                    }
                                }
                            });
                            updateUserInfo(spinnerString, fullnameString, uri, mAuth.getCurrentUser());
                        }
                        else
                        {
                            showMessage("Failed to register" + task.getException().getMessage());
                            signUp.setVisibility(View.VISIBLE);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String spinnerString, final String fullnameString, Uri uri, final FirebaseUser currentUser)
    {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(uri.getLastPathSegment());
        imageFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullnameString)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            passToNextActivity();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void passToNextActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
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

                uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }
}
