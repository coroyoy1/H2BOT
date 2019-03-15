package com.example.administrator.h2bot;
import com.example.administrator.h2bot.dealer.WaterPeddlerDocumentActivity;
import com.example.administrator.h2bot.models.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.tpaaffiliate.TPADocumentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    Button addPhoto, signUp;
    Button continueBtn, continueWaterDealerBtn, continueTPA;
    ImageView imageView;

    double lat;
    double lng;


    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri uri;
    Boolean clickable=false;

    EditText firstNameRegister, lastNameRegister, addressRegister, contactRegister, emailRegister, passwordRegister;
    ProgressDialog progressDialog;
    TextView headerTitle;
    FirebaseUser currentUser;
    FirebaseAuth.AuthStateListener mAuthListener;
    String device_token_id;
    String newToken;
    Boolean isAddressExist = false;
    String mLat, mLong;


    private FirebaseAuth mAuth;
    private String addressLocate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        String user_type = getIntent().getStringExtra("TextValue");
        headerTitle = findViewById(R.id.headerRegister);
        headerTitle.setText(user_type);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        firstNameRegister = findViewById(R.id.RegisterFullName);
        lastNameRegister = findViewById(R.id.RegisterLastName);
        addressRegister = findViewById(R.id.RegisterAddress);
        contactRegister = findViewById(R.id.RegisterContact);
        emailRegister = (EditText) findViewById(R.id.RegisterEmailAddress);
        passwordRegister = findViewById(R.id.RegisterPassword);


        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Creating account...");
        progressDialog.setProgress(0);


        imageView = findViewById(R.id.RegisterPhoto);
        addPhoto = findViewById(R.id.addPhotoRegister);
        signUp = findViewById(R.id.RegisterSignUp);
        continueBtn = findViewById(R.id.continueBtn);
        continueWaterDealerBtn = findViewById(R.id.continueWaterDealerBtn);
        continueTPA = findViewById(R.id.continueTPA);

        if(user_type.equalsIgnoreCase("Water Station")){
            signUp.setVisibility(View.GONE);
            continueWaterDealerBtn.setVisibility(View.GONE);
            continueTPA.setVisibility(View.GONE);
            continueBtn.setVisibility(View.VISIBLE);
        }
        else if(user_type.equalsIgnoreCase("Water Dealer")){
            signUp.setVisibility(View.GONE);
            continueBtn.setVisibility(View.GONE);
            continueTPA.setVisibility(View.GONE);
            continueWaterDealerBtn.setVisibility(View.VISIBLE);
        }
        else if(user_type.equalsIgnoreCase("Third Party Affiliate")){
            signUp.setVisibility(View.GONE);
            continueBtn.setVisibility(View.GONE);
            continueWaterDealerBtn.setVisibility(View.GONE);
            continueTPA.setVisibility(View.VISIBLE);
        }
        else if(user_type.equalsIgnoreCase("Customer")){
            continueBtn.setVisibility(View.GONE);
            continueTPA.setVisibility(View.GONE);
            continueWaterDealerBtn.setVisibility(View.GONE);
            signUp.setVisibility(View.VISIBLE);
        }

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickable=true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        continueTPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNameString = firstNameRegister.getText().toString();
                String lastNameString = lastNameRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactNoString = contactRegister.getText().toString();
                String emailAddressString = emailRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();
                if(firstNameString.isEmpty() || lastNameString.isEmpty() || addressString.isEmpty()
                        || contactNoString.isEmpty() || emailAddressString.isEmpty() || passwordString.isEmpty() || uri == null){
                    Toast.makeText(RegisterActivity.this, "Some fields are missing", Toast.LENGTH_SHORT).show();
                }
                else if(!isEmailValid(emailAddressString)){
                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
                else if(!isValidPhone(contactNoString)){
                    Toast.makeText(RegisterActivity.this, "Phone number is invalid", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Phone number is valid", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, TPADocumentActivity.class);
                    intent.putExtra("firstname", firstNameString);
                    intent.putExtra("lastname", lastNameString);
                    intent.putExtra("address", addressString);
                    intent.putExtra("contactno", contactNoString);
                    intent.putExtra("emailaddress", emailAddressString);
                    intent.putExtra("password", passwordString);
                    startActivity(intent);
                }
            }
        });

        continueWaterDealerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNameString = firstNameRegister.getText().toString();
                String lastNameString = lastNameRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactNoString = contactRegister.getText().toString();
                String emailAddressString = emailRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();
                String filepath = "";
                if(firstNameString.isEmpty() || lastNameString.isEmpty() || addressString.isEmpty()
                        || contactNoString.isEmpty() || emailAddressString.isEmpty() || passwordString.isEmpty() || uri == null){
                    Toast.makeText(RegisterActivity.this, "Some fields are missing", Toast.LENGTH_SHORT).show();
                }
                else if(!isEmailValid(emailAddressString)){
                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
                else{
                    filepath = uri.toString();
                    Intent intent = new Intent(RegisterActivity.this, WaterPeddlerDocumentActivity.class);
                    intent.putExtra("firstname", firstNameString);
                    intent.putExtra("lastname", lastNameString);
                    intent.putExtra("address", addressString);
                    intent.putExtra("contactno", contactNoString);
                    intent.putExtra("emailaddress", emailAddressString);
                    intent.putExtra("password", passwordString);
                    intent.putExtra("filepath", filepath);
                    startActivity(intent);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNameString = firstNameRegister.getText().toString();
                String lastNameString = lastNameRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactNoString = contactRegister.getText().toString();
                String emailAddressString = emailRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();
                String filepath = "";
                if(firstNameString.isEmpty() || lastNameString.isEmpty() || addressString.isEmpty()
                        || contactNoString.isEmpty() || emailAddressString.isEmpty() || passwordString.isEmpty() || uri == null){
                    Toast.makeText(RegisterActivity.this, "Some fields are missing", Toast.LENGTH_SHORT).show();
                }
                else if(!isEmailValid(emailAddressString)){
                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
                else if (uri == null)
                {
                    showMessage("Profile picture does not set");
                }
                else{
                    filepath = uri.toString();
                    Intent intent = new Intent(RegisterActivity.this, WaterStationDocumentVersion2Activity.class);
                    intent.putExtra("firstname", firstNameString);
                    intent.putExtra("lastname", lastNameString);
                    intent.putExtra("address", addressString);
                    intent.putExtra("contactno", contactNoString);
                    intent.putExtra("emailaddress", emailAddressString);
                    intent.putExtra("password", passwordString);
                    intent.putExtra("filepath", filepath);
                    startActivity(intent);
//                    startActivity(new Intent(RegisterActivity.this, WaterStationDocumentVersion2Activity.class));
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(RegisterActivity.this).isFinishing()) {
                    progressDialog.show();
                }
                String firstNameString = firstNameRegister.getText().toString();
                String lastNameString = lastNameRegister.getText().toString();
                String addressString = addressRegister.getText().toString();
                String contactNoString = contactRegister.getText().toString();
                String emailAddressString = emailRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();

                if(passwordString.isEmpty() && firstNameString.isEmpty() && lastNameString.isEmpty()
                        && addressString.isEmpty() && contactNoString.isEmpty() && emailAddressString.isEmpty()
                        && imageView.getDrawable() == null){
                    showMessage("Some fields are missing");
                }
                else if(imageView.getDrawable() == null){
                    showMessage("Photo is not yet set!");
                }
                else if (contactNoString.length() > 11){
                    showMessage("Contact no. must be maximum of 11 characters");
                }
                else{
                    getLocationSetter();
                    if(isAddressExist){
                        CreateAccount(emailAddressString, passwordString);
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    }
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                uri = data.getData();
                if(clickable) {
                    Picasso.get().load(uri).into(imageView);
                }
            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }

    private void CreateAccount(String emailString, String passwordString){
        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String userType = headerTitle.getText().toString();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uidString = firebaseUser.getUid();


                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( RegisterActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                newToken = instanceIdResult.getToken();
                                Log.d("newToken",newToken);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        StorageReference mStorage = FirebaseStorage.getInstance().getReference("station_photos").child(uidString);
                        mStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String stringUri = uri.toString();
                                        UserFile userFile;
                                            userFile = new UserFile(uidString,
                                                stringUri,
                                                firstNameRegister.getText().toString(),
                                                lastNameRegister.getText().toString(),
                                                addressRegister.getText().toString(),
                                                contactRegister.getText().toString(),
                                                "Customer",
                                                "active");

                                            UserAccountFile userAccountFile = new UserAccountFile(uidString,
                                                    emailRegister.getText().toString(),
                                                    passwordRegister.getText().toString(),
                                                    newToken,
                                                    "active");

                                            FirebaseDatabase.getInstance().getReference("User_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userFile)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        FirebaseDatabase.getInstance().getReference("User_Account_File").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userAccountFile)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    insertLatLong(mLat, mLong);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    showMessage("Error, Connection Error");
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        showMessage("Error, Connection Error");
                                                    }
                                                });

                                    }
                                });
                                }
                            });
                        }
                        else
                        {
                            showMessage("Error to save data");
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Error: " + e.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    private void getLocationSetter()
    {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = addressRegister.getText().toString();

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);
            if(address.size() == 0){
                isAddressExist = false;
            }
            else{
                isAddressExist = true;

                lat = LocationAddress.getLatitude();
                lng = LocationAddress.getLongitude();

                mLat = String.valueOf(lat);
                mLong = String.valueOf(lng);
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void insertLatLong(String latitude, String longitude){
        UserLocationAddress userLocationAddress = new UserLocationAddress(FirebaseAuth.getInstance().getCurrentUser().getUid(), latitude, longitude);
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
        locationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userLocationAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Successfully registered");
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //showMessage("Error to get location");
                        progressDialog.dismiss();
                    }
                });
    }

    private void passToNextActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isValidPhone(String phone)
    {
        String expression = "^(09|\\+639)\\d{9}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }
    }
}
