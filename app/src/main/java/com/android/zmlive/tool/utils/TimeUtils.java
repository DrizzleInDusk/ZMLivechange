package com.android.zmlive.tool.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kkan on 2016/7/27.
 */
public class TimeUtils {

    /**
     * 将字符串转换为时间戳
     * @param pattern 时间格式
     */
    public static String getTime(String user_time,String pattern) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }
    /**
     * 将时间戳转换为时间格式
     * @param timestamp  String类型时间戳
     * @param style     设置时间样式
     * @return  时间格式
     */
    public static String toDate(String timestamp, String style){
        long timeStamp = new Long(timestamp);
        timeStamp = timeStamp * 1000L;
        SimpleDateFormat format = new SimpleDateFormat(style);
        String result = format.format(new Date(timeStamp));
        return result;
    }
    /**
     * 将时间戳转换为时间格式
     * @param timestamp String类型时间戳
     * @return  [格式：“yyyy-MM-dd HH:mm:ss”]
     */
    public static String toDate(String timestamp) {
        return toDate(timestamp,"yyyy-MM-dd HH:mm:ss");
    }
    /**
     * 把unix时间戳转换为时间字符串
     * @param timeStamp 时间戳
     * @return [格式：“yyyy-MM-dd”]
     */
    public static String toDateShort(String timeStamp) {
        return toDate(timeStamp, "yyyy-MM-dd");
    }
    /**
     * 时间字符串转换至另一种格式
     * @param timeStamp 录入时间
     * @param pattern 录入时间格式
     * @param pattern2 输出时间格式
     * @return [格式：pattern2]
     */
    public static String datetoDate(String timeStamp,String pattern,String pattern2) {
        return toDate(getTime(timeStamp,pattern), pattern2);
    }

    /**
     * 当前时间戳
     * @return  long 时间戳
     */
    public static long getTimeDate() {
        long currentTime=new Date().getTime();
        return currentTime;
    }

    /**
     * 时差
     *@param before 时间
     * @return time_difference
     */
    public static String getTimelag(String before) {
        String time_difference = "--";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(before);
            Date curDate = new Date(System.currentTimeMillis());
            long diff = curDate.getTime() - date.getTime();// 这样得到的差值是微秒级别
            long seconds = diff / (1000);
            if(seconds<60){
                time_difference = seconds + "秒前";
            }else{
                long minute = seconds / 60;
                if(minute<60){
                    time_difference= minute + "分钟前";
                }else{
                    long hours = minute / 60;
                    if(hours<24){
                        time_difference = hours + "小时前";
                    }else{
                        long days = hours / 24;
                        if(days<31){
                            time_difference = days + "天前";
                        }else{
                            long month = days / 30;
                            if (month < 12) {
                                time_difference = month + "月前";
                            } else {
                                long year = month / 12;
                                time_difference = year + "年前";
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time_difference;
    }

}
