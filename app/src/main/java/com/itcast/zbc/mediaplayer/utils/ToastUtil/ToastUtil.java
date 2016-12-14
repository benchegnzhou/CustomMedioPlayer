package com.itcast.zbc.mediaplayer.utils.ToastUtil;

import android.widget.Toast;

import com.itcast.zbc.mediaplayer.appcation.MyApplication;

/**
 * Created by lenovo on 2016/9/1.
 * 创建一个自己的吐司工具类,使用全局单例的形式，在短时间可以完成多个弹窗的转换
 * author: zbc
 */
public class ToastUtil {
    public static Toast toast;

    /**
     * 弹窗短吐司
     * @param message： 吐司消息
     */
    public static void showToastShort(String message) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.sAppContext, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 弹窗长吐司
     * @param message： 吐司消息
     */
    public static void showToastLong(String message) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.sAppContext, message, Toast.LENGTH_LONG);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

}
