package com.itcast.zbc.mediaplayer.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itcast.zbc.mediaplayer.R;
import com.itcast.zbc.mediaplayer.adapter.VideoCursorListAdapter;
import com.itcast.zbc.mediaplayer.bean.VideoItem;

import com.itcast.zbc.mediaplayer.common.CommonValue;
import com.itcast.zbc.mediaplayer.handler.MyAsyncQueryHandler;
import com.itcast.zbc.mediaplayer.ui.activity.VideoPlayerActivity;
import com.itcast.zbc.mediaplayer.ui.activity.VitamioPlayerActivity;
import com.itcast.zbc.mediaplayer.utils.CursorUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  * 作者：Zbc on 2016/12/5 18:16
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  这个是视频列表fragment
 */
public class VideoListFragment extends BaseFragment {


    @Bind(R.id.lv_videolist)
    ListView lvVideolist;
    private VideoCursorListAdapter cursorListAdapter;

    @Override
    protected void initListener() {
        //数据暂时没有，我们就先将cursor设置为null，这种设置值得推荐，这样使用的效率将会更高
        cursorListAdapter = new VideoCursorListAdapter(getActivity(), null);
        lvVideolist.setAdapter(cursorListAdapter);
        lvVideolist.setOnItemClickListener(new MyOnItemClickListener());
    }

    @Override
    protected void initData() {
        //获取手机里的数据
        //1. 获取内容观察者
        ContentResolver contentResolver = getActivity().getContentResolver();
        //以前是在主线程中完成查询操作的，这样阻塞主线程
//        queryData(contentResolver);
        /**
         * 本来需要自己创建一个子线程完成这个操作，但是扩展性更好的是使用系统封装好的 AsyncQueryHandler 来完成这个操作
         * 这个方法在完成查询的时候在主线程中调用
         */
        AsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(contentResolver);

        /*int token,   相当于handler的what  用于区分事件的类型
        java.lang.Object cookie,  相当于handler的Object ，用于传递 需要刷新的控件的
        android.net.Uri uri,      下面的就是query的那一套了，直接拷贝
        java.lang.String[] projection,
        java.lang.String selection,
        java.lang.String[] selectionArgs,
        java.lang.String orderBy*/

        asyncQueryHandler.startQuery(CommonValue.VIDEO_CURSOR_ASYNCQUERY, cursorListAdapter, Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);

    }



    /**
     * 数据查询
     *
     * @param contentResolver
     */
    private void queryData(ContentResolver contentResolver) {
        //2. 进行查询    Cursor query(Uri, String[], String, String[], String)
        //查询的位置， 查询的关键字， 查询条件占位符 ， 查询条件填充占位符的数组 ，排序
        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);
        CursorUtils.printCursor(VideoListFragment.class, cursor);
        cursorListAdapter.swapCursor(cursor);    //相当于  notifyDataSetInvalidated();  替换原有的cursor   其实底层调用的还是   notifyDataSetInvalidated();这个方法
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_list;
    }

    @Override
    protected void otherOnClick(View v) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        doNext();
        return rootView;
    }

    /**
     * 视频界面条目的监听
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取点击条目的数据    查看源码，发现返回的是一个已经移动到指定位置的cursor
            //一定注意，cursor对象是不稳定的，一定不要直接的将其传递给其他的界面，多人开发万一修改就完了
            //正确的做法是将其转化成为一个been对象然后刷新界面
            Cursor cursor = (Cursor) cursorListAdapter.getItem(position);


            //代码多一般的解析都不会放在这里，而是直接的放在been类中
            ArrayList<VideoItem> videoListItems = VideoItem.purseVideoListItem(cursor);
            Intent intent;
            if (false) {    //定义一个变量，是不是使用Vitamio作为解码器
                //将数据传递到播放界面
                intent = new Intent(getContext(), VideoPlayerActivity.class);
            } else {
                intent = new Intent(getContext(), VitamioPlayerActivity.class);
            }

            intent.putExtra("videoListItems", videoListItems);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }


}
