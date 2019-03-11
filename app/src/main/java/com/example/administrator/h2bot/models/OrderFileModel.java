package com.example.administrator.h2bot.models;

public class OrderFileModel {
    private String orderStationId;
    private String orderCustomerId;
    private String orderNo;
    private String orderAddress;
    private String orderDeliveryDate;
    private String orderDeliveryFee;
    private String orderDeliveryMethod;
    private String orderPricePerGallon;
    private String orderQty;
    private String orderStatus;
    private String orderTotalAmt;
    private String orderWaterType;
    private String orderServiceMethod;

    public OrderFileModel() {
    }

    public OrderFileModel(String orderStationId, String orderCustomerId, String orderNo, String orderAddress, String orderDeliveryDate, String orderDeliveryFee, String orderDeliveryMethod, String orderPricePerGallon, String orderQty, String orderStatus, String orderTotalAmt, String orderWaterType, String orderServiceMethod) {
        this.orderStationId = orderStationId;
        this.orderCustomerId = orderCustomerId;
        this.orderNo = orderNo;
        this.orderAddress = orderAddress;
        this.orderDeliveryDate = orderDeliveryDate;
        this.orderDeliveryFee = orderDeliveryFee;
        this.orderDeliveryMethod = orderDeliveryMethod;
        this.orderPricePerGallon = orderPricePerGallon;
        this.orderQty = orderQty;
        this.orderStatus = orderStatus;
        this.orderTotalAmt = orderTotalAmt;
        this.orderWaterType = orderWaterType;
        this.orderServiceMethod = orderServiceMethod;
    }

    public String getOrderStationId() {
        return orderStationId;
    }

    public void setOrderStationId(String orderStationId) {
        this.orderStationId = orderStationId;
    }

    public String getOrderCustomerId() {
        return orderCustomerId;
    }

    public void setOrderCustomerId(String orderCustomerId) {
        this.orderCustomerId = orderCustomerId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
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

    public String getOrderDeliveryMethod() {
        return orderDeliveryMethod;
    }

    public void setOrderDeliveryMethod(String orderDeliveryMethod) {
        this.orderDeliveryMethod = orderDeliveryMethod;
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

    public String getOrderServiceMethod() {
        return orderServiceMethod;
    }

    public void setOrderServiceMethod(String orderServiceMethod) {
        this.orderServiceMethod = orderServiceMethod;
    }
}
