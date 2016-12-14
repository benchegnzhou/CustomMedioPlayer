package com.itcast.zbc.mediaplayer.ui.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import com.itcast.zbc.mediaplayer.R;


/**
 * @anthor Zbc
 * @data 2016/11/29
 * @联系方式： mappstore@163.com
 * @describe 所有类的基类（其实这就是框架呢），也可以说架构就是界面框架
 * @作用 约定代码结构，统一编程风格
 * 这就是我们这个app的代码架构
 */
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

    /**
     * 子类不需要实现onCreate()方法，只需要返回对应的布局文件就可以了
     * @param savedInstanceState 使用框架结构，规范别人代码
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 初始化完成执行其他方法
     */
    protected  void doNext(){
        initListener();
        initData();
    }
    /**
     * 将子类共有的点击事件放在这里进行处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                otherOnClick(v);
                break;
        }
    }

    /**
     * 让子类处理没有在BaseActivity处理的点击事件
     */
    protected abstract void otherOnClick(View v);



    /**
     * 用来初始化适配器，监听和广播相关的注册
     */
    protected abstract void initListener();

    /**
     * 相关数据的初始化
     */
    protected abstract void initData();

    /**
     * 要求子类实现，获取对应的布局文件
     * @return 返回的参数是一个布局文件
     */
//    protected abstract int getLayoutId();
}
