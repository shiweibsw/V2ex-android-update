package com.bsw.v2ex.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * 类说明： 话题已读/未读,收藏/未收藏数据表，数据库帮助类
 * Created by baishiwei on 2016/3/25.
 */
public class V2EXDataSource {
    public static final String TAG="V2EXDataSource";
    private SQLiteDatabase database;

    public V2EXDataSource(DatabaseHelper dbHelper){
        database=dbHelper.getWritableDatabase();
    }




}
