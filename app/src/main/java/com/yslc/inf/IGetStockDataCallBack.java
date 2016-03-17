package com.yslc.inf;

import java.util.List;

/**
 * 股市行情数据请求结果回调
 * 请求前后回调
 *
 * @author HH
 */
public interface IGetStockDataCallBack {
    void before(Object o);

    void success(List<?> o, double closePrice);

    void failer(Object o);

    <T> void successDetail(T t);
}
