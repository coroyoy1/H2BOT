package com.example.administrator.h2bot.models;

public class UserWallet {
    private String user_id;
    private String user_points;
    private String user_status;

    public UserWallet() {
    }

    public UserWallet(String user_id, String user_points, String user_status) {
        this.user_id = user_id;
        this.user_points = user_points;
        this.user_status = user_status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_points() {
        return user_points;
    }

    public void setUser_points(String user_points) {
        this.user_points = user_points;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}
