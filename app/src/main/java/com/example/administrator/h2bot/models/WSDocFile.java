package com.example.administrator.h2bot.models;

public class WSDocFile {
    private String station_id;
    private String station_business_permit;
    private String station_sanitary_permit;
    private String station_status;

    public WSDocFile()
    {

    }

    public WSDocFile(String station_id, String station_business_permit, String station_sanitary_permit, String station_status) {
        this.station_id = station_id;
        this.station_business_permit = station_business_permit;
        this.station_sanitary_permit = station_sanitary_permit;
        this.station_status = station_status;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getStation_business_permit() {
        return station_business_permit;
    }

    public void setStation_business_permit(String station_business_permit) {
        this.station_business_permit = station_business_permit;
    }

    public String getStation_sanitary_permit() {
        return station_sanitary_permit;
    }

    public void setStation_sanitary_permit(String station_sanitary_permit) {
        this.station_sanitary_permit = station_sanitary_permit;
    }

    public String getStation_status() {
        return station_status;
    }

    public void setStation_status(String station_status) {
        this.station_status = station_status;
    }
}
