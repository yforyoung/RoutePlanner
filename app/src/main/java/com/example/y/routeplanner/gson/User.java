package com.example.y.routeplanner.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yforyoung on 2018/3/21.
 */

public class User implements Serializable {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("username")
    private String userName;
    private String password;
    private String gender;
    private String birthday;
    @SerializedName("head_portrail")
    private String headPortrail;
    private String introduce;
    private String telphone;
    private String  authority;

    public User() {
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User(String  userId, String userName, String password, String gender, String birthday, String headPortrail, String introduce, String telphone, String authority) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.gender = gender;
        this.birthday = birthday;
        this.headPortrail = headPortrail;
        this.introduce = introduce;
        this.telphone = telphone;
        this.authority = authority;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHeadPortrail() {
        return headPortrail;
    }

    public void setHeadPortrail(String headPortrail) {
        this.headPortrail = headPortrail;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
