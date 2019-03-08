package com.example.administrator.h2bot.models;

public class CustomerToMerchantNotifModel {
    private String customer_id;
    private String merchant_id;
    private String order_no;
    private String notification_customer_id;
    private String notification_date;
    private String notification_merchant_id;
    private String notification_message;
    private String notification_order_no;
    private String notification_status;
    private String notification_token_id;

    public CustomerToMerchantNotifModel() {
    }

    public CustomerToMerchantNotifModel(String customer_id, String merchant_id, String order_no, String notification_customer_id, String notification_date, String notification_merchant_id, String notification_message, String notification_order_no, String notification_status, String notification_token_id) {
        this.customer_id = customer_id;
        this.merchant_id = merchant_id;
        this.order_no = order_no;
        this.notification_customer_id = notification_customer_id;
        this.notification_date = notification_date;
        this.notification_merchant_id = notification_merchant_id;
        this.notification_message = notification_message;
        this.notification_order_no = notification_order_no;
        this.notification_status = notification_status;
        this.notification_token_id = notification_token_id;
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

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getNotification_customer_id() {
        return notification_customer_id;
    }

    public void setNotification_customer_id(String notification_customer_id) {
        this.notification_customer_id = notification_customer_id;
    }

    public String getNotification_date() {
        return notification_date;
    }

    public void setNotification_date(String notification_date) {
        this.notification_date = notification_date;
    }

    public String getNotification_merchant_id() {
        return notification_merchant_id;
    }

    public void setNotification_merchant_id(String notification_merchant_id) {
        this.notification_merchant_id = notification_merchant_id;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public String getNotification_order_no() {
        return notification_order_no;
    }

    public void setNotification_order_no(String notification_order_no) {
        this.notification_order_no = notification_order_no;
    }

    public String getNotification_status() {
        return notification_status;
    }

    public void setNotification_status(String notification_status) {
        this.notification_status = notification_status;
    }

    public String getNotification_token_id() {
        return notification_token_id;
    }

    public void setNotification_token_id(String notification_token_id) {
        this.notification_token_id = notification_token_id;
    }
}
