package com.example.administrator.h2bot.absampletestphase;

public class RatingModel {
    private String rating_customer_id;
    private String rating_merchant_id;
    private String rating_comment;
    private String rating_status;
    private String rating_number;
    private String count_user;

    public RatingModel(String rating_customer_id, String rating_merchant_id, String rating_number, String rating_comment, String count_user, String rating_status) {
        this.rating_customer_id = rating_customer_id;
        this.rating_merchant_id = rating_merchant_id;
        this.rating_comment = rating_comment;
        this.rating_status = rating_status;
        this.rating_number = rating_number;
        this.count_user = count_user;
    }

    public String getCount_user() {
        return count_user;
    }

    public void setCount_user(String count_user) {
        this.count_user = count_user;
    }

    public String getRating_number() {
        return rating_number;
    }

    public void setRating_number(String rating_number) {
        this.rating_number = rating_number;
    }

    public RatingModel() {
    }

    public String getRating_customer_id() {
        return rating_customer_id;
    }

    public void setRating_customer_id(String rating_customer_id) {
        this.rating_customer_id = rating_customer_id;
    }

    public String getRating_merchant_id() {
        return rating_merchant_id;
    }

    public void setRating_merchant_id(String rating_merchant_id) {
        this.rating_merchant_id = rating_merchant_id;
    }

    public String getRating_comment() {
        return rating_comment;
    }

    public void setRating_comment(String rating_comment) {
        this.rating_comment = rating_comment;
    }

    public String getRating_status() {
        return rating_status;
    }

    public void setRating_status(String rating_status) {
        this.rating_status = rating_status;
    }
}
