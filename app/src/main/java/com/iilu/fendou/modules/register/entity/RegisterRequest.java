package com.iilu.fendou.modules.register.entity;

public class RegisterRequest {

    private String authCode = "";
    private String mobile = "";
    private String regtype = "";
    private String username = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRegtype() {
        return regtype;
    }

    public void setRegtype(String regtype) {
        this.regtype = regtype;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "authCode='" + authCode + '\'' +
                ", mobile='" + mobile + '\'' +
                ", regtype='" + regtype + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
