package com.example.administrator.h2bot.models;

public class UserWSDocsFile {
    private String station_id;
    private String station_business_permit;
    private String station_sanitary_permit;
    private String station_fire_protection;
    private String station_real_property_taxes;
    private String station_occupancy_permit;
    private String station_physico_chem_permit;
    private String station_status;

    public UserWSDocsFile()
    {

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

    public String getStation_fire_protection() {
        return station_fire_protection;
    }

    public void setStation_fire_protection(String station_fire_protection) {
        this.station_fire_protection = station_fire_protection;
    }

    public String getStation_real_property_taxes() {
        return station_real_property_taxes;
    }

    public void setStation_real_property_taxes(String station_real_property_taxes) {
        this.station_real_property_taxes = station_real_property_taxes;
    }

    public String getStation_occupancy_permit() {
        return station_occupancy_permit;
    }

    public void setStation_occupancy_permit(String station_occupancy_permit) {
        this.station_occupancy_permit = station_occupancy_permit;
    }

    public String getStation_physico_chem_permit() {
        return station_physico_chem_permit;
    }

    public void setStation_physico_chem_permit(String station_physico_chem_permit) {
        this.station_physico_chem_permit = station_physico_chem_permit;
    }

    public String getStation_status() {
        return station_status;
    }

    public void setStation_status(String station_status) {
        this.station_status = station_status;
    }

    public UserWSDocsFile(String station_id, String station_business_permit, String station_sanitary_permit, String station_fire_protection, String station_real_property_taxes, String station_occupancy_permit, String station_physico_chem_permit, String station_status)
    {
        this.station_id = station_id;
        this.station_business_permit = station_business_permit;
        this.station_sanitary_permit = station_sanitary_permit;
        this.station_fire_protection = station_fire_protection;
        this.station_real_property_taxes = station_real_property_taxes;
        this.station_occupancy_permit = station_occupancy_permit;
        this.station_physico_chem_permit = station_physico_chem_permit;
        this.station_status = station_status;
    }
}
