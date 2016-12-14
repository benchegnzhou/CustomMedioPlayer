package com.itcast.zbc.videotestapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void start(View v) {
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://192.168.199.247/qwer1.avi"), "video/avi");
        startActivity(intent);
    }
}
