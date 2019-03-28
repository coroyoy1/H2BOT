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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
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

public class WSProductListIntent extends Fragment implements View.OnClickListener {
    TextView itemN, itemP, itemU, itemS, itemName, itemDescription, itemD;
    Button backBu, updateBu, deleteButton;
    String itemUi, itemNameString, itemPriceString, itemTypeString, itemStatusString, itemKeyString;

    String itemPr, itemTy, itemSt, itemDel, itemDesc, itemNa;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;
    String itemDeliveryString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlistintent, container, false);
        itemP = view.findViewById(R.id.PLIprice);
        itemU = view.findViewById(R.id.PLItype);
        itemS = view.findViewById(R.id.PLIStatus);
        itemD = view.findViewById(R.id.PLIdelivery);
        itemDescription = view.findViewById(R.id.PLIDescription);
        itemN = view.findViewById(R.id.PLIProdName);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("User_WS_WD_Water_Type_File");

        backBu = view.findViewById(R.id.PLIbackbutton);
        updateBu = view.findViewById(R.id.PLIupdatebutton);
        deleteButton = view.findViewById(R.id.PLIDeletebutton);

        deleteButton.setOnClickListener(this);
        backBu.setOnClickListener(this);
        updateBu.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            itemTy = bundle.getString("ItemTypeMDA");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File");
        databaseReference.child(firebaseUser.getUid()).child(itemTy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        WSWDWaterTypeFile wswdWaterTypeFile = dataSnapshot.getValue(WSWDWaterTypeFile.class);
                        if (wswdWaterTypeFile != null)
                        {
                            itemDescription.setText("Description: "+wswdWaterTypeFile.getWater_description());
                            itemN.setText(wswdWaterTypeFile.getWater_name());
                            itemP.setText("Pickup Price Per Gallon: Php "+wswdWaterTypeFile.getPickup_price_per_gallon());
                            itemU.setText("Water Type: "+wswdWaterTypeFile.getWater_type());
                            itemD.setText("Delivery Price Per Gallon: Php "+wswdWaterTypeFile.getDelivery_price_per_gallon());
                            itemS.setText("Status: "+wswdWaterTypeFile.getWater_status());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        }
        return view;
    }

    public void deleteData()
    {
        progressDialog.show();
        databaseReference.child(firebaseUser.getUid()).child(itemTy).removeValue()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Deleted successfully");
                    WSProductListFragment additem = new WSProductListFragment();
                    AppCompatActivity activity = (AppCompatActivity)getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container_ws, additem)
                            .addToBackStack(null)
                            .commit();
                    progressDialog.dismiss();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage("Failed to delete the data, Please check internet connection!");
                    progressDialog.dismiss();
                }
            });
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.PLIbackbutton:
                WSProductListFragment additem = new WSProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.PLIupdatebutton:
                WSProductListUpdate updateitem = new WSProductListUpdate();
                AppCompatActivity activityapp = (AppCompatActivity) v.getContext();
                activityapp.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, updateitem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("ItemPricePLI", itemPr);
                args.putString("ItemTypePLI", itemTy);
                args.putString("ItemStatusPLI", itemSt);
                args.putString("ItemDescPLI", itemDesc);
                args.putString("ItemDeliveryPLI", itemDel);
                args.putString("ItemNamePLI", itemNa);
                updateitem.setArguments(args);
                break;
            case R.id.PLIDeletebutton:
                deleteData();
                break;
        }
    }
}
