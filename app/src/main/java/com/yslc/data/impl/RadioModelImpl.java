package com.yslc.data.impl;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.bean.RadioBean;
import com.yslc.data.inf.IRadioModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 广播播放模块接口实现层
 * <p>
 * Created by HH on 2016/2/27.
 */
public class RadioModelImpl implements IRadioModel {
    private Context context;

    public RadioModelImpl(Context context) {
        this.context = context;
    }

    /**
     * 获取股市广播
     * @param callback
     */
    @Override
    public void getRadioData(GetDataCallback callback) {
        HttpUtil.get(HttpUtil.PLAY_VEDIO, context, null,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject json) {
                        super.onFailure(arg0, json);

                        callback.failer("加载失败,请刷新");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        super.onSuccess(json);
                        //解析数据
                        RadioBean mode = new RadioBean();
                        mode.setRadioUrl(json.optString("Url"));
                        mode.setRadioName(json.optString("RadP_Name"));
                        mode.setRadioTime(json.optString("TimeSpan"));
                        mode.setRadioHostUrl(json.optString("RadP_Img"));
                        mode.setRadioHost(json.optString("RadP_Compere"));

                        callback.success(mode);
                    }

                });
    }

    @Override
    public void getRadioReliveListData(String weekDate, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("WeekDate", weekDate);
        HttpUtil.get(HttpUtil.PLAY_VEDIO_RELIVE, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            callback.failer(null);
                        } else {
                            ArrayList<RadioBean> list = new ArrayList<>();
                            try {
                                // 解析广播重温界面列表
                                JSONArray infoJa = new JSONArray(arg0);
                                RadioBean infoItem;
                                JSONObject tempJo;
                                for (int i = 0, len = infoJa.length(); i < len; i++) {
                                    tempJo = infoJa.getJSONObject(i);
                                    infoItem = new RadioBean();
                                    infoItem.setRadioId(tempJo.optString("RadP_Id"));
                                    infoItem.setRadioName(tempJo.optString("RadP_Name"));
                                    infoItem.setRadioDate(tempJo.optString("RadP_Time"));
                                    list.add(infoItem);
                                }
                                callback.success(list);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.failer(null);
                            }
                        }
                    }

                });
    }

    /**
     * 重播电台节目详情页面第一项节目描述
     * @param radioId 电台id
     * @param callback 回调函数
     */
    @Override
    public void getRadioReliveDetail(String radioId, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("RadP_Id", radioId);
        HttpUtil.get(HttpUtil.PLAY_VEDIO_RELIVE_DETAILS, context, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(int arg0, JSONObject arg1) {
                        super.onSuccess(arg0, arg1);
                        if (arg1.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            callback.failer(null);
                            return;
                        }

                        // 解析详情
                        RadioBean detailBean = new RadioBean();
                        detailBean.setRadioName(arg1.optString("RadP_Name"));
                        detailBean.setRadioHost(arg1.optString("RadP_Compere"));
                        detailBean.setRadioHostUrl(arg1.optString("RadP_Img"));
                        detailBean.setRadioTime(arg1.optString("RadP_Time"));

                        callback.success(detailBean);
                    }

                });
    }

    /**
     * 电台节目往期列表
     * <p>成功后解析数据</p>
     * @param dbName 电台名称
     * @param pageSize 页面大小
     * @param pageIndex 页码
     * @param callback 回调函数
     */
    @Override
    public void getReliveListForHost(String dbName, String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("dbname", dbName);
        params.put("pagesize", pageSize);
        params.put("pageindex", pageIndex);
        HttpUtil.get(HttpUtil.PLAY_VEDIO_RELIVE_DETAILS_LIST, context, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(int arg0, JSONObject arg1) {
                        super.onSuccess(arg0, arg1);
                        //解析数据
                        try {
                            ArrayList<RadioBean> list = new ArrayList<>();
                            JSONArray ja = arg1.getJSONArray("list");
                            RadioBean bean;
                            JSONObject jo;
                            for (int i = 0, len = ja.length(); i < len; i++) {
                                jo = ja.getJSONObject(i);
                                bean = new RadioBean();
                                bean.setRadioId(jo.optString("DbId"));
                                bean.setRadioName(jo.getString("title"));
                                bean.setRadioUrl(jo.optString("song"));
                                bean.setRadioTime(jo.getString("time"));
                                list.add(bean);
                            }
                            callback.success(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
}
