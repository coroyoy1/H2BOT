package com.example.administrator.h2bot.models;

public class WDDocFile {
    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    private String driverLicense;
    private String dealer_status;
    private String dealer_id;

    public String getDealer_status() {
        return dealer_status;
    }

    public void setDealer_status(String dealer_status) {
        this.dealer_status = dealer_status;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public WDDocFile()
    {

    }

    public WDDocFile(String driverLicense, String dealer_status, String dealer_id) {
        this.driverLicense = driverLicense;
        this.dealer_status = dealer_status;
        this.dealer_id = dealer_id;
    }


}
