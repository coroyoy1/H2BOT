package com.example.administrator.h2bot.models;

public class TransactionHeaderFileModel {
    String customer_id;
    String merchant_id;
    String trans_date_issued;
    String trans_delivery_service;
    String trans_no;
    String trans_status;
    String trans_total_amount;
    String total_delivery_fee;
    String trans_total_no_gallons;

    public TransactionHeaderFileModel(){

    }
    public TransactionHeaderFileModel(String customer_id, String merchant_id, String trans_date_issued, String trans_delivery_service, String trans_no, String trans_status, String trans_total_amount, String total_delivery_fee, String trans_total_no_gallons){
        this.customer_id = customer_id;
        this.merchant_id = merchant_id;
        this.trans_date_issued = trans_date_issued;
        this.trans_delivery_service = trans_delivery_service;
        this.trans_no = trans_no;
        this.trans_status = trans_status;
        this.trans_total_amount = trans_total_amount;
        this.total_delivery_fee = total_delivery_fee;
        this.trans_total_no_gallons = trans_total_no_gallons;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getTrans_date_issued() {
        return trans_date_issued;
    }

    public void setTrans_date_issued(String trans_date_issued) {
        this.trans_date_issued = trans_date_issued;
    }

    public String getTrans_delivery_service() {
        return trans_delivery_service;
    }

    public void setTrans_delivery_service(String trans_delivery_service) {
        this.trans_delivery_service = trans_delivery_service;
    }

    public String getTrans_no() {
        return trans_no;
    }

    public void setTrans_no(String trans_no) {
        this.trans_no = trans_no;
    }

    public String getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(String trans_status) {
        this.trans_status = trans_status;
    }

    public String getTrans_total_amount() {
        return trans_total_amount;
    }

    public void setTrans_total_amount(String trans_total_amount) {
        this.trans_total_amount = trans_total_amount;
    }

    public String getTotal_delivery_fee() {
        return total_delivery_fee;
    }

    public void setTotal_delivery_fee(String total_delivery_fee) {
        this.total_delivery_fee = total_delivery_fee;
    }

    public String getTrans_total_no_gallons() {
        return trans_total_no_gallons;
    }

    public void setTrans_total_no_gallons(String trans_total_no_gallons) {
        this.trans_total_no_gallons = trans_total_no_gallons;
    }
}
