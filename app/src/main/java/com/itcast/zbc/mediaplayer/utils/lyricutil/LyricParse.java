package com.itcast.zbc.mediaplayer.utils.lyricutil;

import android.text.TextUtils;

import com.itcast.zbc.mediaplayer.bean.LyricBeen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 *  * 作者：Zbc on 2016/12/27 00:28
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 * 给定路径解析歌词文本
 *  
 */
public class LyricParse {


    /**
     * 歌词文件解析成 list 集合
     */
    public static ArrayList<LyricBeen> parseFile(File file) {
        ArrayList<LyricBeen> lyricses = new ArrayList<>();

        if (file == null || !file.exists()) {   //做一定的健壮性检查
            lyricses.add(new LyricBeen(0, "正在加载歌词..."));
            return lyricses;    //剩下的方法不在执行
        }
        try {
            // 在不同的播放设备上，歌词的编码格式是不相同的，必须制定对应的编码   默认 "utf-8"
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            String content = null;
            while ((content = reader.readLine()) != null) {
                //对当行歌词进行解析
                ArrayList<LyricBeen> lyricBeens = parseSimpleLine(content);
                //将解析后的歌词集合添加到集合容器中
                if (lyricBeens != null && lyricBeens.size() != 0) {
                    lyricses.addAll(lyricBeens);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        //要想实现排序，必须填家这一行代码。这个才是排序的真正的代码
        Collections.sort(lyricses);
        return lyricses;
    }

    /**
     * 解析当行歌词
     * //[01:22.04][02:35.04]寂寞的夜和谁说话
     *
     * @param content 需要解析的文本的内容
     * @return 解析后的歌词内容
     */
    private static ArrayList<LyricBeen> parseSimpleLine(String content) {
        ArrayList<LyricBeen> lyricList = new ArrayList<>();
        if (TextUtils.isEmpty(content)) {
            return null;  //空内容直接返回
        }

        //[01:22.04][02:35.04]寂寞的夜和谁说话
        if (content.matches("]")) {   //没有时间戳的内容暂时不解析
            return null;
        }
        //切割后的文本  [01:22.04    [02:35.04    寂寞的夜和谁说话
        String[] split = content.split("]");
        String text = split[split.length - 1];   //最后的一个是歌词的内容
        for (int i = 0; i < split.length - 1; i++) {
            //将时间解析出来
            int startPoint = parseTime(split[i]);
            //歌词文件格式不正确的处理
            if (startPoint == -1) {  //歌词文件格式错误
                lyricList.clear();
                lyricList.add(new LyricBeen(0, "歌词文件格式错误"));
                return lyricList;
            }
            lyricList.add(new LyricBeen(startPoint, text));
        }

        return lyricList;
    }

    /**
     * 将这样的文本字符串解析成毫秒值  [01:22.04
     *
     * @param s
     * @return
     */
    private static int parseTime(String s) {
        int startPoint=-1;
        try {
            if (TextUtils.isEmpty(s)) {
                return 0;   //文本错误返回当前的时间为0
            }
            //[01    22.04
            String[] splitH = s.split(":");
            //22  04
            String[] split1L = splitH[splitH.length - 1].split("\\.");

            int minute = Integer.parseInt(splitH[0].substring(1, splitH[0].length()));
            int second = Integer.parseInt(split1L[0]);
            int millsecond = Integer.parseInt(split1L[1]);

            startPoint = minute * 60 * 1000 + second * 1000 + millsecond * 10;
            //将时间转换成毫秒值

        } catch (Exception e) {
            startPoint=-1;
        }
        return startPoint;

    }


}
