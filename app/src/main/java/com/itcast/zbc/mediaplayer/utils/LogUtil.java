package com.itcast.zbc.mediaplayer.utils;


import android.text.TextUtils;
import android.util.Log;

 import com.itcast.zbc.mediaplayer.BuildConfig;

/**
 * 输入log
 *
 * @author zbc
 */
public class LogUtil {
    public static void log(String str) {
        if (BuildConfig.LOG_DEBUG) {
            Log.i("i", str);
        }
    }

    private final static String tag = "MEDIA_PLAYER_ZBC_";   //添加公司名标签方便log过滤
    private final static boolean state = true;

    public static void d(String str) {
        if (state)
            Log.d(tag, str);
    }

    /**
     * 带有类名信息的log
     * @author:zbc
     * @param mTag
     * @param str
     */
    public static void d(String mTag, String str) {
        if (state)
            Log.d(mTag, str);
    }


    /**
     * @author:zbc
     * @param clazz 传入类名打印 log
     * @param str  日志的内容
     */
    public static void d(Class clazz, String str) {
        String mTag = tag+clazz.getSimpleName();
        if (state)
            Log.d(mTag, str);
    }

    /**
     * 错误级别的日志
     * @param str
     */
    public static void e(String str) {
        if (state)
            Log.e(tag,str);
    }

    /**
     * 错误级别的日志,可以输出错误信息
     * @param str
     */
    public static void e(String str,Throwable tr) {
        if (state)
            Log.e(tag, str, tr);
    }


    /**
     * 带有类名信息的log
     * @author:zbc
     * @param mTag
     * @param str
     */
    public static void e(String mTag, String str) {
        mTag = TextUtils.isEmpty(mTag) ? tag : mTag;
        if (state)
            Log.e(mTag, str);
    }


    /**
     * @author:zbc
     * @param clazz 传入类名打印 log
     * @param str  日志的内容
     */
    public static void e(Class clazz, String str) {
        String mTag = tag+"  :  "+clazz.getSimpleName();
        if (state)
            Log.e(mTag, str);
    }

}
