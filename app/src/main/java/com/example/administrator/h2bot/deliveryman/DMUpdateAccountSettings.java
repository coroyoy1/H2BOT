package com.example.administrator.h2bot.deliveryman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
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
import android.widget.ImageView;
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

public class DMUpdateAccountSettings extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText firstNameWU, lastNameWU, addressWU, contactNoWU, emailAddressWU, passwordWU, confirmPasswordWU, retypePassword, oldPass;
    private Button updateButton, addPhotobutton, changePassword, changeLicenseButton;
    LinearLayout linearPassword, linearRenewPassword;
    private CircleImageView imageView;
    private ImageView imageView1;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser, user;
    ProgressDialog progressDialog;
    String device_token_id;

    Uri uri, uri1;
    double lat;
    double lng;
    private String newToken;
    boolean isClick=true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_accountupdate, container, false);

        firstNameWU = view.findViewById(R.id.RegisterFullNameUASDM);
        lastNameWU = view.findViewById(R.id.RegisterLastNameUASDM);
        addressWU = view.findViewById(R.id.RegisterAddressUASDM);
        contactNoWU = view.findViewById(R.id.RegisterContactUASDM);
        emailAddressWU = view.findViewById(R.id.RegisterEmailAddressUASDM);
        passwordWU = view.findViewById(R.id.RegisterPasswordUASDM);
        confirmPasswordWU = view.findViewById(R.id.ConfirmPasswordUASDM);
        updateButton = view.findViewById(R.id.RegisterSignUpUASDM);
        imageView = view.findViewById(R.id.imageUASDM);
        addPhotobutton = view.findViewById(R.id.addPhotoUASDM);
        changePassword = view.findViewById(R.id.changePasswordButtonUASDM);
        linearPassword = view.findViewById(R.id.linearPasswordCurrentUASDM);
        linearRenewPassword = view.findViewById(R.id.linearPasswordUpdateUASDM);
        retypePassword = view.findViewById(R.id.RegisterPasswordRetypeUASDM);
        oldPass = view.findViewById(R.id.oldPasswordUASDM);

        imageView1 = view.findViewById(R.id.driverlicense_imageUASDM);
        changeLicenseButton = view.findViewById(R.id.addPhotoUASDMDM);

        changePassword.setTag(0);
        linearRenewPassword.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        user = mAuth.getCurrentUser();

        updateButton.setOnClickListener(this);
        addPhotobutton.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        changeLicenseButton.setOnClickListener(this);

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
        String emailAddressString = emailAddressWU.getText().toString();
        String passwordString = retypePassword.getText().toString();

        if(firstNameString.isEmpty()
                && lastNameString.isEmpty()
                && addressString.isEmpty()
                && contactNoString.isEmpty()
                && emailAddressString.isEmpty()
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
                                            emailAddressWU.setText(userAccountFile.getUser_email_address());

                                            DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("User_File");
                                            reference3.child(firebaseUser.getUid())
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            String getKey = dataSnapshot.child("station_parent").getValue(String.class);
                                                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
                                                            reference2.child(getKey).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                                                    {
                                                                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child(firebaseUser.getUid()).getChildren())
                                                                        {
                                                                            String getPic = dataSnapshot2.child("dealer_drivers_license").getValue(String.class);
                                                                            Picasso.get().load(getPic).fit().centerCrop().into(imageView1);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
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
                                                        user.updateEmail(emailAddressWU.getText().toString())
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
                                                                                                                "Water Station",
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
                                                                                                                                                emailAddressWU.getText().toString(),
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
                                                                                                                                "Water Station",
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
                                                                                                                                                                emailAddressWU.getText().toString(),
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
                                                user.updateEmail(emailAddressWU.getText().toString())
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
                                                                                        "Water Station",
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
                                                                                                                        emailAddressWU.getText().toString(),
                                                                                                                        retypePassword.getText().toString(),
                                                                                                                        newToken,
                                                                                                                        "active"
                                                                                                                );
                                                                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                                                reference.child(user.getUid()).setValue(userAccountFile)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override


                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                Log.d("Hoy",""+emailAddressWU.getText().toString());
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
                                                                                                        "Water Station",
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
                                                                                                                                        emailAddressWU.getText().toString(),
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
        DMAccountSettingsFragment wsdmFragment = new DMAccountSettingsFragment();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_dm, wsdmFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.RegisterSignUpUASDM:
                if(isClick) {
                    updateDataAccountSingular();
                }
                else {
                    updateDataAccount();
                }
                break;
            case R.id.addPhotoUASDM:
                openGallery();
                break;
            case R.id.changePasswordButtonUASDM:
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
            case R.id.addPhotoUASDMDM:
                openGallery();
                break;

        }
    }
}
