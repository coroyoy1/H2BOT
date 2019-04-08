package com.example.administrator.h2bot.tpaaffiliate;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import de.hdodenhof.circleimageview.CircleImageView;

public class TPADeliveredInfoFragment extends Fragment implements View.OnClickListener {

    TextView orderNo,customerName,customerAddress,customerContactNo,waterStationName,stationAddress,stationContactNo,expectedDate,pricePerGallon,quantity,waterType,totalPrice,userTypeDirection;
    Button okButton,switchUser;
    Button backButton;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser, customerNo ;
    CircleImageView imageView;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    ImageView imageviewprofile;
    public String stationID,customerID,orderNumber;
    Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tpa_delivered_info_fragment, container, false);

        orderNo = view.findViewById(R.id.orderNo);
        customerName = view.findViewById(R.id.customerName);
        customerAddress = view.findViewById(R.id.customerAddress);
        customerContactNo = view.findViewById(R.id.customerContactNo);
        waterStationName = view.findViewById(R.id.waterStationName);
        stationAddress = view.findViewById(R.id.stationAddress);
        stationContactNo = view.findViewById(R.id.stationContactNo);
        expectedDate = view.findViewById(R.id.expectedDate);
        pricePerGallon = view.findViewById(R.id.pricePerGallon);
        quantity = view.findViewById(R.id.quantity);
        waterType = view.findViewById(R.id.waterType);
        totalPrice = view.findViewById(R.id.totalPrice);
        okButton = view.findViewById(R.id.okButton);
        imageviewprofile = view.findViewById(R.id.imageviewprofile);

        getBunle();
        getFromCustomerFile();
        getFromBusinessFile();
        getFromUserFile();
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

    public void getBunle()
    {
        bundle = this.getArguments();
        if (bundle != null)
        {
            orderNumber = bundle.getString("orderno");
            customerID = bundle.getString("customerid");
            stationID = bundle.getString("stationid");

            //Log.d("Popo",orderNumber+","+customerID+","+stationID);
        }
    }
    public void getFromCustomerFile()
    {
        Log.d("Popo",orderNumber+","+customerID+","+stationID);
        DatabaseReference customerFile = FirebaseDatabase.getInstance().getReference("Customer_File").child(customerID).child(stationID).child(orderNumber);
        customerFile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderNo.setText(orderNumber);
                customerAddress.setText(dataSnapshot.child("order_address").getValue(String.class));
                expectedDate.setText(dataSnapshot.child("order_date").getValue(String.class));
                pricePerGallon.setText(dataSnapshot.child("order_price_per_gallon").getValue(String.class));
                quantity.setText(dataSnapshot.child("order_qty").getValue(String.class));
                totalPrice.setText(dataSnapshot.child("order_total_amt").getValue(String.class));
                waterType.setText(dataSnapshot.child("order_water_type").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getFromBusinessFile()
    {
        DatabaseReference businessfile = FirebaseDatabase.getInstance().getReference("User_WS_Business_Info_File").child(stationID);
        businessfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stationAddress.setText(dataSnapshot.child("business_address").getValue(String.class));
                stationContactNo.setText(dataSnapshot.child("business_tel_no").getValue(String.class));
                waterStationName.setText(dataSnapshot.child("business_name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getFromUserFile()
    {
        DatabaseReference userfile = FirebaseDatabase.getInstance().getReference("User_File").child(customerID);
        userfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerAddress.setText(dataSnapshot.child("user_firstname").getValue(String.class)+" "+dataSnapshot.child("user_lastname").getValue(String.class));
                customerAddress.setText(dataSnapshot.child("user_address").getValue(String.class));
                customerContactNo.setText(dataSnapshot.child("user_phone_no").getValue(String.class));
                Picasso.get().load(dataSnapshot.child("user_uri").getValue(String.class)).resize(200,200).into(imageviewprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backCOMACC:
                TPADeliveredInfoFragment additem = new TPADeliveredInfoFragment();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
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
