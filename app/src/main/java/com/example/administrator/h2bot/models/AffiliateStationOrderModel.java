package com.example.administrator.h2bot.models;

public class AffiliateStationOrderModel {
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStationid() {
        return stationid;
    }

    public void setStationid(String stationid) {
        this.stationid = stationid;
    }

    public String getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String customerId;
    String stationid;
    String affiliateId;
    String orderNo;
    String status;

    public AffiliateStationOrderModel()
    {

    }

    public AffiliateStationOrderModel(String affiliateId, String stationid, String customerId, String orderNo, String status)
    {
        this.affiliateId = affiliateId;
        this.stationid = stationid;
        this.customerId = customerId;
        this.orderNo = orderNo;
        this.status = status;
    }

}
