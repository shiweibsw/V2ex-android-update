package com.bsw.v2ex.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 类说明： 话题已读/未读,收藏/未收藏数据表，数据库帮助类
 * Created by baishiwei on 2016/3/25.
 */
public class V2EXDataSource {
    public static final String TAG = "V2EXDataSource";
    private SQLiteDatabase database;

    public V2EXDataSource(DatabaseHelper dbHelper) {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * 将某个节点加入收藏或者取消收藏
     *
     * @param nodeName
     * @param favor
     * @return
     */
    public boolean favoriteNode(String nodeName, boolean favor) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NODE_COLUMN_ISFAVOR, favor ? 1 : 0);
        if (database.replace(DatabaseHelper.NODE_TABLE_NAME, nodeName, values) != -1) {
            return true;
        }
        return false;

    }

    /**
     * 根据ID获取某条话题已读和未读的状态
     *
     * @param topicId
     * @return
     */
    public boolean isTopicRead(int topicId) {
        return getTopicField(topicId, DatabaseHelper.TOPIC_COLUMN_READ) == 1;
    }

    /**
     * 获取状态
     *
     * @param topicId
     * @param column
     * @return
     */
    private int getTopicField(int topicId, String column) {
        int result = 0;
        Cursor cursor = database.query(DatabaseHelper.TOPIC_TABLE_NAME, allNodeColumns, DatabaseHelper.TOPIC_COLUMN_TOPICID + "=" + topicId, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(column));
            cursor.close();
        }
        return result;
    }

    private boolean isNodeExisted(String nodeName) {
        Cursor cursor = database.query(DatabaseHelper.NODE_TABLE_NAME, allNodeColumns, DatabaseHelper.NODE_COLUMN_NODENAME + "='" + nodeName + "'", null,
                null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    private String[] allNodeColumns = {DatabaseHelper.NODE_COLUMN_NODENAME,
            DatabaseHelper.NODE_COLUMN_ISFAVOR};

}
