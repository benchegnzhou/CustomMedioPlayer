package com.itcast.zbc.mediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.bean.LyricBeen;

import java.util.ArrayList;

/**
 *  * 作者：Zbc on 2016/12/25 23:13
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *      创建控件显示歌词
 * 注意：
 * 控件直接的集成view的话需要自己处理，onMeasure() 方法
 */
public class LyricsView extends TextView {
    /**
     * 画笔相关属性的初始化
     */
    private Paint mPaint;
    //高粱显示的颜色
    private int mHeightLightTextColor;
    // 文字默认普通色
    private int mOrdinaryTextColor;
    //高亮字号
    private int mHeightLightTextsize;
    private int mOrdinaryTextsize;
    private Context mContext;
    // 测量后控件的宽高
    private int mViewWith;
    private int mViewHeight;
    //记录当前歌词播放的位置
    private int mCenterLine;
    private  ArrayList<LyricBeen> lyricses;
    private int mDuration;
    private int mCurrentTime;


    public LyricsView(Context context) {
        this(context, null);
    }

    public LyricsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    /**
     * 做一些初始化准备工作
     */
    private void init() {
        // 通过资源文件获取对应的资源属性值，完成适配
        mHeightLightTextColor = getResources().getColor(R.color.lyrics_tv_height);
        mOrdinaryTextColor = getResources().getColor(R.color.lyrics_tv_ordinary);
        mHeightLightTextsize = (int) getResources().getDimension(R.dimen.lyrics_tv_heightlight);
        mOrdinaryTextsize = (int) getResources().getDimension(R.dimen.lyrics_tv_ordinary);
        mPaint = new Paint();
        //设置画笔的抗锯齿,防止边缘模糊
        mPaint.setAntiAlias(true);
        mPaint.setColor(mHeightLightTextColor);
        mPaint.setTextSize(mHeightLightTextsize);


        lyricses = new ArrayList<>();
//        mCenterLine = 15;
//        //创建模拟歌词内容
//        for (int i = 0; i < 30; i++) {
//            lyricses.add(new LyricBeen(i * 2000, "这是歌词的第" + i + "条"));
//        }

    }

    /**
     * 设置歌词内容
     * 每一次切换歌曲需要重新设置歌词
     */
    public   void setLyricList(ArrayList<LyricBeen> lyricList) {
        lyricses =lyricList;
    }


    /**
     * 常见的自定义组合控件 大多有两种
     * 1、在onSizeChanged里面写
     * 2、在onFinishInflate里面写
     * 这个是系统回调方法，是系统调用的，这个方法会在这个view的大小发生改变时被系统调用，
     * 只要view大小变化，这个方法就被执行就可以了
     *
     * @param w    变化后的控件的宽度
     * @param h    变化后的控件的高度
     * @param oldw 变化前。。
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //记录变化后的控件的宽高
        mViewWith = w;
        mViewHeight = h;
    }

    /**
     * 文字的绘制在这个方法中进行调用
     * 文本的绘制使用画笔画布完成
     * 绘制的默认坐标点是在文字的左下方
     * 实现文字的居中绘制，需要计算绘制其实点的坐标
     * 摆放起始横坐标 = 控件宽度的一半 - 文字宽度的一半
     * 摆放起始纵坐标 = 控件高度的一般 + 文字高度的一半
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制一行文本
        String text = "正在加载歌词.....";

        Rect rect = new Rect();
        //绘制多行文本
        DrawMultleLines(canvas, rect);

    }


    /**
     * 根据播放时间计算中间行的位置
     *
     * @param currentTime 歌曲当前播放时间
     * @param duration    歌曲的持续时间
     */

