package com.itcast.zbc.mediaplayer.handler;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

import com.itcast.zbc.mediaplayer.adapter.VideoCursorListAdapter;
import com.itcast.zbc.mediaplayer.common.CommonValue;
import com.itcast.zbc.mediaplayer.utils.CursorUtils;

/**
 *  * 作者：Zbc on 2016/12/7 00:11
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *   这个类可以实现数据库的异步增 ， 删 ， 改 ， 查 ，使用的时候只需要重写相应的方法就好了，执行的过程都是在主线程中
 *   使用的最后不要忘记调用startQuery()方法开启线程操作
 *   使用的方法类比handler
 */
public class MyAsyncQueryHandler extends AsyncQueryHandler {


    public MyAsyncQueryHandler(ContentResolver resolver) {
        super(resolver);
    }

    /**
     * 查询完成后这个方法会在主线程回调
     * @param token    相当于handler中的what
     * @param cookie   相当于handler中的Object，传递刷新的控件
     * @param cursor    当前一条记录的cursor，简单说就是查询出来的单条结果
     */
    @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);

        CursorAdapter mAdapter = (CursorAdapter) cookie;  //控件还原
        CursorUtils.printCursor(MyAsyncQueryHandler.class,cursor);
        mAdapter.swapCursor(cursor);    //相当于  notifyDataSetInvalidated();  替换原有的cursor   其实底层调用的还是   notifyDataSetInvalidated();这个方法
        }

}
