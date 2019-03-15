package com.example.administrator.h2bot.models;

import java.util.Date;

public class DateModel {
    String order_delivery_date;

    public String getOrder_delivery_date() {
        return order_delivery_date;
    }

    public void setOrder_delivery_date(String order_delivery_date) {
        this.order_delivery_date = order_delivery_date;
    }

    public DateModel(){
    }
    public DateModel(String order_delivery_date){
        this.order_delivery_date = order_delivery_date;
    }
}
