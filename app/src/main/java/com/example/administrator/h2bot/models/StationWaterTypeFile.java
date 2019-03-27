package com.example.administrator.h2bot.models;

public class StationWaterTypeFile {
    private String water_seller_id;
    private String water_name;
    private String water_type;
    private String water_price;
    private String pickup_price;
    private String delivery_price;
    private String water_description;
    private String water_status;

    public StationWaterTypeFile(String water_seller_id, String water_name, String water_type, String pickup_price, String delivery_price, String water_description, String water_status) {
        this.water_seller_id = water_seller_id;
        this.water_name = water_name;
        this.water_type = water_type;
        this.pickup_price = pickup_price;
        this.delivery_price = delivery_price;
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

    public String getPickup_price() {
        return pickup_price;
    }

    public void setPickup_price(String pickup_price) {
        this.pickup_price = pickup_price;
    }

    public String getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(String delivery_price) {
        this.delivery_price = delivery_price;
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
