package com.example.administrator.h2bot.customer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.absampletestphase.RatingModel;
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionNoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CustomerAllOrdersAdapter extends RecyclerView.Adapter<CustomerAllOrdersAdapter.ViewHolder> {

    public static String qrCode;
    Dialog myDialog;
    private Context myContext;
    private List<TransactionNoModel> transactionList;
    private List<OrderFileModel> orderList;

    private DatabaseReference updateOrderRef;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    String deliveryDate, pickupDate;

    public CustomerAllOrdersAdapter() {
    }

    public CustomerAllOrdersAdapter(Context myContext, List<TransactionNoModel> transactionList, List<OrderFileModel> orderList) {
        this.myContext = myContext;
        this.transactionList = transactionList;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public CustomerAllOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.customer_all_orders_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAllOrdersAdapter.ViewHolder viewHolder, int i) {
        myDialog = new Dialog(myContext);
        updateOrderRef = db.getReference("Customer_File");
        final TransactionNoModel transactionNoModel = transactionList.get(i);
        final OrderFileModel orderModel = orderList.get(i);
        String orderNo = transactionNoModel.getTransOrderNo();

        String orderAddress =orderModel.getOrderAddress();
        String orderCustomerId = orderModel.getOrderCustomerId();
        String orderDateIssued = orderModel.getOrderDateIssued();
        String orderDeliveryDate = orderModel.getOrderDeliveryDate();
        String orderDeliveryCharge = orderModel.getOrderDeliveryCharge();
        String orderServiceType = orderModel.getOrderServiceType();
        String orderMerchantId = orderModel.getOrderMerchantId();
        String orderPricePerGallon = orderModel.getOrderPricePerGallon();
        String orderQty = orderModel.getOrderQty();
        String orderMethod = orderModel.getOrderMethod();
        String orderStatus = orderModel.getOrderStatus();
        String orderTotalAmt = orderModel.getOrderTotalAmt();
        String orderWaterType = orderModel.getOrderWaterType();

//        if(orderMethod.equalsIgnoreCase("Delivery")){
//            deliveryDate = orderModel.getOrderDeliveryDate();
//        }
//        else{
//            pickupDate = orderModel.getOrderDeliveryDate();
//        }

        viewHolder.order_no.setText(orderNo);
        viewHolder.status.setText(orderStatus);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                TextView order_no, water_type, price_per_gallon, qty, address, service_type, delivery_date,
                delivery_fee, status, total_amt, qr_code, order_type, date_issued, deliveryFeePerGal, partialAmount, methodText;
                Button cancelBtn, viewQrQodeBtn;
                qrCode = orderCustomerId.trim().replace(" ", "") +"/"+
                        orderMerchantId.trim().replace(" ", "") + "/" +
                        orderNo.trim().replace(" ", "");

                myDialog.setContentView(R.layout.customer_order_details_custom_dialog);
                order_no = myDialog.findViewById(R.id.order_no);
                water_type = myDialog.findViewById(R.id.water_type);
                price_per_gallon = myDialog.findViewById(R.id.price_per_gallon);
                qty = myDialog.findViewById(R.id.qty);
                address = myDialog.findViewById(R.id.address);
                service_type = myDialog.findViewById(R.id.service_type);
                delivery_date = myDialog.findViewById(R.id.delivery_date);
                delivery_fee = myDialog.findViewById(R.id.deliveryFee);
                status = myDialog.findViewById(R.id.status);
                total_amt = myDialog.findViewById(R.id.total_amt);
                qr_code = myDialog.findViewById(R.id.qr_code);
                cancelBtn = myDialog.findViewById(R.id.cancelBtn);
                viewQrQodeBtn = myDialog.findViewById(R.id.viewQrQodeBtn);
                order_type = myDialog.findViewById(R.id.order_type);
                date_issued = myDialog.findViewById(R.id.dateIssued);
                methodText = myDialog.findViewById(R.id.methodText);

                order_type.setText(orderMethod);
                order_no.setText(orderNo);
                water_type.setText(orderWaterType);
                price_per_gallon.setText(orderPricePerGallon);
                qty.setText(orderQty + " gallon(s)");
                address.setText(orderAddress);
                service_type.setText(orderServiceType);
                if(orderMethod.equalsIgnoreCase("Delivery")){
                    methodText.setText("Delivery Date: ");
                    delivery_date.setText(deliveryDate);
                }
                else{
                    methodText.setText("Pickup Date: ");
                    delivery_date.setText(pickupDate);
                }
                delivery_fee.setText(orderDeliveryCharge);
                status.setText(orderStatus);
                date_issued.setText(orderDateIssued);
                total_amt.setText("Total: " + orderTotalAmt);
                qr_code.setText(qrCode);
                if(status.getText().toString().equalsIgnoreCase("Broadcasting")
                        || status.getText().toString().equalsIgnoreCase("Dispatched")){
                    cancelBtn.setVisibility(View.GONE);
                }
                else if(status.getText().toString().equalsIgnoreCase("Cancelled")){
                    cancelBtn.setVisibility(View.GONE);
                    viewQrQodeBtn.setVisibility(View.GONE);
                }
                else if(status.getText().toString().equalsIgnoreCase("Completed")){
                    cancelBtn.setVisibility(View.GONE);
                    viewQrQodeBtn.setVisibility(View.GONE);
                }
                else{
                    cancelBtn.setVisibility(View.VISIBLE);
                    viewQrQodeBtn.setVisibility(View.VISIBLE);
                }

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderList.clear();
                        transactionList.clear();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(myContext);
                        dialog.setCancelable(false);
                        dialog.setTitle("CONFIRMATION");
                        dialog.setMessage("Do you want to cancel your order?" );
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                    updateOrderRef.child(orderCustomerId)
                                            .child(orderMerchantId)
                                            .child(orderNo)
                                            .child("order_status")
                                            .setValue("Cancelled");
                                    myDialog.dismiss();

                                    Toast.makeText(myContext, "You cancelled an order", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        final AlertDialog alert = dialog.create();
                        alert.show();
                    }
                });

                myDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView order_no;
        TextView status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_no = itemView.findViewById(R.id.order_no);
            status = itemView.findViewById(R.id.status);
        }
    }

    //Marvel
