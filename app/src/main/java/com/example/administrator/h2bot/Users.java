package com.example.administrator.h2bot;

class Users
{
    String userType, fullname, email, age, address, contact, password;
    public Users()
    {

    }
    public Users(String userType, String fullname, String email, String age, String address, String contact, String password)
    {
        this.userType = userType;
        this.fullname = fullname;
        this.email = email;
        this.age = age;
        this.address = address;
        this.contact = contact;
        this.password = password;
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
