package com.example.administrator.h2bot;

public class WSProductListSetterGetter {
    private String itemImage;
    private String itemName;
    private String itemPrice;
    private String itemQuantity;
    private String stringUserID;
    private String waterStyle;

    public WSProductListSetterGetter(String itemImage, String itemName, String itemPrice, String itemQuantity, String stringUserID, String waterStyle)
    {
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.stringUserID = stringUserID;
        this.waterStyle = waterStyle;
    }
    public WSProductListSetterGetter()
    {

    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getStringUserID() {
        return stringUserID;
    }

    public void setStringUserID(String stringUserID) {
        this.stringUserID = stringUserID;
    }

    public String getWaterStyle() {
        return waterStyle;
    }

    public void setWaterStyle(String waterStyle) {
        this.waterStyle = waterStyle;
    }
}
