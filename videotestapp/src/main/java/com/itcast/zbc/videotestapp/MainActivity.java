package com.itcast.zbc.videotestapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @butterknife.Bind(R.id.btn_videoPlay)
    Button btnVideoPlay;
    @butterknife.Bind(R.id.btn_shown_otification)
    Button btnShownOtification;
    @butterknife.Bind(R.id.btn_hidden_notification)
    Button btnHiddenNotification;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterknife.ButterKnife.bind(this);
        initListener();
        initData();
    }


    private void initListener() {
        btnVideoPlay.setOnClickListener(this);
        btnShownOtification.setOnClickListener(this);
        btnHiddenNotification.setOnClickListener(this);
    }


    private void initData() {
        String ss = getIntent().getStringExtra("ss");
        if (TextUtils.isEmpty(ss)) {
            return;
        }
        Log.e("shoudao xiaoxi _______", ss);
    }

    /**
     * 调用外部引用播放音乐
     */
    public void startPlay() {
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://192.168.199.247/qwer2.mp4"), "video/mp4");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_videoPlay:
                startPlay();
                break;
            case R.id.btn_shown_otification:
                shownNotification();
                break;
            case R.id.btn_hidden_notification:
                hiddenNotification();
                break;
        }
    }

    /**
     * 显示通知
     */
    private void shownNotification() {
        Notification notification;
        //系统的版本小于 3.0 使用旧API 其他的使用新API，获取较好的用户体验，同时亦可以在较低的版本上面运行
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            notification = getNotificationByOldAPI();
        } else {
            //notification = getNotificationByNewAPI();
            notification = getCustomNotificationByNewAPI();
        }

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(100, notification);   //这里的请求要和取消通知的请求码对应

    }

    /**
     * 隐藏通知
     */
    private void hiddenNotification() {
        if (notificationManager == null) {   //防止对象没有创建出现空指针
            return;
        }
        notificationManager.cancel(100);
    }

    /**
     * 使用旧的API获取通知栏消息，notification.setLatestEventInfo() 这个方法，在版本6.0已经这个方法已经被移除
     * 兼容范围是：API <  6.0 都可以
     * 过时的方法，兼容低版本的3.0以前的设备使用
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Notification getNotificationByOldAPI() {
        /**
         * 参数1： 状态栏图标  参数2： 状态栏文字提示  参数3 ：消息时间戳  一般是系统当前的毫秒值
         */
        Notification notification = new Notification(R.mipmap.icon, "音乐正在后台播放", System.currentTimeMillis());
        //这个方法只有在，编译器的版本不大于22时候才能生效，此方法在Android 6.0以后被删除了
        String tittile = "通知";
        String content = "视屏播放";
        // notification.setLatestEventInfo(this, tittile, content, getContentIntent());

        //设置下拉消息图标
        notification.largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_audio_play);
        //设置下拉列表消息不可移除
        //消息进程正在后台执行 和 FLAG_NO_CLAER 是一样的,里面有好多的flag自己研究
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    /**
     * 使用新的API可以获得较好的适配体验，但是在低版本的兼容性上面存在问题
     * 兼容范围  3.0 < API
     * Notification.Builder(this).build()               方法最低兼容到API 16   4.1.x
     * Notification.Builder(this).getNotification()     方法最低兼容到API 11   3.0
     *
     * @return
     */
    public Notification getNotificationByNewAPI() {

        Notification noti = new Notification.Builder(this)
                .setTicker("转为后台播放")
                .setContentTitle("音乐正在后台播放 ")
                .setContentText("music: Dream")
                .setContentInfo("我是补充内容")
                .setSmallIcon(R.mipmap.notification_music_playing)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_audio_play))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(getContentIntent())    //获取提示正文的点击响应
                .setOngoing(true)  //设置通知消息不可移除
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0, 300, 500, 700})  //自定义震动  实现效果：延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms。
                .getNotification();


        //.build();  这个方法最低兼容到API  16  Android 4.1 有点太新了，兼容性差
        return noti;
    }

    /**
     * 自定已通知栏布局，实现通知栏消息通知
     *
     * @return
     */
    public Notification getCustomNotificationByNewAPI() {
        Notification noti = new Notification.Builder(this)
                .setTicker("转为后台播放")
                .setSmallIcon(R.mipmap.notification_music_playing)
                .setContent(getVontentView())
                .setOngoing(true)  //设置通知消息不可移除
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0, 300, 500, 700})  //自定义震动  实现效果：延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms。
                .getNotification();
        return noti;
    }

    /**
     * 获取通知消息正文布局
     *
     * @return
     */
    public RemoteViews getVontentView() {
        //  RemoteViews(String packageName, int layoutId) 这里的 packageName 是龚茜彤使用的，系统通过packageName进行反射获取对用的View
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_remoteview);
        remoteViews.setTextViewText(R.id.tv_custom_tittile, "演员");
        remoteViews.setTextViewText(R.id.tv_custom_artist, "薛之谦");
        remoteViews.setImageViewResource(R.id.iv_custom_playstatust, R.mipmap.btn_audio_play);
        //设置点击事件
        remoteViews.setOnClickPendingIntent(R.id.rl_custom, getRlPendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_custom_pre, getPrePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_custom_playstatust, getPlayStatusPendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_custom_next, getPlayNextPendingIntent());
        return remoteViews;
    }

    public PendingIntent getRlPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ss", "最外层点击");
        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return activity;
    }

    public PendingIntent getPrePendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ss", "上一曲");
        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
        PendingIntent activity = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return activity;
    }

    public PendingIntent getPlayStatusPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ss", "暂停播放");
        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
        PendingIntent activity = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return activity;
    }

    public PendingIntent getPlayNextPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ss", "下一曲");
        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
        PendingIntent activity = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return activity;
    }


    /**
     * 生成正文的点击响应
     * 当正文的通知被点击的时候返回这个意图，完成对应的动作
     *
     * @return
     */
    public PendingIntent getContentIntent() {
        // 这个的意图必须是系统提前创建好的，不可以手动创建
        // PendingIntent.getActivities()  启动过个activity
        //PendingIntent.getActivity();  启动单个activity
        //PendingIntent.getBroadcast(); 发送一个广播使用这个
        //PendingIntent.getService()  启动服务对应这个
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://192.168.199.247/qwer2.mp4"), "video/mp4");
        //PendingIntent.FLAG_UPDATE_CURRENT 当前如果有消息就把他更新起来
        PendingIntent broadcast = PendingIntent.getActivity(this, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return broadcast;
    }


}
