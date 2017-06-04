package com.iilu.fendou.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    /**
     * 屏幕中央
     * @param context
     * @param message
     */
    public static void showCenter(Context context, String message) {
        if (context == null) return;
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 屏幕中央
     * @param context 上下文
     * @param resId   资源id
     */
    public static void showCenter(Context context, int resId) {
        if (context == null) return;
        Toast toast = Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 屏幕下方
     * @param context
     * @param message
     */
    public static void showBottom(Context context, String message) {
        if (context == null) return;
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 170);
        toast.show();
    }
}
