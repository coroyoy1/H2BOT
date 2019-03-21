package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.administrator.h2bot.tpaaffiliate.TPAAcceptedFragment;
import com.example.administrator.h2bot.waterstation.WSProductListFragment;
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

public class WPProductAdd extends Fragment implements View.OnClickListener {

    EditText waterProductName, waterProductPrice;
    Spinner waterProductType;
    Button backProductButton, addProductButton;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<String> list;

    ArrayAdapter<String> adapterAdapt;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_product_add, container, false);
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

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        attemptToExit();
                        return true;
                    }
                }
                return false;
            }
        });
        List<String> array1 = new ArrayList<String>();
        List<String> array2 = new ArrayList<String>();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Water_Type_File");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {
                            String userTypes = dataSnapshot1.child("prodName").getValue(String.class);
                            array1.add(userTypes);
                        }
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
                        reference2.child(firebaseUser.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren())
                                        {
                                            String userTypes2 = dataSnapshot2.child("water_type").getValue(String.class);
                                            array2.add(userTypes2);
                                        }
                                        array1.removeAll(array2);
                                        adapterAdapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, array1);
                                        adapterAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        waterProductType.setAdapter(adapterAdapt);
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

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        attemptToExit();
                        return true;
                    }
                }
                return false;
            }
        });


        return view;
    }

    public void saveData()
    {
        String waterPriceString = waterProductPrice.getText().toString();
        String waterTypeString = waterProductType.getSelectedItem().toString();

        if(waterPriceString.isEmpty() || waterTypeString.isEmpty())
        {
            showMessages("Fill up all the fields");
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
                    WPProductListFragment detail = new WPProductListFragment();
                    AppCompatActivity activity = (AppCompatActivity)getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp, detail).addToBackStack(null).commit();
                    progressDialog.dismiss();
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
    public void attemptToExit()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }
}
