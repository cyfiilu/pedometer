package com.iilu.fendou.nets.http;

/**
 * 服务器接口地址
 */
public interface UrlContanier {

    /**
     * 测试环境接口地址
     */
    public static final String upload_base = "http://192.168.20.245:8080/";
    public static final String mobile_base = "http://192.168.20.85:8080/";

    /**
     * 现网环境接口地址
     */
//    public static final String upload_base = "http://sync.wanbu.com.cn/";
//    public static final String mobile_base = "http://mobile.wanbu.com.cn/";

    /**
     * 登录、注册模块
     */
    public static final String GetUserLogin =upload_base + "phoneServer/Login_Reguser_Service/GetUserLogin";
    public static final String Register =upload_base + "phoneServer/Login_Reguser_Service/Register";

}
