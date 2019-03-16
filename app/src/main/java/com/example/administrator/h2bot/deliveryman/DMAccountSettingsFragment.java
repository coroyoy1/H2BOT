package com.example.administrator.h2bot.deliveryman;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSAccountSettingsFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DMAccountSettingsFragment extends Fragment implements View.OnClickListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView userType, stationName, stationFullName, stationAddress, stationEmail, stationContactNo;
    Button updateButton;
    CircleImageView imageView;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_accountsettings, container, false);
        userType = view.findViewById(R.id.userTypeDMYP);
        stationName = view.findViewById(R.id.stationNameDMYP);
        stationFullName = view.findViewById(R.id.fullNameDMYP);
        stationAddress = view.findViewById(R.id.addressDMYP);
        stationEmail = view.findViewById(R.id.emailDMYP);
        stationContactNo = view.findViewById(R.id.contactDMYP);
        updateButton = view.findViewById(R.id.updateAccountDMYP);
        imageView = view.findViewById(R.id.profileImageDMYP);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        dataRetrieve();

        updateButton.setOnClickListener(this);

        return view;
    }

    private void dataRetrieve()
    {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_File");
        reference1.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getParentKey = dataSnapshot.child("station_parent").getValue(String.class);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
                        reference.child(getParentKey).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserWSDMFile userWSDMFile = dataSnapshot.getValue(UserWSDMFile.class);
                                if (userWSDMFile != null) {
                                    if (userWSDMFile.getDelivery_man_id().equals(firebaseUser.getUid())) {
                                        String userId = userWSDMFile.getStation_id();
                                        String customerId = userWSDMFile.getDelivery_man_id();
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
                                        reference1.child(userId)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        UserWSBusinessInfoFile userWSBusinessInfoFile = dataSnapshot.getValue(UserWSBusinessInfoFile.class);
                                                        if (userWSBusinessInfoFile != null) {
                                                            String stationNameString = userWSBusinessInfoFile.getBusiness_name();
                                                            stationName.setText("Station Name: "+stationNameString);

                                                            databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
                                                            databaseReference.child(firebaseUser.getUid())
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            UserFile userFile = dataSnapshot.getValue(UserFile.class);
                                                                            if(userFile != null)
                                                                            {
                                                                                String userTypeString = "User Type: "+userFile.getUser_type();
                                                                                String fullNameString = "Full Name: "+userFile.getUser_firstname()+" "+userFile.getUser_lastname();
                                                                                String addressString = "Full Address: "+userFile.getUser_address();
                                                                                String contactNoString = "Contact No.: "+userFile.getUser_phone_no();
                                                                                String imageUriString = userFile.getUser_uri();

                                                                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                                                                                databaseReference1.child(firebaseUser.getUid())
                                                                                        .addValueEventListener(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                                                                                                if(userAccountFile != null)
                                                                                                {
                                                                                                    String emailString = "Email Address: "+userAccountFile.getUser_email_address();
                                                                                                    userType.setText(userTypeString);
                                                                                                    stationFullName.setText(fullNameString);
                                                                                                    stationAddress.setText(addressString);
                                                                                                    stationContactNo.setText(contactNoString);
                                                                                                    stationEmail.setText(emailString);
                                                                                                    Picasso.get().load(imageUriString).into(imageView);
                                                                                                }
                                                                                                else
                                                                                                {
                                                                                                    showMessages("Data does not exists");
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                showMessages("Data does not available");
                                                                                            }
                                                                                        });
                                                                            }
                                                                            else
                                                                            {
                                                                                showMessages("Data is not available");
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            showMessages("Data does not available");
                                                                        }
                                                                    });

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
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

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.updateAccountDMYP:
                DMUpdateAccountSettings wsdmFragment = new DMUpdateAccountSettings();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_dm, wsdmFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
