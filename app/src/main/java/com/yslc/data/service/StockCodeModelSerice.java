package com.yslc.data.service;

import android.content.Context;
import android.os.AsyncTask;

import com.yslc.app.Constant;
import com.yslc.bean.StockCodeBean;
import com.yslc.data.impl.StockCodeModelImpl;
import com.yslc.data.inf.IStockCodeModel;
import com.yslc.db.StocyCodeSQLHandle;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.SharedPreferencesUtil;

import java.util.ArrayList;

/**
 * 股市代码业务实现层
 * <p>
 * Created by HH on 2016/2/27.
 */
public class StockCodeModelSerice {
    private static final long STOCK_UPDATE_TIME = 86400000;       // 股票代码数据导入更新时间（24h）
    private Context context;
    private IStockCodeModel stockMarketModel;
    private StocyCodeSQLHandle stocyCodeSQLHandle;
    private ArrayList<StockCodeBean> filterCodeList;
    private ArrayList<StockCodeBean> codeAllList;
    private GetDataCallback getDataCallback;

    public StockCodeModelSerice(Context context) {
        this.context = context;
        stocyCodeSQLHandle = new StocyCodeSQLHandle(context);
        stockMarketModel = new StockCodeModelImpl(context);
    }

    /**
     * 获取股票信息列表
     *
     * <p>判断是否需要导入股票代码</p>
     * <p>获取所有股票代码列表并导入数据库</p>
     *
     */
    public void getStockCodeList() {
        if (intoStocyCode()) {//判断是否需要更新
            stockMarketModel.getStockCodeList(new GetDataCallback() {
                @Override
                public <T> void success(T data) {
                    //强转并插入数据库
                    intoStocyDB((ArrayList<StockCodeBean>) data);
                }

                @Override
                public <T> void failer(T data) {

                }
            });
        }
    }

    /**
     * 获取所有股票代码列表并导入数据库
     *
     * @param callback 回调
     */
    public void intoStockCodeList(GetDataCallback callback) {
        stockMarketModel.getStockCodeList(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                intoStocyDB((ArrayList<StockCodeBean>) data);
                callback.success(null);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(null);
            }
        });
    }

    /**
     * 判断上次导入数据的时间是否超过24小时，需要更新股票数据
     * <p>超过24小时或没有数据返回true</p>
     */
    private boolean intoStocyCode() {
        SharedPreferencesUtil spf = new SharedPreferencesUtil(context, Constant.SYSTEM_NAME);
        long currentTime = System.currentTimeMillis();
        //每天更新一次，或者找不到数据也更新
        if (currentTime - spf.getLong(Constant.LAST_UPDATE_TIME_KEY) > STOCK_UPDATE_TIME || !stocyCodeSQLHandle.isData()) {
            spf.setLong(Constant.LAST_UPDATE_TIME_KEY, currentTime);
            return true;
        }

        return false;
    }

    /**
     * 判断本地数据库是否存在数据
     */
    public boolean isData() {
        filterCodeList = new ArrayList<>();
        codeAllList = new ArrayList<>();
        return stocyCodeSQLHandle.isData();
    }

    /**
     * 获取本地所有股票代码
     */
    public ArrayList<StockCodeBean> getLocalAllCode() {
        return stocyCodeSQLHandle.findByAll();
    }

    /**
     * 将最新数据导入数据库
     */
    private void intoStocyDB(ArrayList<StockCodeBean> list) {
        //股票代码数据较多，导入数据需要开启线程
        if (list.size() > 0) {
            new Thread() {
                @Override
                public void run() {
                    stocyCodeSQLHandle.insertAll(list);
                }
            }.start();
        }
    }

    /**
     * 股票代码关键字过滤
     * 异步过滤，并返回代码数据
     */
    public void setFilter(String key) {
        new FilterCodeAsync().execute(key.trim().toLowerCase());
    }

    public void setGetDataCallback(GetDataCallback getDataCallback) {
        this.getDataCallback = getDataCallback;
    }

    class FilterCodeAsync extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            if (codeAllList.size() == 0) {
                codeAllList = getLocalAllCode();
            }

            filterCodeList.clear();
            if (params[0].equals("") && codeAllList.size() > 100) {
                //关键字为空，显示全部代码的前50条
                filterCodeList.addAll(codeAllList.subList(0, 100));
                return null;
            }

            //模糊匹配关键字
            for (StockCodeBean bean : codeAllList) {
                if (bean.getStock_Code().contains(params[0]) || bean.getStock_Abbreviation().contains(params[0])) {
                    filterCodeList.add(bean);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getDataCallback.success(filterCodeList);
        }
    }

}
