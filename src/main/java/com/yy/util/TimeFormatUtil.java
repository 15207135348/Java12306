package com.yy.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TimeFormatUtil {

    public static long date2Stamp(String str, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return date.getTime();
        }
        return  0;
    }

    public static String stamp2Date(long ts, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(ts));
    }

    public static String currentDate(){
        return stamp2Date(System.currentTimeMillis(), "yyyy-MM-dd");
    }

    public static String CNTime2UNTime(String dates)
    {
        String[] split = dates.split("/");
        StringBuilder builder = new StringBuilder();
        for (String date : split)
        {
            long ts = date2Stamp(date, "yyyy年M月d日");
            String da = stamp2Date(ts, "yyyy-MM-dd");
            builder.append(da).append("/");
        }
        return builder.substring(0, builder.length()-1);
    }


    public static String toBeijingGMTTime(String date)
    {
        long timestamp = date2Stamp(date, "yyyy-MM-dd");
        //"Tue Feb 04 2020 00:00:00 GMT+0800 (中国标准时间)"
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
        String res = sdf.format(new Date(timestamp));
        return res.replaceAll("CST", "GMT+0800 (中国标准时间)");
    }

    public static String getGapTime(long ms)
    {
        long hours = ms/1000/60/60;
        long minutes = (ms-hours*(1000 * 60 * 60 ))/(1000* 60);
        String diffTime;
        if(minutes<10){
            diffTime=hours+"时0"+minutes+"分";
        }else {
            diffTime=hours+"时"+minutes+"分";
        }
        return diffTime;
    }



    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(date).getTime();
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long lt){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }



    public static void main(String[] args) {
        System.out.println(toBeijingGMTTime("2020-01-19"));
    }
}
