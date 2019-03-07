package com.example.administrator.h2bot.dealer;

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

public class WPCompletedAccept extends Fragment implements View.OnClickListener {

    TextView orderNo, deliveryDate, customer, contactNo, waterType, itemQuantity, pricePerGallon,  service, address, deliveryFee, totalPrice, deliveryMethod;
    Button backButton;
    String orderNoGET, customerNoGET, merchantNOGET, transactionNo, dataIssuedGET, deliveryStatusGET
            ,transStatusGET, transTotalAmountGET, transDeliveryFeeGET, transTotalNoGallonGET,
            transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail
            ,transStatusDetail, transWaterTypeDetail, customerIDUser, contactNoUser, customerNo ;
    CircleImageView imageView;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_completedtransactionacception, container, false);

        orderNo = view.findViewById(R.id.orderNoCOMACC);
        customer = view.findViewById(R.id.customerCOMACC);
        contactNo = view.findViewById(R.id.contactNoCOMACC);
        waterType = view.findViewById(R.id.waterTypeCOMACC);
        itemQuantity = view.findViewById(R.id.itemQuantityCOMACC);
        pricePerGallon = view.findViewById(R.id.pricePerGallonCOMACC);
        service = view.findViewById(R.id.serviceCOMACC);
        address = view.findViewById(R.id.addressCOMACC);
        deliveryFee = view.findViewById(R.id.deliveryFeeCOMACC);
        totalPrice = view.findViewById(R.id.totalPriceCOMACC);
        backButton = view.findViewById(R.id.backCOMACC);
        imageView = view.findViewById(R.id.imageViewINACC);
        deliveryMethod = view.findViewById(R.id.MethodCOMACC);
        deliveryDate = view.findViewById(R.id.datedeliveredCOMACC);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog.show();

        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            transactionNo = bundle.getString("transactionno");
            customerNo = bundle.getString("transactioncustomer");
        }
        getOrderData();

        return view;
    }

    public void getOrderData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Merchant_Customer_File");
        reference.child(firebaseUser.getUid()).child(customerNo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MerchantCustomerFile merchantCustomerFile = dataSnapshot.getValue(MerchantCustomerFile.class);
                        if(merchantCustomerFile != null)
                        {
                            String customerId = merchantCustomerFile.getCustomer_id();
                            String merchantId = merchantCustomerFile.getStation_id();
                            String status = merchantCustomerFile.getStatus();
                            if(status.equals("AC"))
                            {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_Order_File");
                                reference1.child(customerId).child(merchantId).child(transactionNo)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                                                if (orderModel != null) {
                                                    if (orderModel.getOrder_status().equals("Completed")) {
                                                        orderNo.setText(orderModel.getOrder_no());
                                                        itemQuantity.setText(orderModel.getOrder_qty());
                                                        pricePerGallon.setText(orderModel.getOrder_price_per_gallon());
                                                        totalPrice.setText(orderModel.getOrder_total_amt());
                                                        waterType.setText(orderModel.getOrder_water_type());
                                                        address.setText(orderModel.getOrder_address());
                                                        deliveryMethod.setText(orderModel.getOrder_delivery_method());

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

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
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
            case R.id.backCOMACC:
                WPCompletedAccept additem = new WPCompletedAccept();
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
}
