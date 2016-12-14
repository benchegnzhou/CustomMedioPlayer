package com.itcast.zbc.mediaplayer.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.adapter.listViewPagerAdapter;
import com.itcast.zbc.mediaplayer.ui.fragment.AudioListFragment;
import com.itcast.zbc.mediaplayer.ui.fragment.VideoListFragment;
import com.itcast.zbc.mediaplayer.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {


    @Bind(R.id.tv_Video)
    TextView tvVideo;
    @Bind(R.id.tv_music)
    TextView tvMusic;
    @Bind(R.id.line_tittle)
    TextView lineTittle;
    @Bind(R.id.vp_list)
    ViewPager vpList;
    private com.itcast.zbc.mediaplayer.adapter.listViewPagerAdapter listViewPagerAdapter;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //子类初始化完成调用父类
        doNext();

    }

    /**
     * @param v
     */
    @Override
    protected void otherOnClick(View v) {

    }


    /**
     * 适配器，点击事件的监听,注册广播接收者
     * 这里的PagerAdPater的设计思想就体现了，先设置adapter  然后监听数据改变的思想
     */
    @Override
    protected void initListener() {
        fragmentList = new ArrayList<>();
        listViewPagerAdapter = new listViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        if (listViewPagerAdapter != null) {
            vpList.setAdapter(listViewPagerAdapter);
        }

        vpList.setOnPageChangeListener(new pagerChangeListener());

    }

    /**
     * 数据操作初始化,初始化整个界面
     */
    @Override
    protected void initData() {
        // 填充viewpager 的界面
        fragmentList.add(new VideoListFragment());
        fragmentList.add(new AudioListFragment());
        listViewPagerAdapter.notifyDataSetChanged();
        //初始化选择默认的界面
        vpList.setCurrentItem(0);
        //初始化选中标签0
        upDataAllTabs(0);


        //更新指示器的宽度
        int sreenWith = getWindowManager().getDefaultDisplay().getWidth();
        int indicateWith = sreenWith / fragmentList.size();
        lineTittle.getLayoutParams().width=indicateWith;
        lineTittle.requestLayout(); //请求系统重新测量摆放

    }

    /**
     * viewPager 的滑动监听
     */
    class pagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            // 处理指示器平移
//            最终要使用的位置 = 起始位置 + 偏移位置
//            起始位置 = position * 指示器的宽度
//            偏移位置 = 页面展开的百分比 * 指示器宽度
            int offsetX = (int) (positionOffset * lineTittle.getWidth());
            int startX = position * lineTittle.getWidth();
            int translateX = startX + offsetX;
//            ViewCompat.animate(lineTittle).translationX(translateX).start();  //这两种方式调用出来的效果是不一样的
            ViewCompat.setTranslationX(lineTittle, translateX);

        }

        @Override
        public void onPageSelected(int position) {
            //将选中的标题变大且高亮，没选中的标题变小且变暗
            upDataAllTabs(position);


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 根据选中标签的位置更新，所有的控件的状态
     *
     * @param position 当前选中标签的位置
     */
    private void upDataAllTabs(int position) {
        //这个方法抽取的 过程太重要了，现将所有会变化的公共变量替换成一个变量，然后抽取
        upDataTab(position, tvVideo, 0);
        upDataTab(position, tvMusic, 1);
    }

    /**
     * 这个类的 抽取思想是精髓： 一定多看：
     * 先将要抽取代码中所有变化的量（公共代码差异的量，替换成变量），然后进行方法抽取
     *
     * @param position    当前viewPager 选中的位置
     * @param tab         将要进行操作的view
     * @param tabPosition 被操作view对应的位置
     */
    private void upDataTab(int position, TextView tab, int tabPosition) {
        //变色
        tab.setSelected(position == tabPosition);

        //缩放
        //写代码的时候注意，尽量的提取
        // 动画设置，没有必要进行版本的判断，谷歌提供的兼容类
        if (position == tabPosition) {
            ViewCompat.animate(tab).scaleX(1.1f).scaleY(1.1f).setDuration(260).setInterpolator(new AnticipateOvershootInterpolator()).start();
        } else {
            ViewCompat.animate(tab).scaleX(0.9f).scaleY(0.9f).setDuration(260).setInterpolator(new AnticipateOvershootInterpolator()).start();

        }
    }


}
