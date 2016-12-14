package com.itcast.zbc.mediaplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.List;

/**
 *  * 作者：Zbc on 2016/12/3 01:34
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 * 是在pagerAdapter的基础上进一封装的
 *  
 */
public class listViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList;

    /**
     * 使用快捷键Alt+Insert可以快速的实现构造方法
     * @param fm
     * @param fragmentList
     */
    public listViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
