package com.yslc.data.inf;

import com.yslc.inf.GetDataCallback;
import com.yslc.util.SharedPreferencesUtil;

/**
 * 明星模块网络接口层
 * <p>
 * Created by HH on 2016/2/26.
 */
public interface IStarModel {
    /**
     * 获取明星栏目数据
     */
    void getStarColumnData(GetDataCallback callback);

    /**
     * 加载明星列表
     */
    void getStarListData(String stId, String pageSize, String pageIndex, GetDataCallback callback);

    /**
     * 获取明星文章列表
     *
     * @param sifId
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    void getStarArticleList(String sifId, String pageSize, String pageIndex, GetDataCallback callback);

    /**
     * 明星文章点赞
     *
     * @param snId
     * @param callback
     */
    void doPraiseForArticle(String snId, GetDataCallback callback);

    /**
     * 获取明星文章详情（内容+评论）
     *
     * @param snId
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    void getStarArticleDetail(String snId, String pageSize, String pageIndex, GetDataCallback callback);

    /**
     * 评论明星文章
     *
     * @param userId
     * @param snId
     * @param commentValue
     * @param callback
     */
    void doStarArticleComment(String userId, String snId, String commentValue, GetDataCallback callback);
}
