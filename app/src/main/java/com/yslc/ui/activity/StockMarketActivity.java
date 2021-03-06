package com.yslc.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.bean.ColnumBean;
import com.yslc.bean.StocyCodeBean;
import com.yslc.ui.fragment.StockKFragment;
import com.yslc.ui.fragment.StockMinFragment;
import com.yslc.util.CommonUtil;
import com.yslc.view.ColumnHorizontalScrollView;

import java.util.ArrayList;

/**
 * 股市行情FragmentActivity
 *
 * @author HH
 */
public class StockMarketActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        ColumnHorizontalScrollView.OnSelecterCallback {
    private ColumnHorizontalScrollView mColumnView;
    private ViewPager mViewPager;
    private StocyCodeBean codeBean;
    private int currentScreen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_stock_market;
    }

    @Override
    protected void initView() {
        codeBean = (StocyCodeBean) getIntent().getSerializableExtra("codeBean");
        if (null == codeBean) {
            //默认搜索浦发银行
            codeBean = new StocyCodeBean("sz399001", "深证成指");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setSubtitle(codeBean.getStock_Code());
        toolbar.setTitle(codeBean.getStock_Name());

        mColumnView = (ColumnHorizontalScrollView) findViewById(R.id.columnView);
        if (findViewById(R.id.rightBtn) != null) {
            findViewById(R.id.rightBtn).setVisibility(View.GONE);
        }
        mColumnView.setRightBtnWidth(0);
        currentScreen = getResources().getConfiguration().orientation;
        if (currentScreen == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏
            mColumnView.setTitleWidth(CommonUtil.getScreenWidth(this));
        } else if (currentScreen == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏
            mColumnView.setTitleWidth(CommonUtil.dip2px(this, 200));
            mColumnView.setTextColors(ContextCompat.getColorStateList(this, R.color.gray_to_white));
        }
        mColumnView.setOnSelecterCallback(this);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        // 初始化ViewPager并初始化栏目数据
        initDate();
        initViewPager(codeBean.getStock_Code());
    }

    /**
     * 设置ColnumBar
     * <p>
     */
    private void initDate() {
        ArrayList<ColnumBean> beans = new ArrayList<>();
        ColnumBean bean1 = new ColnumBean("", "分时图", "");
        ColnumBean bean2 = new ColnumBean("", "K线图", "");
        beans.add(bean1);
        beans.add(bean2);

        // 设置栏目数据
        mColumnView.setColumnData(beans);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager(String code) {
        ArrayList<Fragment> listFragment = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);

        StockMinFragment stockMinFragment = new StockMinFragment();
        stockMinFragment.setArguments(bundle);
        listFragment.add(stockMinFragment);

        StockKFragment stockKFragment = new StockKFragment();
        stockKFragment.setArguments(bundle);
        listFragment.add(stockKFragment);

        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), listFragment);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_goto_search_scoky, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            //跳转到搜索界面
            startActivity(new Intent(this, SearchStocyActivity.class));
        } else if (id == R.id.action_turnscreen) {
            //翻转屏幕
            turnScreen();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 翻转屏幕
     */
    private void turnScreen() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏，则切换为横屏
            currentScreen = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏，则切换为竖屏
            currentScreen = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        setRequestedOrientation(currentScreen);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        mColumnView.selectTab(position);
    }

    @Override
    public void onSelecterCallback(int selectPage) {
        mViewPager.setCurrentItem(selectPage);
    }

}