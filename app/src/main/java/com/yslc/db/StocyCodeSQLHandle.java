package com.yslc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yslc.bean.StockCodeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 股票代码列表操作
 * <p>
 * Created by HH on 2016/1/6.
 */
public class StocyCodeSQLHandle {
    private static final String SELECT_ALL = "SELECT * FROM code";
    private static final String FIND_ALL_BY_KEY = "SELECT Stock_Code,Stock_Name,Stock_Abbreviation FROM code";
    private static final String ADD = "INSERT INTO code(Stock_Code,Stock_Name,Stock_Abbreviation) VALUES(?,?,?)";

    private SQLiteDatabase db;

    public StocyCodeSQLHandle(Context context) {
        db = new SQLiteHelper(context).getWritableDatabase();
    }

    /**
     * 是否存在数据
     * <p>查询数据库看是否有数据</p>
     * @return : 是否存在数据
     */
    public boolean isData() {
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        if (cursor.moveToNext()) {
            return true;
        }

        cursor.close();
        return false;
    }

    /**
     * 插入所有数据
     * <p>开启事务删除所有数据，再插入list列表的数据到本地数据库</p>
     * @param list:列表
     */
    public void insertAll(List<StockCodeBean> list) {
        //开启事务
        db.beginTransaction();

        //删除所有数据
        deleteAll();

        for (StockCodeBean bean : list) {
            insertSingle(bean);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 获取所有数据
     *
     * @return list
     */

    public ArrayList<StockCodeBean> findByAll() {
        Cursor cursor = db.rawQuery(FIND_ALL_BY_KEY, null);
        ArrayList<StockCodeBean> list = new ArrayList<>();
        StockCodeBean bean;
        while (cursor.moveToNext()) {
            bean = new StockCodeBean();
            bean.setStock_Code(cursor.getString(0));
            bean.setStock_Name(cursor.getString(1));
            bean.setStock_Abbreviation(cursor.getString(2));
            list.add(bean);
        }

        cursor.close();
        return list;
    }

    /**
     * 插入单条数据
     *
     * @param bean：股票代码bean
     */
    public void insertSingle(StockCodeBean bean) {
        db.execSQL(ADD, new String[]{bean.getStock_Code(), bean.getStock_Name(), bean.getStock_Abbreviation()});
    }

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        String sql = "DELETE FROM code";
        db.execSQL(sql);
    }

}
