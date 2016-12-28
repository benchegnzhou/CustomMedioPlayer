package com.itcast.zbc.mediaplayer.utils.meidiacontrolutile;

import android.content.Context;
import android.content.Intent;

import com.itcast.zbc.mediaplayer.appcation.MyApplication;
import com.itcast.zbc.mediaplayer.common.CommonValue;

/**
 *  * 作者：Zbc on 2016/12/28 13:26
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  音乐视频控制的协调的工具类
 * 视频播放的时候，音乐暂停，视频播放完成，音乐继续
 */
public class MediaUtil {
    //使用全局的静态变量记录音乐的播放状态
    private static boolean isVideoPlaying = false;
    private static boolean StopMusic_flag = false;
    //记录音乐服务是否存活
    private static boolean isMusicServiceLive = false;

    public static boolean isVideoPlaying() {
        return isVideoPlaying;
    }

    public static void setIsVideoPlaying(boolean isVideoPlaying) {
        MediaUtil.isVideoPlaying = isVideoPlaying;
    }

    public static boolean getStopMusic_flag() {
        return StopMusic_flag;
    }

    public static void setStopMusic_flag(boolean StopMusic_flag) {
        MediaUtil.StopMusic_flag = StopMusic_flag;
    }

    public static boolean isMusicServiceLive() {
        return isMusicServiceLive;
    }

    public static void setIsMusicServiceLive(boolean isMusicServiceLive) {
        MediaUtil.isMusicServiceLive = isMusicServiceLive;
    }

    /**
     * 传入的上下文必须是全局的上下文
     * 视频播放的时候控制音乐的播放状态
     *
     * @param
     */
    public static void controlMusicStatus(Context context ,boolean isVideoPlaying) {
        if (!isMusicServiceLive) { //音乐进程没有存活，直接返回
            return;
        }  //剩下的就是音乐进程存活的情况
        MediaUtil.setIsVideoPlaying(isVideoPlaying);
        // 发送广播，通知界面歌曲已经播放请求刷新界面  这里后期使用 EventBus替换更高效
        Intent intent = new Intent(CommonValue.BOADCAST_REQUESTMUSICSTOP);
        intent.putExtra("isVideoPlaying", isVideoPlaying());    //将视频播放的状态传递
        intent.putExtra("StopMusic_flag", StopMusic_flag);      //通过标志位判断，音乐是不是视频播放时候停止的
        context.sendBroadcast(intent);


    }

}
