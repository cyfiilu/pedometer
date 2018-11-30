package com.iilu.fendou.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionUtil {

    /**
     * 检测是否有权限
     * @param activity
     * @param checkPermissionArr
     * @return
     */
    public static boolean checkPermission(Activity activity, String[] checkPermissionArr) {
        if (activity == null || checkPermissionArr == null) return false;
        for (int i = 0; i < checkPermissionArr.length; i++) {
            String checkPermission = checkPermissionArr[i];
            int checkResult = ActivityCompat.checkSelfPermission(activity, checkPermission);
            if (checkResult != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    /**
     * android 6.0 动态申请存储权限，写日志用
     */
    public static void requestPermission(Activity activity, String[] permissionsArr, int requestCode) {
        if (activity == null || permissionsArr == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, permissionsArr, requestCode);
        }
    }
}
