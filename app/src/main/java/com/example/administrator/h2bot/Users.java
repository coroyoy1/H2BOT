package com.example.administrator.h2bot;

class Users
{
    String userType, fullname, email, age, address, contact, password, stationRelatedNo, documentVerify;
    public Users()
    {

    }
    public Users(String userType, String fullname, String email, String age, String address, String contact, String password, String stationRelatedNo, String documentVerify)
    {
        this.userType = userType;
        this.fullname = fullname;
        this.email = email;
        this.age = age;
        this.address = address;
        this.contact = contact;
        this.password = password;
        this.stationRelatedNo = stationRelatedNo;
        this.documentVerify = documentVerify;
    }

    public String getStationRelatedNo() {
        return stationRelatedNo;
    }

    public String getDocumentVerify() {
        return documentVerify;
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
