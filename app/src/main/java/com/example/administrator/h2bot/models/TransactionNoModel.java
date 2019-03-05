package com.example.administrator.h2bot.models;

public class TransactionNoModel {
    private String transOrderNo;
    private String transStatus;

    public TransactionNoModel() {
    }

    public TransactionNoModel(String transOrderNo, String transStatus) {
        this.transOrderNo = transOrderNo;
        this.transStatus = transStatus;
    }

    public String getTransOrderNo() {
        return transOrderNo;
    }

    public void setTransOrderNo(String transOrderNo) {
        this.transOrderNo = transOrderNo;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }
}
