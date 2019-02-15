package com.example.administrator.h2bot.waterstation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    TextView UserTypeWS, StationNameWS, StationRelatedNoWS, UserNameWS, FullNameWS, AgeWS, AddressWS, ContactNoWS, EmailAddressWS;
    ImageView imageView;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_accountsettings,
                container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User_File").child(firebaseUser.getUid());

        UserTypeWS = view.findViewById(R.id.userTypeAS);
        StationNameWS = view.findViewById(R.id.stationNameAS);
        StationRelatedNoWS = view.findViewById(R.id.stationRelatedNoAS);
        FullNameWS = view.findViewById(R.id.fullNameAS);
        AgeWS = view.findViewById(R.id.ageAS);
        AddressWS = view.findViewById(R.id.addressAS);
        ContactNoWS = view.findViewById(R.id.contactAS);
        EmailAddressWS = view.findViewById(R.id.emailAS);
        imageView = view.findViewById(R.id.profileImageAS);

        Button updateDoc = (Button)view.findViewById(R.id.updateDocument);
        Button updateAcc = (Button)view.findViewById(R.id.updateAccount);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserFile user = dataSnapshot.getValue(UserFile.class);
                FullNameWS.setText(user.getUser_firtname()+" "+user.getUser_lastname());
                AddressWS.setText(user.getUser_address());
                ContactNoWS.setText(user.getUser_phone_no());
                UserTypeWS.setText(user.getUser_type());
                Picasso.get()
                        .load(user.getUser_uri())
                        .fit()
                        .centerCrop()
                        .into(imageView);
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
                    EmailAddressWS.setText(userAccount.getUser_email_address());
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
                StationNameWS.setText(userBusiness.getBusiness_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Failed to connect");
            }
        });

        updateAcc.setOnClickListener(this);
        updateDoc.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showMessage(String s)
    {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.updateDocument:
                Intent intent = new Intent(getActivity(), WSAccountSettingsUpdateDoc.class);
                intent.putExtra("Data", "Some Data");
                startActivity(intent);
            break;
            case R.id.updateAccount:
                Intent intent2 = new Intent(getActivity(), WSAccountSettingsUpdateAcc.class);
                intent2.putExtra("Data", "Some Data");
                startActivity(intent2);
                break;
        }
    }
}
