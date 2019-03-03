package com.example.administrator.h2bot.dealer;

public class WaterDealerDriverLicenseModel {
    String mDriverLicense;
    String mDriverPlateNumber;
    String mUserID;
    public WaterDealerDriverLicenseModel(){

    }
    public WaterDealerDriverLicenseModel(String DriverLicense,String DriverPlateNumber, String UserID)
    {
        mDriverLicense = DriverLicense;
        mDriverPlateNumber = DriverPlateNumber;
        mUserID = UserID;

    }

    public String getmDriverLicense() {
        return mDriverLicense;
    }

    public void setmDriverLicense(String mDriverLicense) {
        this.mDriverLicense = mDriverLicense;
    }

    public String getmDriverPlateNumber() {
        return mDriverPlateNumber;
    }

    public void setmDriverPlateNumber(String mDriverPlateNumber) {
        this.mDriverPlateNumber = mDriverPlateNumber;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }
}
