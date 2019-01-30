package com.example.administrator.h2bot;

public class TemporaryGetterSetterForOrdersDeclineAccept {

    String mOrderNo;
    String mCustomerName;
    String mContactNumber;
    String mWaterType;
    String mPricePerGallon;
    String mService;
    String mAddress;
    String mDeliveryFee;
    String mTotalPrice;

   public TemporaryGetterSetterForOrdersDeclineAccept(String orderNo,String customerName,String contactNumber,String waterType, String pricePerGallon, String service, String address, String deliveryFee, String totalPrice)
   {
       mOrderNo = orderNo;
       mCustomerName = customerName;
       mContactNumber = contactNumber;
       mWaterType = waterType;
       mPricePerGallon = pricePerGallon;
       mService = service;
       mAddress = address;
       mDeliveryFee = deliveryFee;
       mTotalPrice = totalPrice;
   }

    public String getmOrderNo() {
        return mOrderNo;
    }

    public void setmOrderNo(String mOrderNo) {
        this.mOrderNo = mOrderNo;
    }

    public String getmCustomerName() {
        return mCustomerName;
    }

    public void setmCustomerName(String mCustomerName) {
        this.mCustomerName = mCustomerName;
    }

    public String getmContactNumber() {
        return mContactNumber;
    }

    public void setmContactNumber(String mContactNumber) {
        this.mContactNumber = mContactNumber;
    }

    public String getmWaterType() {
        return mWaterType;
    }

    public void setmWaterType(String mWaterType) {
        this.mWaterType = mWaterType;
    }

    public String getmPricePerGallon() {
        return mPricePerGallon;
    }

    public void setmPricePerGallon(String mPricePerGallon) {
        this.mPricePerGallon = mPricePerGallon;
    }

    public String getmService() {
        return mService;
    }

    public void setmService(String mService) {
        this.mService = mService;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmDeliveryFee() {
        return mDeliveryFee;
    }

    public void setmDeliveryFee(String mDeliveryFee) {
        this.mDeliveryFee = mDeliveryFee;
    }

    public String getmTotalPrice() {
        return mTotalPrice;
    }

    public void setmTotalPrice(String mTotalPrice) {
        this.mTotalPrice = mTotalPrice;
    }
}
