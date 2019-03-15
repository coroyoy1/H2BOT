package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.h2bot.LoginActivity;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserAccountFile;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.WDDocFile;
import com.example.administrator.h2bot.models.WSBusinessInfoFile;
import com.example.administrator.h2bot.models.WSDocFile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class WaterPeddlerDocumentActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    String userId;
    ImageView businessPermit_image, sanitaryPermit_image;
    Button sanitaryPermitBtn, submitButton;
    RadioGroup deliveryFeeGroup;
    boolean check;
    EditText stationName, stationAddress, endingHour, startingHour, businessDeliveryFeePerGal, businessMinNoCapacity, telNo, deliveryFee, min_no_of_gallons;
    Spinner startSpinner, endSpinner;
    String deliveryMethod, mUri;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private double lat;
    private double lng;
    RadioButton free,fixPrice,perGallon;
    String newToken;

    Uri filepath, filepath2;
    Boolean isPicked = false;
    Boolean isPicked2 = false;
    String mFirstname, mLastname, mAddress, mContact_no, mEmail_address, mPassword, mFilepath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_peddler_document);

        progressDialog = new ProgressDialog(WaterPeddlerDocumentActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Creating account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        startSpinner= findViewById(R.id.startSpinner);
        endSpinner= findViewById(R.id.endSpinner);

        // Button
        sanitaryPermitBtn = findViewById(R.id.sanitaryPermitBtn);
        submitButton = findViewById(R.id.submitButton);

        //Radiobutton
        free = findViewById(R.id.free);
        fixPrice = findViewById(R.id.fixPrice);
        perGallon = findViewById(R.id.perGallon);

        //Imageview
        sanitaryPermit_image = findViewById(R.id.sanitaryPermit_image);

        //EditText
        stationName = findViewById(R.id.stationName);
        stationAddress = findViewById(R.id.stationAddress);
        telNo = findViewById(R.id.telNo);
        endingHour = findViewById(R.id.endingHour);
        startingHour = findViewById(R.id.startingHour);
        deliveryFee = findViewById(R.id.deliveryFee);
        min_no_of_gallons = findViewById(R.id.min_no_of_gallons);

        //Radiogroup
        deliveryFeeGroup = findViewById(R.id.deliveryFeeGroup);

        Bundle bundle = getIntent().getExtras();
        String firstname = bundle.getString("firstname");
        String lastname = bundle.getString("lastname");
        String address = bundle.getString("address");
        String contact_no = bundle.getString("contactno");
        String email_address = bundle.getString("emailaddress");
        String password = bundle.getString("password");
        String filepath = bundle.getString("filepath");

        mFirstname = firstname;
        mLastname = lastname;
        mAddress = address;
        mContact_no = contact_no;
        mEmail_address = email_address;
        mPassword = password;
        mFilepath = filepath;

        String[] arraySpinner = new String[]{
                "AM", "PM"
        };
        String[] arraySpinner2 = new String[]{
                "PM", "AM"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WaterPeddlerDocumentActivity.this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(WaterPeddlerDocumentActivity.this,
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter2);

        sanitaryPermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    isPicked = false;
                    isPicked2 = true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check==true) {
                    CreateAccount(mEmail_address, mPassword);
                    checkDocuments();
//                uploadAllImage();
                }
                else
                {
                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Invalid driver's license. Please capture the license clearly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deliveryFeeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.perGallon:
                        deliveryMethod = "Per Gallon";
                        deliveryFee.setVisibility(View.VISIBLE);
                        deliveryFee.setHint("Delivery fee per gallon");
                        break;

                    case R.id.fixPrice:
                        deliveryMethod = "Fix Price";
                        deliveryFee.setVisibility(View.VISIBLE);
                        deliveryFee.setHint("Fixed delivery fee");
                        break;

                    case R.id.free:
                        deliveryMethod = "Free";
                        deliveryFee.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filepath2 = data.getData();
                if(isPicked2) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath2);
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if(!textRecognizer.isOperational())
                        {
                            Toast.makeText(getApplication(), "No text detected", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb= new StringBuilder();

                            for(int ctr=0;ctr<items.size();ctr++)
                            {
                                TextBlock myItem = items.valueAt(ctr);
                                sb.append(myItem.getValue());
                                sb.append("\n");
                            }
                            if(sb.toString().toLowerCase().contains(mFirstname.toLowerCase()) || sb.toString().toLowerCase().contains(mLastname.toLowerCase())){
                                Picasso.get().load(filepath2).into(sanitaryPermit_image);
                                Toast.makeText(this, "Valid driver's license", Toast.LENGTH_SHORT).show();
                                check = true;
                            }
                            else{
                                sanitaryPermit_image.setImageResource(R.drawable.ic_image_black_24dp);
                                Toast.makeText(WaterPeddlerDocumentActivity.this, "Invalid driver's license. Please capture the license clearly", Toast.LENGTH_SHORT).show();
                                check = false;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            Toast.makeText(WaterPeddlerDocumentActivity.this, "You haven't picked an image",Toast.LENGTH_LONG).show();
        }
    }

    private void CreateAccount(String emailAddress, String password){
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = firebaseUser.getUid();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( WaterPeddlerDocumentActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            newToken = instanceIdResult.getToken();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    StorageReference mStorage = FirebaseStorage.getInstance().getReference("dealer_photos").child(userId);
                    mStorage.putFile(filepath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            String name = mFirstname+" "+mLastname;
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String stringUri = uri.toString();
                                    UserFile userFile = new UserFile(userId,
                                            mFilepath,
                                            mFirstname,
                                            mLastname,
                                            mAddress,
                                            mContact_no,
                                            "Water Dealer",
                                            "active");

                                    UserAccountFile userAccountFile = new UserAccountFile(userId,
                                            mEmail_address,
                                            mPassword,
                                            newToken,
                                            "active");

                                    String startHour = startingHour.getText().toString() + " " + startSpinner.getSelectedItem();
                                    String endHour = endingHour.getText().toString() + " " + endSpinner.getSelectedItem();

                                    WSBusinessInfoFile businessInfoFile = new WSBusinessInfoFile(
                                            userId,
                                            name,
                                            mAddress,
                                            mContact_no,
                                            startHour,
                                            endHour,
                                            deliveryMethod,
                                            deliveryFee.getText().toString(),
                                            min_no_of_gallons.getText().toString(),
                                            "active");

                                    FirebaseDatabase.getInstance().getReference("User_File").child(userId).setValue(userFile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseDatabase.getInstance().getReference("User_Account_File").child(userId).setValue(userAccountFile)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Hohohoho","Ho");
                                                            FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(userId).setValue(businessInfoFile)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("Hihihihi","Hi");
                                                                    progressDialog.dismiss();
                                                                    getLocationSetter();
                                                                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        });

                                }
                            });
                        }
                    });
                    uploadAllImage();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void getLocationSetter()
    {
        progressDialog.show();
        progressDialog.setMessage("Location Finishing");
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = mAddress;

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(userId, getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(userId).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessages("Successfully Submitted");
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessages("Error: " + e.getMessage());
                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {

            ex.printStackTrace();
            progressDialog.dismiss();
        }
        finally {
            progressDialog.dismiss();
        }
    }

    private void showMessages(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void checkDocuments(){
        if(sanitaryPermit_image.getDrawable() == null){
            Toast.makeText(this, "Please fill all the requirments", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void uploadAllImage(){
        if(filepath2 != null){
            FirebaseUser user = mAuth.getCurrentUser();
            userId = user.getUid();
            StorageReference mStorageRef = storageReference.child("station_documents").child(userId +"/"+"sanitaryPermitDocument");
            mStorageRef.putFile(filepath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String stringUri = uri.toString();
                            WDDocFile wsDocFile = new WDDocFile(userId,
                                "active",
                                    stringUri);

                            FirebaseDatabase.getInstance().getReference("User_WD_Docs_File").child(userId).setValue(wsDocFile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(WaterPeddlerDocumentActivity.this, LoginActivity.class));
                                        mAuth.signOut();
                                        finish();
                                        Toast.makeText(WaterPeddlerDocumentActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WaterPeddlerDocumentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    progressDialog.show();
                }
            });
        }
    }
}
