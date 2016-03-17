package com.yslc.util;


import android.os.Handler;

/**
 * 计时器工具类
 * <p>设置计时时间以及回调函数，实现每隔一段时间做某件事</p>
 * <p>
 * Created by HH on 2016/1/23.
 */
public class TimerUtil {
    private OnTimerCallback onTimerCallback;
    private int time; //时间段
    private boolean isStart = false; //计时器是否已经启动
    private Handler handler = new Handler();

    public TimerUtil(int time) {
        this.time = time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable, time);

            if (null != onTimerCallback) {
                onTimerCallback.onTimer();
            }
        }
    };

    /**
     * 计时回调接口
     */
    public interface OnTimerCallback {
        void onTimer();
    }

    public void setOnTimerCallback(OnTimerCallback onTimerCallback) {
        this.onTimerCallback = onTimerCallback;
    }

    /**
     * 开始计时
     */
    public void startTimer() {
        if (!isStart) {
            if (handler == null) {
                handler = new Handler();
            }

            //没有启动计时器则启动
            handler.postDelayed(runnable, time);
        }

        isStart = true;
    }

    /**
     * 停止计时器
     */
    public void stopTimer() {
        if (isStart) {
            handler.removeCallbacks(runnable);
            isStart = false;
        }
    }

    /**
     * 销毁计时器
     */
    public void destroyTimer() {
        if (isStart) {
            handler.removeCallbacks(runnable);
            runnable = null;
            handler = null;
            isStart = false;
        }
    }

}
