package com.yslc.data.inf;

import com.yslc.inf.GetDataCallback;

/**
 * 广播播放模块网络接口层
 * <p>
 * Created by HH on 2016/2/27.
 */
public interface IRadioModel {
    /**
     * 获取直播初始数据
     *
     * @param callback
     */
    void getRadioData(GetDataCallback callback);

    /**
     * 获取广播重播界面列表
     *
     * @param weekDate
     * @param callback
     */
    void getRadioReliveListData(String weekDate, GetDataCallback callback);

    /**
     * 获取节目详细信息
     *
     * @param radioId
     * @param callback
     */
    void getRadioReliveDetail(String radioId, GetDataCallback callback);

    /**
     * 获取某节目往期列表
     *
     * @param dbName
     * @param pageSize
     * @param pageIndex
     * @param callback
     */
    void getReliveListForHost(String dbName, String pageSize, String pageIndex, GetDataCallback callback);

}
