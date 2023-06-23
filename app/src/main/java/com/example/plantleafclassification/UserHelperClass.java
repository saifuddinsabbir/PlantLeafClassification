package com.example.plantleafclassification;

public class UserHelperClass {
    String fullName, userName, dateOfBirth, email, contact, password, gender, district, subDistrict, address;

    public UserHelperClass() {
    }


    public UserHelperClass(String fullName, String userName, String dateOfBirth, String email, String contact, String password, String gender, String district, String subDistrict, String address) {
        this.fullName = fullName;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.contact = contact;
        this.password = password;
        this.gender = gender;
        this.district = district;
        this.subDistrict = subDistrict;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
