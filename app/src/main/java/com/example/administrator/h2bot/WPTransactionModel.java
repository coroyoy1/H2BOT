package com.example.administrator.h2bot;

public class WPTransactionModel {
    String transactionNo;
    String customerName;
    String contactNo;
    String waterType;
    String itemQuantity;
    String pricePerGallon;
    String service;
    String address;

    public String getStatus() {
        return status;
    }

    String status;

    public String getTransactionNo() {
        return transactionNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getWaterType() {
        return waterType;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public String getPricePerGallon() {
        return pricePerGallon;
    }

    public String getService() {
        return service;
    }

    public String getAddress() {
        return address;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    String deliveryFee;
    String totalPrice;

    public WPTransactionModel()
    {

    }
    public  WPTransactionModel(String transactionNo,String customerName,String contactNo,String waterType,String itemQuantity,String pricePerGallon,String service,String address,String deliveryFee,String totalPrice,String status)
    {
        this.transactionNo = transactionNo;
        this.customerName = customerName;
        this.contactNo = contactNo;
        this.waterType = waterType;
        this.itemQuantity = itemQuantity;
        this.pricePerGallon = pricePerGallon;
        this.service = service;
        this.address = address;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}
