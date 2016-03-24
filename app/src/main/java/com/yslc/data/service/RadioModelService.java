package com.yslc.data.service;

import android.content.Context;

import com.yslc.data.impl.RadioModelImpl;
import com.yslc.data.inf.IRadioModel;
import com.yslc.inf.GetDataCallback;

/**
 * 广播播放模块业务层
 * <p>
 * Created by HH on 2016/2/27.
 */
public class RadioModelService {
    private IRadioModel radioModel;
    private int pageSize = 15;
    private int pageIndex = 1;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public RadioModelService(Context context) {
        this.radioModel = new RadioModelImpl(context);
    }

    /**
     * 获取直播初始数据
     *
     * @param callback
     */
    public void getRadioData(GetDataCallback callback) {
        radioModel.getRadioData(callback);
    }

    /**
     * 获取广播重播界面列表
     *
     * @param weekDate
     * @param callback
     */
    public void getRadioReliveListData(String weekDate, GetDataCallback callback) {
        radioModel.getRadioReliveListData(weekDate, callback);
    }

    /**
     * 获取节目详细信息
     *
     * @param radioId
     * @param callback
     */
    public void getRadioReliveDetail(String radioId, GetDataCallback callback) {
        radioModel.getRadioReliveDetail(radioId, callback);
    }

    /**
     * 获取某节目往期列表
     *
     * @param dbName 节目名称
     * @param callback 回调函数
     */
    public void getReliveListForHost(String dbName, GetDataCallback callback) {
        radioModel.getReliveListForHost(dbName, String.valueOf(pageSize), String.valueOf(pageIndex), new GetDataCallback() {
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

}
