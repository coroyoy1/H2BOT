package com.example.administrator.h2bot.models;

public class TPADocFile {

    private String tpa_id;
    private String tpa_status;
    private String driverLicense;

    public TPADocFile(String dealer_id, String dealer_status, String driverLicense) {
        this.tpa_id = dealer_id;
        this.tpa_status = dealer_status;
        this.driverLicense = driverLicense;
    }

    public TPADocFile()
    {

    }

    public String getDealer_id() {
        return tpa_id;
    }

    public void setDealer_id(String dealer_id) {
        this.tpa_id = dealer_id;
    }

    public String getDealer_status() {
        return tpa_status;
    }

    public void setDealer_status(String dealer_status) {
        this.tpa_status = dealer_status;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }
}
