package com.example.administrator.h2bot.models;

import com.google.type.LatLng;

public class UserLocationAddress {
    private String user_id;
    private String user_latitude;
    private String user_longtitude;

    public UserLocationAddress() {
    }

    public UserLocationAddress(String user_id, String user_latitude, String user_longtitude) {
        this.user_id = user_id;
        this.user_latitude = user_latitude;
        this.user_longtitude = user_longtitude;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_latitude() {
        return user_latitude;
    }

    public void setUser_latitude(String user_latitude) {
        this.user_latitude = user_latitude;
    }

    public String getUser_longtitude() {
        return user_longtitude;
    }

    public void setUser_longtitude(String user_longtitude) {
        this.user_longtitude = user_longtitude;
    }
}
