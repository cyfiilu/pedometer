package com.iilu.fendou.modules.login.entity;

public class LoginRequest {

    private String authName = "";
    private String authPassword = "";
    private String username = "";
    private String password = "";
    private String phonemodel = "";
    private String phonebrand = "";

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonemodel() {
        return phonemodel;
    }

    public void setPhonemodel(String phonemodel) {
        this.phonemodel = phonemodel;
    }

    public String getPhonebrand() {
        return phonebrand;
    }

    public void setPhonebrand(String phonebrand) {
        this.phonebrand = phonebrand;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "authName='" + authName + '\'' +
                ", authPassword='" + authPassword + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phonemodel='" + phonemodel + '\'' +
                ", phonebrand='" + phonebrand + '\'' +
                '}';
    }
}
