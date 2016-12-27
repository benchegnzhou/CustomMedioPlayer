package com.itcast.zbc.mediaplayer.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.bean.AudioItem;
import com.itcast.zbc.mediaplayer.common.CommonValue;
import com.itcast.zbc.mediaplayer.ui.activity.AudioPlayerActivity;
import com.itcast.zbc.mediaplayer.utils.LogUtil;
import com.itcast.zbc.mediaplayer.utils.SharedPreferencesUtil;
import com.itcast.zbc.mediaplayer.utils.ToastUtil.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *  * 作者：Zbc on 2016/12/16 02:05
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  音乐播放服务
 */
public class AudioService extends Service {

    //初始化当前播放歌曲的位置为 -1
    private int currentPosition=-1;
    private ArrayList<AudioItem> audioItems;
    private MediaPlayer mediaPlayer;
    private Serializable serializable;
    private AudioBinder audioBinder;

    //定义播放模式
    public static final int PLAYMODE_ALL_REPEAT = 0;
    public static final int PLAYMODE_SINGLE_REPEAT = 1;
    public static final int PLAYMODE_RANDOM = 2;
    private int mPlayMode = PLAYMODE_ALL_REPEAT;   //初始化为列表循环
    private SharedPreferencesUtil preferencesUtil;

    //定义通过通知栏 ，启动sevice的意图
    //上一曲
    public static final int NOTIFICATION_PRE = 0;
    //下一曲
    public static final int NOTIFICATION_NEXT = 1;
    //activity 重新绑定 service 暂停回复继续播放
    public static final int NOTIFICATION_CONTINUE = 2;
    //状态栏暂停播放状态切换
    public static final int NOTIFICATION_CHANGE_PLAYSTATUS = 3;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //提供一个builder作为外界activity控制的中间人
        return audioBinder;
    }

    /**
     * 初始化全局唯一量
     * 这个方法
     */
    @Override
    public void onCreate() {
        audioBinder = new AudioBinder();
        //初始化service 同时获取系统配置文件的播放类型
        preferencesUtil = SharedPreferencesUtil.getInstance(getApplicationContext());
        mPlayMode = preferencesUtil.getIntValue(CommonValue.MUSIC_PLAY_MODE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {   //解决手动结束进程服务崩溃
            return super.onStartCommand(intent, flags, startId);
        }
        //正常的activity启动会获取不到这个值，赋值为默认值-1  ，正常处理   ，其他的 情况是通过通知栏进行切换的
        int notifity_Type = intent.getIntExtra("notifity_Type", -1);

        switch (notifity_Type) {
            case NOTIFICATION_PRE:      //通知栏的上一曲启动的service
                audioBinder.playPre();
                break;
            case NOTIFICATION_NEXT:     //通知栏的下一曲启动的activity
                audioBinder.playNext();
                break;
            case NOTIFICATION_CONTINUE:  //通知栏启动界面activity的时候我们不希望界面改变我们的service播放状态，所以不进行操作
                break;
            case NOTIFICATION_CHANGE_PLAYSTATUS:  //通知栏的播放状态按钮点击
                audioBinder.changPauseStatus(true);
                break;
            default:            //首次绑定服务通过activity启动这个service的情况
                serializable = intent.getSerializableExtra("audioItems");
                //当前传入的 position
                int position = intent.getIntExtra("position", -1);
                if (serializable == null) {   //做空指针检测，增加健壮性    null数据直接进行强转直接就崩了
                    return super.onStartCommand(intent, flags, startId);  //遇到空指针就会直接跳过
                }
                audioItems = (ArrayList<AudioItem>) serializable;
                //处理列表页选中同一首正在播放的歌曲的问题
                if(position==currentPosition){
                    //选中的歌曲正在播放，不做任何的处理直接的刷新界面
                    audioBinder.notifyAudioPlayerListener();
                }else {
                    // 选中的歌曲和当前播放的歌曲不是同一首歌，刷新位置直接播放
                    currentPosition=position;
                    audioBinder.playCurrentMusic();
                }


                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        audioBinder.hiddenNotification();   //当服务进程被杀死的时候要取消通知栏消息，否则空指针
        LogUtil.e("进程结束");
        super.onDestroy();
    }

    /**
     * 作为与外界 activity 通讯的中间人
     */
    public class AudioBinder extends Binder {
        //service 的继续播放的状态
        public static final int BINDER_NOTIFICATION_CONTINUE = NOTIFICATION_CONTINUE;
        private NotificationManager notificationManager;
        private OnAudioPreparedListener onAudioPreparedListener;

        /**
         * 播放指定位置的歌曲
         */
        public void playCurrentMusic() {
            if (audioItems == null || audioItems.size() == 0 || currentPosition == -1) {  //列表数据异常检测
                return;  //遇到空指针，机会直接跳过
            }
//        LogUtil.e(audioItems.toString());

            //播放音乐
            String path = audioItems.get(currentPosition).getPath();
            // 播放新的歌曲之前，必须停止以前的歌曲
            if (mediaPlayer != null) {    //解决每次开始播放，是上一首歌不能停止的问题
                mediaPlayer.reset();
            } else {
                mediaPlayer = new MediaPlayer();
            }
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
                onAudioPreparedListener = new OnAudioPreparedListener();
                mediaPlayer.setOnPreparedListener(onAudioPreparedListener);
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                shownOrUpdataNotification();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 功能： 切换播放的状态
         * 如果正在播放，切换暂停状态
         * 否则，切换播放状态
         *
         * @param isNotification 是不是通过消息通知栏调用的这个方法
         */

        public void changPauseStatus(boolean isNotification) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                if (isNotification) {  //如果是通知栏调用就不需要隐藏通知消息了,直接的刷新一下转台就可以了
                    shownOrUpdataNotification();
                } else {
                    hiddenNotification();   //取消通知栏消息
                }
            } else {
                mediaPlayer.start();
                shownOrUpdataNotification();
            }
        }

        /**
         * 获取当前音乐播放状态
         *
         * @return 返回true： 正在播放
         */
        public boolean getPlayStatus() {
            return mediaPlayer.isPlaying();
        }


        /**
         * 播放上一曲
         */
        public void playPre() {
            currentPosition--;
            if (currentPosition >= 0) {
                playCurrentMusic();
            } else {
                currentPosition = audioItems.size() - 1;
            }
        }

        /**
         * 播放下一曲
         */
        public void playNext() {
            autoPlayNext(true);
        }

        /**
         * 切换音乐播放模式
         */
        public void switchPlayMode() {
            switch (mPlayMode) {
                case PLAYMODE_ALL_REPEAT:
                    mPlayMode = PLAYMODE_SINGLE_REPEAT;
                    break;
                case PLAYMODE_SINGLE_REPEAT:
                    mPlayMode = PLAYMODE_RANDOM;
                    break;
                case PLAYMODE_RANDOM:
                    mPlayMode = PLAYMODE_ALL_REPEAT;
                    break;
            }
            //将播放模式存储在配置文件
            preferencesUtil.putIntValue(CommonValue.MUSIC_PLAY_MODE, mPlayMode);
        }

        /**
         * 获取当前播放模式
         * {@link #PLAYMODE_ALL_REPEAT } : 列表播放,
         * {@link #PLAYMODE_SINGLE_REPEAT } : 单曲循环 ,
         * {@link #PLAYMODE_RANDOM } ： 随机播放
         *
         * @return Int
         */
        public int getPlayMode() {
            return mPlayMode;
        }


        /**
         * 根据播放模式自动播放下一首歌曲
         *
         * @param isNext 是不是播放下一曲调用的这个方法，这时候解决一个单曲循环也能解决跳转的效果
         */

        private void autoPlayNext(boolean isNext) {
            switch (mPlayMode) {
                case PLAYMODE_ALL_REPEAT:
                    currentPosition++;
                    if (currentPosition >= audioItems.size()) {
                        currentPosition = 0;
                    }
                    break;
                case PLAYMODE_SINGLE_REPEAT:
                    break;
                case PLAYMODE_RANDOM:
                    currentPosition = new Random().nextInt(audioItems.size());
                    break;
            }
            // 解决单曲循环的下一曲效果
            if (isNext && mPlayMode == PLAYMODE_SINGLE_REPEAT) {
                currentPosition++;
                if (currentPosition >= audioItems.size()) {
                    currentPosition = 0;
                }
            }
            playCurrentMusic();
        }

        /**
         * 获取当前播放歌曲的信息
         */
        public AudioItem getCurrentMusicData() {
            return audioItems.get(currentPosition);
        }

        /**
         * 获取当前歌曲的总时长
         *
         * @return
         */
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        /**
         * 获取当前歌曲进度
         *
         * @return
         */
        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        /**
         * 跳转播放位置到指定的毫秒数
         *
         * @param msec
         */
        public void seekTo(int msec) {
            mediaPlayer.seekTo(msec);
        }


        /**
         * 手动调用资源加载完成回调，为播放器界面初始化数据
         */
        public  void notifyAudioPlayerListener(){
            if(onAudioPreparedListener==null){  //空指针检查
                return;
            }
            onAudioPreparedListener.onPrepared(mediaPlayer);
        }

        /**
         * MedioPlayer资源加载完成回调
         */
        public class OnAudioPreparedListener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //音乐准备完成，开始播放
                mediaPlayer.start();

                // 发送广播，通知界面歌曲已经播放请求刷新界面  这里后期使用 EventBus替换更高效
                Intent intent = new Intent(CommonValue.BOADCAST_MUSICPREPERA);
                AudioItem audioItem = audioItems.get(currentPosition);
                intent.putExtra("audioItem", audioItem);
                sendBroadcast(intent);

            }
        }

        /**
         * 歌曲播放完成的监听回调
         */
        private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 发送广播，通知界面歌曲已经播放请求刷新界面  这里后期使用 EventBus替换更高效
                Intent intent = new Intent(CommonValue.BOADCAST_MUSICCOMPLETION);
                sendBroadcast(intent);

                //自动播放下一首音乐 ， 单曲循环不播放下一首
                autoPlayNext(false);
            }
        }


        /**
         * 显示通知
         */
        private void shownOrUpdataNotification() {
            Notification notification;
            //系统的版本小于 3.0 使用旧API 其他的使用新API，获取较好的用户体验，同时亦可以在较低的版本上面运行
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                notification = getNotificationByOldAPI();
            } else {
                //notification = getNotificationByNewAPI();
                notification = getCustomNotificationByNewAPI();
            }

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(100, notification);

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

            Notification noti = new Notification.Builder(getApplicationContext())
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
            Notification noti = new Notification.Builder(getApplicationContext())
                    .setTicker("转为后台播放")
                    .setSmallIcon(R.mipmap.notification_music_playing)
                    .setContent(getVontentView())
                    .setOngoing(true)  //设置通知消息不可移除
                    .setDefaults(Notification.DEFAULT_LIGHTS )   // | Notification.DEFAULT_SOUND
                    //.setVibrate(new long[] {0,300,500,700})  //自定义震动  实现效果：延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms。
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
            //获取歌曲名称
            String tittle = getCurrentMusicData().getTittle() == null ? "" : getCurrentMusicData().getTittle();
            //获取歌曲作者
            String artist = getCurrentMusicData().getArtist() == null ? "" : getCurrentMusicData().getArtist();
            remoteViews.setTextViewText(R.id.tv_custom_tittile, tittle);
            remoteViews.setTextViewText(R.id.tv_custom_artist, artist);
            if (mediaPlayer.isPlaying()) { //获取播放状态,并设置对应的图标
                remoteViews.setImageViewResource(R.id.iv_custom_playstatust, R.mipmap.btn_audio_pause);
            } else {
                remoteViews.setImageViewResource(R.id.iv_custom_playstatust, R.mipmap.btn_audio_play);
            }
            //设置点击事件
            remoteViews.setOnClickPendingIntent(R.id.rl_custom, getRlPendingIntent());
            remoteViews.setOnClickPendingIntent(R.id.iv_custom_pre, getPrePendingIntent());
            remoteViews.setOnClickPendingIntent(R.id.iv_custom_playstatust, getPlayStatusPendingIntent());
            remoteViews.setOnClickPendingIntent(R.id.iv_custom_next, getPlayNextPendingIntent());
            return remoteViews;
        }

        public PendingIntent getRlPendingIntent() {    // 最外层点击的意图
            Intent intent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
            intent.putExtra("notifity_Type", NOTIFICATION_CONTINUE);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return activity;
        }

        public PendingIntent getPrePendingIntent() {  //上一曲的意图
            Intent intent = new Intent(getApplicationContext(), AudioService.class);
            intent.putExtra("notifity_Type", NOTIFICATION_PRE);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return service;
        }

        public PendingIntent getPlayStatusPendingIntent() {    //暂停播放的的意图
            Intent intent = new Intent(getApplicationContext(), AudioService.class);
            intent.putExtra("notifity_Type", NOTIFICATION_CHANGE_PLAYSTATUS);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return service;
        }

        public PendingIntent getPlayNextPendingIntent() {
            Intent intent = new Intent(getApplicationContext(), AudioService.class);
            intent.putExtra("notifity_Type", NOTIFICATION_NEXT);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return service;
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
            PendingIntent broadcast = PendingIntent.getActivity(getApplicationContext(), 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return broadcast;
        }
    }


}
