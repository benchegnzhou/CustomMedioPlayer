package com.itcast.zbc.mediaplayer.appcation;

import android.app.Application;
import android.content.Context;

/**
 *  * 作者：Zbc on 2016/12/2 12:48
 *  * 邮箱：mappstore@163.com
 *   功能描述：初始化全局公共变量和方法
 *  
 */
public class MyApplication extends Application {

    public static MyApplication application;
    public static Context sAppContext;


    /**
     * 创建全局单例的对象
     *
     * @return
     */
    public static MyApplication getInstance() {
        if (application == null) {
            application = new MyApplication();
        }
        return application;
    }

    /**
     * 获取全局上下文
     *
     * @return
     */
    public Context getAppContext() {
        if (sAppContext == null) {
            sAppContext = this;
        }
        return sAppContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;

    }
}
