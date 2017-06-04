package com.iilu.fendou.modules.login.entity;

import com.iilu.fendou.modules.entity.BaseModel;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;

@Table("loginuser")
public class UserLogin extends BaseModel {
    @Unique
    private String userid;
    private String username;
    private String password;
    private int time;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
