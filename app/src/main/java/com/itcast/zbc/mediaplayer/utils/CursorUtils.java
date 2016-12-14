package com.itcast.zbc.mediaplayer.utils;

import android.database.Cursor;

/**
 *  * 作者：Zbc on 2016/12/6 18:22
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *   关于cursor操作的工具类
 *  
 */
public class CursorUtils {

    /**
     * cursor打印的工具类
     * 打印cursor中的所有内容
     * @param clazz
     * @param cursor
     */
    public  static void printCursor(Class clazz,Cursor cursor){
    LogUtil.e(clazz,"cursor总数目"+cursor.getCount());
    while (cursor.moveToNext()) {
        LogUtil.e("-----------------------------------");
        for (int i = 0; i <cursor.getColumnCount() ; i++) {
            LogUtil.e(clazz,cursor.getColumnName(i)+"---- :  ----"+cursor.getString(i) );
        }
    }


}



}
