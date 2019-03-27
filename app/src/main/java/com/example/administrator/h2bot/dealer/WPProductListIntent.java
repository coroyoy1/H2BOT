package com.example.administrator.h2bot.dealer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.waterstation.WSProductListFragment;
import com.example.administrator.h2bot.waterstation.WSProductListUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WPProductListIntent extends Fragment implements View.OnClickListener {
    TextView productName, itemP, itemU, itemS,itemDesc;
    Button backBu, updateBu, deleteButton;
    String itemUi, itemNameString, itemPriceString, itemTypeString, itemStatusString, itemDescString;


    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_productlistintent, container, false);
        itemP = view.findViewById(R.id.PLIprice);
        itemU = view.findViewById(R.id.PLItype);
        itemS = view.findViewById(R.id.PLIStatus);
        itemDesc = view.findViewById(R.id.itemDesc);
        productName = view.findViewById(R.id.productName);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        getProductDetail();
        databaseReference = firebaseDatabase.getReference("User_WS_WD_Water_Type_File");

        backBu = view.findViewById(R.id.PLIbackbutton);
        updateBu = view.findViewById(R.id.PLIupdatebutton);
        deleteButton = view.findViewById(R.id.PLIDeletebutton);

        deleteButton.setOnClickListener(this);
        backBu.setOnClickListener(this);
        updateBu.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String itemPr = bundle.getString("ItemPriceMDA");
            String itemTy = bundle.getString("ItemTypeMDA");
            String itemSt = bundle.getString("ItemStatusMDA");
            String itemD = bundle.getString("ItemDesc");

            itemPriceString = bundle.getString("ItemPriceMDA");
            itemTypeString = bundle.getString("ItemTypeMDA");
            itemStatusString = bundle.getString("ItemStatusMDA");
            itemDescString = bundle.getString("ItemDesc");

        }
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

    public void deleteData()
    {
        progressDialog.show();
        databaseReference.child(firebaseUser.getUid()).child(itemTypeString).removeValue()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Deleted successfully");
                    WPProductListFragment additem = new WPProductListFragment();
                    AppCompatActivity activity = (AppCompatActivity)getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container_wp, additem)
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

    public String DataID(String itemUD)
    {
        return itemUD;
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
    public String Datadesc(String itemdesc)
    {
        return itemdesc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.PLIbackbutton:
                WPProductListFragment additem = new WPProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_wp, additem)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.PLIupdatebutton:
                String uidString = DataID(itemUi);
                String typeString = DataType(itemTypeString);
                String priceString = DataPrice(itemPriceString);
                String statusString = DataStatus(itemStatusString);
                String desc = Datadesc(itemDescString);
                WPProductListUpdate updateitem = new WPProductListUpdate();
                AppCompatActivity activityapp = (AppCompatActivity) v.getContext();
                activityapp.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_wp, updateitem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("ItemUidPLI", uidString);
                args.putString("ItemPricePLI", priceString);
                args.putString("ItemTypePLI", typeString);
                args.putString("ItemStatusPLI", statusString);
                args.putString("ItemDesc", desc);
                updateitem.setArguments(args);
                break;
            case R.id.PLIDeletebutton:
                deleteData();
                break;
        }
    }
    public void getProductDetail()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    productName.setText("  Water Gallon Name: " + data.child("water_name").getValue(String.class));
                    itemP.setText("  Price: " + data.child("delivery_price").getValue(String.class));
                    itemU.setText("  Type: " + data.child("water_type").getValue(String.class));
                    itemS.setText("  Status: " + data.child("water_status").getValue(String.class));
                    itemDesc.setText("  Product Description" + data.child("water_description").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
