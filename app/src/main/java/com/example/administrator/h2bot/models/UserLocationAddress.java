package com.example.administrator.h2bot.models;

import com.google.type.LatLng;

public class UserLocationAddress {
    private String user_id;
    private String user_geocode_location_address;

    public UserLocationAddress(String user_id, String user_geocode_location_address) {
        this.user_id = user_id;
        this.user_geocode_location_address = user_geocode_location_address;
    }

    public UserLocationAddress(String uidString, com.google.android.gms.maps.model.LatLng locationFromAddress) {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_geocode_location_address() {
        return user_geocode_location_address;
    }

    public void setUser_geocode_location_address(String user_geocode_location_address) {
        this.user_geocode_location_address = user_geocode_location_address;
    }
}
