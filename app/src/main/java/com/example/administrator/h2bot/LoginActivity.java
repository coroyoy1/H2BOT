package com.example.administrator.h2bot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    EditText emailAddress, passwordType;
    Button loginNow;
    ProgressBar progressLoad;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailAddress = (EditText) findViewById(R.id.usernameEditText);
        passwordType = (EditText) findViewById(R.id.passwordEditText);
        progressLoad = (ProgressBar) findViewById(R.id.progressBar1);

        progressLoad.setVisibility(View.INVISIBLE);

        register = (TextView) findViewById(R.id.registerAccount);
        loginNow = (Button)findViewById(R.id.logInBtn);

        mAuth = FirebaseAuth.getInstance();


//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if(FirebaseAuth.getInstance().getCurrentUser() != null)
//                {
//                    mAuth.signOut();
//                }
//            }
//        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoad.setVisibility(View.VISIBLE);
                loginNow.setVisibility(View.INVISIBLE);
                signInNow();
//                mAuth.signOut();
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }

    private void signInNow()
    {
        String email = emailAddress.getText().toString();
        String password = passwordType.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            progressLoad.setVisibility(View.INVISIBLE);
            loginNow.setVisibility(View.VISIBLE);
            showMessages("Please check your email address or password.");
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(!task.isSuccessful())
                    {
                        progressLoad.setVisibility(View.INVISIBLE);
                        loginNow.setVisibility(View.VISIBLE);
                        showMessages("Please check your internet connection or credentials.");
                    }
                    else
                    {
                        progressLoad.setVisibility(View.INVISIBLE);
                        loginNow.setVisibility(View.VISIBLE);
                        showMessages("Successfully logged-in");
                        finish();
                        //Temporary Output
                        startActivity(new Intent(LoginActivity.this, WaterPeddlerDocumentActivity.class));
                    }
                }
            });
        }
    }

    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
