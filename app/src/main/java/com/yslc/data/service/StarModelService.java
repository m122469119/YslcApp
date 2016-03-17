package com.yslc.data.service;

import android.content.Context;
import android.content.Intent;

import com.yslc.data.impl.StarModelImpl;
import com.yslc.data.inf.IStarModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.activity.LoginActivity;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

/**
 * 明星模块业务层
 * <p>
 * Created by HH on 2016/2/26.
 */
public class StarModelService {
    private Context context;
    private IStarModel starModel;
    private int pageIndex = 1;
    private int pageSize = 15;

    public StarModelService(Context context) {
        this.context = context;
        starModel = new StarModelImpl(context);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public IStarModel getStarModel() {
        return starModel;
    }

    public void setStarModel(IStarModel starModel) {
        this.starModel = starModel;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取明星栏目数据
     */
    public void getStarColumnData(GetDataCallback callback) {
        starModel.getStarColumnData(callback);
    }

    /**
     * 第一次或者刷新加载明星列表
     */
    public void getStarListData(String stId, GetDataCallback callback) {
        starModel.getStarListData(stId, String.valueOf(pageSize), String.valueOf(1), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                pageIndex = 2;
                callback.success(data);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }

    /**
     * 加载更多明星列表
     */
    public void getStarMoreListData(String stId, GetDataCallback callback) {
        starModel.getStarListData(stId, String.valueOf(pageSize), String.valueOf(pageIndex), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                pageIndex++;
                callback.success(data);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }

    /**
     * 获取明星文章列表
     *
     * @param sifId
     * @param callback
     */
    public void getStarArticelList(String sifId, GetDataCallback callback) {
        starModel.getStarArticelList(sifId, String.valueOf(pageSize), String.valueOf(pageIndex), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                pageIndex++;
                callback.success(data);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }

    /**
     * 明星文章点赞
     *
     * @param snId
     * @param callback
     */
    public void doPraiseForArtice(String snId, GetDataCallback callback) {
        starModel.doPraiseForArtice(snId, callback);
    }

    /**
     * 评论明星文章
     *
     * @param snId
     * @param commentValue
     * @param callback
     */
    public void doStarArticeComment(String snId, String commentValue, GetDataCallback callback) {
        // 判断是否登录
        if (!SharedPreferencesUtil.isLogin(context)) {
            ToastUtil.showMessage(context, "请先登录");
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }

        starModel.doStarArticeComment(SharedPreferencesUtil.getUserId(context), snId, commentValue, callback);
    }

    /**
     * 获取明星文章详情（内容+评论）
     *
     * @param isRefresh 是否刷新列表
     * @param snId
     * @param callback
     */
    public void getStarArticeDetail(Boolean isRefresh, String snId, GetDataCallback callback) {
        int temp = pageIndex;
        if (isRefresh) {
            temp = 1;
        }

        starModel.getStarArticeDetail(snId, String.valueOf(pageSize), String.valueOf(temp), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                pageIndex++;
                if (isRefresh) {
                    pageIndex = 2;
                }
                callback.success(data);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }

}
