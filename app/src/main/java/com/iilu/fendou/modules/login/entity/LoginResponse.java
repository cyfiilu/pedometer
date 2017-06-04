package com.iilu.fendou.modules.login.entity;

public class LoginResponse {

    private int redirectflag = 0;
    private String accessToken = "";

    public int getRedirectflag() {
        return redirectflag;
    }

    public void setRedirectflag(int redirectflag) {
        this.redirectflag = redirectflag;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "redirectflag=" + redirectflag +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
