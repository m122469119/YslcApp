package com.yslc.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yslc.ui.base.BaseFragmentActivity;
import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.ui.fragment.MyFragment;
import com.yslc.ui.fragment.NewFragmentActivity;
import com.yslc.ui.fragment.StarFragmentActivity;
import com.yslc.ui.fragment.VedioFragmentActivity;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

/**
 * 首页FragmentActivity,包含咨讯、视频、明星、我，四大Fragment
 *
 * @author HH
 */
public class IndexFragementActivity extends BaseFragmentActivity implements
        OnClickListener {
    private TextView titleTv;
    private boolean mBackKeyPressed;
    private DrawerLayout drawer;

    private int switchId = -1; //需要跳转的id

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_fragementactivity;
    }

    @Override
    protected String getToolbarTitle() {
        return getText(R.string.yslcNew).toString();
    }

    @Override
    protected void onChanceFragment(String title) {
        super.onChanceFragment(title);

        titleTv.setText(title);
    }

    @Override
    protected void initView() {
        titleTv = (TextView) findViewById(R.id.titleText);
        initFragment();
        setmRadioGroup((RadioGroup) findViewById(R.id.radioGroup));
        setDrawerLayout();
    }

    /**
     * 初始化Fragmen
     */
    private void initFragment() {
        // 初始化Fragment
        addFragment(new NewFragmentActivity(), getText(R.string.yslcNew).toString());
        addFragment(new VedioFragmentActivity(), getText(R.string.vedio).toString());
        addFragment(new StarFragmentActivity(), getText(R.string.star).toString());
        addFragment(new MyFragment(), getText(R.string.my).toString());
        showFragment(R.id.fragment);
    }

    /**
     * 初始化左侧侧边栏菜单，绑定单击事件
     */
    private void setDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        findViewById(R.id.magazine).setOnClickListener(this);
        findViewById(R.id.interactives).setOnClickListener(this);
        findViewById(R.id.stockMarket).setOnClickListener(this);
        findViewById(R.id.radio).setOnClickListener(this);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, (Toolbar) findViewById(R.id.toolbar), R.string.empty, R.string.empty) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                switchId = -1;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (switchId != -1) {
                    switchTo(switchId);
                }
            }
        };
        mDrawerToggle.syncState();
        drawer.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onClick(View v) {
        drawer.closeDrawer(Gravity.LEFT);

        switchId = v.getId();
    }

    private void switchTo(int id) {
        switch (id) {
            case R.id.magazine:
                // 显示股市广播直播节目
                startActivity(new Intent(this, RadioPlayerActivity.class));
                break;

            case R.id.interactives:
                // 显示股市广播重温节目
                startActivity(new Intent(this, RadioReliveActivity.class));
                break;

            case R.id.stockMarket:
                // 显示股市行情
                startActivity(new Intent(this, StockMarketActivity.class));
                break;

            case R.id.radio:
                // 支付模块，预留
                ToastUtil.showMessage(this, "支付");
                break;
        }
    }

    /**
     * 再按一次返回键则退出应用程序
     * <p>
     * 超过两秒则擦除第一次操作，两秒内才退出应用程序
     */
    @Override
    public void onBackPressed() {
        if (!mBackKeyPressed) {
            ToastUtil.showMessage(this, getString(R.string.aginClick));
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        } else {
            onFinishActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 清除缓存的股票行情数据
         * K线图横屏和竖屏转换保存的数据需要清除
         * 缓存数据只在横竖屏转换的时候有效
         */
        new SharedPreferencesUtil(this, Constant.CACHE_STOCK_DATA_NAME).clearAll();
    }

}