    public void calculateCenterLine(int currentTime, int duration) {
        //当前歌曲的持续时间
        mDuration = duration;
        mCurrentTime = currentTime;
        for (int i = 1; i < lyricses.size(); i++) {
            //获取一句歌词的起始时间点
            int startPointP = lyricses.get(i - 1).getStartPoint();
            int startPointN = lyricses.get(i).getStartPoint();
            if (currentTime >= startPointP && currentTime < startPointN) {    //歌词高亮的判断时间大于等于上一行但是小于下一行
                mCenterLine = i - 1;
//                LogUtil.e("当前的位置是" + i);
                break;
            } else if (currentTime >= lyricses.get(lyricses.size() - 1).getStartPoint()) {  //对最后一行高亮显示的处理
                mCenterLine = lyricses.size() - 1;
            }

        }
        //出发控件重新绘制
        invalidate();

    }

    /**
     * 获取中间行(高亮行的偏移位置)，实现歌词的自动滚动效果
     * 居中行
     * Y 的偏移量 = 行高 * 时间进度百分比
     * 行高 = 字体的高度 + 行间距
     * 时间百分比 = 行已用时间 / 两句歌词的时间差
     * 行已用时间 = 播放时间 - 上一句歌词的起始时间
     * 两句歌词的时间差 = 下一句的时间 - 上一句的时间
     *
     * @return 返回偏移的位置
     */
    private float getCenterLineOffset(float lineHeight) {
        //获取两句歌词的时间差
        int nextStartPoint;
        LyricBeen currentLyrics = lyricses.get(mCenterLine);
        int currentStartPoint = currentLyrics.getStartPoint();
        if (mCenterLine + 1 < lyricses.size()) {
            LyricBeen nextLyrics = lyricses.get(mCenterLine + 1);   //最后依据的时间使用结束时间进行处理
            nextStartPoint = nextLyrics.getStartPoint();
        } else {
            nextStartPoint = mDuration;
        }

        float lineTime = nextStartPoint - currentStartPoint;

        //获取已用时间
        float UstedTime = mCurrentTime - currentStartPoint;

        //计算时间百分比
        float currentPercent = UstedTime / lineTime;

        // 计算行高偏移量
        float offsetY = lineHeight * currentPercent;
        return offsetY;


    }


    /**
     * 多行文本绘制，遵循公式
     * drawY= centerY + (postion - centerPostion) * （行间距 + 文字的高度）
     *
     * @param canvas
     * @param rect
     */
    private void DrawMultleLines(Canvas canvas, Rect rect) {
        for (int i = 0; i < lyricses.size(); i++) {
            String content = lyricses.get(i).getContent();

            if (i == mCenterLine) {    //当前绘制的行是中间行
                //画笔颜色，字体大小
                mPaint.setColor(mHeightLightTextColor);
                mPaint.setTextSize(mHeightLightTextsize);
            } else {
                mPaint.setColor(mOrdinaryTextColor);
                mPaint.setTextSize(mOrdinaryTextsize);
            }
            //文字的宽度属性可以通过画笔获取,可以测量一个文字，多个文字的上下左右和宽高的属性
            mPaint.getTextBounds(content, 0, content.length(), rect);
            //居中显示一行文字
            // 计算摆放控件的位置   摆放起始横坐标=控件宽度的一半 - 文字宽度的一半


            //重新计算每一行的高度
            float lineHeight = (rect.height() + mContext.getResources().getDimension(R.dimen.lyrics_tv_Padding));
            int centerY = mViewHeight / 2 + rect.height() / 2;
            //自动滚动需要计算中心点的偏移
            float offSetY = getCenterLineOffset(lineHeight);

            //每一行的位置都会在原有位置的基础上进行偏移
            float drawY = centerY + (i - mCenterLine) * lineHeight - offSetY;

            //绘制单行文本
            DrawSimpleLine(canvas, content, drawY);
        }

    }

    /**
     * 给定纵坐标，绘制水平居中的文本
     *
     * @param canvas
     * @param text   绘制的文本
     * @param drawY  给定文本绘制的高度坐标
     */
    private void DrawSimpleLine(Canvas canvas, String text, float drawY) {
        float width = mPaint.measureText(text);   //文本的宽度
        float drawX = mViewWith / 2 - width / 2;   //居中绘制
        canvas.drawText(text, drawX, drawY, mPaint);
    }
}
