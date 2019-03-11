package com.example.administrator.h2bot.deliveryman;

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
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSCompletedAccept;
import com.example.administrator.h2bot.waterstation.WSTransactionsFragment;
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

public class DMCompletedAcception extends Fragment implements View.OnClickListener {


    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice, deliveryMethod, deliveryDate;
    Button backButton;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser;
    CircleImageView imageView;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_completeorderacception, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        orderNo = view.findViewById(R.id.orderNoCOMDMDM);
        customer = view.findViewById(R.id.customerCOMDMDM);
        contactNo = view.findViewById(R.id.contactNoCOMDMDM);
        waterType = view.findViewById(R.id.waterTypeCOMDMDM);
        itemQuantity = view.findViewById(R.id.itemQuantityCOMDMDM);
        pricePerGallon = view.findViewById(R.id.pricePerGallonCOMDMDM);
        service = view.findViewById(R.id.serviceCOMDMDM);
        address = view.findViewById(R.id.addressCOMDMDM);
        deliveryFee = view.findViewById(R.id.deliveryFeeCOMDMDM);
        totalPrice = view.findViewById(R.id.totalPriceCOMDMDM);
        backButton = view.findViewById(R.id.backCOMDMDM);
        imageView = view.findViewById(R.id.imageViewCOMDMDM);
      //  deliveryMethod = view.findViewById(R.id.MethodCOMDMDM);
        deliveryDate = view.findViewById(R.id.datedeliveredCOMDMDM);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        progressDialog.show();

        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            transactionNo = bundle.getString("transactionno");
        }
        displayAllData();

        backButton.setOnClickListener(this);

        return view;
    }

    private void displayAllData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserWSDMFile userWSDMFile = dataSnapshot1.child(firebaseUser.getUid()).getValue(UserWSDMFile.class);
                    if (userWSDMFile != null) {
                        String getStationId = userWSDMFile.getStation_id();
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {
                                    for (DataSnapshot post : dataSnapshot1.child(getStationId).getChildren())
                                    {
                                        OrderModel orderModel = post.getValue(OrderModel.class);
                                        if(orderModel != null)
                                        {
                                            if(orderModel.getOrder_merchant_id().equals(getStationId)
                                                    && orderModel.getOrder_no().equals(transactionNo))
                                            {
                                                if(orderModel.getOrder_status().equals("Completed")) {
                                                    orderNo.setText(orderModel.getOrder_no());
                                                    itemQuantity.setText(orderModel.getOrder_qty());
                                                    pricePerGallon.setText(orderModel.getOrder_price_per_gallon());
                                                    totalPrice.setText(orderModel.getOrder_total_amt());
                                                    waterType.setText(orderModel.getOrder_water_type());
                                                    address.setText(orderModel.getOrder_address());
                                                    //deliveryMethod.setText(orderModel.getOrder_delivery_method());
                                                    //inprogressText.setText(orderModel.getOrder_status());

                                                    DateTime date = new DateTime(orderModel.getOrder_delivery_date());
                                                    String dateString = date.toLocalDate().toString();

                                                    deliveryDate.setText(dateString);
                                                    deliveryFee.setText(orderModel.getOrder_delivery_fee());

                                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("User_File");
                                                    reference2.child(orderModel.getOrder_customer_id())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    UserFile userFile = dataSnapshot.getValue(UserFile.class);
                                                                    if (userFile != null) {
                                                                        String customerPicture = userFile.getUser_uri();
                                                                        Picasso.get().load(customerPicture).into(imageView);
                                                                        contactNo.setText(userFile.getUser_phone_no());
                                                                        String fullname = userFile.getUser_firtname() + " " + userFile.getUser_lastname();
                                                                        customer.setText(fullname);
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    progressDialog.dismiss();
                                                                }
                                                            });

                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backCOMDMDM:
                DMCompleteFragment additem = new DMCompleteFragment();
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

}
