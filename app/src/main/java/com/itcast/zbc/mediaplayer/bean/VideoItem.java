package com.itcast.zbc.mediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import com.itcast.zbc.mediaplayer.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  * 作者：Zbc on 2016/12/7 00:59
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  视频播放列表数据实体类
 */
public class VideoItem implements Serializable {

    private  String tittle;
    private  String path;
    private  long size;
    private  int duration;


    public VideoItem() {

    }



    public VideoItem(String tittle, String path, long size, int duration) {
        this.tittle = tittle;
        this.path = path;
        this.size = size;
        this.duration = duration;
    }



    /**
     * 根据传入的cursor 解析出对应的实体对象，以后的解析一般这么操作
     *
     * @param cursor
     * @return
     */
    public static VideoItem purseVideoItem(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {   //做一波健壮性检查 ， 以后的检查都这么做 ，不要先处理，最后检查，那样显得逻辑乱
            return null;
        }
        VideoItem listItem = new VideoItem();
        String tittle = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        String path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        long size = cursor.getLong(cursor.getColumnIndex(Media.SIZE));
        int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        listItem.tittle = tittle;
        listItem.path = path;
        listItem.size = size;
        listItem.duration = duration;
        return listItem;
    }

    /**
     * 获取视频列表
     *
     * @param cursor
     * @return
     */
    public static ArrayList<VideoItem> purseVideoListItem(Cursor cursor) {
        ArrayList<VideoItem> list = new ArrayList<>();
        //做一下健壮性检查
        if (cursor == null || cursor.getCount() == 0) {
            return list;   //防止空指针
        }
        cursor.moveToPosition(-1);
        int i= 0;
        while (cursor.moveToNext()) {  //移动获取数据就可以了
            VideoItem item =  VideoItem.purseVideoItem(cursor);
            VideoItem videoItem = new VideoItem();
            videoItem.setDuration(item.getDuration());
            videoItem.setTittle(item.getTittle());
            videoItem.setPath(item.getPath());
            videoItem.setSize(item.getSize());

            list.add(item);
        }
        return list;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VideoListItem{" +
                "tittle='" + tittle + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }


}
