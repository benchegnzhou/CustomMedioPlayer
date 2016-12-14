package com.itcast.zbc.mediaplayer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.itcast.zbc.mediaplayer.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //延时跳转到主界面
        //在Android系统中延时操作使用 Handler 更多一些，同时 Handler 是系统提供的，更容易管控
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);  //子线程睡眠2000ms
                gotoMainActivity();

            }
        }).start();*/

        //Handler实现
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoMainActivity();
            }
        },2000);    //延时2000ms界面跳转


    }

    /**
     * 跳转到主线程
     */
    private void gotoMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();  //结束当前界面
    }


}
