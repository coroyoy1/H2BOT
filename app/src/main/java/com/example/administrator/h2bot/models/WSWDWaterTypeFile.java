package com.example.administrator.h2bot.models;

public class WSWDWaterTypeFile {
    private String water_seller_id;
    private String water_name;
    private String water_type;
    private String water_price_per_gallon;
    private String water_description;
    private String water_status;

    public WSWDWaterTypeFile() {
    }

    public WSWDWaterTypeFile(String water_seller_id, String water_name, String water_type, String water_price_per_gallon, String water_description, String water_status) {
        this.water_seller_id = water_seller_id;
        this.water_name = water_name;
        this.water_type = water_type;
        this.water_price_per_gallon = water_price_per_gallon;
        this.water_description = water_description;
        this.water_status = water_status;
    }

    public String getWater_seller_id() {
        return water_seller_id;
    }

    public void setWater_seller_id(String water_seller_id) {
        this.water_seller_id = water_seller_id;
    }

    public String getWater_name() {
        return water_name;
    }

    public void setWater_name(String water_name) {
        this.water_name = water_name;
    }

    public String getWater_type() {
        return water_type;
    }

    public void setWater_type(String water_type) {
        this.water_type = water_type;
    }

    public String getWater_price_per_gallon() {
        return water_price_per_gallon;
    }

    public void setWater_price_per_gallon(String water_price_per_gallon) {
        this.water_price_per_gallon = water_price_per_gallon;
    }

    public String getWater_description() {
        return water_description;
    }

    public void setWater_description(String water_description) {
        this.water_description = water_description;
    }

    public String getWater_status() {
        return water_status;
    }

    public void setWater_status(String water_status) {
        this.water_status = water_status;
    }
}