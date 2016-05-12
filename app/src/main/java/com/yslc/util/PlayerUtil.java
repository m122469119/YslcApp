package com.yslc.util;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * 音频播放工具
 */
public class PlayerUtil implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer mp;
    private OnPlayListener onPlayListener;

    /**
     * 获取播放工具，初始化MediaPlayer
     */
    public PlayerUtil() {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    public interface OnPlayListener {
        void startPlay();
    }

    /**
     * 播放广播
     * <p>播放网络广播</p>
     * @param videoUrl 广播URL
     */
    public void playUrl(String videoUrl) {
        try {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.setDataSource(videoUrl);
            mp.prepareAsync();//播放流的话，不使用prepare
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 继续播放
     */
    public void start() {
        if (null != mp && !mp.isPlaying()) {
            mp.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (null != mp && mp.isPlaying()) {
            mp.pause();
        }
    }

    /**
     * 是否在播放
     */
    public boolean isPlay() {
        return mp != null && mp.isPlaying();
    }

    /**
     * 停止播放(释放空间)
     */
    public void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 回调停止刷新图片动画
        onPlayListener.startPlay();

        // 开始播放
        mp.start();//TODO 应该放到上面startplay
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //TODO 为什么不用
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {//播放流更新监听
    }
}
