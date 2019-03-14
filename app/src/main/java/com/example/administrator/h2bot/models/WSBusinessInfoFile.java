package com.example.administrator.h2bot.models;

public class WSBusinessInfoFile {
    private String business_id;
    private String business_name;
    private String business_address;
    private String business_tel_no;
    private String business_start_time;
    private String business_end_time;
    private String business_delivery_fee_method;
    private String business_delivery_fee;
    private String business_min_no_of_gallons;
    private String business_status;

    public WSBusinessInfoFile() {
    }

    public WSBusinessInfoFile(String business_id, String business_name, String business_address, String business_tel_no, String business_start_time, String business_end_time, String business_delivery_fee_method, String business_delivery_fee, String business_min_no_of_gallons, String business_status) {
        this.business_id = business_id;
        this.business_name = business_name;
        this.business_address = business_address;
        this.business_tel_no = business_tel_no;
        this.business_start_time = business_start_time;
        this.business_end_time = business_end_time;
        this.business_delivery_fee_method = business_delivery_fee_method;
        this.business_delivery_fee = business_delivery_fee;
        this.business_min_no_of_gallons = business_min_no_of_gallons;
        this.business_status = business_status;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
    }

    public String getBusiness_tel_no() {
        return business_tel_no;
    }

    public void setBusiness_tel_no(String business_tel_no) {
        this.business_tel_no = business_tel_no;
    }

    public String getBusiness_start_time() {
        return business_start_time;
    }

    public void setBusiness_start_time(String business_start_time) {
        this.business_start_time = business_start_time;
    }

    public String getBusiness_end_time() {
        return business_end_time;
    }

    public void setBusiness_end_time(String business_end_time) {
        this.business_end_time = business_end_time;
    }

    public String getBusiness_delivery_fee_method() {
        return business_delivery_fee_method;
    }

    public void setBusiness_delivery_fee_method(String business_delivery_fee_method) {
        this.business_delivery_fee_method = business_delivery_fee_method;
    }

    public String getBusiness_delivery_fee() {
        return business_delivery_fee;
    }

    public void setBusiness_delivery_fee(String business_delivery_fee) {
        this.business_delivery_fee = business_delivery_fee;
    }

    public String getBusiness_min_no_of_gallons() {
        return business_min_no_of_gallons;
    }

    public void setBusiness_min_no_of_gallons(String business_min_no_of_gallons) {
        this.business_min_no_of_gallons = business_min_no_of_gallons;
    }

    public String getBusiness_status() {
        return business_status;
    }

    public void setBusiness_status(String business_status) {
        this.business_status = business_status;
    }
}
