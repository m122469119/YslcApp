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
     * @param  resId 资源文件String字符串ID
     * @param isCancle 是否返回键取消
     */
    void showWaitDialogs(int resId, boolean isCancle);

    /**
     * 显示网络请求信息提示框
     *
     * @param info 提示信息（比如正在加载）
     * @param isCancle 是否返回键取消
     */
    void showWaitDialogs(String info, boolean isCancle);
}
