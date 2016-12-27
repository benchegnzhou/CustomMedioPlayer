package com.itcast.zbc.mediaplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SPUtils 这个类主要是用来保存或者获取数据的时候使用
 */
public class SPUtils {
    public static final String SP_NAME = "config";
    private static SharedPreferences sp;

    public static void saveBoolean(Context ct, String key, boolean value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME,ct.MODE_PRIVATE );   //私有权限
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context ct, String key, boolean defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defValue);

    }

    public static void saveString(Context ct, String key, String value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context ct, String key, String defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        return sp.getString(key, defValue);

    }
}
