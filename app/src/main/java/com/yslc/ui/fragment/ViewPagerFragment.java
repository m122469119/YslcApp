package com.yslc.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.data.service.NewModelService;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.ui.base.BaseFragment;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;

import java.util.ArrayList;

/**
 * 滑动的Fragment
 * 获取所有咨讯标题以及Id
 * 使用ViewPager装载，包括不同类型的Fragment
 * 传递不同类型的咨讯Id给Framgent，Fragment通过Id获取资讯内容
 * <p>继承fragment，需要重写两个方法</p>
 * <p>{@link #getTitle()}异步获取标题数据，成功后使用{@link #initViewPager(ArrayList)}创建fragment</p>
 * <p>{@link #createFragment(int)} 创建fragment实例</p>
 * @author tommy
 */
public abstract class ViewPagerFragment extends BaseFragment implements
        OnClickListener, OnPageChangeListener, OnSelecterCallback,
        OnTryListener {
    protected ColumnHorizontalScrollView mColumnView;//滑动标题
    protected ViewPager mViewPager;
    protected LoadView loadView;
//    private NewModelService newModelService;//业务类

    /**
     * 设置布局
     * <p>包含副标题、分隔线和正在加载View</p>
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

//        newModelService = new NewModelService(getContext());
        if (loadView.setStatus(LoadView.LOADING)) {
            //获取数据
            getTitle();
        }

    }

    /**
     * 获取副标题数据
     * <p>异步下载副标题列表信息</p>
     * <p>成功后使用{@link ViewPagerFragment#initViewPager(ArrayList)}根据标题数量初始化fragment</p>
     */
    protected abstract void getTitle();

    /**
     * 初始化ViewPager
     * 传递咨讯Id
     * <p>初始化fragment，每个fragment保存标题id</p>
     * <p>把建好的fragment列表放到适配器并设置好ViewPager的适配器</p>
     * @param listTitle 副标题集合
     */
    protected void initViewPager(ArrayList<ColnumBean> listTitle) {
        ArrayList<Fragment> listFragment = new ArrayList<>();
        Fragment fragment;
        Bundle bundler;
        for (int i = 0, len = listTitle.size(); i < len; i++) {
            fragment = createFragment(i);
            bundler = new Bundle();
            bundler.putString("id", listTitle.get(i).getId());
            fragment.setArguments(bundler);
            listFragment.add(fragment);
        }

        MyFragmentAdapter adapter = new MyFragmentAdapter(getChildFragmentManager(), listFragment);
        mViewPager.setOffscreenPageLimit(2);//预加载
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    /**
     * 返回fragment实例
     * @return
     */
    protected abstract Fragment createFragment(int position);

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
