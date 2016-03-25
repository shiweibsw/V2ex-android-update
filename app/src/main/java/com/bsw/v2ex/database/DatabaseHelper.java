package com.bsw.v2ex.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "v2ex_bsw.db";
    public static final int DB_VERSION = 1;

    // 话题数据表字段(话题ID,收藏状态,阅读状态)
    public static final String TOPIC_TABLE_NAME = "topics_table";
    public static final String TOPIC_COLUMN_ID = "_id";
    public static final String TOPIC_COLUMN_TOPICID = "topic_id";
    public static final String TOPIC_COLUMN_FAVOR = "isfavored";
    public static final String TOPIC_COLUMN_READ = "isread";

    //创建话题数据表
    private static final String TOPIC_TABLE_CREATE = "CREATE TABLE " + TOPIC_TABLE_NAME
            + "(" + TOPIC_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TOPIC_COLUMN_TOPICID + " INTEGER UNIQUE NOT NULL, "
            + TOPIC_COLUMN_READ + " INTEGER NOT NULL, "
            + TOPIC_COLUMN_FAVOR + " INTEGER NOT NULL);";

    //节点数据表字段(节点ID,收藏状态)
    public static final String NODE_TABLE_NAME = "nodes_table";
    public static final String NODE_COLUMN_ID = "_id";
    public static final String NODE_COLUMN_NODENAME = "node_name";
    public static final String NODE_COLUMN_ISFAVOR = "isfavored";

    //创建节点数据表
    private static final String NODE_TABLE_CREATE = "CREATE TABLE " + NODE_TABLE_NAME
            + "(" + NODE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NODE_COLUMN_NODENAME + " CHAR(256) UNIQUE NOT NULL, "
            + NODE_COLUMN_ISFAVOR + " INTEGER NOT NULL);";

    //使用volatile同步mDBHelper，保证多线程访问时的唯一性；
    private volatile static DatabaseHelper mDBHelper;


    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //使用这种单例模式可以有效解决多线程的并发访问
    public static synchronized DatabaseHelper getInstacce(Context context) {
        if (mDBHelper == null) {
            synchronized (DatabaseHelper.class) {
                if (mDBHelper == null) {
                    mDBHelper = new DatabaseHelper(context);
                }
            }
        }
        return mDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TOPIC_TABLE_CREATE);
        db.execSQL(NODE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TOPIC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + NODE_TABLE_NAME);
        onCreate(db);
    }
}
