package com.yslc.inf;

/**
 * 定义网络请求信息框操作
 *
 * @author XHH
 */
public interface ILoadingDialogCallBack {

    /**
     * 隐藏网络请求信息提示框
     */
    void hideWaitDialog();

    /**
     * 显示网络请求信息提示框
     *
     * @param isCancle 是否返回键取消
     */
    void showWaitDialogs(int resId, boolean isCancle);

    void showWaitDialogs(String info, boolean isCancle);
}
