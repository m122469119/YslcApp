package com.yslc.data.impl;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.bean.AdBean;
import com.yslc.bean.ColnumBean;
import com.yslc.bean.CommentBean;
import com.yslc.bean.NewBean;
import com.yslc.bean.StockInfo;
import com.yslc.data.inf.INewModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.HttpUtil;
import com.yslc.util.LogUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.LoadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 新闻资讯模块接口实现层
 * <p>
 * Created by HH on 2016/2/26.
 */
public class NewModelImpl implements INewModel {
    private Context context;

    public NewModelImpl(Context context) {
        this.context = context;
    }

    @Override
    public void getColumnData(String btid, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("btid", btid);
        HttpUtil.get(HttpUtil.GET_COLNUM, context, params,
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
                            // 获取失败
                            callback.failer(null);
                        } else {
                            // 获取栏目成功
                            try {
                                JSONArray ja = new JSONArray(arg0);
                                // 解析数据
                                ArrayList listTitle = new ArrayList<>();
                                ColnumBean cb;
                                JSONObject jo;
                                for (int i = 0, len = ja.length(); i < len; i++) {
                                    jo = ja.getJSONObject(i);
                                    cb = new ColnumBean();
                                    cb.setId(jo.optString("StID"));
                                    cb.setName(jo.optString("StName"));
                                    cb.setStOrder(jo.optString("StOrder"));
                                    listTitle.add(cb);
                                }

                                callback.success(listTitle);
                            } catch (JSONException e) {
                                // 暂无数据
                                callback.failer(null);
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void fristLoadFastNewData(String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("pageSize", pageSize);
        params.put("pageIndex", pageIndex);
        HttpUtil.get(HttpUtil.GET_MAIN_FAST, context, params,
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
                            // 加载失败
                            callback.failer(null);
                        } else {
                            callback.success(jsonNewDataJson(arg0));
                        }
                    }
                });
    }

    @Override
    public void loadMoreNewData(String sstId, String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("sstid", sstId);
        params.put("pageSize", String.valueOf(pageSize));
        params.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.GET_MAIN, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer(HttpUtil.ERROR_INFO);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            callback.failer(HttpUtil.ERROR_INFO);
                        } else {
                            callback.success(jsonNewDataJson(arg0));
                        }
                    }
                });
    }

    @Override
    public void loadMoreNewData(GetDataCallback callback) {
        HttpUtil.get(HttpUtil.GET_MAIN, context, null,
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
                            //解析咨讯
                            ArrayList<NewBean> newList = jsonNewDataJson(arg0);
                            ArrayList<AdBean> imgList = parseImageNew(arg0);
                            HashMap<ArrayList<AdBean>, ArrayList<NewBean>> map = new HashMap<>();
                            map.put(imgList, newList);
                            callback.success(map);
                        }
                    }
                });
    }

    /**
     * 解析咨讯
     *
     * @param json
     * @return
     */
    private ArrayList<NewBean> jsonNewDataJson(String json) {
        ArrayList<NewBean> list = new ArrayList<>();
        try {
            //**** 同一接口，（快讯和咨讯）返回的json格式不统一，仅供测试（后期需修改） *******
            JSONArray infoJa;
            if (json.indexOf("[") < json.indexOf("{")) {
                infoJa = new JSONArray(json);
            } else {
                infoJa = new JSONObject(json).getJSONArray("NewsInfo");
            }

            NewBean infoItem;
            JSONObject tempJo;
            for (int i = 0, len = infoJa.length(); i < len; i++) {
                tempJo = infoJa.getJSONObject(i);
                infoItem = new NewBean();
                infoItem.setNild(tempJo.optString("NiId"));
                infoItem.setNiTitle(tempJo.optString("NiTitle"));
                infoItem.setNiContent(tempJo.optString("NiContent"));
                infoItem.setNiTime(tempJo.optString("NiTime"));
                infoItem.setWhatColor(tempJo.optString("NiTop"));
                infoItem.setReadNum(tempJo.optString("NiNumber"));
                infoItem.setNiImg(tempJo.optString("NiImg"));
                list.add(infoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    /**
     * 解析图片咨讯列表
     *
     * @param json
     */
    private ArrayList<AdBean> parseImageNew(String json) {
        ArrayList<AdBean> list = new ArrayList<>();
        try {
            JSONArray adJa = new JSONObject(json).getJSONArray("PagePicture");
            AdBean ad;
            JSONObject jo;
            for (int i = 0, len = adJa.length(); i < len; i++) {
                ad = new AdBean();
                jo = adJa.getJSONObject(i);
                ad.setTitle(jo.optString("Title"));
                ad.setImgUrl(jo.optString("Img"));
                ad.setLinkUrl(jo.optString("Url"));
                list.add(ad);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return list;
        }

    }

    @Override
    public void getStockInfo(GetDataCallback callback) {
        HttpUtil.get(HttpUtil.GET_STOCK, context, null,
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
                            return;
                        }

                        // 解析大盘
                        try {
                            JSONArray ja = new JSONArray(arg0);
                            JSONObject jo;
                            ArrayList<StockInfo> list = new ArrayList<>();
                            for (int i = 0, len = ja.length(); i < len; i++) {
                                jo = ja.getJSONObject(i);
                                StockInfo mode = new StockInfo();
                                mode.setName(jo.optString("name"));
                                mode.setDiffer(jo.optString("proportion"));
                                mode.setNow(jo.optString("now"));
                                mode.setProportion(jo.optString("proportion"));
                                list.add(mode);
                            }

                            callback.success(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    @Override
    public void doNewComment(String uiId, String niId, String ncContent, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("UiID", uiId);
        params.put("NiId", niId);
        params.put("NcContent", ncContent);
        HttpUtil.post(HttpUtil.POST_COMMENT, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer("发表评论失败");
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        try {
                            JSONObject jo = new JSONObject(arg0);
                            if (jo.optString("Status").equals(
                                    HttpUtil.ERROR_CODE)) {
                                callback.failer(jo.optString("msg"));
                            } else {
                                // 发表成功
                                callback.success(jo.optString("msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void getNewCommentList(String niId, String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("NiId", niId);
        params.put("pagesize", pageSize);
        params.put("pageindex", pageIndex);
        HttpUtil.get(HttpUtil.GET_COMMENT, context, params,
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
                            return;
                        }

                        try {
                            JSONObject tempJo;
                            CommentBean comment;
                            JSONArray ja = new JSONArray(arg0);
                            ArrayList<CommentBean> listData = new ArrayList<>();
                            for (int i = 0, len = ja.length(); i < len; i++) {
                                tempJo = ja.getJSONObject(i);
                                comment = new CommentBean();
                                comment.setNcid(tempJo.optString("Ncid"));
                                comment.setNcikName(tempJo.optString("Nickname"));
                                comment.setUiImg(tempJo.optString("UiImg"));
                                comment.setContent(tempJo.optString("Content"));
                                comment.setTime(tempJo.optString("Time"));
                                listData.add(comment);
                            }
                            callback.success(listData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
