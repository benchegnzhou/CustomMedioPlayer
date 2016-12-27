package com.itcast.zbc.mediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  * 作者：Zbc on 2016/12/7 00:59
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  视频播放列表数据实体类
 */
public class AudioItem implements Serializable {

    private  String tittle;
    private  String path;
    private  String artist;



    public AudioItem() {

    }


    public AudioItem(String tittle, String path, String artist) {
        this.tittle = tittle;
        this.path = path;
        this.artist = artist;
    }

    /**
     * 根据传入的cursor 解析出对应的实体对象，以后的解析一般这么操作
     *
     * @param cursor
     * @return
     */
    public static AudioItem purseVideoItem(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {   //做一波健壮性检查 ， 以后的检查都这么做 ，不要先处理，最后检查，那样显得逻辑乱
            return null;
        }
        AudioItem listItem = new AudioItem();
        String tittle = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        String path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));

        listItem.tittle = tittle;
        listItem.path = path;
        listItem.artist=artist;
        return listItem;
    }

    /**
     * 获取视频列表
     *
     * @param cursor
     * @return
     */
    public static ArrayList<AudioItem> purseVideoListItem(Cursor cursor) {
        ArrayList<AudioItem> list = new ArrayList<>();
        //做一下健壮性检查
        if (cursor == null || cursor.getCount() == 0) {
            return list;   //防止空指针
        }
        cursor.moveToPosition(-1);
        int i= 0;
        while (cursor.moveToNext()) {  //移动获取数据就可以了
            AudioItem item =  AudioItem.purseVideoItem(cursor);
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "tittle='" + tittle + '\'' +
                ", path='" + path + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
