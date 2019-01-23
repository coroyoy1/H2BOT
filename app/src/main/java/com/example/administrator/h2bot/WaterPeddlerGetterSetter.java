package com.example.administrator.h2bot;

public class WaterPeddlerGetterSetter {
    private String mItemImage;
    private String mItemName;
    private String mWaterType;
    private String mItemPrice;
    private String mItemQuantity;
    private String mUserID;

    public WaterPeddlerGetterSetter()
    {

    }
    public WaterPeddlerGetterSetter(String itemImage, String itemName, String waterType, String itemPrice, String itemQuantity, String userID) {
        mItemImage = itemImage;
        mItemName = itemName;
        mWaterType = waterType;
        mItemPrice = itemPrice;
        mItemQuantity = itemQuantity;
        mUserID = userID;
    }

    public String getmItemImage() {
        return mItemImage;
    }

    public void setmItemImage(String mItemImage) {
        this.mItemImage = mItemImage;
    }

    public String getmItemName() {
        return mItemName;
    }

    public void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public String getmWaterType() {
        return mWaterType;
    }

    public void setmWaterType(String mWaterType) {
        this.mWaterType = mWaterType;
    }

    public String getmItemPrice() {
        return mItemPrice;
    }

    public void setmItemPrice(String mItemPrice) {
        this.mItemPrice = mItemPrice;
    }

    public String getmItemQuantity() {
        return mItemQuantity;
    }

    public void setmItemQuantity(String mItemQuantity) {
        this.mItemQuantity = mItemQuantity;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }
}
