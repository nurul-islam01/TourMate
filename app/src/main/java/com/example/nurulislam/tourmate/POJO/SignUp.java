package com.example.nurulislam.tourmate.POJO;

import java.io.Serializable;

public class SignUp implements Serializable {
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userId;

    public SignUp(String userName, String userEmail, String userPhone, String userId) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userId = userId;
    }

    public SignUp() {
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserId() {
        return userId;
    }
}
