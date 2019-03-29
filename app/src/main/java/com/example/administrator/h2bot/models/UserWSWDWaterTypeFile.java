package com.example.administrator.h2bot.models;

public class UserWSWDWaterTypeFile {
   private String water_type;
   private String delivery_price_per_gallon;
   private String pickup_price_per_gallon;
   private String water_status;

    public UserWSWDWaterTypeFile() {
    }

    public UserWSWDWaterTypeFile(String water_type, String delivery_price_per_gallon, String pickup_price_per_gallon, String water_status) {
        this.water_type = water_type;
        this.delivery_price_per_gallon = delivery_price_per_gallon;
        this.pickup_price_per_gallon = pickup_price_per_gallon;
        this.water_status = water_status;
    }

    public String getWater_type() {
        return water_type;
    }

    public void setWater_type(String water_type) {
        this.water_type = water_type;
    }

    public String getDelivery_price_per_gallon() {
        return delivery_price_per_gallon;
    }

    public void setDelivery_price_per_gallon(String delivery_price_per_gallon) {
        this.delivery_price_per_gallon = delivery_price_per_gallon;
    }

    public String getPickup_price_per_gallon() {
        return pickup_price_per_gallon;
    }

    public void setPickup_price_per_gallon(String pickup_price_per_gallon) {
        this.pickup_price_per_gallon = pickup_price_per_gallon;
    }

    public String getWater_status() {
        return water_status;
    }

    public void setWater_status(String water_status) {
        this.water_status = water_status;
    }
}
