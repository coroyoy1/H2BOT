package com.example.administrator.h2bot.models;

public class TPAModel {
    private String tpa_id;
    private String tpa_drivers_license;
    private String tpa_status;

    public TPAModel() {
    }

    public TPAModel(String tpa_id, String tpa_drivers_license, String tpa_status) {
        this.tpa_id = tpa_id;
        this.tpa_drivers_license = tpa_drivers_license;
        this.tpa_status = tpa_status;
    }

    public String getTpa_id() {
        return tpa_id;
    }

    public void setTpa_id(String tpa_id) {
        this.tpa_id = tpa_id;
    }

    public String getTpa_drivers_license() {
        return tpa_drivers_license;
    }

    public void setTpa_drivers_license(String tpa_drivers_license) {
        this.tpa_drivers_license = tpa_drivers_license;
    }

    public String getTpa_status() {
        return tpa_status;
    }

    public void setTpa_status(String tpa_status) {
        this.tpa_status = tpa_status;
    }
}
