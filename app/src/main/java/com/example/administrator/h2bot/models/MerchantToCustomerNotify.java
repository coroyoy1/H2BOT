package com.example.administrator.h2bot.models;

public class MerchantToCustomerNotify {
    private String notification_merchant_id, notification_customer_id,
    notification_token_id, notification_date, notification_message, notification_status,
            notification_order_no;

    public MerchantToCustomerNotify(String notification_order_no, String notification_merchant_id, String notification_customer_id, String notification_token_id, String notification_date, String notification_message, String notification_status) {
        this.notification_order_no = notification_order_no;
        this.notification_merchant_id = notification_merchant_id;
        this.notification_customer_id = notification_customer_id;
        this.notification_token_id = notification_token_id;
        this.notification_date = notification_date;
        this.notification_message = notification_message;
        this.notification_status = notification_status;
    }

    public MerchantToCustomerNotify() {
    }

    public String getNotification_order_no() {
        return notification_order_no;
    }

    public void setNotification_order_no(String notification_order_no) {
        this.notification_order_no = notification_order_no;
    }

    public String getNotification_merchant_id() {
        return notification_merchant_id;
    }

    public void setNotification_merchant_id(String notification_merchant_id) {
        this.notification_merchant_id = notification_merchant_id;
    }

    public String getNotification_customer_id() {
        return notification_customer_id;
    }

    public void setNotification_customer_id(String notification_customer_id) {
        this.notification_customer_id = notification_customer_id;
    }

    public String getNotification_token_id() {
        return notification_token_id;
    }

    public void setNotification_token_id(String notification_token_id) {
        this.notification_token_id = notification_token_id;
    }

    public String getNotification_date() {
        return notification_date;
    }

    public void setNotification_date(String notification_date) {
        this.notification_date = notification_date;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public String getNotification_status() {
        return notification_status;
    }

    public void setNotification_status(String notification_status) {
        this.notification_status = notification_status;
    }
}
