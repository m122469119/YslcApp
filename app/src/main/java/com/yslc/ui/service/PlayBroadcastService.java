package com.yslc.ui.service;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Administrator on 2016/5/10.
 */
public class PlayBroadcastService extends Service implements MediaPlayer.OnPreparedListener
    , MediaPlayer.OnCompletionListener{
    private MediaPlayer mp;
    public static final String BROADCAST_INTENT = "com.yslc.BROADCAST";

    private BroadcastConsole mBinder = new BroadcastConsole();

    /**
     * 准备成功监听
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mBinder.start();
    }

    /**
     * 播放完成监听
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {//播放完成监听
        notifyNext();
    }

    /**
     * 使用广播通知下一首
     */
    private void notifyNext() {
        //TODO 通知下一首
        Intent intent = new Intent(BROADCAST_INTENT);
        intent.putExtra("style","next");
        sendBroadcast(intent);
    }

    /**
     * Binder类
     */
    public class BroadcastConsole extends Binder {
        /**
         * 播放
         * @param radioUrl 播放地址
         */
        public void play(String radioUrl) {//根据地址播放
            try{
                if(mp.isPlaying()){
                    mp.stop();
                }
                mp.reset();
                mp.setDataSource(radioUrl);
                mp.prepareAsync();
//                mBinder.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         * 继续播放
         */
        public void start(){//继续播放
            if(null != mp && !mp.isPlaying()){
                mp.start();
                notifyPlay();
            }
        }

        /**
         * 暂停
         */
        public void pause(){
            if(null != mp && mp.isPlaying()){
                mp.pause();
            }
        }

        /**
         * 判断是否正在播放
         * @return
         */
        public boolean isPlay(){
            return mp != null && mp.isPlaying();
        }

        /**
         * 停止并是否资源
         */
        public void stop(){
            if(mp != null){
                mp.stop();
                mp.release();
            }
        }
    }

    /**
     * 使用广播通知开始播放
     */
    private void notifyPlay() {
        Intent intent = new Intent(BROADCAST_INTENT);
        intent.putExtra("style", "play");
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMedia();//初始化播放器
    }

    /**
     * 初始化播放器
     * <p>设置监听器</p>
     */
    private void initMedia() {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinder.stop();
        mp = null;
    }
}
