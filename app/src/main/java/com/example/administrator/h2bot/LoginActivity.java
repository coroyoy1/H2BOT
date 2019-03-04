package com.example.administrator.h2bot;
import com.example.administrator.h2bot.dealer.WaterPeddlerDocumentActivity;
import com.example.administrator.h2bot.dealer.WaterPeddlerHomeActivity;
import com.example.administrator.h2bot.tpaaffiliate.*;
import com.example.administrator.h2bot.customer.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.deliveryman.DeliveryManMainActivity;
import com.example.administrator.h2bot.waterstation.WaterStationMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    EditText emailAddress, passwordType;
    Button loginNow;
    ProgressDialog progressDialog;
     FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    // FirebaseDatabase databaseConnection;
     DatabaseReference refConnection;
    FirebaseUser currentUser;
    String userHERE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //FirebaseApp.initializeApp(this);

        //databaseConnection = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        emailAddress = findViewById(R.id.usernameEditText);
        passwordType = findViewById(R.id.passwordEditText);
        register = findViewById(R.id.registerAccount);
        loginNow = findViewById(R.id.logInBtn);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if(currentUser != null)
        {
            userHERE = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File").child(currentUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String userFile = dataSnapshot.child("user_getUID").getValue().toString();
                            if(userFile.equals(mAuth.getCurrentUser().getUid()))
                            {
                                if(!(LoginActivity.this).isFinishing())
                                {
                                    progressDialog.show();
                                }
                                finish();
                                userTypeLogin();
                            }
                            else
                            {
                                showMessages("Account is not available");
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showMessages("Account does not available");
                            progressDialog.dismiss();
                        }
                    });
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(LoginActivity.this, zCreateAccountOptionUserTypeActivity.class);
                startActivity(intent);
            }
        });
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInNow();
//                tempLogin();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void signInNow()
    {
        String email = emailAddress.getText().toString();
        String password = passwordType.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            progressDialog.dismiss();
            showMessages("Please check your email address or password.");
        }
        else
        {
            if(!(LoginActivity.this).isFinishing())
            {
                progressDialog.dismiss();
            }
            mAuth.signInWithEmailAndPassword(emailAddress.getText().toString(), passwordType.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {

                    userHERE = currentUser.getUid();
                    if(!task.isSuccessful())
                    {
                        showMessages("Please check your internet connection or credentials.");
                    }
                    else {
                        userTypeLogin();
                }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessages("Account is not exists or internet connection is not connected/low connection");
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void userTypeLogin()
    {
        refConnection = FirebaseDatabase.getInstance().getReference("User_File").child(userHERE);
        refConnection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.child("user_type").getValue(String.class);
                String documentVerify = dataSnapshot.child("user_status").getValue(String.class);
                if (userType.equals("Customer") && documentVerify.equals("active")) {
                    //Temporary Output
                    startActivity(new Intent(LoginActivity.this, CustomerMainActivity.class));
//                    showMessages("Successfully logged-in as Customer");
                    finish();
                    progressDialog.dismiss();
                }
                else if(userType.equals("Water Station")){
                    if(documentVerify.equals("inactive"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, WaterStationDocumentVersion2Activity.class));
                        finish();
                    }
                    else if(documentVerify.equals("unconfirmed"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MerchantAccessVerification.class));
                    }
                    else if(documentVerify.equals("active"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, WaterStationMainActivity.class));
                        finish();
                    }
                    showMessages("Successfully logged-in as Station Owner");
                    progressDialog.dismiss();
                }
                else if(userType.equals("Delivery Man"))
                {
                    if(documentVerify.equals("active"))
                    {
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(LoginActivity.this, DeliveryManMainActivity.class));
                    }
                    showMessages("Successfully logged-in as Delivery Man");
                    progressDialog.dismiss();
                }
                else if(userType.equals("Water Dealer"))
                {
                    if(documentVerify.equals("inactive"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, WaterPeddlerDocumentActivity.class));
                        showMessages("Water Dealer Not Verified");
                    }
                    else if(documentVerify.equals("unverified"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MerchantAccessVerification.class));
                    }
                    else if(documentVerify.equals("active"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, WaterPeddlerHomeActivity.class));
                        showMessages("Water Dealer Verified");
                    }
                    showMessages("Successfully logged-in as Water Dealer");
                    progressDialog.dismiss();
                }
                else if(userType.equals("Third Party Affiliate"))
                {
                    if(documentVerify.equals("inactive"))
                    {
                        progressDialog.dismiss();
                        showMessages("Your registration is still on process. Please wait for the confirmation that will be sent through SMS.");
                    }
                    else if(documentVerify.equals("active"))
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, TPAAffiliateMainActivity.class));
                        showMessages("Successfully logged-in as Third Party Affiliate");
                        finish();
                    }
                    progressDialog.dismiss();
                }
                else
                {
                    showMessages("Failed to Login");
                    progressDialog.dismiss();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Failed to find existing data of the account");
            }
        });
    }

    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

//    public void tempLogin()
//    {
//        String email = emailAddress.getText().toString().trim();
//        if(email.equals("customer"))
//        {
//            startActivity(new Intent(LoginActivity.this, CustomerMainActivity.class));
//        }
//        else if(email.equals("dealer"))
//        {
//            startActivity(new Intent(LoginActivity.this, WaterPeddlerHomeActivity.class));
//        }
//        else if(email.equals("station"))
//        {
//            startActivity(new Intent(LoginActivity.this, WaterStationMainActivity.class));
//        }
//        else if(email.equals("delivery"))
//        {
//            startActivity(new Intent(LoginActivity.this, DeliveryManMainActivity.class));
//        }
//        else if(email.equals("tpa"))
//        {
//            startActivity(new Intent(LoginActivity.this, TPAAffiliateMainActivity.class));
//        }
//        else
//        {
//            showMessages("error");
//        }
//    }
}
