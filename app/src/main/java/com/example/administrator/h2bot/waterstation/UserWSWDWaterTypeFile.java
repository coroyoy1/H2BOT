package com.example.administrator.h2bot.waterstation;

public class UserWSWDWaterTypeFile {
   private String water_seller_id;
   private String water_genKey;
   private String water_name;
   private String water_type;
   private String water_price_per_gallon;
   private String water_status;

    public UserWSWDWaterTypeFile() {
    }

    public UserWSWDWaterTypeFile(String water_seller_id, String water_genKey, String water_name,String water_type, String water_price_per_gallon, String water_status) {
        this.water_seller_id = water_seller_id;
        this.water_genKey = water_genKey;
        this.water_name = water_name;
        this.water_type = water_type;
        this.water_price_per_gallon = water_price_per_gallon;
        this.water_status = water_status;
    }

    public String getWater_name() {
        return water_name;
    }

    public void setWater_name(String water_name) {
        this.water_name = water_name;
    }

    public String getWater_seller_id() {
        return water_seller_id;
    }

    public void setWater_seller_id(String water_seller_id) {
        this.water_seller_id = water_seller_id;
    }

    public String getWater_genKey() {
        return water_genKey;
    }

    public void setWater_genKey(String water_genKey) {
        this.water_genKey = water_genKey;
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

    public String getWater_status() {
        return water_status;
    }

    public void setWater_status(String water_status) {
        this.water_status = water_status;
    }
}
