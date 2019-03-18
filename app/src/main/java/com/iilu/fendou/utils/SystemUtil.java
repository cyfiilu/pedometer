package com.iilu.fendou.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.iilu.fendou.configs.ParseConfig;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class SystemUtil {

    private static final Logger mlog = Logger.getLogger(SystemUtil.class);

    public static void showInpputMethod(Context context, View view) {
        if (context == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏输入法
     *
     * @param context
     * @param view    The token of the window that is making the request
     */
    public static void hideInputMethod(Context context, View view) {
        if (context == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏输入法
     *
     * @param context
     */
    public static void hideInputMethod(Context context) {
        if (context == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            Activity activity = (Activity) context;
            if (activity != null) {
                View view = activity.getWindow().getCurrentFocus();
                if (view != null) {
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    /**
     * @param context
     * @param duration
     */
    public static void vibrate(Context context, long duration) {
        if (context == null) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, duration};
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取开机时间
     */
    public static long getBoottime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    /**
     * 判断app是否活着方式
     *
     * @param context
     * @return
     */
    public static boolean isAppAlive(Context context) {
        if (context == null) return false;
        String packageName = context.getPackageName();
        ActivityManager aManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (aManager != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = aManager.getRunningAppProcesses();
            for (int i = 0; i < processInfos.size(); i++) {
                ActivityManager.RunningAppProcessInfo info = processInfos.get(i);
                if (packageName.equalsIgnoreCase(info.processName)) return true;
            }
        }
        return false;
    }

    /**
     * 获取状态栏高度 方式1
     * 可在初始化时调用
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight_1(Context context) {
        int height = 0;
        // 获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源ID获取响应的尺寸值
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    /**
     * 反射获取状态栏高度 方式2
     * 可在初始化时调用
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight_2(Context context) {
        int height = 0;
        if (context == null) return height;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int h = (int) ParseUtil.parseValue(ParseConfig.INTEGER, field.get(obj).toString());
            height = context.getResources().getDimensionPixelSize(h);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return height;
    }

    /**
     * 获取状态栏高度 方法3
     * 应用区的顶端位置即状态栏的高度
     * 注意：该方法不能在初始化的时候用，只能在onWindowFocusChanged之后调用
     *
     * @return
     */
    public static int getStatusBarHeight_3(Context context) {
        if (!(context instanceof Activity)) return 0;
        Rect rect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 获取状态栏高度 方法4
     * 状态栏高度 = 屏幕高度 - 应用区高度
     * 注意：该方法不能在初始化的时候用，只能在onWindowFocusChanged之后调用
     *
     * @return
     */
    public static int getStatusBarHeight_4(Context context) {
        if (!(context instanceof Activity)) return 0;
        Activity activity = ((Activity) context);
        // 屏幕
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 应用区域
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return dm.heightPixels - rect.height();  // 状态栏高度 = 屏幕高度 - 应用区域高度
    }

    /**
     * 获取标题栏高度 方法1
     * 标题栏高度 = View绘制区顶端位置(其实就是 标题栏 + 状态栏) - 应用区顶端位置(其实就是状态栏)
     * 注意：该方法不能在初始化的时候用，只能在onWindowFocusChanged之后调用
     *
     * @param context
     * @return
     */
    public static int getTitleBarHeight_1(Context context) {
        if (!(context instanceof Activity)) return 0;
        Activity activity = ((Activity) context);

        // 应用区域
        Rect appRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(appRect);

        // View绘制区域
        int viewTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return viewTop - appRect.top;
    }

    /**
     * 获取标题栏高度 方法2
     * 标题栏高度 = 应用区高度 - View绘制区高度
     * 注意：该方法不能在初始化的时候用，只能在onWindowFocusChanged之后调用
     * 这种方法获取到的标题栏高度，好像多了一个状态栏高度，因此又减去一个状态栏高度
     *
     * @param context
     * @return
     */
    public static int getTitleBarHeight_2(Context context) {
        if (!(context instanceof Activity)) return 0;
        Activity activity = ((Activity) context);

        // 应用区域
        Rect appRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(appRect);

        // View绘制区域
        Rect viewRect = new Rect();
        // findViewById获取到的view就是程序不包括标题栏 和 状态栏的部分
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(viewRect);
        return appRect.height() - viewRect.height() - appRect.top;
    }

    /**
     * 获取屏幕的默认分辨率，单位：px
     * 可在初始化时调用
     *
     * @param context
     * @return
     */
    public static Point getScreenParams(Context context) {
        if (context == null) return null;
        if (!(context instanceof Activity)) return null;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return new Point(display.getWidth(), display.getHeight());
    }

    /**
     * 通过WindowManager获取屏幕宽度，单位：px
     * 可在初始化时调用
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (context == null) return 0;
        if (!(context instanceof Activity)) return 0;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 通过Resources获取屏幕高度，单位：px
     * 可在初始化时调用
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 通过Resources获取底部导航栏高度
     * 可在初始化时调用
     * 注意：底部没有导航栏时，也能获取到一个 大于0 的值
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int height = 0;
        if (context == null) return height;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

}
