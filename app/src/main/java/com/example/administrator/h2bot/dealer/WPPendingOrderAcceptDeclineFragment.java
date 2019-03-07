package com.example.administrator.h2bot.dealer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WPPendingOrderAcceptDeclineFragment extends Fragment implements View.OnClickListener {
    Button acceptButton, declineButton, viewLocationButton;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
   // private List<TransactionHeaderFileModel> headerPO;
   // private List<TransactionDetailFileModel> detailPO;
    //private PendingListAdapter POAdapter;
    ProgressDialog progressDialog;
    String transactionNo;

    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon, service, deliveryFee, totalPrice, address, deliveryMethod, deliveryDate;
    String transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail, transStatusDetail, transWaterTypeDetail;
    String customerIDUser, contactNoUser;
    String orderNoGET , customerNoGET , merchantNOGET , dataIssuedGET , deliveryStatusGET , transStatusGET , transTotalAmountGET , transDeliveryFeeGET, transTotalNoGallonGET;
    CircleImageView imageView;
    String customerNo;
    Bundle args;
     public WPPendingOrderAcceptDeclineFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_pendingacception, container, false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        progressDialog.show();

        orderNo = view.findViewById(R.id.orderNoPOACC);
        customer = view.findViewById(R.id.customerPOACC);
        contactNo = view.findViewById(R.id.contactNoPOACC);
        waterType = view.findViewById(R.id.waterTypePOACC);
        itemQuantity = view.findViewById(R.id.itemQuantityPOACC);
        pricePerGallon = view.findViewById(R.id.pricePerGallonPOACC);
        deliveryFee = view.findViewById(R.id.deliveryFeePOACC);
        totalPrice = view.findViewById(R.id.totalPricePOACC);
        imageView = view.findViewById(R.id.imageViewPOACC);
        address = view.findViewById(R.id.addressPOACC);
        deliveryMethod = view.findViewById(R.id.MethodPOACC);
        deliveryDate = view.findViewById(R.id.datedeliveredPOACC);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        acceptButton = view.findViewById(R.id.acceptPOA);
        declineButton = view.findViewById(R.id.declinePOA);
        viewLocationButton = view.findViewById(R.id.viewLocationButtonPOA);

        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
        viewLocationButton.setOnClickListener(this);

        args = this.getArguments();
        if(args != null)
        {
            transactionNo = args.getString("transactNoString");
            customerNo = args.getString("transactioncustomer");
            showMessages(transactionNo);
        }
        getOrderData();
        return view;
    }

    private void showMessages(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
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
                                                if(orderModel != null)
                                                {
                                                    if(orderModel.getOrder_status().equals("Pending")) {
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
                showMessages("Data does not available");
                progressDialog.dismiss();
            }
        });
    }


    public void dialogView()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        progressDialog.show();
                        updateStatus();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to accept the order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    public void dialogView2()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        progressDialog.show();
                        updateStatusCancelled();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to decline the order?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void updateStatus()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    if (postSnap.child("trans_no").getValue(String.class).equals(transactionNo)
                            && postSnap.child("trans_status").getValue(String.class).equals("Pending")) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
                        reference1.child(transactionNo).child("trans_status").setValue("In-Progress")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
                                databaseReference.child(transactionNo).child("trans_status").setValue("In-Progress")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showMessages("Successfully updated");
                                            WPInProgressFragment additem = new WPInProgressFragment();
                                            AppCompatActivity activity = (AppCompatActivity)getContext();
                                            activity.getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                    .replace(R.id.fragment_container_wp, additem)
                                                    .addToBackStack(null)
                                                    .commit();
                                            Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
                                            progressDialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showMessages("Failed to update task, please check internet connection");
                                            progressDialog.dismiss();
                                        }
                                    });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessages("Failed to update data");
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Data cant be retrieve");
            }
        });
    }
    private void updateStatusCancelled()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    if (postSnap.child("trans_no").getValue(String.class).equals(transactionNo)
                            && postSnap.child("trans_status").getValue(String.class).equals("Pending")) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
                        reference1.child(transactionNo).child("trans_status").setValue("Cancelled")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
                                        databaseReference.child(transactionNo).child("trans_status").setValue("Cancelled")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        showMessages("Successfully updated");
                                                        WPPendingOrdersFragment additem = new WPPendingOrdersFragment();
                                                        AppCompatActivity activity = (AppCompatActivity)getContext();
                                                        activity.getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                                .replace(R.id.fragment_container_wp, additem)
                                                                .addToBackStack(null)
                                                                .commit();
                                                        Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("In-Progress");
                                                        progressDialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        showMessages("Failed to update task, please check internet connection");
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessages("Failed to update data");
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessages("Data cant be retrieve");
            }
        });
    }

    public void viewLocationPass()
    {
        MapMerchantFragmentRenew additem = new MapMerchantFragmentRenew();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_wp, additem)
                .addToBackStack(null)
                .commit();
        args.putString("TransactNoSeen1", transactionNo);
        additem.setArguments(args);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.acceptPOA:
                dialogView();
                break;
            case R.id.declinePOA:
                dialogView2();
                break;
            case R.id.viewLocationButtonPOA:
                viewLocationPass();
                break;
        }
    }
}
