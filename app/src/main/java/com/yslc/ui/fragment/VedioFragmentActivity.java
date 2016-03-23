package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.NewModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.view.LoadView;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.LoadView.OnTryListener;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;

/**
 * 视频Fragment
 * 思路同咨讯Fragmetn,没有使用ViewPager装载子Fragment
 * 此Fragment跟NewFragmentActivity太过相识，没有注释的方法请参考NewFragmentActivity同类方法
 * @author HH
 */
public class VedioFragmentActivity extends BaseFragment implements
        OnClickListener, OnSelecterCallback, OnTryListener {
    private LoadView loadView;
    private ColumnHorizontalScrollView mColumnView;
    private ArrayList<ColnumBean> listTitle;
    private NewModelService newModelService;

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    /**
     * 初始化布局
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);
        //loadView
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //副标题
        mColumnView = (ColumnHorizontalScrollView) views
                .findViewById(R.id.columnView);
        mColumnView.setOnSelecterCallback(this);
        views.findViewById(R.id.rightBtn).setOnClickListener(this);
        //实例业务类
        newModelService = new NewModelService(getContext());
        // 获取视频栏目数据
        if (loadView.setStatus(LoadView.LOADING)) {
            setTitle();
        }
    }

    /**
     * 获取视频栏目数据
     */
    private void setTitle() {
        newModelService.getColumnData("2C430AF1-FD0E-433D-B40D-F51BDB99FEC0", new GetDataCallback() {
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
     * 传递视频Id到子Framgent
     *
     * @param index 显示的子Fragment的下标
     */
    private void startFragment(int index) {
        mColumnView.selectTab(index);//选中某个副标题
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        VideoFragment fragment = new VideoFragment();
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
                // 点击下一个栏目按钮
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
            setTitle();
        }
    }

}
