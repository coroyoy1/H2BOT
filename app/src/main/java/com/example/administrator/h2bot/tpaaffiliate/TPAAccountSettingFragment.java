package com.example.administrator.h2bot.tpaaffiliate;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.dealer.WPAccountSettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TPAAccountSettingFragment extends Fragment {

    TextView lastnameTextView, firstnameTextView,FullNameWS, AddressWS, ContactNoWS, EmailAddressWS, points;
    Button updateAccount,updateDocs;
    ImageView imageView;
    Dialog dialog;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    public TPAAccountSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tpa_fragment_account_setting, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        firstnameTextView = view.findViewById(R.id.firstnameTextView);
        lastnameTextView = view.findViewById(R.id.lastnameTextView);
        AddressWS = view.findViewById(R.id.addressAS);
        ContactNoWS = view.findViewById(R.id.contactAS);
        EmailAddressWS = view.findViewById(R.id.emailAS);
        imageView = view.findViewById(R.id.profileImageAS);
        updateDocs = view.findViewById(R.id.updateDocs);
        updateAccount = view.findViewById(R.id.updateAccount);
        points = view.findViewById(R.id.points);
        userFile();
        userAccountFile();
        wallet();
        updateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TPAUpdateAccountSettings wsdmFragment = new TPAUpdateAccountSettings();
                AppCompatActivity activity = (AppCompatActivity)getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, wsdmFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        updateDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TPADocumentUpdate wsdmFragment = new TPADocumentUpdate();
                AppCompatActivity activity = (AppCompatActivity)getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, wsdmFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });

        return view;
    }
    public void userFile()
    {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User_File").child(firebaseUser.getUid());
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstnameTextView.setText(dataSnapshot.child("user_firstname").getValue(String.class));
                lastnameTextView.setText(dataSnapshot.child("user_lastname").getValue(String.class));
                AddressWS.setText(dataSnapshot.child("user_address").getValue(String.class));
                ContactNoWS.setText(dataSnapshot.child("user_phone_no").getValue(String.class));
                Picasso.get().load(dataSnapshot.child("user_uri").getValue(String.class)).fit().centerCrop().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void wallet()
    {
        DatabaseReference walletpoints = FirebaseDatabase.getInstance().getReference("User_Wallet").child(firebaseUser.getUid());
        walletpoints.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                points.setText(dataSnapshot.child("user_points").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void userAccountFile()
    {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User_Account_File").child(firebaseUser.getUid());
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EmailAddressWS.setText(dataSnapshot.child("user_email_address").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void ShowPopUpAccountSettingUpdate(View view) {
        Button cancelBtn;
        Button saveChangesBtn;
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.account_settings_popup);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        saveChangesBtn = dialog.findViewById(R.id.saveChangesBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Temporary", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
