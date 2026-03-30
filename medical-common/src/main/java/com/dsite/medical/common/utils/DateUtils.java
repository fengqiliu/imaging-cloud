package com.dsite.medical.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {

    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前日期格式
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期字符串
     */
    public static String getNowStr() {
        return dateToStr(new Date());
    }

    /**
     * 日期转字符串
     */
    public static String dateToStr(Date date) {
        return dateToStr(date, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 日期转字符串
     */
    public static String dateToStr(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 字符串转日期
     */
    public static Date strToDate(String dateStr) {
        return strToDate(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 字符串转日期
     */
    public static Date strToDate(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期增加天数
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        long time = date.getTime();
        time += days * 24L * 60 * 60 * 1000;
        return new Date(time);
    }
}
