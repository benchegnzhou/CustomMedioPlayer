package com.itcast.zbc.mediaplayer.common;

/**
 *  * 作者：Zbc on 2016/12/15 18:59
 *  * 邮箱：mappstore@163.com
 * 功能描述：这里定义全局静态协议号
 *  
 */
public class CommonValue {
    /**
     * 列表页 cursor查询协议
     */
    public static final  int VIDEO_CURSOR_ASYNCQUERY = 1000;
    /**
     * 列表页 cursor查询协议
     */
    public static final  int AUDIO_CURSOR_ASYNCQUERY = 1001;
    /**
     *  文件存储名
     */
    public static final  String MUSIC_PLAY_MODE = "music_play_mode";
    /**
     * 服务中音乐播放完成广播
     */
    public static final  String BOADCAST_MUSICCOMPLETION = "com.bencheng.musiccomplation";
    /**
     * 服务中资源加载完成广播
     */
    public static final  String BOADCAST_MUSICPREPERA = "com.bencheng.musicprepare";

    /**
     * 视频播放时候请求音乐暂停
     */
    public static final  String BOADCAST_REQUESTMUSICSTOP = "com.bencheng.musicstopbyvideo";



}
