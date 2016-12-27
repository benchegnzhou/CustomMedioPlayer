package com.itcast.zbc.mediaplayer.utils.lyricutil;

import android.os.Environment;

import java.io.File;

/**
 *  * 作者：Zbc on 2016/12/27 23:04
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  歌词加载器
 */
public class LyricsLoader {
    /**
     * 加载歌词
     * 可以完成本地文件目录和目录下的lrc文件夹lrc text歌词的加载
     *
     * @return
     */
    public static File LoadLrcFile(String tittleName) {
        //与我们存放歌曲和歌词相关的跟目录
        File rootFile = new File(Environment.getExternalStorageDirectory(), "/music/");
        //在本地根目录查找 .lrc 和 .txt 文件
        File file = new File(rootFile, tittleName + ".lrc");
        if (file.exists()) {
            return file;
        }
        file = new File(rootFile, tittleName + ".txt");
        if (file.exists()) {
            return file;
        }
        //在本地lrc文件目录查找 .lrc 和 .txt 文件
        file = new File(rootFile, "lrc/" + tittleName + ".lrc");
        if (file.exists()) {
            return file;
        }
        file = new File(rootFile, "lrc/" + tittleName + ".txt");
        if (file.exists()) {
            return file;
        }

        //去服务器查找下载
        //......

        //如果还是没有找到
        return null;


    }


}
