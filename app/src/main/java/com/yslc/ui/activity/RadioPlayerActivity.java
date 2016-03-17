package com.yslc.ui.activity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.bean.RadioBean;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.RadioModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.PlayerUtil;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.PlayerUtil.OnPlayListener;
import com.yslc.util.ViewUtil;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 广播播放Activity(直播)
 *
 * @author HH
 */
public class RadioPlayerActivity extends BaseActivity implements
        OnClickListener, OnPlayListener, TimerUtil.OnTimerCallback {
    private PlayerUtil pv;
    private ImageButton playImg, refreshImg;
    private Animation animation;
    private boolean isPlay = true;
    private TimerUtil timerUtil;
    private RadioModelService radioModelService;
    private boolean isPrepared = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_player;
    }

    @Override
    protected void initView() {
        ViewUtil.TranslucentStatusBar(this);

        findViewById(R.id.backBtn).setOnClickListener(this);
        playImg = (ImageButton) findViewById(R.id.paly_pouse);
        playImg.setOnClickListener(this);
        refreshImg = (ImageButton) findViewById(R.id.refreshBtn);
        refreshImg.setOnClickListener(this);
        animation = AnimationUtils.loadAnimation(this, R.anim.load_progress_anim);

        pv = new PlayerUtil();
        pv.setOnPlayListener(this);
        timerUtil = new TimerUtil(0);
        timerUtil.setOnTimerCallback(this);
        radioModelService = new RadioModelService(this);

        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                onFinishActivity();
                break;

            case R.id.paly_pouse:
                if (isPlay) {
                    // 原来是播放的，改为暂停
                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_play));
                    pv.pause();
                    isPlay = false;
                } else {
                    // 原来是暂停的，改为播放
                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pause));
                    pv.start();
                    isPlay = true;
                }
                break;

            case R.id.refreshBtn:
                if (!isPlay) {
                    // 原来是暂停的，改为播放
                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pause));
                    isPlay = true;
                }

                // 重新刷新
                getData();
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (!CommonUtil.isNetworkAvalible(this)) {
            ToastUtil.showMessage(this, HttpUtil.NO_INTERNET_INFO);
            return;
        }

        refreshImg.startAnimation(animation);
        playImg.setEnabled(false);
        radioModelService.getRadioData(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                setData((RadioBean) data);
            }

            @Override
            public <T> void failer(T data) {
                refreshImg.clearAnimation();
                ToastUtil.showMessage(RadioPlayerActivity.this, data.toString());
            }
        });
    }

    @Override
    public void onTimer() {
        // 再次获取数据
        getData();
    }

    /**
     * 设置数据
     */
    private void setData(RadioBean mode) {
        // 设置头像和标题
        ImageLoader.getInstance().displayImage(mode.getRadioHostUrl(), (ImageView) findViewById(R.id.starImg),
                ViewUtil.getCircleOptions());
        ((TextView) findViewById(R.id.text_time)).setText(mode.getRadioName());
        ((TextView) findViewById(R.id.text_info)).setText("主持人:" + mode.getRadioHost());

        playImg.setEnabled(true);
        refreshImg.setEnabled(true);

        if (!pv.isPlay()) {
            // 开始播放广播
            pv.playUrl(mode.getRadioUrl());
        } else {
            startPlay();
        }

        startTimeThread(Integer.parseInt(mode.getRadioTime()));
    }

    private void startTimeThread(int time) {
        // 下一次访问定时
        time = time < 0 ? 10000 : time * 1000;
        timerUtil.stopTimer();
        timerUtil.setTime(time);
        timerUtil.startTimer();
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

        if (null != pv && pv.isPlay()) {
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

        timerUtil.destroyTimer();
    }

    /**
     * 播放回调
     */
    @Override
    public void startPlay() {
        isPrepared = true;
        refreshImg.clearAnimation();
        // 可以进行播放暂停控制
        playImg.setEnabled(true);
    }

}
