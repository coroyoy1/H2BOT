package com.example.administrator.h2bot.models;

public class CustomerToMerchantNotifModel {
    private String user_id;
    private String notification_id;

    public CustomerToMerchantNotifModel() {
    }

    public CustomerToMerchantNotifModel(String user_id, String notification_id) {
        this.user_id = user_id;
        this.notification_id = notification_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }
}
