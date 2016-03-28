package com.yslc.data.impl;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yslc.bean.StockCodeBean;
import com.yslc.data.inf.IStockCodeModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 股市行情接口实现层
 * <p>
 * Created by HH on 2016/2/27.
 */
public class StockCodeModelImpl implements IStockCodeModel {
    private Context context;

    public StockCodeModelImpl(Context context) {
        this.context = context;
    }

    /**
     * 获取股票代码列表数据
     * <p>连接网络获取股票信息</p>
     * @param callback
     */
    @Override
    public void getStockCodeList(GetDataCallback callback) {
        HttpUtil.post(HttpUtil.GET_STOCK_CODElIST, context, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject arg0) {
                super.onSuccess(arg0);

                try {
                    if (arg0.optString("Status").equals("-2")) {
                        callback.failer(null);
                        return;
                    }
                    //解析数据
                    JSONArray ja = arg0.getJSONArray("stock");
                    ArrayList<StockCodeBean> list = new ArrayList<>(ja.length());
                    StockCodeBean bean;
                    JSONObject jo;
                    for (int i = 0, len = ja.length(); i < len; i++) {
                        bean = new StockCodeBean();
                        jo = ja.getJSONObject(i);
                        bean.setStock_Code(jo.optString("Stock_Code"));
                        bean.setStock_Name(jo.optString("Stock_Name"));
                        bean.setStock_Abbreviation(jo.optString("Stock_Abbreviation"));
                        list.add(bean);
                    }
                    //返回数据
                    callback.success(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
                callback.failer(null);
            }
        });
    }
}
