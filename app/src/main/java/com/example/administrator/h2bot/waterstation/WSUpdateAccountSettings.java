package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserAccountFile2;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class WSUpdateAccountSettings extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText firstNameWU, lastNameWU, addressWU, contactNoWU, emailAddressWU, passwordWU, confirmPasswordWU, oldPassword;
    private Button updateButton, addPhotobutton;
    private CircleImageView imageView;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    String device_token_id;
    String passwordString1;
    String passwordString2;
    String password;String password2;
    String uriString2;
    String passwordString;
    String confirmPasswordString;
    Uri uri;
    double lat;
    double lng;
    AuthCredential credential;
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
        oldPassword = view.findViewById(R.id.oldPassword);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        updateButton.setOnClickListener(this);
        addPhotobutton.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        DatabaseReference retrieve = FirebaseDatabase.getInstance().getReference("User_Account_File").child(firebaseUser.getUid());
        DatabaseReference retrieve2 = FirebaseDatabase.getInstance().getReference("User_File").child(firebaseUser.getUid());
        retrieve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emailAddressWU.setText(dataSnapshot.child("user_email_address").getValue(String.class));
                password2 = (dataSnapshot.child("user_password").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieve2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addressWU.setText(dataSnapshot.child("user_address").getValue(String.class));
                firstNameWU.setText(dataSnapshot.child("user_firtname").getValue(String.class));
                lastNameWU.setText(dataSnapshot.child("user_firtname").getValue(String.class));
                contactNoWU.setText(dataSnapshot.child("user_firtname").getValue(String.class));
                Picasso.get()
                        .load(dataSnapshot.child("user_uri").getValue(String.class))
                        .fit()
                        .centerCrop()
                        .into(imageView);
                Bitmap bm = BitmapFactory.decodeFile(dataSnapshot.child("user_uri").getValue(String.class));
                imageView.setImageBitmap(bm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    public void updateDataAccount() {
        String firstNameString = firstNameWU.getText().toString();
        String lastNameString = lastNameWU.getText().toString();
        String addressString = addressWU.getText().toString();
        String contactNoString = contactNoWU.getText().toString();
        String emailAddressString = emailAddressWU.getText().toString();
         password = oldPassword.getText().toString();
         passwordString = passwordWU.getText().toString();
         confirmPasswordString = confirmPasswordWU.getText().toString();

        if (firstNameString.isEmpty()
                || lastNameString.isEmpty()
                || addressString.isEmpty()
                || contactNoString.isEmpty()
                || emailAddressString.isEmpty()
                ) {
            showMessages("Please fill up all the fields before you update!");
            return;
        } else {
            DatabaseReference passwordreference = FirebaseDatabase.getInstance().getReference("User_Account_File").child(firebaseUser.getUid());
            passwordreference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("passwordnako", password + "=" + dataSnapshot.child("user_password").getValue());
                    if (!password.isEmpty() && !passwordString.isEmpty() && !confirmPasswordString.isEmpty())
                    {
                        if(passwordString.equals(confirmPasswordString))
                        {
                            if (!password.isEmpty() && password.equals(dataSnapshot.child("user_password").getValue(String.class))) {
                                Log.d("Passsnaki","Hi");
                                thisGetUpdateData(firstNameString, lastNameString, addressString, contactNoString,
                                        emailAddressString, passwordString, confirmPasswordString);
                            } else {
                                showMessage("Incorrect old password");
                            }
                        }
                        else {
                            showMessage("New password and confirm password does not match!");
                        }
                        if(password.isEmpty() || passwordString.isEmpty() || confirmPasswordString.isEmpty())
                        {
                            showMessage("Please fill all the password fields if you want to change your password!");
                        }
                    }
                    if(password.isEmpty() && passwordString.isEmpty() && confirmPasswordString.isEmpty())
                    {
                        Log.d("Passsnako","Hi");
                        Log.d("Passs",""+password2);
                        Log.d("Passs",""+passwordString);
                        Log.d("Passs",""+confirmPasswordString);
                        thisGetUpdateData2(firstNameString, lastNameString, addressString, contactNoString,
                                emailAddressString, password2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.get().load(uri).into(imageView);
        } else {
            showMessages("Choose an image");
        }
    }

    private void getLocationSetter() {
        Geocoder coder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = addressWU.getText().toString();

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
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            successMessages();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("Failed to get location");
                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    private void thisGetUpdateData(String firstNameString, String lastNameString, String addressString, String contactNoString, String emailAddressString, String passwordString, String confirmPasswordString) {
        progressDialog.show();
        Log.d("pass",""+password);
        Log.d("pass1",""+passwordString);
        Log.d("pass2",""+confirmPasswordString);
        DatabaseReference retrieve4 = FirebaseDatabase.getInstance().getReference("User_Account_File").child(firebaseUser.getUid());
        retrieve4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 passwordString1 = dataSnapshot.child("user_password").getValue(String.class);
                 passwordString2 = dataSnapshot.child("user_password").getValue(String.class);
                 Log.d("p1",""+passwordString1);
                Log.d("p2",""+passwordString2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(passwordString.isEmpty() || password.isEmpty() || confirmPasswordString.isEmpty())
        {
             credential = EmailAuthProvider.getCredential(emailAddressString, passwordString1);
        }
        else {
            credential = EmailAuthProvider.getCredential(emailAddressString, passwordWU.getText().toString());
        }
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseUser.updateEmail(emailAddressString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                firebaseUser.updatePassword(passwordString)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
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
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(passwordString.isEmpty()||password.isEmpty()||confirmPasswordString.isEmpty())
                                                                                        {
                                                                                            Log.d("Gago","Gago");
                                                                                            UserAccountFile2 userAccountFile = new UserAccountFile2(
                                                                                                    firebaseUser.getUid(),
                                                                                                    emailAddressString,
                                                                                                    passwordString1,
                                                                                                    "active");
                                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                            databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            Log.d("Gaga","Gaga");
                                                                                                            getLocationSetter();
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            showMessages("Failed to save the user account");
                                                                                                            progressDialog.dismiss();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                        else
                                                                                        {

                                                                                            UserAccountFile2 userAccountFile = new UserAccountFile2(
                                                                                                    firebaseUser.getUid(),
                                                                                                    emailAddressString,
                                                                                                    passwordString,
                                                                                                    "active");
                                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                            databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            getLocationSetter();
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            showMessages("Failed to save the user account");
                                                                                                            progressDialog.dismiss();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.d("Gogo","Gogo");
                                                                                        showMessages(e.getMessage());
                                                                                        progressDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                showMessages("Choose an image");
                                                                                progressDialog.dismiss();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                showMessages("Image error");
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessages("Password error");
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessages("Failed to update the information");
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }
    private void thisGetUpdateData2(String firstNameString, String lastNameString, String addressString, String contactNoString, String emailAddressString, String password2) {
        progressDialog.show();
        DatabaseReference retrieve4 = FirebaseDatabase.getInstance().getReference("User_Account_File").child(firebaseUser.getUid());
        retrieve4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                passwordString1 = dataSnapshot.child("user_password").getValue(String.class);
                passwordString2 = dataSnapshot.child("user_password").getValue(String.class);
                Log.d("p1",""+passwordString1);
                Log.d("p2",""+passwordString2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(password.isEmpty()||passwordString.isEmpty()||confirmPasswordString.isEmpty())
        {
            credential = EmailAuthProvider.getCredential(emailAddressString, password2);
        }
        else
        {
            credential = EmailAuthProvider.getCredential(emailAddressString, passwordString);
        }
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseUser.updateEmail(emailAddressString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                firebaseUser.updatePassword(password2)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("users_photo");
                                                storageReference.putFile(uri)
                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                Log.d("Gaga","Gaga");
                                                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        Log.d("Gago","Gago");
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
                                                                                        if(passwordString.isEmpty()||password.isEmpty()||confirmPasswordString.isEmpty())
                                                                                        {
                                                                                            Log.d("Gagi","Gagi");
                                                                                            showMessages("Updated successfully");
                                                                                            UserAccountFile2 userAccountFile = new UserAccountFile2(
                                                                                                    firebaseUser.getUid(),
                                                                                                    emailAddressString,
                                                                                                    password2,
                                                                                                    "active");
                                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                            databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            getLocationSetter();
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            showMessages("Failed to save the user account");
                                                                                                            progressDialog.dismiss();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            Log.d("Gagu","Gagu");
                                                                                            UserAccountFile2 userAccountFile = new UserAccountFile2(
                                                                                                    firebaseUser.getUid(),
                                                                                                    emailAddressString,
                                                                                                    passwordString,
                                                                                                    "active");
                                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                            databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            Log.d("Giga","Giga");
                                                                                                            getLocationSetter();
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            showMessages("Failed to save the user account");
                                                                                                            progressDialog.dismiss();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.d("Gigi","Gigi");
                                                                                        showMessages("Failed to save the data");
                                                                                        progressDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                showMessages("Choose an image");
                                                                                progressDialog.dismiss();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                showMessages("Image error");
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessages("Password error 2");
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessages("Failed to update the information");
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }

    private void successMessages() {
        showMessages("Updated successfully");
        WSAccountSettingsFragment wsdmFragment = new WSAccountSettingsFragment();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_ws, wsdmFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
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
