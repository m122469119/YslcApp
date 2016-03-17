package com.yslc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库创建
 * <p>
 * Created by HH on 2016/1/6.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    /**
     * @param context 上下文环境
     * @param DBNAME   要创建的数据库名称
     * @param VERSION  要创建的数据库版本
     * @param table_name  要创建的表的名称
     * @param table_body  要创建的表的sql语句
     */

    private static final String DBNAME = "yslc.db";
    private static final int VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS code(Stock_Code varchar(10), Stock_Name varchar(30), Stock_Abbreviation varchar(30))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table if exists Log_Search");
        onCreate(db);
    }

}
