package com.example.administrator.h2bot;

class Users
{
    String userType, fullname, email, age, address, contact, password, stationRelatedNo, status, imageUri;
    public Users()
    {

    }


    public Users(String userType, String fullname, String email, String age, String address, String contact, String password, String stationRelatedNo, String status, String imageUri)
    {
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
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStationRelatedNo(String stationRelatedNo) {
        this.stationRelatedNo = stationRelatedNo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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
