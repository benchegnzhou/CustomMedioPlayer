package com.itcast.zbc.mediaplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itcast.zbc.mediaplayer.R;


/**
 *  * 作者：Zbc on 2016/12/5 18:18
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  是VideoFragment和 MusicFragment共有代码的抽取
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(),null);   //生成全局变量，方便子类调用

        return rootView;
    }


    protected void doNext(){
        initListener();
        initData();
    }


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
    protected abstract int getLayoutId();

    /**
     * 将子类共有的点击事件放在这里进行处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getFragmentManager().popBackStack();  //这个是fragment的退出方式
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
}
