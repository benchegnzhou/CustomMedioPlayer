package com.itcast.zbc.mediaplayer.adapter;

import android.content.Context;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcast.zbc.mediaplayer.R;


/**
 *  * 作者：Zbc on 2016/12/15 18:08
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  
 */
public class AudioCursorListAudioAdapter extends CursorAdapter {


    public AudioCursorListAudioAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioCursorListAudioAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AudioCursorListAudioAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //获取行的view 没有的话创建
        View view = LayoutInflater.from(context).inflate(R.layout.audio_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.tv_tittle = (TextView) view.findViewById(R.id.tv_audio_list_tittle);
        holder.tv_arties = (TextView) view.findViewById(R.id.tv_audio_list_arties);
        //要想在bindView中获取的到view必须设置Tag
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tv_tittle.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        holder.tv_arties.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));

    }

    private class ViewHolder {
        public TextView tv_tittle, tv_arties;
    }


}
