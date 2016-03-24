package com.yslc.ui.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.bean.ColnumBean;
import com.yslc.ui.fragment.RadioReliveFragment;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;

/**
 * 股市重温FragmentActivity
 * 显示一周股市电台的重温节目列表
 * 每周界面列表不同，分为周一至周五以及周六和周日三种
 *
 * @author HH
 */
public class RadioReliveActivity extends BaseActivity implements
        OnPageChangeListener, OnSelecterCallback {
    private ColumnHorizontalScrollView mColumnView;//副标题
    private ViewPager mViewPager;//主内容

    /**
     * 设置布局
     * <p>包含标题栏，副标题，主内容ViewPager</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_relive;
    }

    /**
     * 设置标题
     * @return
     */
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.interactives);//TODO 引用String资源
    }

    /**
     * 初始化布局
     * <p>初始化副标题并设置点击事件</p>
     * <p>关联ViewPager并初始化</p>
     */
    @Override
    protected void initView() {
        mColumnView = (ColumnHorizontalScrollView) findViewById(R.id.columnView);
        findViewById(R.id.rightBtn).setVisibility(View.GONE);//不要副标题栏的点击按钮
        mColumnView.setRightBtnWidth(0);
        mColumnView.setOnSelecterCallback(this);//副标题点击事件
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        // 初始化ViewPager并初始化栏目数据
        initViewPager(initRadioDate());
    }

    /**
     * （设置副标题）初始化广播日期类型并设置ColnumBar
     * <p>
     * 重温广播日期分为周一至周五以及周六日
     * 日期的Id分别表示为“1-5,6,7”
     * <p>此副标题不用联网加载</p>
     * @return 副标题数据
     */
    private ArrayList<ColnumBean> initRadioDate() {
        ArrayList<ColnumBean> beans = new ArrayList<>();

        beans.add(new ColnumBean("1-5", "周一至周五", ""));
        beans.add(new ColnumBean("6", "周六", ""));
        beans.add(new ColnumBean("7", "周日", ""));

        // 设置栏目数据
        mColumnView.setColumnData(beans);
        return beans;
    }

    /**
     * 初始化ViewPager
     * <p>根据副标题数量添加fragment</p>
     * <p>设置适配器并监听</p>
     * @param beans 副标题数据
     */
    private void initViewPager(ArrayList<ColnumBean> beans) {
        ArrayList<Fragment> listFragment = new ArrayList<>();
        Fragment fragment;
        Bundle bundler;
        for (int i = 0, len = beans.size(); i < len; i++) {
            fragment = new RadioReliveFragment();
            bundler = new Bundle();
            bundler.putSerializable("bean", beans.get(i));
            fragment.setArguments(bundler);
            listFragment.add(fragment);
        }

        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), listFragment);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * ViewPager滑动事件
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        mColumnView.selectTab(position);
    }

    /**
     * 点击副标题事件
     * @param selectPage
     */
    @Override
    public void onSelecterCallback(int selectPage) {
        mViewPager.setCurrentItem(selectPage);
    }

}
