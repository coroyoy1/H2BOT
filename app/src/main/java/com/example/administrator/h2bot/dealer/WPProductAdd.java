package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.models.WSWDWaterTypeFile;
import com.example.administrator.h2bot.models.WSWDWaterTypeFile2;
import com.example.administrator.h2bot.tpaaffiliate.TPAAcceptedFragment;
import com.example.administrator.h2bot.waterstation.WSProductListFragment;
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

public class WPProductAdd extends Fragment implements View.OnClickListener {

    TextInputLayout waterProductName, waterProductPrice, waterProductDescription;
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
        View view = inflater.inflate(R.layout.fragment_wp_product_add, container, false);

        list = new ArrayList<String>();

        //Text Layout Edit text
        waterProductName = view.findViewById(R.id.waterProductName);
        waterProductPrice = view.findViewById(R.id.deliveryPrice);
        waterProductType = view.findViewById(R.id.waterSpinner);
        waterProductDescription = view.findViewById(R.id.waterDescription);


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
        String productPriceString = waterProductPrice.getEditText().getText().toString();
        String productDescriptionString = waterProductDescription.getEditText().getText().toString();

        if (productNameString.isEmpty())
        {
            showMessages("Fields should not be empty!");
        }
        else if (productTypeString.isEmpty())
        {
            showMessages("Fields should not be empty!");
        }
        else if (productPriceString.isEmpty())
        {
            showMessages("Fields should not be empty!");
        }
        else
        {
            saveData(productNameString, productTypeString, productPriceString, productDescriptionString);
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

    private void saveData(String productNameString, String productTypeString, String productPriceString, String productDescriptionString)
    {
        isExists = true;
        if (productDescriptionString.isEmpty())
        {
            productDescriptionString = "";
        }
        WSWDWaterTypeFile2 userWSWDWaterTypeFile1 = new WSWDWaterTypeFile2(
                firebaseUser.getUid(),
                productNameString,
                productTypeString,
                productPriceString,
                productDescriptionString,
                "Available"
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
                            WPProductListFragment additem = new WPProductListFragment();
                            AppCompatActivity activity = (AppCompatActivity)getContext();
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.fragment_container_wp, additem)
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


//    public void retrieveData()
//    {
//        List<String> array1 = new ArrayList<String>();
//        List<String> array2 = new ArrayList<String>();
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Water_Type_File");
//        databaseReference
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
//                        {
//                            String userTypes = dataSnapshot1.child("prodName").getValue(String.class);
//                            array1.add(userTypes);
//                        }
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
//                        reference.child(firebaseUser.getUid())
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren())
//                                        {
//                                            String userTypes2 = dataSnapshot2.child("water_type").getValue(String.class);
//                                            array2.add(userTypes2);
//                                        }
//                                        array1.removeAll(array2);
//                                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, array1);
//                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                        waterProductType.setAdapter(adapter);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }

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
                WPProductListFragment additem = new WPProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_wp, additem)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
