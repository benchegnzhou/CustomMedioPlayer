package com.itcast.zbc.mediaplayer.bean;

/**
 *  * 作者：Zbc on 2016/12/26 13:44
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 * 歌词实体类:
 * 记录一条歌词信息的时间和歌词的内容
 *  
 */
public class LyricBeen implements Comparable<LyricBeen> {
    //歌词的时间
    private int startPoint;
    //事件点对应的内容
    private String content;

    public LyricBeen(int startPoint, String content) {
        this.startPoint = startPoint;
        this.content = content;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "startPoint=" + startPoint +
                ", content='" + content + '\'' +
                '}';
    }

    /**
     * 对歌词按照时间进行排序
     * @param another
     * @return
     */
    @Override
    public int compareTo(LyricBeen another) {

        return startPoint - another.getStartPoint();
    }
}
