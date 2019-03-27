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
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.TransactionNoModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        String myAddress = orderModel.getOrderAddress();
        String customerId = orderModel.getOrderCustomerId();
        String dateIssued = orderModel.getOrderDateIssued();
        String deliveryFee = orderModel.getOrderDeliveryFee();
        String deliveryFeePerGallon = orderModel.getOrderDeliveryFeePerGallon();
        String serviceType = orderModel.getOrderDeliveryMethod();
        String stationId = orderModel.getOrderStationId();
        String partialAmt = orderModel.getOrderPartialAmt();
        String pricePerGallon = orderModel.getOrderPricePerGallon();
        String quantity = orderModel.getOrderQty();
        String serviceMethod = orderModel.getOrderServiceMethod();
        String myStatus = orderModel.getOrderStatus();
        String totalAmt = orderModel.getOrderTotalAmt();
        String waterType = orderModel.getOrderWaterType();

        if(serviceMethod.equalsIgnoreCase("Delivery")){
            deliveryDate = orderModel.getOrderDeliveryDate();
        }
        else{
            pickupDate = orderModel.getOrderDeliveryDate();
        }

        viewHolder.order_no.setText(orderNo);
        viewHolder.status.setText(myStatus);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                TextView order_no, water_type, price_per_gallon, qty, address, service_type, delivery_date,
                delivery_fee, status, total_amt, qr_code, order_type, date_issued, deliveryFeePerGal, partialAmount, methodText;
                Button cancelBtn, viewQrQodeBtn;
                qrCode = customerId.trim().replace(" ", "") +"/"+
                        stationId.trim().replace(" ", "") + "/" +
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
                deliveryFeePerGal = myDialog.findViewById(R.id.deliveryFeePerGallon);
                partialAmount = myDialog.findViewById(R.id.partialPayment);
                methodText = myDialog.findViewById(R.id.methodText);

                order_type.setText(serviceMethod);
                order_no.setText(orderNo);
                water_type.setText(waterType);
                price_per_gallon.setText(pricePerGallon);
                qty.setText(quantity + " gallon(s)");
                address.setText(myAddress);
                service_type.setText(serviceType);
                if(serviceMethod.equalsIgnoreCase("Delivery")){
                    methodText.setText("Delivery Date: ");
                    delivery_date.setText(deliveryDate);
                }
                else{
                    methodText.setText("Pickup Date: ");
                    delivery_date.setText(pickupDate);
                }
                delivery_fee.setText(deliveryFee);
                status.setText(myStatus);
                date_issued.setText(dateIssued);
                deliveryFeePerGal.setText(deliveryFeePerGallon);
                partialAmount.setText(partialAmt);
                total_amt.setText("Total: " + totalAmt);
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
                                    updateOrderRef.child(customerId)
                                            .child(stationId)
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
}
