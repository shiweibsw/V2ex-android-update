package com.bsw.v2ex.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class FileUtils {
    /**
     * 根据文件的绝对路径判断文件是否存在
     */
    public static boolean isExistDataCache(Context context, String path) {
        boolean exist = false;
        File data = context.getFileStreamPath(path);
        if (data.exists()) {
            exist = true;
        }
        return exist;
    }
}
