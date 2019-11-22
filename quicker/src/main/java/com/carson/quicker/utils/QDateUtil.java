package com.carson.quicker.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QDateUtil {

    /**
     * 默认时间字符串格式 "HH:mm"
     */
    public final static String TIME_HH_MM = "HH:mm";

    /**
     * 默认时间字符串格式 "HH:mm:ss"
     */
    public final static String TIME_DEFAULT = "HH:mm:ss";


    /**
     * 默认日期字符串格式 "yyyy-MM-dd"
     */
    public final static String DATE_DEFAULT = "yyyy-MM-dd";

    /**
     * 默认日期字符串格式 "yyyy-MM-dd HH:mm"
     */
    public final static String DATE_HH_MM = "yyyy-MM-dd HH:mm";

    /**
     * 默认日时字符串格式 "yyyy-MM-dd HH:mm:ss"
     */
    public final static String DATETIME_DEFAULT = "yyyy-MM-dd HH:mm:ss";


    /**
     * 默认日时字符串格式 "yyyy-MM-dd HH:mm:ss EEEE"
     */
    public final static String DATETIME_WEEK = "yyyy-MM-dd HH:mm:ss EEEE";


    public static String getTime() {
        return getFormat(new Date(), TIME_DEFAULT);
    }

    public static String getDate() {
        return getFormat(new Date(), DATE_DEFAULT);
    }

    public static String getDateTime() {
        return getFormat(new Date(), DATETIME_DEFAULT);
    }

    public static String getFormat(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }


    public static Date parseTime(String timeText) {
        return parse(timeText, TIME_DEFAULT);
    }

    public static Date parseDate(String timeText) {
        return parse(timeText, DATE_DEFAULT);
    }

    public static Date parseDateTime(String timeText) {
        return parse(timeText, DATETIME_DEFAULT);
    }

    public static Date parse(String timeText, String pattern) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            date = dateFormat.parse(timeText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
