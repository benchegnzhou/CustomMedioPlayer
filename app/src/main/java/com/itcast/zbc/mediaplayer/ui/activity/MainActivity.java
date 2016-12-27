package com.itcast.zbc.mediaplayer.ui.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
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

    private static final int PERMISSION_READ_EXTERNAL_STORAGE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    // 对应6.0 查询网络读写权限，如果没有权限暂时让程序卡在这里，防止崩溃
    if(queryReadExternalPermission()){
        //子类初始化完成调用父类
        doNext();
    }






    }

    /**
     * @param v
     */
    @Override
    protected void otherOnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Video:
                vpList.setCurrentItem(0);
                break;
            case R.id.tv_music:
                vpList.setCurrentItem(1);
                break;
        }
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

        //标题点击监听
        tvVideo.setOnClickListener(this);
        tvMusic.setOnClickListener(this);

        //页面滑动监听
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
        lineTittle.getLayoutParams().width = indicateWith;
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


    /**
     *  * 6.0设备网络权限请求
     * 这里是sd卡权限请求
     * @return {     返回 true: 表示这个网络权限已经申请过了 返回： false 表示这个权限没有申请过}
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean queryReadExternalPermission() {
        boolean hadReadExternalPermission=false;
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){   //android 系统版本6.0一下的不用执行权限判断
            return true;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            LogUtil.e(this.getClass(), "READ permission IS NOT granted...");
            // 没有对应权限，权限请求带代码
            requestReadExternalPermission();
        } else {
            LogUtil.e(this.getClass(), "READ permission is granted...");
            hadReadExternalPermission=true;
        }
        return hadReadExternalPermission;
    }



    /**
     * 对应权限的申请代码实体
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestReadExternalPermission(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            LogUtil.e(this.getClass(), "READ permission IS NOT granted...");

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                LogUtil.e(this.getClass(), "11111111111111");
            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
                LogUtil.e(this.getClass(), "222222222222");
            }
        } else {
            LogUtil.e(this.getClass(), "READ permission is granted...");
        }
    }

    /**
     * 权限申请成功后的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        LogUtil.e(this.getClass(), "requestCode=" + requestCode + "; --->" + permissions.toString()
                + "; grantResult=" + grantResults.toString());
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    // request successfully, handle you transactions


                    //子类初始化完成调用父类
                    doNext();
                } else {

                    // permission denied
                    // request failed
                }
                return;
            }
            default:
                break;

        }
    }

}
