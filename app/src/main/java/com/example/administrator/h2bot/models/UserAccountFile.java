package com.example.administrator.h2bot.models;

public class UserAccountFile {
    private String user_getUID;
    private String user_email_address;
    private String user_password;
    private String user_device_token;
    private String user_status;

    public UserAccountFile(String user_getUID, String user_email_address, String user_password, String user_device_token, String user_status)
    {
        this.user_getUID = user_getUID;
        this.user_email_address = user_email_address;
        this.user_password = user_password;
        this.user_device_token = user_device_token;
        this.user_status = user_status;
    }

    public UserAccountFile()
    {

    }

    public String getUser_getUID() {
        return user_getUID;
    }

    public void setUser_getUID(String user_getUID) {
        this.user_getUID = user_getUID;
    }

    public String getUser_email_address() {
        return user_email_address;
    }

    public void setUser_email_address(String user_email_address) {
        this.user_email_address = user_email_address;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_device_token() {
        return user_device_token;
    }

    public void setUser_device_token(String user_device_token) {
        this.user_device_token = user_device_token;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}
