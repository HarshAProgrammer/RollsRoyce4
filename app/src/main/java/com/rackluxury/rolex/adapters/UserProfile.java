package com.rackluxury.rolex.adapters;

public class UserProfile {

    public String userName;
    public String userEmail;
    public String userPhoneNo;

    public UserProfile(){

    }

    public UserProfile(String userName, String userEmail, String userPhoneNo){
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNo = userPhoneNo;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }
}
