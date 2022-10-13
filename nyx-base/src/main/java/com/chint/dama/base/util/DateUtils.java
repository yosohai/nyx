
package com.chint.dama.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    private static final String[] parsePatterns = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public DateUtils() {
    }

    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    public static String covertTimeToDate(String time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(time) * 1000L));
    }

    public static long covertTimeToDateLong(String dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = formatter.parse(dateTime);
            return date.getTime();
        } catch (ParseException var3) {
            var3.printStackTrace();
            return 9223372036854775807L;
        }
    }

    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    public static String formatDate(Date date, Object... pattern) {
        if (date == null) {
            return null;
        } else {
            String formatDate = null;
            if (pattern != null && pattern.length > 0) {
                formatDate = DateFormatUtils.format(date, pattern[0].toString());
            } else {
                formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
            }

            return formatDate;
        }
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateTimeT() {
        return formatDate(new Date(), "HH:mm:ss.SSSZ");
    }

    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        } else {
            try {
                return parseDate(str.toString(), parsePatterns);
            } catch (ParseException var2) {
                return null;
            }
        }
    }

    public static long pastDays(Date date) {
        long t = (new Date()).getTime() - date.getTime();
        return t / 86400000L;
    }

    public static long getDeltaDays(Date startDate, Date endDate) {
        long t = endDate.getTime() - startDate.getTime();
        return t / 86400000L;
    }

    public static long getDeltaWeeks(Date startDate, Date endDate) {
        long t = endDate.getTime() - startDate.getTime();
        return t / 604800000L;
    }

    public static long getDeltaMonths(Date startDate, Date endDate) {
        long t = endDate.getTime() - startDate.getTime();
        return t / -1702967296L;
    }

    public static String toStr() {
        return toStr(new Date());
    }

    public static String toStr(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static long pastHour(Date date) {
        long t = (new Date()).getTime() - date.getTime();
        return t / 3600000L;
    }

    public static long pastMinutes(Date date) {
        long t = (new Date()).getTime() - date.getTime();
        return t / 60000L;
    }

    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / 86400000L;
        long hour = timeMillis / 3600000L - day * 24L;
        long min = timeMillis / 60000L - day * 24L * 60L - hour * 60L;
        long s = timeMillis / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L;
        long sss = timeMillis - day * 24L * 60L * 60L * 1000L - hour * 60L * 60L * 1000L - min * 60L * 1000L - s * 1000L;
        return (day > 0L ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    public static double getDaysBetweenDate(Date before, Date after) {
        return (double)(getMillisecBetweenDate(before, after) / 86400000L);
    }

    public static long getMillisecBetweenDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return afterTime - beforeTime;
    }

    public static String getFirstDayOfMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(2, 0);
        c.set(5, 1);
        String first = format.format(c.getTime());
        return first;
    }

    public static double getDoubleType(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(dateString);
        return (double)date.getTime();
    }

    public static int calLastedTime(Date startTime, Date endTime) {
        long a = startTime.getTime();
        long b = endTime.getTime();
        int c = (int)((a - b) / 1000L);
        return c;
    }

    public static String getPassHours(String startTime, int hours) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = df.parse(startTime);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, calendar.get(11) + hours);
        return df.format(calendar.getTime());
    }

    public static String getAddDate(String time, int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = dateFormat.parse(time);
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        Date newDate = null;

        try {
            newDate = addDate(date, days);
        } catch (ParseException var6) {
            var6.printStackTrace();
        }

        String st = dateFormat.format(newDate);
        return st;
    }

    public static String getAddDateEx(String time, int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = dateFormat.parse(time);
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        Date newDate = null;

        try {
            newDate = addDate(date, days);
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        SimpleDateFormat dateFormatEx = new SimpleDateFormat("yyyy-MM-dd");
        String st = dateFormatEx.format(newDate);
        return st;
    }

    public static Date toDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException var4) {
            var4.printStackTrace();
        }

        return date;
    }

    public static Date addDate(Date date, long day) throws ParseException {
        long time = date.getTime();
        day = day * 24L * 60L * 60L * 1000L;
        time += day;
        return new Date(time);
    }

    public static Date getMoment(int hour, int min, int sec, int mill) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(11, hour);
        calendar.set(12, min);
        calendar.set(13, sec);
        calendar.set(14, mill);
        return calendar.getTime();
    }

    public static Date getMoment(int year, int month, int day, int hour, int min, int sec, int mill) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, month);
        calendar.set(5, day);
        calendar.set(11, hour);
        calendar.set(12, min);
        calendar.set(13, sec);
        calendar.set(14, mill);
        return calendar.getTime();
    }
}
