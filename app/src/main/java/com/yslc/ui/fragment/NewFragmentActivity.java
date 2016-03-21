package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.NewModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.bean.ColnumBean;
import com.yslc.view.LoadView;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.LoadView.OnTryListener;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;

/**
 * 咨讯Fragment
 * 获取所有咨讯标题以及Id
 * 使用ViewPager装载，包括不同类型的Fragment
 * 传递不同类型的咨讯Id给Framgent，Fragment通过Id获取资讯内容
 *
 * @author HH
 */
public class NewFragmentActivity extends BaseFragment implements
        OnClickListener, OnPageChangeListener, OnSelecterCallback,
        OnTryListener {
    private ColumnHorizontalScrollView mColumnView;//滑动标题
    private ViewPager mViewPager;
    private LoadView loadView;
    private NewModelService newModelService;//业务类

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_new;
    }

    /**
     * 初始化布局
     * <p>load监听</p>
     * <p>关联副标题布局并设置监听</p>
     * <p>关联Viewpager布局</p>
     * <p>监听副标题有边按钮</p>
     * <p>初始化业务类</p>
     * <p>获取和加载数据</p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);

        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        mColumnView = (ColumnHorizontalScrollView) views
                .findViewById(R.id.columnView);
        mColumnView.setOnSelecterCallback(this);
        mViewPager = (ViewPager) views.findViewById(R.id.viewPager);
        views.findViewById(R.id.rightBtn).setOnClickListener(this);

        newModelService = new NewModelService(getContext());
        if (loadView.setStatus(LoadView.LOADING)) {
            //获取数据
            getTitle();
        }

    }

    /**
     * 获取咨讯栏目数据
     */
    private void getTitle() {
        newModelService.getColumnData("C879A54D-7605-4619-A0F8-3F2516D87C05", new GetDataCallback() {
            /**
             * 成功获取栏目数据
             * <p>设置副标题栏</p>
             * <p>根据数据初始化ViewPager</p>
             * @param data 栏目标题列表
             * @param <T>
             */
            @Override
            public <T> void success(T data) {
                ArrayList<ColnumBean> list = (ArrayList<ColnumBean>) data;
                if (list.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    loadView.setStatus(LoadView.SUCCESS);
                    // 设置栏目数据
                    mColumnView.setColumnData(list);
                    // 初始化Fragments
                    initViewPager(list);
                }
            }

            @Override
            public <T> void failer(T data) {
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 初始化ViewPager
     * 传递咨讯Id
     * <p>初始化fragment，每个fragment保存标题id</p>
     * <p>把建好的fragment列表放到适配器并设置好ViewPager的适配器</p>
     */
    private void initViewPager(ArrayList<ColnumBean> listTitle) {
        ArrayList<Fragment> listFragment = new ArrayList<>();
        Fragment fragment;
        Bundle bundler;
        for (int i = 0, len = listTitle.size(); i < len; i++) {
            if (i == 0) {//快讯和头版头条布局不同一般，所以单独创建
                fragment = new NewFragmentFast();
            } else if (i == 1) {
                fragment = new NewFragmentOne();
            } else {
                fragment = new NewFragmentTwo();
            }
            bundler = new Bundle();
            bundler.putString("id", listTitle.get(i).getId());
            fragment.setArguments(bundler);
            listFragment.add(fragment);
        }

        MyFragmentAdapter adapter = new MyFragmentAdapter(getChildFragmentManager(), listFragment);
        mViewPager.setOffscreenPageLimit(1);//预加载
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightBtn:
                // 向右切换栏目
                mColumnView.goRight();
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }

    @Override
    public void onPageScrolled(int position, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        mColumnView.selectTab(position);
    }

    /**
     * 点击副标题后，回调
     * @param selectPage
     */
    @Override
    public void onSelecterCallback(int selectPage) {
        mViewPager.setCurrentItem(selectPage);
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {//显示正在加载成功
            //获取副标题数据
            getTitle();
        }
    }

}
