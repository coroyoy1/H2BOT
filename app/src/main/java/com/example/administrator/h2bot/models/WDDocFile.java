package com.example.administrator.h2bot.models;

public class WDDocFile {

    private String dealer_id;
    private String dealer_status;
    private String driverLicense;

    public WDDocFile(String dealer_id, String dealer_status, String driverLicense) {
        this.dealer_id = dealer_id;
        this.dealer_status = dealer_status;
        this.driverLicense = driverLicense;
    }

    public WDDocFile()
    {

    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getDealer_status() {
        return dealer_status;
    }

    public void setDealer_status(String dealer_status) {
        this.dealer_status = dealer_status;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }
}
