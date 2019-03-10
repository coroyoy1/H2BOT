package com.example.administrator.h2bot.models;

public class TransactionNoModel {
    private String transOrderNo;

    public TransactionNoModel() {
    }

    public TransactionNoModel(String transOrderNo) {
        this.transOrderNo = transOrderNo;
    }

    public String getTransOrderNo() {
        return transOrderNo;
    }

    public void setTransOrderNo(String transOrderNo) {
        this.transOrderNo = transOrderNo;
    }
}
