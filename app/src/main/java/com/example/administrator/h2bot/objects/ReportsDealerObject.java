package com.example.administrator.h2bot.objects;

public class ReportsDealerObject {

    private String waterType;
    private int itemSold;
    private double income;

    public ReportsDealerObject(String waterType, int itemSold, double income){
        this.waterType = waterType;
        this.itemSold = itemSold;
        this.income = income;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public int getItemSold() {
        return itemSold;
    }

    public void setItemSold(int itemSold) {
        this.itemSold = itemSold;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
