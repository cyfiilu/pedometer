package com.iilu.fendou.utils;

import android.text.format.DateUtils;

import com.iilu.fendou.configs.ParseConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil extends DateUtils {

    public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

    private static SimpleDateFormat mSdfCurrDay_1 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat mSdfCurrDay_2 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat mSdfHour = new SimpleDateFormat("dd");

    /**
     * 将传进来的毫秒时间，转换成其所在天的凌晨零点
     * @param mills
     * @return 0：转换失败；
     */
    public static long convertToZeroTime(long mills) {
        long zeroMills = 0;
        String date = mSdfCurrDay_1.format(mills);
        Date zeroDate = null;
        try {
            zeroDate = mSdfCurrDay_2.parse(date + "000000");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        if (zeroDate != null) {
            c.setTime(zeroDate);
            zeroMills = c.getTimeInMillis();
        }
        return zeroMills;
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getCurrDayDate() {
        return mSdfCurrDay_1.format(System.currentTimeMillis());
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public static int getCurrHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取毫秒时间数中的小时
     * @param millis
     * @return
     */
    public static int getHour(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(millis));
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /***
     * 获得指定格式的时间
     * @param format
     * @param timeMillis
     * @return
     */
    public static String formatDate(String format, long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timeMillis);
    }

    /**
     * Unix时间戳转换成时间格式
     *
     * @param format
     * @param timeStamp
     * @return
     */
    public static String formatUnixTimeStamp(String format, long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timeStamp * 1000);
    }

    /**
     * 将普通时间转换为 Unix 时间戳
     *
     * @param format
     * @param dateStr
     * @return
     */
    public static long toUnixTimeStamp(String format, String dateStr) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        long time = 0;
        try {
            Date date = df.parse(dateStr);
            time = date.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取多少个小时之前/之后的日期
     *
     * @param format   日期格式
     * @param currDate 当前日期
     * @param hours    多少个小时之后
     * @return
     */
    public static String offsetHourDate(String format, String currDate, int hours) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(sdf.parse(currDate));
            c.add(Calendar.HOUR, hours);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date = c.getTime();
        return sdf.format(date);
    }

    /**
     * 多少天之前/之后的日期
     *
     * @param format
     * @param currDate
     * @param days     多少天之后
     * @return
     */
    public static String offsetDate(String format, String currDate, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(sdf.parse(currDate));
            c.add(Calendar.DATE, days);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date = c.getTime();
        return sdf.format(date);
    }

    /**
     * 检查日期是否正确
     *
     * @param format
     * @param date
     * @return
     */
    public static boolean isDateFormatRight(String format, String date) {
        boolean isRight;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            isRight = true;
        } catch (Exception e) {
            isRight = false;
        }
        return isRight;
    }

    public static String convertToFormatDate(String date) {
        if (date == null || date.length() == 0) return "";
        long dateMills = (long) ParseUtil.parseValue(ParseConfig.LONG, date);
        return convertToFormatDate(dateMills);
    }

    public static String convertToFormatDate(long dateMills) {
        long currDateMills = new Date().getTime() / 1000;
        long oneminute = 60L; // 分钟
        long onehours = 60L * 60; // 小时
        long oneday = 60L * 60 * 24; // 天
        long onemonth = 60L * 60 * 24 * 30; // 月
        long oneyear = 60L * 60 * 24 * 30 * 12; // 年
        long result = currDateMills - dateMills / 1000;
        String str = "";
        if (result < oneminute) { // 小于1分钟
            str = "刚刚";
        } else if (oneminute <= result && result < onehours) { // 分钟
            str = ((int) result / oneminute) + "分钟前";
        } else if (onehours <= result && result < oneday) { // 小时
            str = ((int) result / onehours) + "小时前";
        } else if (oneday <= result && result < onemonth) { // 天
            str = ((int) result / oneday) + "天前";
        } else if (onemonth <= result && result < oneyear) { // 月
            str = ((int) result / onemonth) + "月前";
        } else if (oneyear <= result) { // 年
            str = ((int) result / oneyear) + "年前";
        }
        return str;
    }

    public static void main(String[] args) {

    }

}
