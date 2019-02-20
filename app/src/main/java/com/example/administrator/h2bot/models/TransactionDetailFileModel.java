package com.example.administrator.h2bot.models;

public class TransactionDetailFileModel {
    String  trans_delivery_fee_per_gallon;
    String trans_no;
    String trans_no_of_gallons;
    String trans_partial_amount;
    String trans_price_per_gallon;
    String trans_status;
    String trans_water_type;

    public TransactionDetailFileModel(){

    }
    public TransactionDetailFileModel(String trans_delivery_fee_per_gallon, String trans_no, String trans_no_of_gallons, String trans_partial_amount, String trans_price_per_gallon, String trans_status, String trans_water_type){
        this.trans_delivery_fee_per_gallon = trans_delivery_fee_per_gallon;
        this.trans_no = trans_no;
        this.trans_no_of_gallons = trans_no_of_gallons;
        this.trans_partial_amount = trans_partial_amount;
        this.trans_price_per_gallon = trans_price_per_gallon;
        this.trans_status = trans_status;
        this.trans_water_type = trans_water_type;
    }

    public String getTrans_delivery_fee_per_gallon() {
        return trans_delivery_fee_per_gallon;
    }

    public void setTrans_delivery_fee_per_gallon(String trans_delivery_fee_per_gallon) {
        this.trans_delivery_fee_per_gallon = trans_delivery_fee_per_gallon;
    }

    public String getTrans_no() {
        return trans_no;
    }

    public void setTrans_no(String trans_no) {
        this.trans_no = trans_no;
    }

    public String getTrans_no_of_gallons() {
        return trans_no_of_gallons;
    }

    public void setTrans_no_of_gallons(String trans_no_of_gallons) {
        this.trans_no_of_gallons = trans_no_of_gallons;
    }

    public String getTrans_partial_amount() {
        return trans_partial_amount;
    }

    public void setTrans_partial_amount(String trans_partial_amount) {
        this.trans_partial_amount = trans_partial_amount;
    }

    public String getTrans_price_per_gallon() {
        return trans_price_per_gallon;
    }

    public void setTrans_price_per_gallon(String trans_price_per_gallon) {
        this.trans_price_per_gallon = trans_price_per_gallon;
    }

    public String getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(String trans_status) {
        this.trans_status = trans_status;
    }

    public String getTrans_water_type() {
        return trans_water_type;
    }

    public void setTrans_water_type(String trans_water_type) {
        this.trans_water_type = trans_water_type;
    }
}
