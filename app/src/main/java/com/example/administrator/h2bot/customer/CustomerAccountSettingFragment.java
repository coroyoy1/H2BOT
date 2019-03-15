package com.example.administrator.h2bot.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerAccountSettingFragment extends Fragment {
    Button updateBtn;
    EditText firstname, lastname, address, emailAddress, password, phoneNumber;
    TextView name;
    ImageView user_photo;


    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference userFileRef, userAccountFileRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    String mUserCurrentId;
    ArrayList<UserAccountFile> userAccount;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_account_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUserCurrentId = mAuth.getCurrentUser().getUid();
        mUser = mAuth.getCurrentUser();
        userFileRef = db.getReference("User_File");
        userAccountFileRef = db.getReference("User_Account_File");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        userAccount = new ArrayList<UserAccountFile>();

        updateBtn = view.findViewById(R.id.updateBtn);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        emailAddress = view.findViewById(R.id.emailAddress);
        password = view.findViewById(R.id.password);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        user_photo = view.findViewById(R.id.user_photo);

        getPersonalInfo(mUserCurrentId);

        onClickListenders();
        return view;
    }

    public void onClickListenders(){
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(updateBtn.getText().toString().equalsIgnoreCase("Save Changes")){
//                    firstname.setEnabled(false);
//                    lastname.setEnabled(false);
//                    address.setEnabled(false);
//                    emailAddress.setEnabled(false);
//                    password.setEnabled(false);
//                    phoneNumber.setEnabled(false);
//                    updateBtn.setText("Update");
//                    updateUserFileInfo(mUserCurrentId);
//                }
//                else {
//                    firstname.setEnabled(true);
//                    lastname.setEnabled(true);
//                    address.setEnabled(true);
//                    emailAddress.setEnabled(true);
//                    password.setEnabled(true);
//                    phoneNumber.setEnabled(true);
//                    updateBtn.setText("Save Changes");
//                }
                CustomerUpdateAccount additem = new CustomerUpdateAccount();
                AppCompatActivity activity = (AppCompatActivity)getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, additem)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    //geocode if update address
    public void getPersonalInfo(String id){

        userAccountFileRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emailAddress.setText(dataSnapshot.child("user_email_address").getValue(String.class));
                password.setText(dataSnapshot.child("user_password").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Datebase Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        userFileRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mName;
                mName = dataSnapshot.child("user_firstname").getValue(String.class)
                        + " " + dataSnapshot.child("user_lastname").getValue(String.class);
                name.setText(mName);
                firstname.setText(dataSnapshot.child("user_firstname").getValue(String.class));
                lastname.setText(dataSnapshot.child("user_lastname").getValue(String.class));
                address.setText(dataSnapshot.child("user_address").getValue(String.class));
                phoneNumber.setText(dataSnapshot.child("user_phone_no").getValue(String.class));
                Picasso.get().load(dataSnapshot.child("user_uri").getValue(String.class)).into(user_photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Datebase Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserFileInfo(String id){
        userAccountFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isEmailExist = false;
                Boolean isPasswordExist = false;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    UserAccountFile userAccountFile = data.getValue(UserAccountFile.class);
                    if(userAccountFile.getUser_email_address().equalsIgnoreCase(emailAddress.getText().toString())){
                        isEmailExist = true;
                        break;
                    }
                    else{
                        isEmailExist = false;
                    }
                }
                if(isEmailExist){
                    progressDialog.dismiss();
                }
                else{
                    HashMap<String, Object> userFile = new HashMap<>();
                    HashMap<String, Object> userAccountFile = new HashMap<>();

                    userFile.put("user_address", address.getText().toString());
                    userFile.put("user_firtname", firstname.getText().toString());
                    userFile.put("user_lastname", lastname.getText().toString());
                    userFile.put("user_phone_no", phoneNumber.getText().toString());

                    userAccountFile.put("user_email_address", emailAddress.getText().toString());
                    userAccountFile.put("user_password", password.getText().toString());

                    userFileRef.child(id).updateChildren(userFile);
                    userAccountFileRef.child(id).updateChildren(userAccountFile);
                    reauthenticateCredentials(emailAddress.getText().toString(), password.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void reauthenticateCredentials(String emailAddress, String password){
        AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, password);

        mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                mUser.updateEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setCancelable(false);
                        dialog.setTitle("CONFIRMATION");
                        dialog.setMessage("Please login to continue" );
                        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                Toast.makeText(getActivity(), "Successfully logout", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                        final AlertDialog alert = dialog.create();
                        alert.show();
                    }
                });

                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        mUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setCancelable(false);
                                dialog.setTitle("CONFIRMATION");
                                dialog.setMessage("Please login to continue" );
                                dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        mAuth.signOut();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        Toast.makeText(getActivity(), "Successfully logout", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    }
                                });
                                final AlertDialog alert = dialog.create();
                                alert.show();
                            }
                        });
                    }
                });
            }
        });
    }
    ////                Geocoder coder = new Geocoder(getActivity().getApplicationContext());
////                List<Address> address;
////                LatLng p1 = null;
////                try {
////                    // May throw an IOException
////                    address = coder.getFromLocationName(strAddress, 1);
////                    if (address == null) {
////                        return;
////                    }
////                    Address location = address.get(0);
////                    p1 = new LatLng(location.getLatitude(), location.getLongitude());
////                    map.addMarker(new MarkerOptions()
////                        .position(p1));
////
////
////                } catch (IOException ex) {
////
////                    ex.printStackTrace();
////                }
////                MarkerOptions options = new MarkerOptions();
////                options.position(p1).title(strAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
////                map.addMarker(options).showInfoWindow();
////                float zoomLevel = 16.0f;
////                map.moveCamera(CameraUpdateFactory.newLatLngZoom(p1, zoomLevel));
//                 }
}
