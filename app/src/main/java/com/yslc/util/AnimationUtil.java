package com.yslc.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * 动画工具类
 * <p>
 * Created by Administrator on 2015/12/24.
 */
public class AnimationUtil implements Animation.AnimationListener {
    private OnMyAnimationEnd onAnimationEndLintener;

    public AnimationUtil(OnMyAnimationEnd onAnimationEndLintener) {
        this.onAnimationEndLintener = onAnimationEndLintener;
    }

    /**
     * 渐变动画
     *
     * @param alphaStart 起始渐变值
     * @param alphaEnd   结束渐变值
     * @param duration   动画时间
     * @return anim
     */
    public Animation getAlphaAnimation(Float alphaStart, Float alphaEnd, int duration) {
        AlphaAnimation anim = new AlphaAnimation(alphaStart, alphaEnd);
        anim.setDuration(duration);
        anim.setAnimationListener(this);
        return anim;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (null != onAnimationEndLintener) {
            onAnimationEndLintener.onMyAnimationEnd();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /**
     * 动画结束监听器
     */
    public interface OnMyAnimationEnd {
        void onMyAnimationEnd();
    }

}
