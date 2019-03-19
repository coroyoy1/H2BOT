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
import android.widget.RadioButton;
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
import java.util.Collections;
import java.util.List;

public class WSProductListUpdate extends Fragment implements View.OnClickListener {

    EditText productUpdateName, productUpdatePrice;
    Spinner productUpdateStatus, productUpdateType;
    Button backUpItem, updateUpItem;
    RadioButton valid, invalid;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<String> list;

    String statusGet;
    String keyGet;

    ProgressDialog progressDialog;
    String[] arraySpinner;
    ArrayAdapter<String> adapter;

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

        productUpdatePrice = view.findViewById(R.id.waterUpdatePrice);
        productUpdateType = view.findViewById(R.id.waterUpdateSpinner);

        productUpdateType.setAdapter(adapter);

        valid = view.findViewById(R.id.avaiableRadio);
        invalid = view.findViewById(R.id.unavailableRadio);

        backUpItem = view.findViewById(R.id.waterUpdateBack);
        updateUpItem = view.findViewById(R.id.waterUpdateAdd);

        backUpItem.setOnClickListener(this);
        updateUpItem.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String itemPr = bundle.getString("ItemPricePLI");
            String itemTy = bundle.getString("ItemTypePLI");
            String itemUi = bundle.getString("ItemUidPLI");
            String itemSt = bundle.getString("ItemStatusPLI");

            if (itemSt.equals("active")) {
                valid.setChecked(true);
                invalid.setChecked(false);
                statusGet = "active";
            } else {
                valid.setChecked(false);
                invalid.setChecked(true);
                statusGet = "inactive";
            }
        }

        retrieveData();

        return view;
    }

    public void updateData() {
        String statY = "";
        if (valid.isChecked()) {
            statY = "active";
        } else if (invalid.isChecked()) {
            statY = "inactive";
        }
        String prodType = productUpdateType.getSelectedItem().toString();
        String prodPrice = productUpdatePrice.getText().toString();

        if (prodType.isEmpty() && prodPrice.isEmpty()) {
            showMessage("Please fill up all the fields");
            return;
        } else {
            dataConnection(prodType, prodPrice, statY);
        }

    }

    //Display Data~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void retrieveData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Water_Type_File");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String waterTypes = dataSnapshot1.getValue(String.class);
                    list.add(waterTypes);
                }
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productUpdateType.setAdapter(adapter);
                if (list != null)
                {
                    retrieveDataBasedOnUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("No data available");
            }
        });
    }

    //Close Display Data~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Retrieving Data~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void retrieveDataBasedOnUser()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    UserWSWDWaterTypeFile userWS = dataSnapshot1.getValue(UserWSWDWaterTypeFile.class);
                    if (userWS != null)
                    {
                        String waterType = userWS.getWater_type();
                        for (int counter = 0; counter < adapter.getCount(); counter++)
                        {
                            if (adapter.getItem(counter).equalsIgnoreCase(waterType))
                            {
                                productUpdateType.setSelection(counter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("No data available");
            }
        });
    }

    //CloseRetrieving Data~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private void dataConnection(String prodType, String prodPrice, String prodStat) {
        progressDialog.show();
        UserWSWDWaterTypeFile userWSWDWaterTypeFile = new UserWSWDWaterTypeFile(
                firebaseUser.getUid(),
                prodType,
                prodPrice,
                prodStat
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
