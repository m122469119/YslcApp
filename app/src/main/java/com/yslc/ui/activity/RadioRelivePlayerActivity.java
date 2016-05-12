package com.yslc.ui.activity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.bean.RadioBean;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.PlayerUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 广播播放Activity(重播)
 * <p>与RadioPlayerActivity差不多，少了一个计时功能</p>
 * @see RadioPlayerActivity
 * @author HH
 */
public class RadioRelivePlayerActivity extends BaseActivity implements
        OnClickListener, PlayerUtil.OnPlayListener {
    private PlayerUtil pv;
    private CheckBox playImg;
    private ImageButton  refreshImg;
    private Animation animation;
    private RadioBean bean;
    private boolean isPlay = true;
    private boolean isPrepared = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_player;
    }

    /**
     * 初始化布局
     * <p>包含状态栏、播放工具、标题、返回键、刷新、播放键设置</p>
     * <p>开始设置控件数据</p>
     */
    @Override
    protected void initView() {
        super.initView();

        ViewUtil.TranslucentStatusBar(this);//状态栏透明
        bean = (RadioBean) getIntent().getSerializableExtra("RadioBean");//获取播放数据
        pv = new PlayerUtil();//播放工具类
        pv.setOnPlayListener(this);

        //标题
        ((TextView) findViewById(R.id.titleText)).setText(getString(R.string.interactives));
        findViewById(R.id.backBtn).setOnClickListener(this);//返回键
        playImg = (CheckBox) findViewById(R.id.paly_pouse);//播放/停止按钮
        playImg.setOnClickListener(this);
        refreshImg = (ImageButton) findViewById(R.id.refreshBtn);//刷新按钮
        refreshImg.setOnClickListener(this);
        //为刷新按钮设置动画
        animation = AnimationUtils.loadAnimation(this, R.anim.load_progress_anim);
        refreshImg.startAnimation(animation);

        setData(bean);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                onFinishActivity();//返回
                break;

            case R.id.paly_pouse:
                // 暂停或播放
                if (isPlay) {
                    // 原来是播放的，改为暂停
//                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_play));
                    pv.pause();
                    isPlay = false;
                } else {
                    // 原来是暂停的，改为播放
//                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pause));
                    pv.start();
                    isPlay = true;
                }
                break;

            case R.id.refreshBtn:
                if (!isPlay) {
                    // 原来是暂停的，改为播放
//                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pause));
                    isPlay = true;
                }
                playImg.setEnabled(false);

                // 刷新,开始播放广播
                if (!pv.isPlay()) {//不是在播放则开始刷新
                    refreshImg.startAnimation(animation);
                    pv.playUrl(bean.getRadioUrl());//下载电台播放
                } else {//播放则啥都不做
                    startPlay();//停止刷新动画
                }
                break;
        }
    }

    /**
     * 设置数据
     */
    private void setData(RadioBean bean) {
        if (!CommonUtil.isNetworkAvalible(this)) {//检测网络
            ToastUtil.showMessage(this, HttpUtil.NO_INTERNET_INFO);
            return;
        }

        // 设置头像和标题
        ImageLoader loader = ImageLoader.getInstance();
        loader.displayImage(bean.getRadioHostUrl(),//头像
                (ImageView) findViewById(R.id.starImg),
                ViewUtil.getCircleOptions());
        //设置界面名称和主持人
        ((TextView) findViewById(R.id.text_time)).setText(bean.getRadioName());
        ((TextView) findViewById(R.id.text_info)).setText("主持人:" + bean.getRadioHost());

        playImg.setEnabled(true);
        refreshImg.setEnabled(true);

        // 开始播放广播
        pv.playUrl(bean.getRadioUrl());
    }

    /**
     * 播放回调
     * <p>下载好电台后，停止刷新动画，改变播放按钮</p>
     */
    @Override
    public void startPlay() {
        isPrepared = true;
        refreshImg.clearAnimation();
        playImg.setEnabled(true);
    }

    /**
     * 播放音乐
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (null != pv && isPrepared) {
            pv.start();
        }
    }

    /**
     * 停止音乐
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (null != pv) {
            pv.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != pv) {
            // 释放MedioPlay空间
            pv.stop();
        }
    }

}

