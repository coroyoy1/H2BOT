package com.example.administrator.h2bot.waterstation;

public class ProductGetterSetter {
    private String mItemImage;
    private String mItemName;
    private String mWaterType;
    private String mItemPrice;
    private String mItemQuantity;
    private String mUserID;
    private String mItemNo;
    private String mItemUID;


    public ProductGetterSetter()
    {

    }


    public ProductGetterSetter(String itemImage, String itemName, String waterType, String itemPrice, String itemQuantity, String userID, String itemNo, String itemUID) {
        mItemImage = itemImage;
        mItemName = itemName;
        mWaterType = waterType;
        mItemPrice = itemPrice;
        mItemQuantity = itemQuantity;
        mUserID = userID;
        mItemNo = itemNo;
        mItemUID = itemUID;
    }

    public String getmItemNo() {
        return mItemNo;
    }

    public void setmItemNo(String mItemNo) {
        this.mItemNo = mItemNo;
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

    public String getmItemUID() {
        return mItemUID;
    }

    public void setmItemUID(String mItemUID) {
        this.mItemUID = mItemUID;
    }
}
