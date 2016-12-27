package com.itcast.zbc.mediaplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.utils.DateUtils;
import com.itcast.zbc.mediaplayer.utils.LogUtil;

/**
 *  * 作者：Zbc on 2016/12/6 20:05
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  
 */
public class VideoCursorListAdapter extends CursorAdapter {

    public VideoCursorListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoCursorListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }


    /**
     * 创建新的view 和 viewHolder
     *
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.tv_tittle = (TextView) view.findViewById(R.id.tv_video_list_tittle);
        holder.tv_size = (TextView) view.findViewById(R.id.tv_video_list_videosize);
        holder.tv_duration = (TextView) view.findViewById(R.id.tv_video_list_videoDuration);


        view.setTag(holder);   //想要复用成功，必须设置tag
        return view;
    }

    /**
     * 填充条目内容    此时 view不可能为空  因为不为空则复用，为空就调用newView创建
     *
     * @param view
     * @param context
     * @param cursor  已经移动到指定位置的cursor 我们只需要解析对应的内容就可以了
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();    //这个不可能为空
        long size = cursor.getLong(cursor.getColumnIndex(Media.SIZE));
        int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        holder.tv_tittle.setText(cursor.getString(cursor.getColumnIndex(Media.TITLE)));

        holder.tv_size.setText(Formatter.formatFileSize(context,size));
        holder.tv_duration.setText(DateUtils.videoTimeFormat(duration));

    }

    private class ViewHolder {
        TextView tv_tittle, tv_duration, tv_size;


    }
}
