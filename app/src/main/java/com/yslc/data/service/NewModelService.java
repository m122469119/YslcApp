package com.yslc.data.service;

import android.content.Context;
import android.content.Intent;

import com.yslc.app.Constant;
import com.yslc.data.impl.NewModelImpl;
import com.yslc.data.inf.INewModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.activity.LoginActivity;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.LoadView;

/**
 * 新闻资讯模块业务层
 * <p>
 * Created by HH on 2016/2/26.
 */
public class NewModelService {
    private Context context;
    private INewModel newModel;
    private int pageSize = 15;
    private int pageIndex = 1;

    public NewModelService(Context context) {
        this.context = context;
        newModel = new NewModelImpl(context);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * 获取栏目数据
     *
     * @param btid
     * @param callback
     */
    public void getColumnData(String btid, GetDataCallback callback) {
        newModel.getColumnData(btid, callback);
    }

    /**
     * 第一次加载快讯数据
     *
     * @param callback
     */
    public void fristLoadFastNewData(GetDataCallback callback) {
        newModel.fristLoadFastNewData(String.valueOf(pageSize), "1", new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                //下一次加载第二页的数据
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
     * 加载带有广告条的资讯
     * <p>资讯下头版头条数据</p>
     * @param callback
     */
    public void loadMoreNewData(GetDataCallback callback) {
        newModel.loadMoreNewData(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                callback.success(data);
                pageIndex = 2;
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }


    /**
     * 加载更多资讯，用于分页实现
     * <p>如果第一次加载或者下拉刷新，则显示第一页</p>
     * @param isFrist:是否第一次加载或者下拉刷新
     * @param sstId 作为请求参数
     * @param callback 回调函数
     */
    public void loadMoreNewData(boolean isFrist, String sstId, GetDataCallback callback) {
        int tempIndex = pageIndex;
        if (isFrist) {
            tempIndex = 1;
        }
        newModel.loadMoreNewData(sstId, String.valueOf(pageSize), String.valueOf(tempIndex),
                new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                pageIndex++;
                if (isFrist) {
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

    /**
     * 获取大盘信息（当前价以及跌涨幅）
     *
     * @param callback
     */
    public void getStockInfo(GetDataCallback callback) {
        newModel.getStockInfo(callback);
    }

    /**
     * 新闻详情页发布评论
     *
     * @param niId
     * @param ncContent
     * @param callback
     */
    public void doNewComment(String niId, String ncContent, GetDataCallback callback) {
        //判断是否有网络
        if (!CommonUtil.isNetworkAvalible(context)) {
            ToastUtil.showMessage(context, HttpUtil.NO_INTERNET_INFO);
            return;
        }

        //判断是否登录
        if (!SharedPreferencesUtil.isLogin(context)) {
            ToastUtil.showMessage(context, "请先登录");
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }


        newModel.doNewComment(SharedPreferencesUtil.getUserId(context), niId, ncContent, callback);
    }

    /**
     * 获取新闻页面评论数据
     *
     * @param niId
     * @param callback
     */
    public void getNewCommentList(boolean isFrist, String niId, GetDataCallback callback) {
        int tempIndex = pageIndex;
        if (isFrist) {
            tempIndex = 1;
        }

        newModel.getNewCommentList(niId, String.valueOf(pageSize), String.valueOf(tempIndex), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                pageIndex++;
                if (isFrist) {
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
