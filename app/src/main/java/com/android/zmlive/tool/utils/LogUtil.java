package com.android.zmlive.tool.utils;

import android.util.Log;

/**
 * Created by Kkan on 2016/10/31.
 */

public class LogUtil {
    /**
     * Log.v详细(Verbose)信息,输出颜色为黑色
     * @param tag
     * @param mess
     */
    public static void showVLog(String tag,String mess){
        Log.v(tag,mess);
    }
    /**
     * Log.d调试(Debug)信息,输出颜色是蓝色
     * @param tag
     * @param mess
     */
    public static void showDLog(String tag,String mess){
        Log.d(tag,mess);
    }

    /**
     * Log.i通告(Info)信息,输出颜色为绿色
     * @param tag
     * @param mess
     */
    public static void showILog(String tag,String mess){
        Log.i(tag,mess);
    }

    /**
     * Log.w警告(Warn)信息,输出颜色为橙色
     * @param tag
     * @param mess
     */
    public static void showWLog(String tag,String mess){
        Log.w(tag,mess);
    }
    /**
     * Log.e错误(Error)信息,输出颜色为红色
     * @param tag
     * @param mess
     */
    public static void showELog(String tag,String mess){
        Log.e(tag,mess);
    }
    public static void showELog(String tag,String mess,Exception e){
        Log.e(tag,mess,e);
    }
}
