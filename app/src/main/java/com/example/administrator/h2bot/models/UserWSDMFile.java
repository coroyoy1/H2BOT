package com.example.administrator.h2bot.models;

public class UserWSDMFile {
    private String station_id;
    private String delivery_man_id;
    private String dealer_drivers_license;

    public UserWSDMFile()
    {

    }
    public UserWSDMFile(String station_id, String delivery_man_id, String dealer_drivers_license)
    {
        this.station_id = station_id;
        this.delivery_man_id = delivery_man_id;
        this.dealer_drivers_license = dealer_drivers_license;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getDelivery_man_id() {
        return delivery_man_id;
    }

    public void setDelivery_man_id(String delivery_man_id) {
        this.delivery_man_id = delivery_man_id;
    }

    public String getDealer_drivers_license() {
        return dealer_drivers_license;
    }

    public void setDealer_drivers_license(String dealer_drivers_license) {
        this.dealer_drivers_license = dealer_drivers_license;
    }
}
