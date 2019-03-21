package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WSProductAdd extends Fragment implements View.OnClickListener {

    EditText waterProductName, waterProductPrice;
    Spinner waterProductType;
    Button backProductButton, addProductButton;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> list;

    ArrayAdapter<String> adapter;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_product_add, container, false);

        list = new ArrayList<String>();

        waterProductPrice = view.findViewById(R.id.waterPrice);
        waterProductType = view.findViewById(R.id.waterSpinner);

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

        retrieveData();

        return view;
    }


    public void retrieveData()
    {
        List<String> array1 = new ArrayList<String>();
        List<String> array2 = new ArrayList<String>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Water_Type_File");
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {
                            String userTypes = dataSnapshot1.child("prodName").getValue(String.class);
                            array1.add(userTypes);
                        }
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
                        reference.child(firebaseUser.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren())
                                        {
                                            String userTypes2 = dataSnapshot2.child("water_type").getValue(String.class);
                                            array2.add(userTypes2);
                                        }
                                        array1.removeAll(array2);
                                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, array1);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        waterProductType.setAdapter(adapter);
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

    public void saveData()
    {
        String waterPriceString = waterProductPrice.getText().toString();
        String waterTypeString = waterProductType.getSelectedItem().toString();

        if(waterPriceString.equals("") && waterTypeString.equals(""))
        {
            showMessages("Please fill all fields");
            return;
        }
        else {
            addProduct(waterPriceString, waterTypeString);
        }
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void addProduct(String waterPriceString, String waterTypeString) {
        String uidString = mAuth.getUid();
        String keyString = databaseReference.push().getKey();

        progressDialog.show();

        UserWSWDWaterTypeFile userWSWDWaterTypeFile = new UserWSWDWaterTypeFile(uidString, waterTypeString, waterPriceString, "active");
        databaseReference.child(uidString).child(waterTypeString).setValue(userWSWDWaterTypeFile)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessages("Item added successfully");
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
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessages("Failed to add the item or the connection is interrupted");
                    progressDialog.show();
                }
            });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.waterAdd:
                saveData();
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
