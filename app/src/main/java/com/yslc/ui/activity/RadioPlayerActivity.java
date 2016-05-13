package com.yslc.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.R;
import com.yslc.bean.RadioBean;
import com.yslc.ui.base.BaseActivity;
import com.yslc.ui.service.PlayBroadcastService;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;

import org.json.JSONObject;

/**
 * 广播播放Activity(直播)
 * <p>股市广播Activity</p>
 * @author HH
 */
public class RadioPlayerActivity extends BaseActivity implements
        OnClickListener {
    private CheckBox playImg;
    private ImageButton  refreshImg;
    private Animation animation;
    private PlayBroadcastService.BroadcastConsole console;
    private BroadcastReceiver receiver;
    private boolean isOnline = false;//是否直播
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            console = (PlayBroadcastService.BroadcastConsole)service;//
            getData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 内部类，广播监听器
     */
    public class LocateReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String style = intent.getStringExtra("style");
            if(style.equals("play")){//开始播放广播（更新UI）
                startPlay();
            }else if(style.equals("next")){//播放完成广播
                if(isOnline){
                    getData();//下一首
                }else {
                    finish();
                }
            }
        }
    }
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
        mode = (RadioBean)getIntent().getSerializableExtra("RadioBean");
        if(null == mode){
            isOnline = true;
        }else{
            isOnline = false;
            //股市重播
            ((TextView) findViewById(R.id.titleText)).setText(getString(R.string.interactives));
        }

        ViewUtil.TranslucentStatusBar(this);//状态栏透明

        findViewById(R.id.backBtn).setOnClickListener(this);//返回
        playImg = (CheckBox) findViewById(R.id.paly_pouse);//播放
        playImg.setOnClickListener(this);
        refreshImg = (ImageButton) findViewById(R.id.refreshBtn);//刷新
        refreshImg.setOnClickListener(this);
        //关联animation(刷新旋转）
        animation = AnimationUtils.loadAnimation(this, R.anim.load_progress_anim);
        //播放设置
        //开启服务
        Intent bindIntent = new Intent(this, PlayBroadcastService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        //注册监听器
        registerReceiver();

    }

    /**
     * 注册监听器
     */
    private void registerReceiver() {
        receiver = new LocateReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayBroadcastService.BROADCAST_INTENT);
        registerReceiver(receiver, intentFilter);
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
                if (playImg.isChecked()) {//当前为播放状态
                    console.pause();
                } else {
                    console.start();
                }
                break;

            case R.id.refreshBtn:
                if (console.isPlay()) {
                    console.stop();
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

        refreshUI();

        if(!isOnline){//有数据证明是重播
            setData(mode);
            return;
        }
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
                        mode = ParseUtil.parseSingleRadioBean(json);
                        setData(mode);
                    }
                });

    }

    private RadioBean mode;

    /**
     * 刷新是的ui设置
     */
    private void refreshUI() {
        refreshImg.startAnimation(animation);//为刷新按钮设置动画
        playImg.setClickable(false);
    }



    /**
     * 设置数据
     */
    private void setData(RadioBean mode) {
        // 设置头像和标题
        ImageLoader.getInstance().displayImage(mode.getRadioHostUrl(), (ImageView)
                findViewById(R.id.starImg), ViewUtil.getCircleOptions());
        ((TextView) findViewById(R.id.text_time)).setText(mode.getRadioName());
        ((TextView) findViewById(R.id.text_info)).setText("主持人:" + mode.getRadioHost());

        refreshImg.setEnabled(true);

        console.play(mode.getRadioUrl());
    }


    /**
     * 播放音乐
     */
    @Override
    protected void onResume() {
        super.onResume();

//        if (null != pv && isPrepared) {
//            pv.start();
//        }
    }

    /**
     * 停止音乐
     */
    @Override
    protected void onPause() {
        super.onPause();

//        if (null != pv && pv.isPlay()) {
//            pv.pause();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//注销广播监听
        unbindService(connection);//解绑服务
//        if (null != pv) {
//            // 释放MedioPlay空间
//            pv.stop();
//        }

    }

    /**
     * 开始播放时的ui设置
     * <p>停止刷新圆圈</p>
     * <p>改变播放按钮状态</p>
     */
    public void startPlay() {
        refreshImg.clearAnimation();
        // 可以进行播放暂停控制
        playImg.setClickable(true);
        playImg.setChecked(false);
    }

}
