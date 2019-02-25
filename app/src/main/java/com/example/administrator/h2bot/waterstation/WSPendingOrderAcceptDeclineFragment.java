package com.example.administrator.h2bot.waterstation;
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
import com.example.administrator.h2bot.maps.MapMerchantFragment;
import com.example.administrator.h2bot.maps.MapMerchantFragmentRenew;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.android.gms.maps.GoogleMap;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WSPendingOrderAcceptDeclineFragment  extends Fragment implements View.OnClickListener {
    Button acceptButton, declineButton, viewLocationButton;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
   // private List<TransactionHeaderFileModel> headerPO;
   // private List<TransactionDetailFileModel> detailPO;
    //private PendingListAdapter POAdapter;
    ProgressDialog progressDialog;
    String transactionNo;

    TextView orderNo, customer, contactNo, waterType, itemQuantity, pricePerGallon, service, deliveryFee, totalPrice;
    String transDeliveryFeePerGallonDetail, transNoDetail, transNoOfGallonDetail, transPartialAmountDetail, transPricePerGallonDetail, transStatusDetail, transWaterTypeDetail;
    String customerIDUser, contactNoUser;
    String orderNoGET , customerNoGET , merchantNOGET , dataIssuedGET , deliveryStatusGET , transStatusGET , transTotalAmountGET , transDeliveryFeeGET, transTotalNoGallonGET;
    CircleImageView imageView;
    Bundle bundle;
     public WSPendingOrderAcceptDeclineFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_pendingacception, container, false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);

        progressDialog.show();

        orderNo = view.findViewById(R.id.orderNoPOACC);
        customer = view.findViewById(R.id.customerNamePOACC);
        contactNo = view.findViewById(R.id.contactNoPOACC);
        waterType = view.findViewById(R.id.waterTypePOACC);
        itemQuantity = view.findViewById(R.id.itemQuantityPOACC);
        pricePerGallon = view.findViewById(R.id.pricePerGallonPOACC);
        service = view.findViewById(R.id.servicePOACC);
        deliveryFee = view.findViewById(R.id.deliveryFeePOACC);
        totalPrice = view.findViewById(R.id.totalPricePOACC);
        imageView = view.findViewById(R.id.customerImagePOACC);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        acceptButton = view.findViewById(R.id.acceptPOA);
        declineButton = view.findViewById(R.id.declinePOA);
        viewLocationButton = view.findViewById(R.id.viewLocationButtonPOA);

        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
        viewLocationButton.setOnClickListener(this);

        Bundle args = this.getArguments();
        if(args != null)
        {
            transactionNo = args.getString("transactNoString");
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren())
                {
                    if(postSnapShot.child("trans_no").getValue(String.class).equals(transactionNo)
                            && postSnapShot.child("trans_status").getValue(String.class).equals("Pending"))
                    {
                        orderNoGET = postSnapShot.child("trans_no").getValue(String.class);
                        customerNoGET = postSnapShot.child("customer_id").getValue(String.class);
                        merchantNOGET = postSnapShot.child("merchant_id").getValue(String.class);
                        dataIssuedGET = postSnapShot.child("trans_date_issued").getValue(String.class);
                        deliveryStatusGET = postSnapShot.child("trans_delivered_service").getValue(String.class);
                        transStatusGET = postSnapShot.child("trans_status").getValue(String.class);
                        transTotalAmountGET = postSnapShot.child("trans_total_amount").getValue(String.class);
                        transDeliveryFeeGET = postSnapShot.child("trans_total_delivery_fee").getValue(String.class);
                        transTotalNoGallonGET = postSnapShot.child("trans_total_no_of_gallons").getValue(String.class);

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot postSnap : dataSnapshot.getChildren())
                                {
                                    if(postSnap.child("trans_no").getValue(String.class).equals(transactionNo))
                                    {
                                        transDeliveryFeePerGallonDetail = postSnap.child("trans_delivery_fee_per_gallon").getValue(String.class);
                                        transNoDetail = postSnap.child("trans_no").getValue(String.class);
                                        transNoOfGallonDetail = postSnap.child("trans_no_of_gallons").getValue(String.class);
                                        transPartialAmountDetail = postSnap.child("trans_partial_amount").getValue(String.class);
                                        transPricePerGallonDetail = postSnap.child("trans_price_per_gallon").getValue(String.class);
                                        transStatusDetail = postSnap.child("trans_status").getValue(String.class);
                                        transWaterTypeDetail = postSnap.child("trans_water_type").getValue(String.class);
                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User_File");
                                        databaseReference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot post : dataSnapshot.getChildren())
                                                {
                                                    if(customerNoGET.equals(post.child("user_getUID").getValue(String.class)))
                                                    {
                                                        customerIDUser = post.child("user_firtname").getValue(String.class)+" "+post.child("user_lastname").getValue(String.class);
                                                        contactNoUser = post.child("user_phone_no").getValue(String.class);
                                                        String imageUi = post.child("user_uri").getValue(String.class);
                                                        Picasso.get().load(imageUi).fit().centerCrop().into(imageView);
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                                orderNo.setText(orderNoGET);
                                                customer.setText(customerIDUser);
                                                contactNo.setText(contactNoUser);
                                                waterType.setText(transWaterTypeDetail);
                                                itemQuantity.setText(transTotalNoGallonGET);
                                                pricePerGallon.setText(transPricePerGallonDetail);
                                                service.setText(deliveryStatusGET);
                                                deliveryFee.setText(transDeliveryFeeGET);
                                                totalPrice.setText(transTotalAmountGET);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                showMessages("User file does not have this data");
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        showMessages("Data is not available");
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                showMessages("Data is not available");
                                progressDialog.dismiss();
                            }
                        });
                    }
                    else
                    {
                        showMessages("Data does not available");
                        progressDialog.dismiss();
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
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to accept?").setPositiveButton("Yes", dialogClickListener)
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
                                            WSInProgressFragment additem = new WSInProgressFragment();
                                            AppCompatActivity activity = (AppCompatActivity)getContext();
                                            activity.getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                    .replace(R.id.fragment_container_ws, additem)
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
                .replace(R.id.fragment_container_ws, additem)
                .addToBackStack(null)
                .commit();
        bundle = new Bundle();
        bundle.putString("TransactNoSeen1", transactionNo);
        additem.setArguments(bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.acceptPOA:
                dialogView();
                break;
            case R.id.declinePOA:
                break;
            case R.id.viewLocationButtonPOA:
                viewLocationPass();
                break;
        }
    }
}
