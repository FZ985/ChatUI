package com.yilian.blurdemo;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public final class DateTime {
    public static final long DAY = 86400000L;
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final String FORMAT_DATE_TIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_SHORT_TIME = "HH:mm";
    private static final String FORMAT_TIME = "HH:mm:ss";
    public static final long HALF_DAY = 43200000L;
    public static final long WEEKLY = 604800000L;
    private static TimeZone gDefaultTimeZone;
    private static SimpleDateFormat gFormatter = new SimpleDateFormat();

    static {
        gDefaultTimeZone = TimeZone.getTimeZone("GMT+8");
        gFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    public static String format(String paramString) {
        return format(paramString, new Date());
    }

    public static String format(String paramString, long paramLong) {
        return format(paramString, new Date(paramLong));
    }

    public static String format(String paramString, long paramLong, TimeZone paramTimeZone) {
        return format(paramString, new Date(paramLong), paramTimeZone);
    }

    public static String format(String paramString, Date paramDate) {
        return format(paramString, paramDate, gDefaultTimeZone);
    }

    public static String format(String paramString, Date paramDate, TimeZone paramTimeZone) {
        synchronized (gFormatter) {
            gFormatter.setTimeZone(paramTimeZone);
            gFormatter.applyPattern(paramString);
            String str = gFormatter.format(paramDate);
            return str;
        }
    }

    public static String format(String paramString, TimeZone paramTimeZone) {
        return format(paramString, new Date(), paramTimeZone);
    }

    public static String toDate() {
        return format("yyyy-MM-dd", new Date());
    }

    public static String toDate(long paramLong) {
        return format("yyyy-MM-dd", new Date(paramLong));
    }

    public static String toDate(Date paramDate) {
        return format("yyyy-MM-dd", paramDate);
    }

    public static String toDateTime() {
        return format("yyyy-MM-dd HH:mm:ss", new Date());
    }

    public static String toDateTime(long paramLong) {
        return format("yyyy-MM-dd HH:mm:ss", new Date(paramLong));
    }

    public static String toDateTime(Date paramDate) {
        return format("yyyy-MM-dd HH:mm:ss", paramDate);
    }

    public static String toDateTimeMs() {
        return format("yyyy-MM-dd HH:mm:ss.SSS", new Date());
    }

    public static String toDateTimeMs(long paramLong) {
        return format("yyyy-MM-dd HH:mm:ss.SSS", new Date(paramLong));
    }

    public static String toDateTimeMs(Date paramDate) {
        return format("yyyy-MM-dd HH:mm:ss.SSS", paramDate);
    }

    public static String toShortTime() {
        return format("HH:mm", new Date());
    }

    public static String toShortTime(long paramLong) {
        return format("HH:mm", new Date(paramLong));
    }

    public static String toShortTime(Date paramDate) {
        return format("HH:mm", paramDate);
    }

    public static String toTime() {
        return format("HH:mm:ss", new Date());
    }

    public static String toTime(long paramLong) {
        return format("HH:mm:ss", new Date(paramLong));
    }

    public static String toTime(Date paramDate) {
        return format("HH:mm:ss", paramDate);
    }

    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String format, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Date toDate(String format, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private static SimpleDateFormat format_ymdhms = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss

    public static String getSsimpleDateFormatYMDHMS(long time) {
        return format_ymdhms.format(new Date(time));
    }


    /**
     * 通过时间戳判断 是今天还是昨天还是更早
     *
     * @param mills
     * @return
     */
    public static String getDate(long mills) {
        long oneDay = 24 * 60 * 60 * 1000;
        long currentMills = System.currentTimeMillis();
        long time = currentMills - mills;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentMills));
        int HH = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        int ss = calendar.get(Calendar.SECOND);
        long hourMills = HH * 60 * 60 * 1000;
        long mmMills = mm * 60 * 1000;
        long ssMills = ss * 1000;
        long todayTotals = hourMills + mmMills + ssMills;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if (time <= todayTotals) {
            return "今天 " + format.format(new Date(mills));
        }
        if (time <= (todayTotals + oneDay)) {
            return "昨天 " + format.format(new Date(mills));
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mills));
    }


    public static String timeAgo(String timeStr) {
        Date date;
        if (TextUtils.isEmpty(timeStr)) {
            return "";
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(timeStr);

            long timeStamp = date.getTime();

            Date currentTime = new Date();
            long currentTimeStamp = currentTime.getTime();
            long seconds = (currentTimeStamp - timeStamp) / 1000;

            long minutes = Math.abs(seconds / 60);
            long hours = Math.abs(minutes / 60);
            long days = Math.abs(hours / 24);

            if (seconds <= 15) {
                return "刚刚";
            } else if (seconds < 60) {
                return seconds + "秒前";
            } else if (seconds < 120) {
                return "1分钟前";
            } else if (minutes < 60) {
                return minutes + "分钟前";
            } else if (minutes < 120) {
                return "1小时前";
            } else if (hours < 24) {
                return hours + "小时前";
            } else if (hours < 24 * 2) {
                return "1天前";
            } else if (days < 30) {
                return days + "天前";
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                String dateString = formatter.format(date);
                return dateString;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String timeFormatOne(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return "";
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(timeStr);
            SimpleDateFormat formatReverse = new SimpleDateFormat("yyyy/MM/dd");
            return formatReverse.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}