package com.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by cwj on 16/2/4.
 * 日期工具类
 * 1.TimeStamp的值规定为10位,秒
 * 2.月份的值规定为0～11
 */
public class DateUtils {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 获取今天的date
     *
     * @return
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 获取日期的字符串,以指定的格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDateString(Date date, String format) {
        if (date == null || TextUtils.isEmpty(format))
            return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINESE);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取日期的字符串,以默认的格式
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {
        return getDateString(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 获取时间的字符串,以默认的格式
     *
     * @param date
     * @return
     */
    public static String getTimeString(Date date) {
        return getTimeString(date, DEFAULT_TIME_FORMAT);
    }

    /**
     * 获取时间的字符串,以指定的格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String getTimeString(Date date, String format) {
        if (date == null || TextUtils.isEmpty(format))
            return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINESE);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取几天以后的日期
     *
     * @param dates
     * @return
     */
    public static Date getFutureDate(int dates) {
        return getDate(dates);
    }

    /**
     * 获取几天以前的日期
     *
     * @param dates
     * @return
     */
    public static Date getPreviousDate(int dates) {
        return getDate(-dates);
    }

    /**
     * 根据年月日获得date
     * 注:传入的月份应该也是从0开到11的
     *
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static Date getDate(int year, int month, int date) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, date);
        return calendar.getTime();
    }

    /**
     * 根据年月日时分秒获得date
     * 注:传入的月份应该也是从0开到11的
     *
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static Date getDate(int year, int month, int date, int hour, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, date, hour, minute, second);
        return calendar.getTime();
    }

    //获取前后某一天的日期
    private static Date getDate(int dates) {
        Date today = getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, dates);
        return calendar.getTime();
    }

    /**
     * 获取日期的指定字段(年月日时分秒等)
     * 字段要用Calender的字段(exa:Calender.YEAR)
     * 注:月份返回的是索引,从0开始到11
     *
     * @param date
     * @param field
     * @return
     */
    public static int getValue(Date date, int field) {
        if (date == null)
            return -1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    /**
     * 获得两个date的差值,单位为秒
     * 始终为非负值
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDateDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return -1;
        long diff = date1.getTime() - date2.getTime();
        return Math.abs(diff / 1000);
    }

    /**
     * 判断两个时间是否超过某个值
     *
     * @param date1
     * @param date2
     * @param second 指定时间差,单位是秒
     * @return
     */
    public static boolean overTime(Date date1, Date date2, long second) {
        return getDateDifference(date1, date2) >= second;
    }

}
