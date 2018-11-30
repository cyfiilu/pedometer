package com.iilu.fendou.utils;

public class LogUtil {

    /** 调试版本：打印所有log */
    private static final boolean isDebugged = true;
    /** 发布版本：只将d、e写入文件 */
    private static final boolean isReleased = true;

    public static void v(String tag, String msg) {
        if (isDebugged) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (isDebugged) {
            android.util.Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebugged || isReleased) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isDebugged || isReleased) {
            android.util.Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebugged) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (isDebugged) {
            android.util.Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebugged) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (isDebugged) {
            android.util.Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebugged || isReleased) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isDebugged || isReleased) {
            android.util.Log.e(tag, msg, tr);
        }
    }

    public static void wtf(String tag, String msg) {
        if (isDebugged) {
            android.util.Log.wtf(tag, msg);
        }
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if (isDebugged) {
            android.util.Log.wtf(tag, msg, tr);
        }
    }

    /**
     * 生成消息
     * @param msg
     * @return
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder().append(caller.getClassName()).append(".").append(caller.getMethodName())
                .append("(" + caller.getFileName() + ":" + caller.getLineNumber() + ")")
                .append("  ").append(msg).toString();
    }
}
