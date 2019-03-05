package com.example.administrator.h2bot.models;

public class MerchantCustomerFile {
    private String customer_id;
    private String station_id;
    private String status;

    public MerchantCustomerFile(String customer_id, String station_id, String status) {
        this.customer_id = customer_id;
        this.station_id = station_id;
        this.status = status;
    }

    public MerchantCustomerFile() {
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
