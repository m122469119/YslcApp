package com.yslc.data.impl;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yslc.bean.StocyCodeBean;
import com.yslc.data.inf.IStockCodeModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.HttpUtil;
import com.yslc.util.LogUtil;
import com.yslc.util.ToastUtil;

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

                    JSONArray ja = arg0.getJSONArray("stock");
                    ArrayList<StocyCodeBean> list = new ArrayList<>(ja.length());
                    StocyCodeBean bean;
                    JSONObject jo;
                    for (int i = 0, len = ja.length(); i < len; i++) {
                        bean = new StocyCodeBean();
                        jo = ja.getJSONObject(i);
                        bean.setStock_Code(jo.optString("Stock_Code"));
                        bean.setStock_Name(jo.optString("Stock_Name"));
                        bean.setStock_Abbreviation(jo.optString("Stock_Abbreviation"));
                        list.add(bean);
                    }
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
