package com.yslc.data.inf;

import com.yslc.inf.GetDataCallback;

/**
 * 股市行情模块网络接口层
 * <p>
 * Created by HH on 2016/2/27.
 */
public interface IStockCodeModel {

    /**
     * 获取所有股票代码列表
     *
     * @param callback
     */
    void getStockCodeList(GetDataCallback callback);
}
