package com.yslc.data.impl;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.bean.CommentBean;
import com.yslc.bean.StarBean;
import com.yslc.data.inf.IStarModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.HttpUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.LoadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 明星模块网络实现层
 * <p>
 * Created by HH on 2016/2/26.
 */
public class StarModelImpl implements IStarModel {
    private Context context;

    public StarModelImpl(Context context) {
        this.context = context;
    }

    /**
     * 获取明星数据
     * @param callback
     */
    @Override
    public void getStarColumnData(GetDataCallback callback) {
        HttpUtil.get(HttpUtil.GET_STAR_TYPE, context, null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        // 获取栏目成功并解析
                        ArrayList<ColnumBean> listTitle = new ArrayList<>();
                        try {
                            JSONArray ja = new JSONArray(arg0);
                            ColnumBean cb;
                            JSONObject jo;
                            for (int i = 0, len = ja.length(); i < len; i++) {
                                jo = ja.getJSONObject(i);
                                cb = new ColnumBean();
                                cb.setId(jo.optString("St_Id"));
                                cb.setName(jo.optString("St_Name"));
                                listTitle.add(cb);
                            }
                            callback.success(listTitle);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 明星选项卡，加载更多
     * @param stId 副标题id
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    @Override
    public void getStarListData(String stId, String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("St_Id", stId);
        params.put("pageSize", pageSize);
        params.put("pageIndex", pageIndex);

        HttpUtil.get(HttpUtil.GET_STAR_LIST, context, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(JSONObject jo) {
                        super.onSuccess(jo);

                        callback.success(parseStarJson(jo));
                    }
                });
    }

    /**
     * 解析明星列表JSON
     */
    private ArrayList<StarBean> parseStarJson(JSONObject jo) {
        ArrayList<StarBean> list = new ArrayList<>();
        try {
            JSONArray infoJa = jo.getJSONArray("StarList");
            JSONObject tempJo;
            StarBean infoItem;
            for (int i = 0, len = infoJa.length(); i < len; i++) {
                tempJo = infoJa.getJSONObject(i);
                infoItem = new StarBean();
                infoItem.setSif_Id(tempJo.optString("Sif_Id"));
                infoItem.setSif_Name(tempJo.optString("Sif_Name"));
                infoItem.setSif_Title(tempJo.optString("Sif_Title"));
                infoItem.setSif_Img(tempJo.optString("Sif_Img"));
                infoItem.setContent(tempJo.optString("content"));
                infoItem.setSn_Time(tempJo.optString("Sn_Time"));
                list.add(infoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    /**
     * 获取明星文章列表（文章标题列表）
     * <p>获取成功后解析</p>
     * @param sifId 明星id
     * @param pageSize 页面大小
     * @param pageIndex 页码
     * @param callback 回调
     */
    @Override
    public void getStarArticleList(String sifId, String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Sif_Id", sifId);
        params.put("pageSize", pageSize);
        params.put("pageIndex", pageIndex);
        HttpUtil.get(HttpUtil.GET_STAR_CONTENT_LIST, context, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(JSONObject jo) {
                        super.onSuccess(jo);
                        if (jo.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            callback.failer(null);
                        } else {
                            HashMap<StarBean, ArrayList<StarBean>> map = new HashMap<>();
                            try {
                                // 解析明星个人资料
                                JSONObject starJo = jo.getJSONObject("StarInfo");
                                StarBean mode = new StarBean();
                                if (null != starJo) {
                                    mode.setSif_Id(starJo.optString("Sif_Id"));
                                    mode.setSif_Img(starJo.optString("Sif_Img"));
                                    mode.setSif_Name(starJo
                                            .optString("Sif_Name"));
                                    mode.setSif_Relation(starJo
                                            .optString("Sif_Relation"));
                                    mode.setSif_Degree(starJo
                                            .optString("Sif_Degree"));
                                }

                                // 解析文章列表
                                ArrayList<StarBean> list = new ArrayList<>();
                                JSONArray ja = jo.getJSONArray("StarNewsList");
                                for (int i = 0, len = ja.length(); i < len; i++) {
                                    JSONObject tempJo = ja.getJSONObject(i);
                                    StarBean modes = new StarBean();
                                    modes.setSif_Id(tempJo.optString("Sn_Id"));
                                    modes.setSif_Title(tempJo
                                            .optString("Sn_Title"));
                                    modes.setContent(tempJo
                                            .optString("Sn_Content"));
                                    modes.setSn_Time(tempJo.optString("Sn_Time"));
                                    modes.setSif_ComNumber(tempJo
                                            .optString("ComNumber"));
                                    modes.setSif_Praise(tempJo
                                            .optString("Sn_Praise"));

                                    list.add(modes);
                                }
                                map.put(mode, list);
                                callback.success(map);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }

    /**
     * 点赞
     * @param snId 文章id
     * @param callback 回调
     */
    @Override
    public void doPraiseForArticle(String snId, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Sn_Id", snId);
        HttpUtil.get(HttpUtil.DO_PRAISE, context, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer("点赞失败");
                    }

                    @Override
                    public void onSuccess(JSONObject arg0) {
                        super.onSuccess(arg0);

                        if (!arg0.optString("Status").equals(
                                HttpUtil.ERROR_CODE)) {
                            callback.success(arg0.optString("msg"));//成功
                        } else {
                            callback.failer(arg0.optString("msg"));
                        }
                    }
                });
    }

    /**
     * 获取明星文章以及文章的评论列表
     * @param snId 文章id
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    @Override
    public void getStarArticleDetail(String snId, String pageSize, String pageIndex, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Sn_Id", snId);
        params.put("pageSize", pageSize);
        params.put("pageIndex", pageIndex);
        HttpUtil.get(HttpUtil.GET_STAR_COMTENT_COMMENT, context, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(JSONObject jo) {
                        super.onSuccess(jo);

                        if (jo.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            callback.failer(null);
                            return;
                        }

                        try {
                            // 解析评论列表
                            ArrayList<CommentBean> list = new ArrayList<>();
                            JSONArray ja = jo.getJSONArray("CommentList");
                            JSONObject tempJo;
                            CommentBean mode;
                            for (int i = 0, len = ja.length(); i < len; i++) {
                                tempJo = ja.getJSONObject(i);
                                mode = new CommentBean();
                                mode.setNcikName(tempJo.optString("Ui_Nickname"));
                                mode.setUiImg(tempJo.optString("Ui_Img"));
                                mode.setTime(tempJo.optString("Snc_Time"));
                                mode.setContent(tempJo.optString("Snc_Content"));
                                list.add(mode);
                            }

                            // 解析博文详情
                            StarBean modes = null;
                            JSONObject articalJo = jo.getJSONObject("StarNews");
                            if (null != articalJo) {
                                modes = new StarBean();
                                // 解析文章详情
                                modes.setSif_Title(articalJo
                                        .optString("Sn_Title"));
                                modes.setSn_Time(articalJo.optString("Sn_Time"));
                                modes.setContent(articalJo
                                        .optString("Sn_Content"));
                                modes.setSif_Img(articalJo.optString("Sn_Img"));
                            }

                            HashMap<StarBean, ArrayList<CommentBean>> map = new HashMap<>();
                            map.put(modes, list);
                            callback.success(map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 发表评论
     * @param userId 用户id
     * @param snId 文章id
     * @param commentValue 评论内容
     * @param callback 回调
     */
    @Override
    public void doStarArticleComment(String userId, String snId, String commentValue, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Ui_Id", userId);
        params.put("Sn_Id", snId);
        params.put("Snc_Content", commentValue);
        HttpUtil.post(HttpUtil.GET_STAR_COMMENT_COMMINT, context, params,
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
                                // 发表失败
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
}
