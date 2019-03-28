package com.example.administrator.h2bot.models;

public class TimePickerConstructor {
    private String hour;
    private String minute;
    private String status;

    public TimePickerConstructor(String hour, String minute, String status) {
        this.hour = hour;
        this.minute = minute;
        this.status = status;
    }

    public TimePickerConstructor() {
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
