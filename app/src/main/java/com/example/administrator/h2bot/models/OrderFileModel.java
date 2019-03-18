package com.example.administrator.h2bot.models;

public class OrderFileModel {
    private String orderAddress;
    private String orderCustomerId;
    private String orderDateIssued;
    private String orderDeliveryDate;
    private String orderDeliveryFee;
    private String orderDeliveryFeePerGallon;
    private String orderDeliveryMethod;
    private String orderStationId;
    private String orderNo;
    private String orderPartialAmt;
    private String orderPricePerGallon;
    private String orderQty;
    private String orderServiceMethod;
    private String orderStatus;
    private String orderTotalAmt;
    private String orderWaterType;

    public OrderFileModel() {
    }

    public OrderFileModel(String orderAddress, String orderCustomerId, String orderDateIssued, String orderDeliveryDate, String orderDeliveryFee, String orderDeliveryFeePerGallon, String orderDeliveryMethod, String orderStationId, String orderNo, String orderPartialAmt, String orderPricePerGallon, String orderQty, String orderServiceMethod, String orderStatus, String orderTotalAmt, String orderWaterType) {
        this.orderAddress = orderAddress;
        this.orderCustomerId = orderCustomerId;
        this.orderDateIssued = orderDateIssued;
        this.orderDeliveryDate = orderDeliveryDate;
        this.orderDeliveryFee = orderDeliveryFee;
        this.orderDeliveryFeePerGallon = orderDeliveryFeePerGallon;
        this.orderDeliveryMethod = orderDeliveryMethod;
        this.orderStationId = orderStationId;
        this.orderNo = orderNo;
        this.orderPartialAmt = orderPartialAmt;
        this.orderPricePerGallon = orderPricePerGallon;
        this.orderQty = orderQty;
        this.orderServiceMethod = orderServiceMethod;
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

    public String getOrderDeliveryFee() {
        return orderDeliveryFee;
    }

    public void setOrderDeliveryFee(String orderDeliveryFee) {
        this.orderDeliveryFee = orderDeliveryFee;
    }

    public String getOrderDeliveryFeePerGallon() {
        return orderDeliveryFeePerGallon;
    }

    public void setOrderDeliveryFeePerGallon(String orderDeliveryFeePerGallon) {
        this.orderDeliveryFeePerGallon = orderDeliveryFeePerGallon;
    }

    public String getOrderDeliveryMethod() {
        return orderDeliveryMethod;
    }

    public void setOrderDeliveryMethod(String orderDeliveryMethod) {
        this.orderDeliveryMethod = orderDeliveryMethod;
    }

    public String getOrderStationId() {
        return orderStationId;
    }

    public void setOrderStationId(String orderStationId) {
        this.orderStationId = orderStationId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderPartialAmt() {
        return orderPartialAmt;
    }

    public void setOrderPartialAmt(String orderPartialAmt) {
        this.orderPartialAmt = orderPartialAmt;
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

    public String getOrderServiceMethod() {
        return orderServiceMethod;
    }

    public void setOrderServiceMethod(String orderServiceMethod) {
        this.orderServiceMethod = orderServiceMethod;
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
