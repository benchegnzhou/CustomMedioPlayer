package com.itcast.zbc.mediaplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.adapter.AudioCursorListAudioAdapter;
import com.itcast.zbc.mediaplayer.bean.AudioItem;
import com.itcast.zbc.mediaplayer.common.CommonValue;
import com.itcast.zbc.mediaplayer.handler.MyAsyncQueryHandler;
import com.itcast.zbc.mediaplayer.ui.activity.AudioPlayerActivity;
import com.itcast.zbc.mediaplayer.utils.CursorUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  * 作者：Zbc on 2016/12/5 18:16
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  音频界面fragment
 */
public class AudioListFragment extends BaseFragment {


    @Bind(R.id.lv_videolist)
    ListView lvVideolist;
    private AudioCursorListAudioAdapter cursorListAudioAdapter;

    @Override
    protected void initListener() {
        cursorListAudioAdapter = new AudioCursorListAudioAdapter(getActivity(), null);
        lvVideolist.setAdapter(cursorListAudioAdapter);
        //条目列表平点击监听
        lvVideolist.setOnItemClickListener(new MyOnItemClickListener());
    }

    @Override
    protected void initData() {
        //查询音乐数据
        ContentResolver resolver = getActivity().getContentResolver();
        //调试完成，进一步优化，查询使用异步线程
        //注意使用cursorAdapter必须添加首列为 "_id" 做为cursorAdapter排序使用  ，没有的时候可以将随意的一列as 为"_id"就可以了
//        Cursor curcor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, MediaStore.Video.Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);
        MyAsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(resolver);
        asyncQueryHandler.startQuery(CommonValue.AUDIO_CURSOR_ASYNCQUERY,cursorListAudioAdapter,Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, MediaStore.Video.Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audio_list;
    }

    @Override
    protected void otherOnClick(View v) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        doNext();  //调用剩下的方法
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 音乐条目列表点击监听
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取一个条目位置的item 看源码，返回的是一个移动到指定位置的cursor
            Cursor cursor = (Cursor) cursorListAudioAdapter.getItem(position);
            //解析列表数据成一个been类
            ArrayList<AudioItem> audioItems = AudioItem.purseVideoListItem(cursor);

            //开启音乐播放窗口
            Intent intent=new Intent(getActivity(),AudioPlayerActivity.class);
            intent.putExtra("audioItems",audioItems);
            intent.putExtra("position",position);
            startActivity(intent);

        }
    }
}
