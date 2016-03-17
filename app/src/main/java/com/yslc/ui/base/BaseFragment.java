package com.yslc.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yslc.R;
import com.yslc.view.LoadView;

/**
 * 所有Fragment基类
 *
 * @author HH
 */
public abstract class BaseFragment extends Fragment {
    private View views;
    private boolean isLoad;
    private LoadView loadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (views == null && getLayoutId() != 0) {
            views = inflater.inflate(getLayoutId(), container, false);
            loadView = (LoadView) views.findViewById(R.id.view);
            findView(views);

            if (getUserVisibleHint() && null != loadView && loadView.getStatus() == LoadView.NONE && !isLoad) {
                //界面显示并且未加载
                isLoad = true;
                onFristLoadData();
            }
        }

        if (getLayoutId() != 0) {
            //防止Fragment加载进父容器出错
            ViewGroup parans = (ViewGroup) views.getParent();
            if (null != parans) {
                parans.removeView(views);
            }
        }

        return views;
    }

    /**
     * 布局Id
     *
     * @return
     */
    protected int getLayoutId() {
        return 0;
    }

    /**
     * 界面初始化
     *
     * @param : views 该Fragment的根容器
     */
    protected void findView(View views) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && null != loadView && !isLoad) {
            onFristLoadData();
            isLoad = true;
        }
    }

    /**
     * 第一次加载数据
     * <p>
     * 主要针对ViewPager的预加载情况，防止每次请求数据
     */
    protected void onFristLoadData() {

    }

}
