package com.itcast.zbc.mediaplayer.utils.UtilityVolley;

import com.itcast.zbc.mediaplayer.utils.LogUtil;

import java.io.File;

/**
 *  * 作者：Zbc on 2016/12/27 17:57
 *  * 邮箱：mappstore@163.com
 * 功能描述：
 *  
 */
public class FileUtil {

    private static void list(File file) {
        if (file.isDirectory())//判断file是否是目录  
        {
            File[] lists = file.listFiles();
            {
                for (int i = 0; i < lists.length; i++) {
                    list(lists[i]);//是目录就递归进入目录内再进行判断  
                }
            }
        }
        System.out.println(file);//file不是目录，就输出它的路径名，这是递归的出口  
    }

    public static void printfFile(File file) {
        if (file == null && !file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        LogUtil.e("一共有文件：____" + files.length+"个");
        for (int i = 0; i < files.length; i++) {
            LogUtil.e("文件路径：____" + files[i].getPath() + "文件大小：___" + files[i].length());
        }
    }


}
