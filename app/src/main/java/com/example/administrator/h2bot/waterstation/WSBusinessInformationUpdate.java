package com.example.administrator.h2bot.waterstation;

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
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
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

public class WSBusinessInformationUpdate extends Fragment implements View.OnClickListener{

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

    Uri uri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.z_merchant_ws_updateinformationxml, container, false);
        if(view !=null)
        {
            dataSet(view);
        }
        else
        {
            showMessage("No data set given");
        }


        return view;
    }



    public void updateData()
    {
        storageReference.child(firebaseUser.getUid())
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String uriImage = uri.toString();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File");
                                databaseReference.child(firebaseUser.getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                UserWSBusinessInfoFile userWSBusinessInfoFile = new UserWSBusinessInfoFile(

                                                );
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Error to update image");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Error to update your information");
                    }
                });
    }

    private void showMessage(String wordMessage) {
        Toast.makeText(getActivity(), wordMessage, Toast.LENGTH_LONG).show();
    }

    public void getInput()
    {
        if(imageView.getDrawable() != null)
        {
            if(waterStationName.getText().toString().isEmpty()
            && waterStationAddress.getText().toString().isEmpty()
            && waterStationPhone.getText().toString().isEmpty()
            && waterStationStartTime.getText().toString().isEmpty()
            && waterStationEndTime.getText().toString().isEmpty()
            && waterStationMinimumGallon.getText().toString().isEmpty()
            && waterStationDeliveryFee.getText().toString().isEmpty())
            {
                showMessage("Please kindly input the requirements!");
            }
            else
            {

            }
        }
    }

    public void dataSet(View view)
    {
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("user_station_photo_display");
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        addPhotoButton = view.findViewById(R.id.addPhotoUIWS);
        updateButton = view.findViewById(R.id.updateInfoButtonUIWS);

        imageView = view.findViewById(R.id.imageViewUIWS);

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


        deliveryFeeFix = view.findViewById(R.id.waterStationFixUIS);
        deliveryFeePerGallon = view.findViewById(R.id.waterStationPerGallonUIS);

        addPhotoButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);

        updateData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addPhotoUIWS:
                break;
            case R.id.updateInfoButtonUIWS:
                break;
        }
    }
}
