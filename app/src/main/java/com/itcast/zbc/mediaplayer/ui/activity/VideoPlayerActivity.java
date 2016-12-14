package com.itcast.zbc.mediaplayer.ui.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.bean.VideoItem;
import com.itcast.zbc.mediaplayer.utils.DateUtils;
import com.itcast.zbc.mediaplayer.utils.LogUtil;
import com.itcast.zbc.mediaplayer.utils.ToastUtil.ToastUtil;
import com.itcast.zbc.mediaplayer.view.VideoView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideoPlayerActivity extends BaseActivity {

    private static final int MSG_WHAT_UPDATASYSTEM_TIME = 0;
    private static final int MSG_WHAT_UPDATA_VIEDEO_POSITION = 1;
    private static final int MSG_WHAT_HIDE_CONTROLOR = 2;
    @Bind(R.id.vd_videoview)
    VideoView vdVideoview;
    @Bind(R.id.iv_video_mute)
    ImageView ivVideoMute;
    @Bind(R.id.sb_video_current_volume)
    SeekBar sbVideoCurrentVolume;
    @Bind(R.id.tv_video_current_time)
    TextView tvVideoCurrentTime;
    @Bind(R.id.sb_video_current)
    SeekBar sbVideoCurrent;
    @Bind(R.id.tv_video_count_time)
    TextView tvVideoCountTime;
    @Bind(R.id.iv_video_back)
    ImageView ivVideoBack;
    @Bind(R.id.iv_video_pre)
    ImageView ivVideoPre;
    @Bind(R.id.iv_video_pause)
    ImageView ivVideoPause;
    @Bind(R.id.iv_video_next)
    ImageView ivVideoNext;
    @Bind(R.id.iv_video_screensize)
    ImageView ivVideoScreensize;
    @Bind(R.id.tx_video_tittle)
    TextView txVideoTittle;
    @Bind(R.id.iv_video_electric)
    ImageView ivVideoElectric;
    @Bind(R.id.tx_video_system_time)
    TextView txVideoSystemTime;
    @Bind(R.id.view_video_alpha_cover)
    View viewVideoAlphaCover;
    //顶部面板
    @Bind(R.id.ll_video_top)
    LinearLayout llVideoTop;
    //底部面板
    @Bind(R.id.ll_video_bottom)
    LinearLayout llVideoBottom;
    @Bind(R.id.ll_video_loading)
    LinearLayout llVideoLoading;
    @Bind(R.id.ll_video_loading_inplaying)
    ProgressBar llVideoLoadingInplaying;
    private VideoBroadcastReceiver broadcastReceiver;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_UPDATASYSTEM_TIME:
                    upDataSystemTime();
                    break;
                case MSG_WHAT_UPDATA_VIEDEO_POSITION:
                    upDataPlayPosition();    //实时播放时间更新
                    break;
                case MSG_WHAT_HIDE_CONTROLOR:
                    hideControlor();    //5s延时到，隐藏控制面板
                    break;


            }

        }
    };
    private AudioManager myAudioManager;
    private int currentVolume;
    //列表数据集合
    private ArrayList<VideoItem> videoListItems;
    //当前条目位置
    private int currentVideosPosition;

    //当前的播放进度
    private int currentPosition;
    //当前条目
    private VideoItem videoItem;

    //底部面板的高
    private int bottomH;
    //顶部面板的高
    private int topH;
    private GestureDetector gestureDetector;
    //控制面板状态的标志
    private boolean MenuShowing = true;
    //后台播放暂停，资源已经加载完成
    private boolean state_background_pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏   
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //此两段代码必须设置在setContentView()方法之前
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);

        doNext();
    }

    /**
     * 将媒体库的路径转换成真实物理存储地址
     *
     * @param contentUri content://media/external/video/media/56937
     * @return 真实的物理地址uri
     */
    private Uri conrentTransformUri(Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor actualimagecursor = this.managedQuery(contentUri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        actualimagecursor.moveToFirst();

        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);
        return Uri.fromFile(file);
    }

    @Override
    protected void otherOnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video_pause:  //播放暂停按钮
                upDataPause();
                upDataPauseStatus();
                break;
            case R.id.iv_video_mute:    //静音按钮
                upDataMuteStatus();
                break;
            case R.id.iv_video_back:    //返回按钮
                finish();
                break;
            case R.id.iv_video_pre:    //上一视频按钮
                currentVideosPosition--;
                ivVideoPre.setEnabled(currentVideosPosition >= 0); //现在的条目小于0将状态设置伟不可点击
                ivVideoNext.setEnabled(currentVideosPosition < videoListItems.size());  //现在的条目大于制定的条目将状态设置伟不可点击
                if (currentVideosPosition < 0) {//现在的条目大于指定的条目不在进行播放操作
                    return;
                }
                videoPlayPosition();
                break;
            case R.id.iv_video_next:    //下一视频按钮
                currentVideosPosition++;
                ivVideoPre.setEnabled(currentVideosPosition >= 0); //现在的条目小于0将状态设置伟不可点击
                ivVideoNext.setEnabled(currentVideosPosition < videoListItems.size());  //现在的条目大于制定的条目将状态设置伟不可点击
                if (currentVideosPosition >= videoListItems.size()) {  //现在的条目大于指定的条目不在进行播放操作
                    return;
                }
                videoPlayPosition();
                break;

            case R.id.iv_video_screensize:    //返回按钮
                vdVideoview.switchFullScreen();
                upDatabtnFullScreen();
                break;

        }
    }

    /**
     * 更新全全屏按钮
     */
    private void upDatabtnFullScreen() {
        if (vdVideoview.isFullSreen()) {
            ivVideoScreensize.setImageResource(R.drawable.btn_video_defaultscreen);
        } else {
            ivVideoScreensize.setImageResource(R.drawable.btn_video_fullscreen);
        }


    }

    @Override
    protected void initListener() {
        //资源异步加载完成监听
        vdVideoview.setOnPreparedListener(new MyOnPreparedListener());

        //注册视频监听
        vdVideoview.setOnCompletionListener(new MyOnCompletionListener());
        ivVideoPause.setOnClickListener(this);
        ivVideoBack.setOnClickListener(this);
        ivVideoPre.setOnClickListener(this);
        ivVideoNext.setOnClickListener(this);
        ivVideoMute.setOnClickListener(this);
        ivVideoScreensize.setOnClickListener(this);


        // 注册广播获取系统电量信息
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        broadcastReceiver = new VideoBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);


        //注册进度条监听
        //音量进度条拖动监听,seekBar是可以公用一个监听器的
        MyOnSeekBarChangeListener seekBarChangeListener = new MyOnSeekBarChangeListener();
        sbVideoCurrentVolume.setOnSeekBarChangeListener(seekBarChangeListener);
        sbVideoCurrent.setOnSeekBarChangeListener(seekBarChangeListener);


        //初始化控制栏
        hideContrlorOnInit();

        //注册手势监听器,使用的时候需要将当前的系统的触摸事件交给他，才能分析
        //原来的类实现的抽象方法太多的时候，可以考虑使用他的子类
        gestureDetector = new GestureDetector(this, new mySimpleOnGestureListener());

        //注册资源缓冲监听
        vdVideoview.setBufferingUpdateListener(new MyOnBufferingUpdateListener());
        vdVideoview.setOnInfoListener(new MyOnInfoListener());
        vdVideoview.setOnErrorListener(new MyOnErrorListener());


    }


    @Override
    protected void initData() {
        /**
         * 外部打开媒体处理
         */
        //content://media/external/video/media/56937
        Uri uri = getIntent().getData();
        if (uri != null) {   //内容不为空，说明是一个通过文件管理器打开的视频
            if (uri.toString().contains("content://media/")) {   //如果是媒体库地址，获取到的是一个内容提供者提供的地址
                uri = conrentTransformUri(uri);
            }//如果不是媒体库形式的uri那就是一个真实路径的了后面的就统一处理
            LogUtil.e("uri=" + uri);
            vdVideoview.setVideoURI(uri);  //播放外部打开的指定视频
            //初始化标题
            txVideoTittle.setText(uri.getPath());
            //将上一曲和下一曲禁用
            ivVideoPre.setEnabled(false);
            ivVideoNext.setEnabled(false);
        } else {   //uri为空，说明是本地打开的视频
            videoListItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("videoListItems");
            //做一波健壮性检查
            if (videoListItems.size() == 0 || videoListItems == null) {
                return;
            }
            currentVideosPosition = getIntent().getIntExtra("position", 0);
            LogUtil.e("currentPosition" + currentPosition);
            //初始化播放选中位置的视频
            videoPlayPosition();
        }


        //更新系统时间
        upDataSystemTime();

        //初始化音量进度
        myAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取系统的最大音量
        int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//AudioManager这里面封装了来电铃声，通话，蓝牙，系统，闹钟，音乐等各种情景的铃声
        //当前音量获取
        int currentVolume = getCurrentVolume();
        //根据系统的音量界面初始化当前的音量值
        sbVideoCurrentVolume.setMax(maxVolume);
        sbVideoCurrentVolume.setProgress(currentVolume);

        //初始化屏幕亮度为全亮度
        ViewCompat.setAlpha(viewVideoAlphaCover, 0);
        //获取屏幕的高度
        mHalfScreenH = getWindowManager().getDefaultDisplay().getHeight() / 2;
        //获取屏幕的宽度
        mHalfScreenW = getWindowManager().getDefaultDisplay().getWidth() / 2;
    }


    /**
     * 静音按钮状态更新
     */
    private void upDataMuteStatus() {

        //如果当前音量不为0，则设置为静音
        if (getCurrentVolume() != 0) {
            currentVolume = getCurrentVolume();  //保存当前音量，方便下次还原
            setCurrentVolume(0, 0);
        } else {  //如果当前音量为0，则还原音量
            setCurrentVolume(currentVolume, 0);
        }


    }

    /**
     * 当前媒体音量是设置
     *
     * @param flag_showSystemDialog 是不是显示系统自带的音量进度条
     * @param volume                目标设置音量值
     */
    private void setCurrentVolume(int volume, int flag_showSystemDialog) {
        myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, flag_showSystemDialog);
        //同步更新进度条
        sbVideoCurrentVolume.setProgress(volume);
    }

    /**
     * 获取当前的媒体音量
     *
     * @return
     */
    private int getCurrentVolume() {
        return myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 暂停播放界面的更新
     * 工作的时候界面的逻辑代码和功能代码是分开的，这样方便后期的维护
     * 同时供代码和界面代码单独写还可以提高两者调用的灵活性
     */
    private void upDataPauseStatus() {
        if (vdVideoview.isPlaying()) {
            //开启handler实时进度更新
            upDataPlayPosition();
            ivVideoPause.setImageResource(R.drawable.btn_video_pause);
        } else {
            //handler更新消息移除
            handler.removeMessages(MSG_WHAT_UPDATA_VIEDEO_POSITION);
            ivVideoPause.setImageResource(R.drawable.btn_video_play);
        }

    }

    /**
     * 暂停更新的方法
     * 作用： 切换暂停的状态和更新暂停按钮的图片
     */
    private void upDataPause() {
        if (vdVideoview.isPlaying()) {
            vdVideoview.pause();   //调用videoView的暂停方法
        } else {
            vdVideoview.start();   //调用videoView的播放方法
        }
    }

    /**
     * 界面消失的时候调用
     */
    @Override
    protected void onResume() {
        if (state_background_pause) {   //异步线程资源加载完成，可以播放
            vdVideoview.start();   //调用videoView的播放方法
        }
        super.onResume();
    }

    /**
     * 界面恢复的时候调用
     */
    @Override
    protected void onPause() {
        if (vdVideoview.isPlaying()) {
            vdVideoview.pause();   //调用videoView的暂停方法
            state_background_pause = true;
        }
        super.onPause();
    }

    /**
     * 获取顶部和底部控制栏的测量宽高
     * 获取旷告是不可以在onCreate方法中直接的获取的到的
     * 有两种方式获取的到，分别是：
     * 1. 使用全局的监听回调，调用完成必须马上注销监听，不然一直回调；测量的结果就是实际的显示的效果，精确；慢效率低调用在measure()方法的后面
     * 2. 使用布局加载时的估计值；；在层叠布局中不能使用；调用快，在控件渲染之前就已经侧来过完成，但是在层叠布局中不精确
     */
    private void hideContrlorOnInit() {
        //创建布局监听器的形式获取控件的宽高
        //这种方式更加的精确，但是执行效率厚底一些
        llVideoTop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                topH = llVideoTop.getHeight();
                LogUtil.e("上栏的高度：" + topH);
                llVideoTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewCompat.animate(llVideoTop).translationY(-topH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                //更改面板状态的标志位
                MenuShowing = false;
            }
        });
        //这种方法的调用效率更高，优先执行
        llVideoBottom.measure(0, 0);
        bottomH = llVideoBottom.getMeasuredHeight();
        LogUtil.e("底部面板的高度： " + bottomH);
        ViewCompat.animate(llVideoBottom).translationY(bottomH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }


    /**
     * 切换控制面板的显示状态
     */
    private void switchControlor() {
        //如果是开就变成关，如果是关就变开

        if (MenuShowing) {
            hideControlor();
        } else {
            showControlor();
            //清除上一次的操作   ，代码是成对出现的最好放在一起
            handler.removeMessages(MSG_WHAT_HIDE_CONTROLOR);
            //打开面板的时候，延时5s自动关闭
            handler.sendEmptyMessageDelayed(MSG_WHAT_HIDE_CONTROLOR, 5000);
        }


    }

    /**
     * 控制面板显示
     */
    private void showControlor() {
        ViewCompat.animate(llVideoTop).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ViewCompat.animate(llVideoBottom).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        MenuShowing = true;
    }

    /**
     * 控制面板隐藏
     */
    private void hideControlor() {
        //这两种方式的区别，很显然 translationYBy(topH)实在移动后的基础上移动，translationY(bottomH)在布局文件基础上每次都是
        //ViewCompat.animate(llVideoTop).translationYBy(topH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ViewCompat.animate(llVideoTop).translationY(-topH).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        ViewCompat.animate(llVideoBottom).translationY(bottomH).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        MenuShowing = false;
    }

    /**
     * 视频播放进度更新
     */
    private void upDataPlayPosition() {
        //获取实时播放进度并更新
        int currentPosition = vdVideoview.getCurrentPosition();
        upDataVideoTimeUI(currentPosition);
        LogUtil.e("currentPosition" + currentPosition);
        //通过延时消息循环更新
        handler.sendEmptyMessageDelayed(MSG_WHAT_UPDATA_VIEDEO_POSITION, 500);
    }

    private void upDataVideoTimeUI(int currentPosition) {
        tvVideoCurrentTime.setText(DateUtils.videoTimeFormat(currentPosition));
        //进度条进度值设置
        sbVideoCurrent.setProgress(currentPosition);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 广播有注册就得有反注册
        unregisterReceiver(broadcastReceiver);
        //handler使用完成后会内存泄漏，应该在界面销毁的时候移除
        handler.removeCallbacksAndMessages(null);// // 参数为null时会清除所有消息。
    }


    /**
     * 视屏播放切换，播放指定位置的视频
     */
    private void videoPlayPosition() {
        videoItem = videoListItems.get(currentVideosPosition);
        String videoPath = videoItem.getPath();
        vdVideoview.setVideoPath(videoPath);    //设置播放路径
        //公司要求快速开发的时候，可以使用系统自带的播放控制进度条，虽然丑但是还不错
        //vdVideoview.setMediaController(new MediaController(this));
        LogUtil.e(videoItem.toString());
        //初始化标题
        txVideoTittle.setText(videoItem.getTittle());
    }

    /**
     * 更新界面的系统时间
     */
    private void upDataSystemTime() {
        String systemTime = DateUtils.systemTimeFormat();
        txVideoSystemTime.setText(systemTime);
        handler.sendEmptyMessageDelayed(MSG_WHAT_UPDATASYSTEM_TIME, 500);
    }

    /**
     * 当进度条改变的时候调用
     * 媒体音量进度和视频播放进度公用一套
     * seekBar的监听器是可以公用一套的，通过seekBar的id区别
     */
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }  //只处理来自用户的操作
            switch (seekBar.getId()) {
                case R.id.sb_video_current_volume:
                    /**
                     * 根据进度条修改系统的音量
                     *  参数1： 参数的类型（媒体音量还是通话等）
                     *  参数2：目标值
                     *  参数3： 是不是显示系统自带的Dialog 0：不显示，1：显示
                     */
                    setCurrentVolume(progress, 0);
                    break;
                case R.id.sb_video_current:
                    //进度条进度值设置
                    vdVideoview.seekTo(progress);
                    break;
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //清除上一次的操作   ，代码是成对出现的最好放在一起
            handler.removeMessages(MSG_WHAT_HIDE_CONTROLOR);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //打开面板的时候，延时5s自动关闭
            handler.sendEmptyMessageDelayed(MSG_WHAT_HIDE_CONTROLOR, 5000);
        }
    }

    /**
     * 接收电池变化信息的广播
     */
    private class VideoBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取系统电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            updataBatteryPic(level);
        }
    }

    /**
     * 时时更新电量显示图片
     *
     * @param level
     */
    private void updataBatteryPic(int level) {
//        LogUtil.e("当前系统电量"+level);
        if (level < 10) {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_0);
        } else if (level < 20) {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_10);
        } else if (level < 40) {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_20);
        } else if (level < 60) {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_40);
        } else if (level < 80) {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_60);
        } else if (level < 100) {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_80);
        } else {
            ivVideoElectric.setImageResource(R.mipmap.ic_battery_100);
        }


    }

    private float mStartY = 0;   //手指按下时的Y坐标
    private float mStartX = 0;   //手指按下时的X坐标
    private int moveDownVolume = 0;  // 音量滑动
    private float startAlpha;     //手指按下时的控件透明度
    //获取屏幕的高度的一半
    private int mHalfScreenH;
    //获取屏幕的宽度的一半
    private int mHalfScreenW;

    /**
     * 屏幕触摸事件的监听
     * 最终使用的音量 = 起始音量 + 偏移音量
     * 偏移音量 = 最大音量 * 划过屏幕的百分比
     * 划过屏幕的百分比 = 手指划过屏幕的距离 / 屏幕的高度
     * 手指划过屏幕的距离 = 手指当前的位置 - 是指落下的位置
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //屏幕的手势分析器如果分析手势的操作，必须先获取到当前的触摸事件
        gestureDetector.onTouchEvent(event);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = event.getY();
                mStartX = event.getX();
                //系统当前音量获取
                moveDownVolume = getCurrentVolume();
                //获取起始透明度
                startAlpha = ViewCompat.getAlpha(viewVideoAlphaCover);
                break;
            case MotionEvent.ACTION_MOVE:
                float mCurrentY = event.getY();
                //滑动的距离
                float offsetY = mStartY - mCurrentY;

                //手势滑动的百分比
                float movePresent = offsetY / mHalfScreenH;
                if (mStartX < mHalfScreenW) {   //左侧屏幕滑动
                    //根据手指移动百分比，改变屏幕亮度
                    upDataMoveAlpha(movePresent);
                } else {
                    //根据手指移动百分比，改变媒体音量
                    upDataMoveVolume(movePresent);
                }
                break;

        }

        return true;
    }

    /**
     * 根据手指滑动改变媒体音量
     *
     * @param movePresent
     */
    private void upDataMoveVolume(float movePresent) {
        //获取系统的最大音量
        int maxVolume = sbVideoCurrentVolume.getMax();
        //音量的偏移值
        int offsetVolume = (int) (maxVolume * movePresent);
        //最终的音量
        int finalVolume = moveDownVolume + offsetVolume;
        //将音量设置给系统
        setCurrentVolume(finalVolume, 0);
    }

    /**
     * 根据手指滑动改变屏幕亮度
     * 这里折中，使用蒙版的形式改变亮度
     * 其实工作中也是，一种方式实现有难度，可以采用另一种方式
     *
     * @param movePresent
     */
    private void upDataMoveAlpha(float movePresent) {
        float finalApha = startAlpha - movePresent * 1;
        //对亮度值进行最后的限定
        if (finalApha >= 0 && finalApha <= 0.8) {   //这里系统没有为我们的透明度做上下限的约束，需要我们自己约束
            //将透明度设置给控件
            ViewCompat.setAlpha(viewVideoAlphaCover, finalApha);
        }
    }


    /**
     * 设置MediaPlayer的异步加载监听
     * 这个方法在资源异步加载完成后调用
     * 在给videoView设置完路径后直接的调用vdVideoview.start(); 可能会在资源异步加载还没完成的时候就调用播放start了
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //视频加载完成，隐藏加载的遮罩
            llVideoLoading.setVisibility(View.GONE);

            //资源加载完成，开启播放
            vdVideoview.start();
            //开始播放的时候更新按钮的播放状态
            upDataPauseStatus();

            //更新播放进度和总时长
            int duration = vdVideoview.getDuration();
            tvVideoCountTime.setText(DateUtils.videoTimeFormat(duration));
            //进度最大时长设置
            sbVideoCurrent.setMax(duration);
            //初始化当前时间
            int currentPosition = vdVideoview.getCurrentPosition();
            upDataCurrentTimeUI(currentPosition);
            upDataPlayPosition();
        }
    }

    /**
     * 当前播放时间界面UI更新
     *
     * @param currentPosition
     */
    private void upDataCurrentTimeUI(int currentPosition) {
        upDataVideoTimeUI(currentPosition);
    }

    /**
     * 更能视频播放结束的时候此方法被调用
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //当视频播放结束的时候次方法被调用
            //解决部分视频播放完成视频时间没有同步完成的bug
            upDataCurrentTimeUI(vdVideoview.getDuration());
            //视频播放完成，停止handler消息
            handler.removeMessages(MSG_WHAT_UPDATA_VIEDEO_POSITION);

            //同步视频播放按钮
            upDataPauseStatus();
            LogUtil.e("vdVideoview.isPlaying():::" + vdVideoview.isPlaying());
        }
    }


    /**
     * 手势分析器
     */
    private class mySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * 这个是单击
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            switchControlor();
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击时调用
         * 双击全屏切换
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // LogUtil.e("onDoubleTap");
            vdVideoview.switchFullScreen();
            upDatabtnFullScreen();
            return super.onDoubleTap(e);
        }


        /**
         * 长按时调用
         * 长按暂停
         *
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            //LogUtil.e("onLongPress");
            upDataPause();
            upDataPauseStatus();
            super.onLongPress(e);
        }
    }

    /**
     * 网络视频缓冲进度的监听器
     */
    private class MyOnBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (vdVideoview.getCurrentPosition() == vdVideoview.getDuration() && percent == 100) {   //刷新进度再次达到100%刷新完成不在刷新
                return;
            }
            float loadingProgress = vdVideoview.getDuration() * percent / 100f;    //将缓存进度进行换算
            sbVideoCurrent.setSecondaryProgress((int) loadingProgress);
        }
    }

    /**
     * 当视频不放过程中，缓冲属性发生变化时被回调
     * 在这里处理了缓冲进度条
     */
    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    ToastUtil.showToastShort("当前网络差");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:   //视频播放过程中，缓冲开始
                    llVideoLoadingInplaying.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:     //视频播放过程中，缓冲结束
                    llVideoLoadingInplaying.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    }

    /**
     * 视频播放错误时回调
     */
    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, final int what, int extra) {
            String errMsg="";
            switch (what){
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    errMsg="网络连接超时，请检查网络或服务器无资源";
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    errMsg="未知的服务器或视频";
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    errMsg="暂不支持的视频格式";
                    break;
            }
            //通过dialog提示错误信息
            AlertDialog.Builder dialog =new AlertDialog.Builder(VideoPlayerActivity.this);
            dialog.setIcon(R.mipmap.notification_music_playing);
            dialog.setTitle("警告");
            dialog.setMessage(TextUtils.isEmpty(errMsg)?"该视屏无法播放":errMsg);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   finish();
                }
            });
            dialog.create().show();
            return false;
        }
    }
}
