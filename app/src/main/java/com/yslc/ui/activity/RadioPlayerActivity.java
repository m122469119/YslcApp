package com.yslc.ui.activity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.bean.RadioBean;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.service.PlayBroadcastService;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.PlayerUtil;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.PlayerUtil.OnPlayListener;
import com.yslc.util.ViewUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * 广播播放Activity(直播)
 * <p>股市广播Activity</p>
 * @author HH
 */
public class RadioPlayerActivity extends BaseActivity implements
        OnClickListener, OnPlayListener, TimerUtil.OnTimerCallback {
    private PlayerUtil pv;
    private RadioButton playImg;
    private ImageButton refreshImg;
    private Animation animation;
    private boolean isPlay = true;
    private TimerUtil timerUtil;
    private boolean isPrepared = false;
//    private PlayBroadcastService.BroadcastConsole console;
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            console = (PlayBroadcastService.BroadcastConsole)service;
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_player;
    }

    /**
     * 初始化布局
     * <p>关联返回按钮，播放按钮，刷新按钮并监听</p>
     * <p>关联动画</p>
     * <p>播放工具类、计时器设置</p>
     * <p>实例化业务逻辑类</p>
     * <p>获取数据</p>
     */
    @Override
    protected void initView() {
        ViewUtil.TranslucentStatusBar(this);//状态栏透明

        findViewById(R.id.backBtn).setOnClickListener(this);//返回
        playImg = (RadioButton) findViewById(R.id.paly_pouse);//播放
        playImg.setOnClickListener(this);
        refreshImg = (ImageButton) findViewById(R.id.refreshBtn);//刷新
        refreshImg.setOnClickListener(this);
        //关联animation(刷新旋转）
        animation = AnimationUtils.loadAnimation(this, R.anim.load_progress_anim);
        //播放设置
        pv = new PlayerUtil();
        pv.setOnPlayListener(this);//播放回调
        //计时器设置
        timerUtil = new TimerUtil(0);
        timerUtil.setOnTimerCallback(this);
        //开启服务
//        Intent bindIntent = new Intent(this, PlayBroadcastService.class);
//        bindService(bindIntent,connection,BIND_AUTO_CREATE);
//        unbindService(connection);

        getData();
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                onFinishActivity();
                //TODO 询问是否停止播放
                break;

            case R.id.paly_pouse:
                if (isPlay) {
                    // 原来是播放的，改为暂停
                    playImg.setChecked(true);
//                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_play));
                    pv.pause();
                    isPlay = false;
                } else {
                    // 原来是暂停的，改为播放
//                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pause));
                    playImg.setChecked(false);
                    pv.start();
                    isPlay = true;
                }
                break;

            case R.id.refreshBtn:
                if (!isPlay) {
                    // 原来是暂停的，改为播放
//                    playImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pause));
                    playImg.setChecked(false);
                    isPlay = true;
                }

                // 重新刷新
                getData();
                break;
        }
    }

    /**
     * 获取数据
     * <p>获取数据成功则设置数据</p>
     */
    private void getData() {
        if (!CommonUtil.isNetworkAvalible(this)) {//判断网络
            ToastUtil.showMessage(this, HttpUtil.NO_INTERNET_INFO);
            return;
        }

        showGetDataView();

        HttpUtil.get(HttpUtil.PLAY_VEDIO, this, null,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject json) {
                        super.onFailure(arg0, json);
                        refreshImg.clearAnimation();
                        ToastUtil.showMessage(RadioPlayerActivity.this, "加载失败,请刷新");
//                        callback.failer("加载失败,请刷新");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        super.onSuccess(json);
                        //解析数据
                        RadioBean mode = ParseUtil.parseSingleRadioBean(json);
                        setData(mode);
                    }
                });

    }

    private void showGetDataView() {
        refreshImg.startAnimation(animation);//为刷新按钮设置动画
        playImg.setEnabled(false);//播放按钮
    }


    /**
     * 定时获取数据
     */
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
        } else {//已经在播放则停止刷新圆圈
            startPlay();
        }

        startTimeThread(Integer.parseInt(mode.getRadioTime()));
    }

    /**
     * 设置计时器时间，播放完成后，再次获取数据
     *
     * @param time
     */
    private void startTimeThread(int time) {
        // 下一次访问定时
        time = time < 0 ? 10000 : time * 1000;//3416*1000毫秒
        timerUtil.stopTimer();//停止计时
        timerUtil.setTime(time);//设置计时器时间
        timerUtil.startTimer();//开始计时
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

//        timerUtil.destroyTimer();
    }

    /**
     * 播放监听回调（注意不是开始播放的方法）
     * <p>停止刷新圆圈</p>
     * <p>改变播放按钮状态</p>
     */
    @Override
    public void startPlay() {
        isPrepared = true;
        refreshImg.clearAnimation();
        // 可以进行播放暂停控制
        playImg.setEnabled(true);
    }

}
