package com.yslc.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.view.BaseIndicator;
import com.yslc.view.BaseViewPagerAdapter;

/**
 * 欢迎页面Activity，实现图片滑动，滑动到最后一页显示进入APP的按钮
 *
 * @author HH
 */
public class WelcomeActivity extends BaseActivity implements OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private View gotoApp;
    private SharedPreferencesUtil spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
        setTranslucentStatusBar(true);

        if (isFristOpenApp()) {
            // 不是第一次打开软件,直接进入到启动页
            startActivity(new Intent(this, SplahActivity.class));
            onFinishActivity();
        }
    }

    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    /**
     * 初始化页面
     *
     * <p>设置按钮点击事件</p>
     * <p>设置ViewPager滑动事件</p>
     * <p>设置ViewPager图片</p>
     * <p>设置ViewPager适配器</p>
     */
    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(this);

        // 设置滑动图片数据
        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(this);
        //设置本地图片
        adapter.setSdImage(
                new ArrayList<>(Arrays.asList(R.drawable.test1,
                        R.drawable.test2, R.drawable.test3, R.drawable.test4)),
                false);
        viewPager.setAdapter(adapter);

        // 创建指示器绑定ViewPager
        ((BaseIndicator) findViewById(R.id.myIndicator)).setViewPager(viewPager, this);
        //进入主页button
        gotoApp = findViewById(R.id.gotoApp);
        gotoApp.setOnClickListener(this);
    }

    /**
     * 判断是否第一次打开APP
     * @return 注意：返回true代表不是第一次，返回false代表是第一次
     */
    private boolean isFristOpenApp() {
        spf = new SharedPreferencesUtil(this,
                Constant.SYSTEM_NAME);
        return spf.getBoolean(Constant.SYSTEM_ISFRIST_KEY);
    }

    /**
     * 进入主页点击事件
     * <p>进入主页，并把是否第一次进入甚至为true(代表下次不是第一次）</p>
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gotoApp) {
            // 进入启动页,维护下次不再是第一次打开APP
            startActivity(new Intent(WelcomeActivity.this, SplahActivity.class));
            spf.setBoolean(Constant.SYSTEM_ISFRIST_KEY, true);
            onFinishActivity();
        }
    }
//-------------下面三个方法是重写OnChangeListener监听的接口方法-------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //滑动到最后一页需要显示进入APP按钮
        if (position == viewPager.getAdapter().getCount() - 1) {
            gotoApp.setVisibility(View.VISIBLE);
        } else {
            gotoApp.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
    }

}
