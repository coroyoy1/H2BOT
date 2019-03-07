package com.example.administrator.h2bot.models;

public class OrderModel {
    String order_address;
    String order_customer_id;
    String order_delivery_date;
    String order_delivery_fee;
    String order_delivery_method;
    String order_no;
    String order_price_per_gallon;
    String order_qty;
    String order_merchant_id;
    String order_status;
    String order_total_amt;
    String order_water_type;

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    public String getOrder_customer_id() {
        return order_customer_id;
    }

    public void setOrder_customer_id(String order_customer_id) {
        this.order_customer_id = order_customer_id;
    }

    public String getOrder_delivery_date() {
        return order_delivery_date;
    }

    public void setOrder_delivery_date(String order_delivery_date) {
        this.order_delivery_date = order_delivery_date;
    }

    public String getOrder_delivery_fee() {
        return order_delivery_fee;
    }

    public void setOrder_delivery_fee(String order_delivery_fee) {
        this.order_delivery_fee = order_delivery_fee;
    }

    public String getOrder_delivery_method() {
        return order_delivery_method;
    }

    public void setOrder_delivery_method(String order_delivery_method) {
        this.order_delivery_method = order_delivery_method;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_price_per_gallon() {
        return order_price_per_gallon;
    }

    public void setOrder_price_per_gallon(String order_price_per_gallon) {
        this.order_price_per_gallon = order_price_per_gallon;
    }

    public String getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(String order_qty) {
        this.order_qty = order_qty;
    }

    public String getOrder_merchant_id() {
        return order_merchant_id;
    }

    public void setOrder_merchant_id(String order_station_id) {
        this.order_merchant_id = order_station_id;
    }

        public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_total_amt() {
        return order_total_amt;
    }

    public void setOrder_total_amt(String order_total_amt) {
        this.order_total_amt = order_total_amt;
    }

    public String getOrder_water_type() {
        return order_water_type;
    }

    public void setOrder_water_type(String order_water_type) {
        this.order_water_type = order_water_type;
    }

    public OrderModel()
    {

    }

    public OrderModel(String order_address, String order_customer_id,String order_delivery_date,String order_delivery_fee,String order_delivery_method, String order_no,
                      String order_price_per_gallon,String order_qty,String order_merchant_id, String order_status, String order_total_amt, String order_water_type)
    {
        this.order_address = order_address;
        this.order_customer_id = order_customer_id;
        this.order_delivery_date = order_delivery_date;
        this.order_delivery_fee = order_delivery_fee;
        this.order_delivery_method = order_delivery_method;
        this.order_no = order_no;
        this.order_price_per_gallon = order_price_per_gallon;
        this.order_qty = order_qty;
        this.order_merchant_id = order_merchant_id;
        this.order_status = order_status;
        this.order_total_amt = order_total_amt;
        this.order_water_type = order_water_type;
    }

}
