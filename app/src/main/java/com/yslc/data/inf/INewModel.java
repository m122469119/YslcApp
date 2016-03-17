package com.yslc.data.inf;

import com.yslc.inf.GetDataCallback;
import com.yslc.util.SharedPreferencesUtil;

/**
 * 咨讯新闻模块网络接口层
 * <p>
 * Created by HH on 2016/2/26.
 */
public interface INewModel {
    /**
     * 获取栏目数据
     *
     * @param btid : 类型Id
     */
    void getColumnData(String btid, GetDataCallback callback);

    /**
     * 第一次加载快讯
     *
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    void fristLoadFastNewData(String pageSize, String pageIndex, GetDataCallback callback);

    /**
     * 加载带有广告条的资讯
     *
     * @param callback
     */
    void loadMoreNewData(GetDataCallback callback);

    /**
     * 加载更多资讯，用于分页实现
     *
     * @param sstId
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    void loadMoreNewData(String sstId, String pageSize, String pageIndex, GetDataCallback callback);

    /**
     * 获取大盘信息（当前价以及跌涨幅）
     *
     * @param callback
     */
    void getStockInfo(GetDataCallback callback);

    /**
     * 新闻详情页发布评论
     *
     * @param uiId
     * @param niId
     * @param ncContent
     * @param callback
     */
    void doNewComment(String uiId, String niId, String ncContent, GetDataCallback callback);

    /**
     * 获取新闻页面评论数据
     *
     * @param niId
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    void getNewCommentList(String niId, String pageSize, String pageIndex, GetDataCallback callback);
}
