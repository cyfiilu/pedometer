package com.iilu.fendou.utils;

import java.math.BigDecimal;

public class NumberUtil {

    /**
     * @param f 接收的double类型数据
     * @param scale 精确到的小数位数
     * @return float
     */
    public static float round(float f, int scale, int mode) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Float.toString(f));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, mode).floatValue();
    }

    /**
     * 四舍五入
     *
     * @param f
     * @return
     */
    public static int round(float f) {
        BigDecimal b = new BigDecimal(f);
        return b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

}
