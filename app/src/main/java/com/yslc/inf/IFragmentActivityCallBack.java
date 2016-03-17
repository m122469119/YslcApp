package com.yslc.inf;

import android.support.v4.app.Fragment;

/**
 * 定义FragmentActivity接口
 *
 * @author HH
 */
public interface IFragmentActivityCallBack {
    /**
     * 添加Fragment
     */
    void addFragment(Fragment fragment, String title);

    /**
     * 初始化显示Fragment
     */
    void showFragment(int id);

    /**
     * 设置显示Fragment
     */
    void setFragment(int position);
}
