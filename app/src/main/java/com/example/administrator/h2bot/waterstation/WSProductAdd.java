package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.StationWaterTypeFile;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.models.WSWDWaterTypeFile;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.List;

public class WSProductAdd extends Fragment implements View.OnClickListener {

    TextInputLayout waterProductName, waterProductDescription, waterProductPickup, waterProductDeliveryPrice;
    TextInputLayout waterProductType;
    Button backProductButton, addProductButton;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> list;

    ArrayAdapter<String> adapter;

    ProgressDialog progressDialog;
    boolean isExists=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_product_add, container, false);

        list = new ArrayList<String>();

        //Text Layout Edit text
        waterProductName = view.findViewById(R.id.waterProductName);
        waterProductType = view.findViewById(R.id.waterSpinner);
        waterProductDescription = view.findViewById(R.id.waterDescription);
        waterProductPickup = view.findViewById(R.id.waterPickUp);
        waterProductDeliveryPrice = view.findViewById(R.id.waterDeliveryPrice);


        backProductButton = view.findViewById(R.id.waterBack);
        addProductButton = view.findViewById(R.id.waterAdd);

        backProductButton.setOnClickListener(this);
        addProductButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User_WS_WD_Water_Type_File");


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        return view;
    }

    public void validateEditText()
    {
        String productNameString = waterProductName.getEditText().getText().toString();
        String productTypeString = waterProductType.getEditText().getText().toString();
        String productDescriptionString = waterProductDescription.getEditText().getText().toString();
        String productPickupString = waterProductPickup.getEditText().getText().toString();
        String productDeliveryPriceString = waterProductDeliveryPrice.getEditText().getText().toString();

        if (productDeliveryPriceString.isEmpty())
        {
            productDeliveryPriceString = "0";
        }

        if (productNameString.isEmpty())
        {
            showMessages("Fields should not be empty!");
        }
        else if (productTypeString.isEmpty())
        {
            showMessages("Fields should not be empty!");
        }
        else if (productPickupString.isEmpty())
        {
            showMessages("Pick up price should not be empty");
        }
        else
        {
            saveData(productNameString, productTypeString, productDescriptionString, productPickupString, productDeliveryPriceString);
        }

    }

    public void checkDataIfExists()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
        databaseReference.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(waterProductType.getEditText().getText().toString().trim()))
                        {
                            showMessages("Already Exists");
                        }
                        else
                        {
                            validateEditText();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void saveData(String productNameString, String productTypeString, String productDescriptionString, String productPickupString, String productDeliveryPriceString)
    {
        isExists = true;
        if (productDescriptionString.isEmpty())
        {
            productDescriptionString = "";
        }
        WSWDWaterTypeFile userWSWDWaterTypeFile1 = new WSWDWaterTypeFile(
                firebaseUser.getUid(),
                productNameString,
                productTypeString,
                productPickupString,
                productDeliveryPriceString,
                productDescriptionString,
                "available"
        );
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
        dataRef.child(firebaseUser.getUid()).child(productTypeString).setValue(userWSWDWaterTypeFile1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            showMessages("Successfully added");
                            progressDialog.dismiss();
                            WSProductListFragment additem = new WSProductListFragment();
                            AppCompatActivity activity = (AppCompatActivity)getContext();
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.fragment_container_ws, additem)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessages("Fail to add your product, please check your internet connection");
                        progressDialog.dismiss();
                    }
                });
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.waterAdd:
                checkDataIfExists();
                break;
            case R.id.waterBack:
                WSProductListFragment additem = new WSProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
