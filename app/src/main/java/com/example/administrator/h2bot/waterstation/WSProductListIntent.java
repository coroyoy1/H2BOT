package com.example.administrator.h2bot.waterstation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class WSProductListIntent extends Fragment implements View.OnClickListener {
    TextView itemN, itemP, itemU;
    Button backBu, updateBu, deleteButton;
    String itemUi, itemNameString, itemPriceString, itemTypeString, itemStatusString, itemKeyString;


    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlistintent, container, false);
        itemN = view.findViewById(R.id.PLIitemname);
        itemP = view.findViewById(R.id.PLIprice);
        itemU = view.findViewById(R.id.PLItype);

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
            String itemNa = bundle.getString("ItemNameMDA");
            String itemPr = bundle.getString("ItemPriceMDA");
            String itemTy = bundle.getString("ItemTypeMDA");
            String itemSt = bundle.getString("ItemStatusMDA");
            String itemKey = bundle.getString("ItemKeyMDA");

            itemUi = bundle.getString("ItemUidMDA");
            itemN.setText("Item Name: "+itemNa);
            itemP.setText("    Price: "+itemPr);
            itemU.setText("     Type: "+itemTy);

            itemNameString = bundle.getString("ItemNameMDA");
            itemPriceString = bundle.getString("ItemPriceMDA");
            itemTypeString = bundle.getString("ItemTypeMDA");
            itemStatusString = bundle.getString("ItemStatusMDA");
            itemKeyString = bundle.getString("ItemKeyMDA");


        }
        return view;
    }

    public void deleteData()
    {
        progressDialog.show();
        String deleteKey = DataKey(itemKeyString);
        databaseReference.child(firebaseUser.getUid()).child(deleteKey).removeValue()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Successfully Deleted");
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
                    showMessage("Error to delete data, Please check internet connection!");
                    progressDialog.dismiss();
                }
            });
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
    }

    public String DataID(String itemUD)
    {
        return itemUD;
    }
    public String DataName(String itemName)
    {
        return itemName;
    }
    public String DataPrice(String itemPrice)
    {
        return itemPrice;
    }
    public String DataType(String itemType)
    {
        return itemType;
    }
    public String DataStatus(String itemStatus)
    {
        return itemStatus;
    }
    public String DataKey(String itemKey)
    {
        return itemKey;
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
                String uidString = DataID(itemUi);
                String nameString = DataName(itemNameString);
                String typeString = DataType(itemTypeString);
                String priceString = DataPrice(itemPriceString);
                String statusString = DataStatus(itemStatusString);
                String keyString = DataKey(itemKeyString);

                WSProductListUpdate updateitem = new WSProductListUpdate();
                AppCompatActivity activityapp = (AppCompatActivity) v.getContext();
                activityapp.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, updateitem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("ItemUidPLI", uidString);
                args.putString("ItemNamePLI", nameString);
                args.putString("ItemPricePLI", priceString);
                args.putString("ItemTypePLI", typeString);
                args.putString("ItemStatusPLI", statusString);
                args.putString("ItemKeyPLI", keyString);
                updateitem.setArguments(args);
                break;
            case R.id.PLIDeletebutton:
                deleteData();
                break;
        }
    }
}
