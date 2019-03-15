package com.example.administrator.h2bot.models;

public class UserFile {
    private String user_getUID;
    private String user_firstname;
    private String user_lastname;
    private String user_phone_no;
    private String user_address;
    private String user_type;
    private String user_status;
    private String user_uri;


    public UserFile()
    {
    }

    public UserFile(String user_getUID, String user_uri, String user_firstname, String user_lastname, String user_address, String user_phone_no, String user_type, String user_status)
    {
        this.user_getUID = user_getUID;
        this.user_uri = user_uri;
        this.user_firstname = user_firstname;
        this.user_lastname = user_lastname;
        this.user_phone_no = user_phone_no;
        this.user_address = user_address;
        this.user_type = user_type;
        this.user_status = user_status;
    }

    public String getUser_uri() {
        return user_uri;
    }

    public void setUser_uri(String user_uri) {
        this.user_uri = user_uri;
    }

    public String getUser_getUID() {
        return user_getUID;
    }

    public void setUser_getUID(String user_getUID) {
        this.user_getUID = user_getUID;
    }

    public String getUser_firstname() {
        return user_firstname;
    }

    public void setUser_firstname(String user_firtname) {
        this.user_firstname = user_firtname;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getUser_phone_no() {
        return user_phone_no;
    }

    public void setUser_phone_no(String user_phone_no) {
        this.user_phone_no = user_phone_no;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}
