package com.example.administrator.h2bot.models;

public class OrderFileModel {
    private String orderAddress;
    private String orderCustomerId;
    private String orderDateIssued;
    private String orderDeliveryDate;
    private String orderDeliveryCharge;
    private String orderServiceType;
    private String orderMerchantId;
    private String orderNo;
    private String orderPricePerGallon;
    private String orderQty;
    private String orderMethod;
    private String orderStatus;
    private String orderTotalAmt;
    private String orderWaterType;

    public OrderFileModel() {
    }

    public OrderFileModel(String orderAddress, String orderCustomerId, String orderDateIssued, String orderDeliveryDate, String orderDeliveryCharge, String orderServiceType, String orderMerchantId, String orderNo, String orderPricePerGallon, String orderQty, String orderMethod, String orderStatus, String orderTotalAmt, String orderWaterType) {
        this.orderAddress = orderAddress;
        this.orderCustomerId = orderCustomerId;
        this.orderDateIssued = orderDateIssued;
        this.orderDeliveryDate = orderDeliveryDate;
        this.orderDeliveryCharge = orderDeliveryCharge;
        this.orderServiceType = orderServiceType;
        this.orderMerchantId = orderMerchantId;
        this.orderNo = orderNo;
        this.orderPricePerGallon = orderPricePerGallon;
        this.orderQty = orderQty;
        this.orderMethod = orderMethod;
        this.orderStatus = orderStatus;
        this.orderTotalAmt = orderTotalAmt;
        this.orderWaterType = orderWaterType;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderCustomerId() {
        return orderCustomerId;
    }

    public void setOrderCustomerId(String orderCustomerId) {
        this.orderCustomerId = orderCustomerId;
    }

    public String getOrderDateIssued() {
        return orderDateIssued;
    }

    public void setOrderDateIssued(String orderDateIssued) {
        this.orderDateIssued = orderDateIssued;
    }

    public String getOrderDeliveryDate() {
        return orderDeliveryDate;
    }

    public void setOrderDeliveryDate(String orderDeliveryDate) {
        this.orderDeliveryDate = orderDeliveryDate;
    }

    public String getOrderDeliveryCharge() {
        return orderDeliveryCharge;
    }

    public void setOrderDeliveryCharge(String orderDeliveryCharge) {
        this.orderDeliveryCharge = orderDeliveryCharge;
    }

    public String getOrderServiceType() {
        return orderServiceType;
    }

    public void setOrderServiceType(String orderServiceType) {
        this.orderServiceType = orderServiceType;
    }

    public String getOrderMerchantId() {
        return orderMerchantId;
    }

    public void setOrderMerchantId(String orderMerchantId) {
        this.orderMerchantId = orderMerchantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderPricePerGallon() {
        return orderPricePerGallon;
    }

    public void setOrderPricePerGallon(String orderPricePerGallon) {
        this.orderPricePerGallon = orderPricePerGallon;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public String getOrderMethod() {
        return orderMethod;
    }

    public void setOrderMethod(String orderMethod) {
        this.orderMethod = orderMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTotalAmt() {
        return orderTotalAmt;
    }

    public void setOrderTotalAmt(String orderTotalAmt) {
        this.orderTotalAmt = orderTotalAmt;
    }

    public String getOrderWaterType() {
        return orderWaterType;
    }

    public void setOrderWaterType(String orderWaterType) {
        this.orderWaterType = orderWaterType;
    }
}
