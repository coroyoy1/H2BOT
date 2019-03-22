package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.waterstation.WSAccountSettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class WPUpdateAccountSettings extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText firstNameWU, lastNameWU, addressWU, contactNoWU, passwordWU, confirmPasswordWU, retypePassword, oldPass;
    private Button updateButton, addPhotobutton, changePassword;
    LinearLayout linearPassword, linearRenewPassword;
    String emailnako;
    private CircleImageView imageView;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser, user;
    ProgressDialog progressDialog;
    String device_token_id;

    Uri uri;
    double lat;
    double lng;
    private String newToken;
    boolean isClick=true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_waterdealeraccountsettings, container, false);

        firstNameWU = view.findViewById(R.id.RegisterFullNameUAS);
        lastNameWU = view.findViewById(R.id.RegisterLastNameUAS);
        addressWU = view.findViewById(R.id.RegisterAddressUAS);
        contactNoWU = view.findViewById(R.id.RegisterContactUAS);
        passwordWU = view.findViewById(R.id.RegisterPasswordUAS);
        confirmPasswordWU = view.findViewById(R.id.ConfirmPasswordUAS);
        updateButton = view.findViewById(R.id.RegisterSignUpUAS);
        imageView = view.findViewById(R.id.imageUAS);
        addPhotobutton = view.findViewById(R.id.addPhotoUAS);
        changePassword = view.findViewById(R.id.changePasswordButton);
        linearPassword = view.findViewById(R.id.linearPasswordCurrent);
        linearRenewPassword = view.findViewById(R.id.linearPasswordUpdate);
        retypePassword = view.findViewById(R.id.RegisterPasswordRetypeUAS);
        oldPass = view.findViewById(R.id.oldPassword);

        changePassword.setTag(0);
        linearRenewPassword.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        user = mAuth.getCurrentUser();

        updateButton.setOnClickListener(this);
        addPhotobutton.setOnClickListener(this);
        changePassword.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        if(mAuth.getCurrentUser() != null)
        {
            RetrieveDataThroughEditText();
        }

        return view;
    }

    public void updateDataAccountSingular()
    {
        String firstNameString = firstNameWU.getText().toString();
        String lastNameString = lastNameWU.getText().toString();
        String addressString = addressWU.getText().toString();
        String contactNoString = contactNoWU.getText().toString();
        String passwordString = retypePassword.getText().toString();

        if(firstNameString.isEmpty()
                && lastNameString.isEmpty()
                && addressString.isEmpty()
                && contactNoString.isEmpty()
                && emailnako.isEmpty()
                && passwordString.isEmpty())
        {
            showMessages("Please fill up all the fields before you update!");
        }
        else
        {
            checkOldPassword();
        }
    }

    public void updateDataAccount()
    {
        String firstNameString = firstNameWU.getText().toString();
        String lastNameString = lastNameWU.getText().toString();
        String addressString = addressWU.getText().toString();
        String contactNoString = contactNoWU.getText().toString();
        String passwordString = passwordWU.getText().toString();
        String confirmPasswordString = confirmPasswordWU.getText().toString();

        if(firstNameString.isEmpty()
            && lastNameString.isEmpty()
            && addressString.isEmpty()
            && emailnako.isEmpty()
            && passwordString.isEmpty()
            && confirmPasswordString.isEmpty())
        {
            showMessages("Please fill up all the fields before you update!");
        }
        else
        {
            updateInfoWithRenewPassword();
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
            showMessages("Choose an image");
        }
    }

    private void getLocationSetter()
    {
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

            Log.d("latlng",getLocateLatitude+","+getLocateLongtitude);
            UserLocationAddress userLocationAddress = new UserLocationAddress(mAuth.getCurrentUser().getUid(), getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("latlng2",getLocateLatitude+","+getLocateLongtitude);
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

    private void RetrieveDataThroughEditText()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
        reference.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserFile userFile = dataSnapshot.getValue(UserFile.class);
                        if(userFile !=  null)
                        {
                            firstNameWU.setText(userFile.getUser_firstname());
                            lastNameWU.setText(userFile.getUser_lastname());
                            addressWU.setText(userFile.getUser_address());
                            emailnako = user.getEmail();
                            contactNoWU.setText(userFile.getUser_phone_no());
                            Picasso.get().load(userFile.getUser_uri()).into(imageView);
                        }
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                        reference1.child(firebaseUser.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                                        if (userAccountFile != null)
                                        {
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        showMessage("Your account does not exists");
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessage("Your account does not exists");
                    }
                });
    }


    private void updateInfoWithRenewPassword()
    {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
            reference.child(firebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                            if (userAccountFile != null)
                            {
                                String getEmail = userAccountFile.getUser_email_address();
                                String getPassword = userAccountFile.getUser_password();

                                if (confirmPasswordWU.getText().toString().equals("") && passwordWU.getText().toString().equals(""))
                                {
                                    showMessage("New password and Confirm password should not be null");
                                    return;
                                }

                                if(oldPass.getText().toString().equals(getPassword))
                                {
                                        if(confirmPasswordWU.getText().toString().equals(passwordWU.getText().toString()))
                                        {
                                            AuthCredential credential = EmailAuthProvider.getCredential(getEmail, getPassword);
                                            user.reauthenticate(credential)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updateEmail(emailnako)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(passwordWU.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                    if (uri == null) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
                        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserFile userFile1 = dataSnapshot.getValue(UserFile.class);
                                String uriSet = userFile1.getUser_uri();
                                UserFile userFile = new UserFile(
                                        user.getUid(),
                                        uriSet,
                                        firstNameWU.getText().toString(),
                                        lastNameWU.getText().toString(),
                                        addressWU.getText().toString(),
                                        contactNoWU.getText().toString(),
                                        "Water Dealer",
                                        "active"
                                );
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                databaseReference.child(user.getUid()).setValue(userFile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                newToken = instanceIdResult.getToken();
                                                UserAccountFile userAccountFile = new UserAccountFile(
                                                        user.getUid(),
                                                        emailnako,
                                                        passwordWU.getText().toString(),
                                                        newToken,
                                                        "active"
                                                );
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                reference.child(user.getUid()).setValue(userAccountFile)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                getLocationSetter();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                showMessage("User does not exists");
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessage("Failed to get token");
                                            }
                                        });
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
                    }
                    else {
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
                                                        user.getUid(),
                                                        uriString,
                                                        firstNameWU.getText().toString(),
                                                        lastNameWU.getText().toString(),
                                                        addressWU.getText().toString(),
                                                        contactNoWU.getText().toString(),
                                                        "Water Dealer",
                                                        "active"
                                                );
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                databaseReference.child(user.getUid()).setValue(userFile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                        .addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
                                                            @Override
                                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                                newToken = instanceIdResult.getToken();
                                                                UserAccountFile userAccountFile = new UserAccountFile(
                                                                        user.getUid(),
                                                                        emailnako,
                                                                        passwordWU.getText().toString(),
                                                                        newToken,
                                                                        "active"
                                                                );
                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                reference.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                getLocationSetter();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                showMessage("User does not exists");
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                showMessage("Failed to get token");
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage("Failed to update image");
                                    }
                                });
                    }
                }
            });
                }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage("Data does not updated");
                                }
                            });
                }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage("Failed to update information");
                                    }
                                });
                    }
                                        else
                                        {
                                            showMessage("New Password and Confirm New Password does not match!");
                                        }
                                }
                                else
                                {
                                    showMessage("Current password does not match");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
    }


    private void thisGetUpdateData(String firstNameString, String lastNameString, String addressString, String contactNoString, String emailnako, String passwordString, String confirmPasswordString) {
        progressDialog.show();
        firebaseUser.updateEmail(emailnako)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    firebaseUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
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
                    firebaseUser.updatePassword(confirmPasswordWU.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(uri == null) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
                                    reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserFile userFile1 = dataSnapshot.getValue(UserFile.class);
                                            String uri = userFile1.getUser_uri();
                                            UserFile userFile = new UserFile(
                                                    firebaseUser.getUid(),
                                                    uri,
                                                    firstNameString,
                                                    lastNameString,
                                                    addressString,
                                                    contactNoString,
                                                    "Water Dealer",
                                                    "active"
                                            );
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                            databaseReference.child(firebaseUser.getUid()).setValue(userFile)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            UserAccountFile userAccountFile = new UserAccountFile(
                                                                    firebaseUser.getUid(),
                                                                    emailnako,
                                                                    passwordString,
                                                                    device_token_id,
                                                                    "active"
                                                            );
                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                            databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d("Hiy","Hi");
                                                                            getLocationSetter();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            showMessages("Failed to save User Account");
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    });

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            showMessages("Failed to save data");
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else {
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
                                                                    "Water Dealer",
                                                                    "active"
                                                            );
                                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                                            databaseReference.child(firebaseUser.getUid()).setValue(userFile)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            UserAccountFile userAccountFile = new UserAccountFile(
                                                                                    firebaseUser.getUid(),
                                                                                    emailnako,
                                                                                    passwordString,
                                                                                    device_token_id,
                                                                                    "active"
                                                                            );
                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                            databaseReference1.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            getLocationSetter();
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            showMessages("Failed to save User Account");
                                                                                            progressDialog.dismiss();
                                                                                        }
                                                                                    });

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            showMessages("Failed to save data");
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    showMessages("Choose an image!");
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    showMessages("image error");
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessages("password error");
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

    private void updateInfoWithoutRenewPassword()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
        reference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                        if (userAccountFile != null)
                        {
                            String getEmail = userAccountFile.getUser_email_address();
                            String getPassword = userAccountFile.getUser_password();
                            Log.d("Hoy",getEmail+","+getPassword);
                            AuthCredential credential = EmailAuthProvider.getCredential(getEmail, getPassword);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updateEmail(emailnako)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (uri == null) {
                                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
                                                                        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                UserFile userFile1 = dataSnapshot.getValue(UserFile.class);
                                                                                String uriSet = userFile1.getUser_uri();
                                                                                UserFile userFile = new UserFile(
                                                                                        user.getUid(),
                                                                                        uriSet,
                                                                                        firstNameWU.getText().toString(),
                                                                                        lastNameWU.getText().toString(),
                                                                                        addressWU.getText().toString(),
                                                                                        contactNoWU.getText().toString(),
                                                                                        "Water Dealer",
                                                                                        "active"
                                                                                );
                                                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                                                                databaseReference.child(user.getUid()).setValue(userFile)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                                                                        .addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                                                                                newToken = instanceIdResult.getToken();
                                                                                                                UserAccountFile userAccountFile = new UserAccountFile(
                                                                                                                        user.getUid(),
                                                                                                                        emailnako,
                                                                                                                        retypePassword.getText().toString(),
                                                                                                                        newToken,
                                                                                                                        "active"
                                                                                                                );
                                                                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                                                reference.child(user.getUid()).setValue(userAccountFile)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override


                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                getLocationSetter();
                                                                                                                            }
                                                                                                                        })
                                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                                            @Override
                                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                                showMessage("User does not exists");
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                showMessage("Failed to get token");
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    } else {
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
                                                                                                        user.getUid(),
                                                                                                        uriString,
                                                                                                        firstNameWU.getText().toString(),
                                                                                                        lastNameWU.getText().toString(),
                                                                                                        addressWU.getText().toString(),
                                                                                                        contactNoWU.getText().toString(),
                                                                                                        "Water Dealer",
                                                                                                        "active"
                                                                                                );
                                                                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                                                                                databaseReference.child(user.getUid()).setValue(userFile)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                                                                                        .addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                                                                                                newToken = instanceIdResult.getToken();
                                                                                                                                UserAccountFile userAccountFile = new UserAccountFile(
                                                                                                                                        user.getUid(),
                                                                                                                                        emailnako,
                                                                                                                                        retypePassword.getText().toString(),
                                                                                                                                        newToken,
                                                                                                                                        "active"
                                                                                                                                );
                                                                                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                                                                reference.child(firebaseUser.getUid()).setValue(userAccountFile)
                                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                                getLocationSetter();
                                                                                                                                            }
                                                                                                                                        })
                                                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                                                            @Override
                                                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                                                showMessage("User does not exists");
                                                                                                                                            }
                                                                                                                                        });
                                                                                                                            }
                                                                                                                        })
                                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                                            @Override
                                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                                showMessage("Failed to get token");
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        showMessage("Failed to update image");
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                showMessage("Data does not updated");
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showMessage("Failed to update information");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkOldPassword()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
        reference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserAccountFile userAccountFile1 = dataSnapshot.getValue(UserAccountFile.class);
                        if(userAccountFile1 != null)
                        {
                            if(userAccountFile1.getUser_password().equals(retypePassword.getText().toString()))
                            {
                                updateInfoWithoutRenewPassword();
                            }
                            else
                            {
                                showMessage("Password does not match, Please retype it again!");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessage("Failed to retrieve information");
                    }
                });
    }

    private void successMessages() {
        showMessages("Updated successfully");
        WPAccountSettingsFragment wsdmFragment = new WPAccountSettingsFragment();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_wp, wsdmFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.RegisterSignUpUAS:
                if(isClick) {
                    updateDataAccountSingular();
                }
                else {
                    updateDataAccount();
                }
                break;
            case R.id.addPhotoUAS:
                openGallery();
                break;
            case R.id.changePasswordButton:
                changePassword.setText("");
                final int status = (Integer) v.getTag();
                switch (status)
                {
                    case 0:
                        changePassword.setText("Cancel Change Password");
                        linearPassword.setVisibility(View.GONE);
                        linearRenewPassword.setVisibility(View.VISIBLE);
                        isClick=false;
                        v.setTag(1);
                        break;
                    case 1:
                        changePassword.setText("Change Password");
                        linearPassword.setVisibility(View.VISIBLE);
                        linearRenewPassword.setVisibility(View.GONE);
                        isClick=true;
                        v.setTag(0);
                        break;
                }
                break;

        }
    }
}
