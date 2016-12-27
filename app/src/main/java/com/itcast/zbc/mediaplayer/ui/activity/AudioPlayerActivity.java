package com.itcast.zbc.mediaplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.bean.AudioItem;
import com.itcast.zbc.mediaplayer.bean.LyricBeen;
import com.itcast.zbc.mediaplayer.common.CommonValue;
import com.itcast.zbc.mediaplayer.service.AudioService;
import com.itcast.zbc.mediaplayer.utils.DateUtils;
import com.itcast.zbc.mediaplayer.utils.LogUtil;
import com.itcast.zbc.mediaplayer.utils.UtilityVolley.FileUtil;
import com.itcast.zbc.mediaplayer.utils.lyricutil.LyricParse;
import com.itcast.zbc.mediaplayer.utils.lyricutil.LyricsLoader;
import com.itcast.zbc.mediaplayer.view.LyricsView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioPlayerActivity extends BaseActivity {

    @Bind(R.id.iv_baseback_icon)
    ImageView ivBasebackIcon;
    @Bind(R.id.tv_audio_tittle)
    TextView tvAudioTittle;
    @Bind(R.id.tv_audio_list_arties)
    TextView tvAudioListArties;
    @Bind(R.id.iv_audio_wave)
    ImageView ivAudioWave;
    @Bind(R.id.sb_audio_current)
    SeekBar sbAudioCurrent;
    @Bind(R.id.iv_audio_playmodle)
    ImageView ivAudioPlaymodle;
    @Bind(R.id.iv_audio_pre)
    ImageView ivAudioPre;
    @Bind(R.id.iv_audio_pause)
    ImageView ivAudioPause;
    @Bind(R.id.iv_audio_next)
    ImageView ivAudioNext;
    @Bind(R.id.iv_audio_musiclist)
    ImageView ivAudioMusiclist;
    @Bind(R.id.tv_audio_duration)
    TextView tvAudioDuration;
    //显示的歌词
    @Bind(R.id.lrcv_audio)
    LyricsView lrcvAudio;

    private int currentPosition;
    private ArrayList<AudioItem> audioItems;
    private AudioServiceConnection connection;
    private AudioService.AudioBinder audioBinder;

    private AudioItem audioItem;
    private static final int MSG_UPDATACURRENTPISITION = 0;
    //歌词刷新
    private static final int MSG_UPDATALYRIC = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATACURRENTPISITION:
                    upDataPosition();
                    break;
                case MSG_UPDATALYRIC:
                    upDataLyrics();  //自动刷新歌词
                    break;
            }
        }
    };

    /**
     * 一首歌曲播放完成后的广播
     */
    private MyCompletionReceiver myCompletionReceiver;
    /**
     * 媒体资源加载完成的广播
     */
    private MyPrepareReceiver prepareReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        ButterKnife.bind(this);
        doNext();
    }

    @Override
    protected void otherOnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_baseback_icon:  //返回按钮
                finish();
                break;
            case R.id.iv_audio_playmodle:
                SwitchPlayMode();
                break;
            case R.id.iv_audio_pre:
                playPre();
                break;
            case R.id.iv_audio_pause:
                changgePlayStatus();
                break;
            case R.id.iv_audio_next:
                playNext();
                break;
            case R.id.iv_audio_musiclist:
                break;

        }
    }

    /**
     * 切换播放模式
     */
    private void SwitchPlayMode() {
        audioBinder.switchPlayMode();
        upDataPlayModeBtn();
    }

    /**
     * 更新播放模式按钮图片
     */
    private void upDataPlayModeBtn() {
        switch (audioBinder.getPlayMode()) {
            case AudioService.PLAYMODE_ALL_REPEAT:
                ivAudioPlaymodle.setImageResource(R.drawable.btn_playmodle_allrepeat);
                break;
            case AudioService.PLAYMODE_SINGLE_REPEAT:
                ivAudioPlaymodle.setImageResource(R.drawable.btn_playmodle_singlerepeat);
                break;
            case AudioService.PLAYMODE_RANDOM:
                ivAudioPlaymodle.setImageResource(R.drawable.btn_playmodle_random);
                break;
        }
    }

    /**
     * 播放上一曲
     */
    private void playPre() {
        audioBinder.playPre();
    }

    /**
     * 播放下一曲
     */
    private void playNext() {
        audioBinder.playNext();
    }


    /**
     * 切换播放状态
     */
    private void changgePlayStatus() {
        audioBinder.changPauseStatus(false);
        upDataPlayStatusBtn();
    }

    /**
     * 更新暂停播放按钮的图片
     */
    private void upDataPlayStatusBtn() {
        if (audioBinder.getPlayStatus()) {
            changeBtnPause();
        } else {
            changeBtnPlay();
        }

    }


    /**
     * 切换播放播放按钮
     */
    private void changeBtnPlay() {
        ivAudioPause.setImageResource(R.drawable.btn_audio_play);
    }

    /**
     * 切换暂停按钮
     */
    private void changeBtnPause() {
        ivAudioPause.setImageResource(R.drawable.btn_audio_pause);
    }


    /**
     * 切换播放歌曲的信息
     */
    private void upDataMusicData() {
        String tittle = audioItem.getTittle();
        String artist = audioItem.getArtist();
        tvAudioTittle.setText(tittle);
        tvAudioListArties.setText(artist);
    }

    /**
     * 更新播放进度
     */
    private void upDataPosition() {
        int duration = audioBinder.getDuration();
        int currentPosition = audioBinder.getCurrentPosition();
        if (duration > 3600000 && currentPosition == 0) {   // 解决资源正在加载，时间无法获取问题
            return;
        }
        tvAudioDuration.setText(DateUtils.videoTimeFormat(currentPosition) + "/" + DateUtils.videoTimeFormat(duration));
        sbAudioCurrent.setMax(duration);
        sbAudioCurrent.setProgress(currentPosition);
        handler.sendEmptyMessageDelayed(MSG_UPDATACURRENTPISITION, 200);

    }

    @Override
    protected void initListener() {
        //添加按钮的点击监听
        ivAudioPlaymodle.setOnClickListener(this);
        ivAudioPre.setOnClickListener(this);
        ivAudioPause.setOnClickListener(this);
        ivAudioNext.setOnClickListener(this);
        ivAudioMusiclist.setOnClickListener(this);
        ivBasebackIcon.setOnClickListener(this);

        // 进度条拖拽监听
        sbAudioCurrent.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());


        //广播注册
        IntentFilter filterPrepare = new IntentFilter(CommonValue.BOADCAST_MUSICPREPERA);
        prepareReceiver = new MyPrepareReceiver();
        registerReceiver(prepareReceiver, filterPrepare);

        IntentFilter filterCompation = new IntentFilter(CommonValue.BOADCAST_MUSICCOMPLETION);
        myCompletionReceiver = new MyCompletionReceiver();
        registerReceiver(myCompletionReceiver, filterCompation);
    }

    @Override
    protected void initData() {
        Intent intent = new Intent(getIntent());   //在原有的 Intent 的基础上创建一个复制有原有数据的intent
        intent.setClass(this, AudioService.class);
        startService(intent);
        connection = new AudioServiceConnection();
        //绑定服务
        bindService(intent, connection, BIND_AUTO_CREATE);

        //初始化界面为还没有开始播放的图标
        initBtn();
    }

    /**
     * 完成播放前的界面状态的初始化
     */
    private void initBtn() {
        //按钮状态初始化
        changeBtnPlay();
        //开启示波动画
        AnimationDrawable animationDrawable = (AnimationDrawable) ivAudioWave.getDrawable();
        animationDrawable.start();

        //获取焦点实现滚动
        tvAudioListArties.setFocusable(true);
        tvAudioListArties.requestFocus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //服务在activity销毁的时候一定要解绑
        unbindService(connection);

        // 已注册的广播注销
        unregisterReceiver(prepareReceiver);
        unregisterReceiver(myCompletionReceiver);

        //移除所有的消息和回调
        handler.removeCallbacksAndMessages(null);
    }


    /**
     * activity是通过ServiceConnection进行通信的
     */
    private class AudioServiceConnection implements ServiceConnection {
        /**
         * @param name    区别多个服务进程
         * @param service 是从服务端出传递过来的一个可控的对象
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioBinder = (AudioService.AudioBinder) service;
            // notifity_Type是不是有值，判断是否 service 启动，如果不是service启动取值为 -1
            int notifity_type = getIntent().getIntExtra("notifity_Type", -1);
            if (notifity_type == audioBinder.BINDER_NOTIFICATION_CONTINUE) {
                audioBinder.notifyAudioPlayerListener();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    /**
     * 当前歌曲播放完成后调用
     */
    private class MyCompletionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeBtnPlay();
        }
    }


    /**
     * 音乐资异步加载完成后调用
     */
    private class MyPrepareReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收在歌曲资源异步加载完成
            upDataPlayStatusBtn();
            audioItem = (AudioItem) intent.getSerializableExtra("audioItem");

            //音乐信息更新
            upDataMusicData();

            //开启播放进度更新
            upDataPosition();

            // 播放模式按钮更新
            upDataPlayModeBtn();

            // 更新播放按钮
            changeBtnPause();

            //刷新歌词
            //加载歌词文件
            loadLyricFile();
            upDataLyrics();
        }
    }



    /**
     * 加载歌词文件
     * 每一次歌曲切换需要重新设置歌词
     */
    private void loadLyricFile() {
        ArrayList<LyricBeen> lyricList = LyricParse.parseFile(LyricsLoader.LoadLrcFile(audioItem.getTittle()));
        lrcvAudio.setLyricList(lyricList);
        for (int i = 0; i < lyricList.size(); i++) {
            LogUtil.e(lyricList.toString());
        }
    }
    /**
     * 歌词实时滚动刷新
     */
    private void upDataLyrics() {
        //调用控件的方法刷新歌词
        lrcvAudio.calculateCenterLine(audioBinder.getCurrentPosition(), audioBinder.getDuration());
        //100ms刷新一次歌词
        handler.sendEmptyMessageDelayed(MSG_UPDATALYRIC, 2);
    }


    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {    //排除来自系统的自动更新
                return;
            }
            audioBinder.seekTo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
