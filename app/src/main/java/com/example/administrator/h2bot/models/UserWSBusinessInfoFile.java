package com.example.administrator.h2bot.models;

public class UserWSBusinessInfoFile {
    private String business_id;
    private String business_name;
    private String business_start_time;
    private String business_end_time;
    private String business_delivery_service_status;
    private String business_delivery_free_or_not;
    private String business_delivery_fee_per_gal;
    private String business_min_no_of_capacity;
    private String business_tel_no;
    private String business_address;
    private String business_status;

    public UserWSBusinessInfoFile()
    {

    }
    public UserWSBusinessInfoFile(
            String business_id,
            String business_name,
            String business_start_time,
            String business_end_time,
            String business_delivery_service_status,
            String business_delivery_free_or_not,
            String business_delivery_fee_per_gal,
            String business_min_no_of_capacity,
            String business_tel_no,
             String business_address,
             String business_status
    )
    {
        this.business_id =business_id;
        this.business_name = business_name;
        this.business_start_time = business_start_time;
        this.business_end_time = business_end_time;
        this.business_delivery_service_status = business_delivery_service_status;
        this.business_delivery_free_or_not = business_delivery_free_or_not;
        this.business_delivery_fee_per_gal = business_delivery_fee_per_gal;
        this.business_min_no_of_capacity = business_min_no_of_capacity;
        this.business_tel_no = business_tel_no;
        this.business_address = business_address;
        this.business_status = business_status;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
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

    public String getBusiness_delivery_service_status() {
        return business_delivery_service_status;
    }

    public void setBusiness_delivery_service_status(String business_delivery_service_status) {
        this.business_delivery_service_status = business_delivery_service_status;
    }

    public String getBusiness_delivery_free_or_not() {
        return business_delivery_free_or_not;
    }

    public void setBusiness_delivery_free_or_not(String business_delivery_free_or_not) {
        this.business_delivery_free_or_not = business_delivery_free_or_not;
    }

    public String getBusiness_delivery_fee_per_gal() {
        return business_delivery_fee_per_gal;
    }

    public void setBusiness_delivery_fee_per_gal(String business_delivery_fee_per_gal) {
        this.business_delivery_fee_per_gal = business_delivery_fee_per_gal;
    }

    public String getBusiness_min_no_of_capacity() {
        return business_min_no_of_capacity;
    }

    public void setBusiness_min_no_of_capacity(String business_min_no_of_capacity) {
        this.business_min_no_of_capacity = business_min_no_of_capacity;
    }

    public String getBusiness_tel_no() {
        return business_tel_no;
    }

    public void setBusiness_tel_no(String business_tel_no) {
        this.business_tel_no = business_tel_no;
    }

    public String getBusiness_status() {
        return business_status;
    }

    public void setBusiness_status(String business_status) {
        this.business_status = business_status;
    }
}
