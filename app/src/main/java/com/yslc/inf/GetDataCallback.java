package com.yslc.inf;

/**
 * 对于数据请求结果的回调
 * <p> 请求成功以及请求失败的回调操作 </p>
 * <p> 实现页面以及数据获取的分离 </p>
 * <p>
 * Created by HH on 2016/2/25.
 */
public interface GetDataCallback {
    /**
     * 请求成功返回对应数据
     * 一般是对象或者对象数组
     *
     * @param data
     * @param <T>
     */
    <T> void success(T data);

    /**
     * 请求失败返回对应数据
     * 一般是字符串也可不返回
     *
     * @param data
     * @param <T>
     */
    <T> void failer(T data);
}
