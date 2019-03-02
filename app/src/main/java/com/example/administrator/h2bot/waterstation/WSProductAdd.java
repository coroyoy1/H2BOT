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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class WSProductAdd extends Fragment implements View.OnClickListener {

    EditText waterProductName, waterProductPrice;
    Spinner waterProductType;
    Button backProductButton, addProductButton;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_product_add, container, false);

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

        String[] arraySpinner = new String[]{
                "Mineral", "Distilled", "Purified", "Alkaline"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterProductType.setAdapter(adapter);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        return view;
    }

    public void saveData()
    {
        String waterPriceString = waterProductPrice.getText().toString();
        String waterTypeString = waterProductType.getSelectedItem().toString();

        if(waterPriceString.equals("") && waterTypeString.equals(""))
        {
            showMessages("Fill up first the requirement");
            return;
        }
        else {
            addProduct(waterPriceString, waterTypeString);
        }
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
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
                    showMessages("Product Successfully Added");
                    progressDialog.dismiss();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessages("Product does not added or the connection is interrupted");
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
