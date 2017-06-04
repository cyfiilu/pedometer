package com.iilu.fendou.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {

    /**
     * -1：无网络状态
     * @param context
     * @return
     */
    public static int getNetState(Context context) {
        int TYPE_NONE = -1;
        if (context == null) return TYPE_NONE;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connManager.getActiveNetworkInfo();
        if( netinfo == null) return TYPE_NONE;
        return netinfo.getType();
    }

    /**
     * 检查网络是否可用
     * @return true：可用；false：不可用
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        return netinfo != null && netinfo.isConnected();
    }

    /**
     * 判断网络当前连接模式是否是Wifi
     * @param context
     * @return 是wifi返回true
     */
    public static boolean isWifiNetConnected(Context context) {
        if (context == null) return false;
        // 获取系统的连接服务
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取网络的连接情况
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        // wifi连接
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断网络当前连接模式是否是数据流量
     * @param context
     * @return 是数据流量返回true
     */
    public static boolean isDataNetConnected(Context context) {
        if (context == null) return false;
        // 获取系统的连接服务
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取网络的连接情况
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        // wifi连接
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
