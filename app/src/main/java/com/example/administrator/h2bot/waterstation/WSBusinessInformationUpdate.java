package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.WaterStationDocumentVersion2Activity;
import com.example.administrator.h2bot.models.UserLocationAddress;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WSBusinessInformationUpdate extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText waterStationName, waterStationAddress, waterStationPhone, waterStationStartTime,
    waterStationEndTime, waterStationMinimumGallon, waterStationDeliveryFee;

    RadioButton deliveryServiceYes, deliveryServiceNo, deliveryServiceFree, deliveryFeePerGallon,
    deliveryFeeFix;

    Button addPhotoButton, updateButton;

    ImageView imageView;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Intent intent;
    LinearLayout linearLayout1, linearLayout2, linearLayout3;

    Uri uri;
    private ProgressDialog progressDialog;
    double lat;
    double lng;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_merchant_ws_updateinformationxml, container, false);
        intent = new Intent();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("user_station_photo_display");
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        addPhotoButton = view.findViewById(R.id.addPhotoUIWS);
        updateButton = view.findViewById(R.id.updateInfoButtonUIWS);

        imageView = view.findViewById(R.id.imageViewUIWS);
        linearLayout1 = view.findViewById(R.id.linearMinGal);
        linearLayout2 = view.findViewById(R.id.linearDelFee);
        linearLayout3 = view.findViewById(R.id.linearDelFeeNext);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);


        waterStationName = view.findViewById(R.id.waterStationNameUIS);
        waterStationAddress = view.findViewById(R.id.waterStationFullAddressUIS);
        waterStationPhone = view.findViewById(R.id.waterStationPhoneUIS);
        waterStationStartTime = view.findViewById(R.id.waterStationStartTimeUIS);
        waterStationEndTime = view.findViewById(R.id.waterStationEndTimeUIS);
        waterStationMinimumGallon  = view.findViewById(R.id.waterStationMinimumGallonUIS);
        waterStationDeliveryFee = view.findViewById(R.id.waterStationDeliveryFeeUIS);

        deliveryServiceYes = view.findViewById(R.id.waterStationYesUIS);
        deliveryServiceNo = view.findViewById(R.id.waterStationNoUIS);
        deliveryServiceFree = view.findViewById(R.id.waterStationFreeUIS);
        deliveryServiceYes.setChecked(true);


        deliveryFeeFix = view.findViewById(R.id.waterStationFixUIS);
        deliveryFeePerGallon = view.findViewById(R.id.waterStationPerGallonUIS);
        deliveryFeePerGallon.setChecked(true);

        addPhotoButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        deliveryServiceYes.setOnClickListener(this);
        deliveryServiceNo.setOnClickListener(this);
        deliveryServiceFree.setOnClickListener(this);


        return view;
    }



    public void updateData()
    {
        progressDialog.show();
        storageReference.child(firebaseUser.getUid())
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String deliveryStatusIf;
                                String deliveryStatusFreeIf;
                                String deliveryFeePerGalIf;
                                String deliveryMinNoCapaIf;
                                if(deliveryServiceYes.isChecked())
                                {
                                    deliveryStatusIf = "Active";
                                    deliveryStatusFreeIf = "Not";
                                    deliveryFeePerGalIf = waterStationDeliveryFee.getText().toString();
                                    deliveryMinNoCapaIf = waterStationMinimumGallon.getText().toString();
                                }
                                else if(deliveryServiceNo.isChecked())
                                {
                                    deliveryStatusIf = "Inactive";
                                    deliveryStatusFreeIf = "Not";
                                    deliveryFeePerGalIf = waterStationDeliveryFee.getText().toString();
                                    deliveryMinNoCapaIf = "None";
                                }
                                else if(deliveryServiceFree.isChecked())
                                {
                                    deliveryStatusIf = "Active";
                                    deliveryStatusFreeIf = "Free";
                                    deliveryFeePerGalIf = "None";
                                    deliveryMinNoCapaIf = "None";
                                }
                                else
                                {
                                    showMessage("Choose a radio button");
                                    return;
                                }

                                String uriImage = uri.toString();
                                UserWSBusinessInfoFile userWSBusinessInfoFile = new UserWSBusinessInfoFile(
                                    firebaseUser.getUid(),
                                        waterStationName.getText().toString(),
                                        waterStationStartTime.getText().toString(),
                                        waterStationEndTime.getText().toString(),
                                        deliveryStatusIf,
                                        deliveryStatusFreeIf,
                                        deliveryFeePerGalIf,
                                        deliveryMinNoCapaIf,
                                        waterStationPhone.getText().toString(),
                                        waterStationAddress.getText().toString(),
                                        "Active",
                                        uriImage
                                );
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
                                databaseReference.child(firebaseUser.getUid()).setValue(userWSBusinessInfoFile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                getLocationSetter();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Error to update the image");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed to update your information");
                    }
                });
    }

    private void showMessage(String wordMessage) {
        Toast.makeText(getActivity(), wordMessage, Toast.LENGTH_SHORT).show();
    }

    private void getLocationSetter()
    {
        progressDialog.show();
        progressDialog.setMessage("Location Finishing");
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        Address LocationAddress = null;
        String locateAddress = waterStationAddress.getText().toString();

        try {
            address = coder.getFromLocationName(locateAddress, 5);

            LocationAddress = address.get(0);

            lat = LocationAddress.getLatitude();
            lng = LocationAddress.getLongitude();

            String getLocateLatitude = String.valueOf(lat);
            String getLocateLongtitude = String.valueOf(lng);

            UserLocationAddress userLocationAddress = new UserLocationAddress(firebaseUser.getUid(), getLocateLatitude, getLocateLongtitude);
            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("User_LatLong");
            locationRef.child(firebaseUser.getUid()).setValue(userLocationAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("Submitted successfully");
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("Failed to get location");
                            progressDialog.dismiss();
                        }
                    });

        } catch (IOException ex) {

            ex.printStackTrace();
            progressDialog.dismiss();
        }
        finally {
            showMessage("Failed to locate your address");
            progressDialog.dismiss();
        }
    }

    public void getInput()
    {
        if(imageView.getDrawable() != null)
        {
            if(waterStationName.getText().toString().isEmpty()
            && waterStationAddress.getText().toString().isEmpty()
            && waterStationPhone.getText().toString().isEmpty()
            && waterStationStartTime.getText().toString().isEmpty()
            && waterStationEndTime.getText().toString().isEmpty())
            {
                showMessage("Please fill all the fields");
            }
            else
            {
                updateData();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                imageView.setColorFilter(null);
                uri = data.getData();
                Picasso.get().load(uri).into(imageView);
            }

        else
        {
            showMessage("Image is not selected");
        }
    }

    public void openGallery()
    {
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addPhotoUIWS:
                openGallery();
                break;
            case R.id.updateInfoButtonUIWS:
                getInput();
                break;
            case R.id.waterStationYesUIS:
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
                break;
            case R.id.waterStationNoUIS:
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                break;
            case R.id.waterStationFreeUIS:
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                break;
            case R.id.waterStationPerGallonUIS:
                break;
            case R.id.waterStationFixUIS:
                break;
        }
    }
}
