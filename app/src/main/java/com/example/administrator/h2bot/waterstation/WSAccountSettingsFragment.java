package com.example.administrator.h2bot.waterstation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class WSAccountSettingsFragment extends Fragment implements View.OnClickListener {

    TextView UserTypeWS, StationNameWS,FullNameWS, AddressWS, ContactNoWS, EmailAddressWS, pointsView;
    ImageView imageView;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_accountsettings,
                container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        attemptToExit();
                        return true;
                    }
                }
                return false;
            }
        });


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User_File").child(firebaseUser.getUid());

        UserTypeWS = view.findViewById(R.id.userTypeAS);
        StationNameWS = view.findViewById(R.id.stationNameAS);
        FullNameWS = view.findViewById(R.id.fullNameAS);
        AddressWS = view.findViewById(R.id.addressAS);
        ContactNoWS = view.findViewById(R.id.contactAS);
        EmailAddressWS = view.findViewById(R.id.emailAS);
        imageView = view.findViewById(R.id.profileImageAS);
        pointsView = view.findViewById(R.id.pointsWAS);

        Button updateAcc = view.findViewById(R.id.updateAccount);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserFile user = dataSnapshot.getValue(UserFile.class);
                if(user != null)
                {
                    FullNameWS.setText(user.getUser_firstname()+" "+user.getUser_lastname());
                    AddressWS.setText(user.getUser_address());
                    ContactNoWS.setText(user.getUser_phone_no());
                    UserTypeWS.setText(user.getUser_type());

                    Picasso.get()
                            .load(user.getUser_uri())
                            .fit()
                            .centerCrop()
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to connect");
            }
        });
        databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountFile userAccount = dataSnapshot.getValue(UserAccountFile.class);
                if(userAccount != null)
                {
                    EmailAddressWS.setText(userAccount.getUser_email_address());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to connect");
            }
        });
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(firebaseUser.getUid());
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserWSBusinessInfoFile userBusiness = dataSnapshot.getValue(UserWSBusinessInfoFile.class);
                if(userBusiness != null)
                {
                    StationNameWS.setText(userBusiness.getBusiness_name());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to connect");
            }
        });

        databaseReference3 = FirebaseDatabase.getInstance().getReference("User_Wallet");
        databaseReference3.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserWallet userWallet = dataSnapshot.getValue(UserWallet.class);
                        if (userWallet != null)
                        {
                            if (userWallet.getUser_status().equals("active"))
                            {
                                pointsView.setText(userWallet.getUser_points());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessage("Failed to get load or wallet");
                    }
                });

        updateAcc.setOnClickListener(this);
        return view;
    }
    public void attemptToExit()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to exit application?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showMessage(String s)
    {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.updateAccount:
                WSUpdateAccountSettings wsdmFragment = new WSUpdateAccountSettings();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_ws, wsdmFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
        }
    }
}
