package com.iilu.fendou.utils;

import com.iilu.fendou.configs.ParseConfig;

public class ParseUtil {

    /**
     * string类型转其他基本类型，转换方式1
     * @param type
     * @param value
     * @return
     */
    public static Object valueOf(int type, Object value) {
        try {
            switch (type) {
                case ParseConfig.INTEGER:
                    return Integer.valueOf(value.toString());
                case ParseConfig.FLOAT:
                    return Float.valueOf(value.toString());
                case ParseConfig.LONG:
                    return Long.valueOf(value.toString());
                case ParseConfig.DOUBLE:
                    return Double.valueOf(value.toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * string类型转其他基本类型，转换方式2
     * @param type
     * @param value
     * @return
     */
    public static Object parseValue(int type, Object value) {
        try {
            switch (type) {
                case ParseConfig.INTEGER:
                    return Integer.parseInt(value.toString());
                case ParseConfig.FLOAT:
                    return Float.parseFloat(value.toString());
                case ParseConfig.LONG:
                    return Long.parseLong(value.toString());
                case ParseConfig.DOUBLE:
                    return Double.parseDouble(value.toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //double d = (double) parseValue(ParseConfig.DOUBLE, 2.1);
        //System.out.println(d);
    }

}
