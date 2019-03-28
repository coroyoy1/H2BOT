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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.models.WSWDWaterTypeFile;
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
import java.util.Collections;
import java.util.List;

public class WSProductListUpdate extends Fragment implements View.OnClickListener {

    TextInputLayout productName, productPrice, productDescription, productUpdateDelivery, productUpdatePickup;

    TextView productUpdateStatus, productUpdateType;
    Button backUpItem, updateUpItem;
    RadioButton valid, invalid;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<String> list;

    String statusGet;

    ProgressDialog progressDialog;
    String[] arraySpinner;
    ArrayAdapter<String> adapter;

    String itemTy;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlistintentupdate, container, false);

        list = new ArrayList<String>();
        arraySpinner = new String[]{};

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User_WS_WD_Water_Type_File");
        firebaseUser = mAuth.getCurrentUser();

        //Edit text Text Layout
        productName = view.findViewById(R.id.waterNameUPW);
        productDescription = view.findViewById(R.id.waterDescriptionUWP);
        productUpdatePickup = view.findViewById(R.id.waterPickUpUWP);
        productUpdateDelivery = view.findViewById(R.id.waterDeliveryPriceUWP);
        productUpdateType = view.findViewById(R.id.waterTypeUWP);

        valid = view.findViewById(R.id.avaiableRadio);
        invalid = view.findViewById(R.id.unavailableRadio);

        backUpItem = view.findViewById(R.id.waterUpdateBack);
        updateUpItem = view.findViewById(R.id.waterUpdateAdd);

        backUpItem.setOnClickListener(this);
        updateUpItem.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             itemTy = bundle.getString("ItemTypePLI");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
            reference.child(firebaseUser.getUid()).child(itemTy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    WSWDWaterTypeFile wswdWaterTypeFile = dataSnapshot.getValue(WSWDWaterTypeFile.class);
                    if (wswdWaterTypeFile != null)
                    {
                        productDescription.getEditText().setText(wswdWaterTypeFile.getWater_description());
                        productName.getEditText().setText(wswdWaterTypeFile.getWater_name());
                        productUpdateDelivery.getEditText().setText(wswdWaterTypeFile.getDelivery_price());
                        productUpdatePickup.getEditText().setText(wswdWaterTypeFile.getPickup_price());
                        productUpdateType.setText(wswdWaterTypeFile.getWater_type());
                        if (wswdWaterTypeFile.getWater_status().toLowerCase().equals("available".toLowerCase()))
                        {
                            valid.setChecked(true);
                            invalid.setChecked(false);
                            statusGet = "available";
                        }
                        else if (wswdWaterTypeFile.getWater_status().toLowerCase().equals("unavailable".toLowerCase()))
                        {
                            valid.setChecked(false);
                            invalid.setChecked(true);
                            statusGet = "unavailable";
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return view;
    }

    public void updateData() {
        String statY = "";
        if (valid.isChecked()) {
            statY = "available";
        } else if (invalid.isChecked()) {
            statY = "unavailable";
        }

        String prodName = productName.getEditText().getText().toString();
        String prodType = productUpdateType.getText().toString();
        String prodPickup = productUpdatePickup.getEditText().getText().toString();
        String prodDelivery = productUpdateDelivery.getEditText().getText().toString();
        String prodDescription = productDescription.getEditText().getText().toString();

        if (prodName.isEmpty() && prodType.isEmpty()
            && prodPickup.isEmpty() && prodDelivery.isEmpty()
            && prodDescription.isEmpty())
        {
            showMessage("Fields should not empty");
        }
        else {
            dataConnection(prodName, prodType, prodPickup, prodDelivery, prodDescription, statY);
        }


    }

    //Display Data~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //CloseRetrieving Data~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void dataConnection(String prodName, String prodType, String prodPickup, String prodDelivery, String prodDescription, String statY) {
        progressDialog.show();
        WSWDWaterTypeFile userWSWDWaterTypeFile = new WSWDWaterTypeFile(
                firebaseUser.getUid(),
                prodName,
                prodType,
                prodPickup,
                prodDelivery,
                prodDescription,
                statY
        );
        databaseReference.child(firebaseUser.getUid()).child(prodType).setValue(userWSWDWaterTypeFile)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Product updated successfully");
                    progressDialog.dismiss();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage("Failed to Update, Please check your internet connection");
                    progressDialog.dismiss();
                }
            });
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.waterUpdateBack:
                WSProductListFragment additem = new WSProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.waterUpdateAdd:
                updateData();
                break;

        }
    }
}
