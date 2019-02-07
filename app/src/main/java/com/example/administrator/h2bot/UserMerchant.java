package com.example.administrator.h2bot;

class UserMerchant
{
    String username, userType, fullname, email, age, address, contact, password, stationRelatedNo, status, imageUri, stationName;
    public UserMerchant()
    {

    }
    public UserMerchant(String username, String userType, String fullname, String email, String age, String address, String contact, String password,String stationName, String stationRelatedNo, String status, String imageUri)
    {
        this.username = username;
        this.userType = userType;
        this.fullname = fullname;
        this.email = email;
        this.age = age;
        this.address = address;
        this.contact = contact;
        this.password = password;
        this.stationRelatedNo = stationRelatedNo;
        this.status = status;
        this.imageUri = imageUri;
        this.stationName = stationName;
    }
    public String getImageUri()
    {
        return imageUri;
    }

    public String getUsername()
    {
        return username;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationRelatedNo() {
        return stationRelatedNo;
    }

    public String getStatus() {
        return status;
    }

    public String getUserType() {
        return userType;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getPassword() {
        return password;
    }
}
