package com.yslc.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.yslc.data.service.StockCodeModelSerice;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.AnimationUtil;

/**
 * 启动页,实现启动图透明度渐变的动画
 *
 * <p>开始加载股票数据</p>
 * @author HH
 */
public class SplahActivity extends BaseActivity implements AnimationUtil.OnMyAnimationEnd {
    //动画时间
    private static final int ANIMATION_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatusBar(true);

        //开启启动页透明度渐变动画
        findViewById(R.id.splahBg).startAnimation(//imageView
                new AnimationUtil(this).getAlphaAnimation(0.5f, 1.0f, ANIMATION_TIME));

        //导入股票代码
        new StockCodeModelSerice(this).getStockCodeList();

    }

    /**
     * 布局只包含一张ImageView
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splah;
    }

    /**
     * 动画监听
     * <p>动画结束后进入主页</p>
     */
    @Override
    public void onMyAnimationEnd() {
        //动画结束后，进入主页
        startActivity(new Intent(SplahActivity.this,
                IndexFragementActivity.class));

        onFinishActivity();
    }

}
