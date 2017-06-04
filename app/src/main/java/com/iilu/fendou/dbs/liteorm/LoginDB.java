package com.iilu.fendou.dbs.liteorm;


import com.iilu.fendou.modules.login.entity.UserLogin;

import java.util.List;

public class LoginDB {

    public static void saveLoginUser(String userid, String username, String password, int updateLoginTime) {
        UserLogin model = new UserLogin();
        model.setUserid(userid);
        model.setUsername(username);
        model.setPassword(password);
        model.setTime(updateLoginTime);
        LiteOrmDB.insert(model);
    }

    public static void updateLogintime(String userid, String username, String password, int updateLoginTime) {
        UserLogin model = new UserLogin();
        model.setUsername(username);
        model.setUserid(userid);
        model.setPassword(password);
        model.setTime(updateLoginTime);
        LiteOrmDB.update(model);
    }

    public static UserLogin queryLastLoginUser() {
        List<UserLogin> list = LiteOrmDB.queryByDescOrder(UserLogin.class, "time", 0, 1);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            UserLogin model = new UserLogin();
            model.setUsername("");
            model.setPassword("");
            return model;
        }
    }

    public static List<UserLogin> queryAllLoginUser() {
        List<UserLogin> list = LiteOrmDB.queryByDescOrder(UserLogin.class, "time", 0, 3);
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public static boolean isUserExist(int userid) {
        List<UserLogin> list = LiteOrmDB.queryByWhere(UserLogin.class, "userid", new Object[]{ userid });
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
