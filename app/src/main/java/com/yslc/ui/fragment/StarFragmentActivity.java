package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.StarModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.view.LoadView;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.LoadView.OnTryListener;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;

/**
 * 明星FragmentActivity
 *
 * @author HH
 */
public class StarFragmentActivity extends BaseFragment implements
        OnClickListener, OnSelecterCallback, OnTryListener {
    private ColumnHorizontalScrollView mColumnView;
    private LoadView loadView;
    private ArrayList<ColnumBean> listTitle;
    private StarModelService starModelService;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_star;
    }

    @Override
    protected void findView(View views) {
        super.findView(views);

        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        mColumnView = (ColumnHorizontalScrollView) views.findViewById(R.id.columnView);
        mColumnView.setOnSelecterCallback(this);
        views.findViewById(R.id.rightBtn).setOnClickListener(
                StarFragmentActivity.this);

        // 加载数据
        starModelService = new StarModelService(getContext());
        if (loadView.setStatus(LoadView.LOADING)) {
            getTitle();
        }
    }

    /**
     * 获取明星类型数据
     */
    private void getTitle() {
        starModelService.getStarColumnData(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                listTitle = (ArrayList<ColnumBean>) data;
                if (listTitle.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    loadView.setStatus(LoadView.SUCCESS);
                    // 设置栏目数据
                    mColumnView.setColumnData(listTitle);
                    // 显示第一个类型的视频列表
                    startFragment(0);
                }
            }

            @Override
            public <T> void failer(T data) {
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 启动某个Fragment
     */
    private void startFragment(int index) {
        mColumnView.selectTab(index);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        StarFragment fragment = new StarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", listTitle.get(index).getId());
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightBtn:
                // 设置下一个栏目
                mColumnView.goRight();
                break;
        }
    }

    @Override
    public void onSelecterCallback(int i) {
        mColumnView.selectTab(i);
        startFragment(i);
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getTitle();
        }
    }

}
