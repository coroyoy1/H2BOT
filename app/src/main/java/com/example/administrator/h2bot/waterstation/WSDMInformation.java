package com.example.administrator.h2bot.waterstation;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class WSDMInformation extends Fragment {

    String emailOf, uidOf;
    TextView nameTo, addressTo, contactNoTo, statusTo, emailTo;
    FirebaseUser firebaseUser;
    CircleImageView imageTo;
    Button backTo, deleteTo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_dm_dminformation, container, false);

        nameTo = view.findViewById(R.id.fullnameDMDM);
        addressTo = view.findViewById(R.id.addressDMDM);
        contactNoTo = view.findViewById(R.id.contactNoDMDM);
        statusTo = view.findViewById(R.id.statusDMDM);
        imageTo = view.findViewById(R.id.imageCircleDMDM);
        backTo = view.findViewById(R.id.backOnDMDM);
        deleteTo = view.findViewById(R.id.deleteOnDMDM);
        emailTo = view.findViewById(R.id.emailAddDMDM);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String nameOf = "Full Name: "+bundle.getString("NameDM");
            String addressOf = "Full Address: "+bundle.getString("AddressDM");
            String contactNoOf = "Contact No.: "+bundle.getString("ContactNoDM");
            String statusOf = "Status: "+bundle.getString("StatusDM");
            String imageOf = bundle.getString("ImageDM");
            uidOf = bundle.getString("uidDelMan");
            if(uidOf != null)
            {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Account_File");
                reference.child(uidOf).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserAccountFile userAccountFile = dataSnapshot.getValue(UserAccountFile.class);
                        if(userAccountFile != null)
                        {
                            String email = "Email Address: "+userAccountFile.getUser_email_address();
                            emailTo.setText(email);
                            nameTo.setText(nameOf);
                            addressTo.setText(addressOf);
                            contactNoTo.setText(contactNoOf);
                            statusTo.setText(statusOf);

                            Picasso.get().load(imageOf).into(imageTo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }

        backTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSDMFragment backFragment = new WSDMFragment();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_ws, backFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        deleteTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationToDelete();
            }
        });

        return view;
    }

    private void confirmationToDelete()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteDeliveryMan();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to remove this delivery man?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteDeliveryMan()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_File");
        reference.child(uidOf).removeValue()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_Account_File");
                    reference1.child(uidOf).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showMessages("Your Delivery man has sucessfully removed");
                        }
                    });
                }
            }
        });
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
}