//    public void checkIfOrderCompleted(String transNo)
//    {
//        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Customer_File");
//        reference.child(firebaseUser.getUid()).child(getStation_Id).child(transNo)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
//                        if (orderModel != null)
//                        {
//                            if (orderModel.getOrder_status().equalsIgnoreCase("Completed"))
//                            {
//                                String orderNo = orderModel.getOrder_no();
//                                if (!orderNo.isEmpty())
//                                {
//                                    showDialogRate();
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }
//    public void ratingOfCustomer(String rating_customer_id, String rating_merchant_id, String rating_number, String rating_comment, String rating_status)
//    {
//
//        RatingModel ratingModel = new RatingModel(
//                rating_customer_id,
//                rating_merchant_id,
//                rating_number,
//                rating_comment,
//                "1",
//                rating_status
//        );
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ratings");
//        databaseReference.child(firebaseUser.getUid()).child("yzSnsbkYLBcQ32f11ckMHAajyT73").setValue(ratingModel)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        showMessage("Rated successfully");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        showMessage("Failed to save your feedback");
//                    }
//                });
//    }
//    public void showDialogRate()
//    {
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.z_customer_rate, null);
//
//        laterButton = dialogView.findViewById(R.id.laterRatingButton);
//        submitButton = dialogView.findViewById(R.id.submitRatingButton);
//        additonalComment = dialogView.findViewById(R.id.addtionalRate);
//        ratingBar = dialogView.findViewById(R.id.ratingStarsFeedback);
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setCancelable(false);
//        final AlertDialog alertDialog = dialogBuilder.create();
//
//        laterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String rateString = String.valueOf(ratingBar.getRating());
//                String rating_customer_id = firebaseUser.getUid(),
//                        rating_merchant_id = getStation_Id,
//                        rating_number = rateString,
//                        rating_comment = additonalComment.getText().toString(),
//                        rating_status = "AC";
//                ratingOfCustomer(rating_customer_id,rating_merchant_id, rating_number, rating_comment, rating_status);
//            }
//        });
//        alertDialog.show();
//    }
//    private void showMessage(String s) {
//        Toast.makeText(), s, Toast.LENGTH_LONG).show();
//    }
}
