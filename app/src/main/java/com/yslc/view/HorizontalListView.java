package com.yslc.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 自定义支持横向滚动的ListView
 *
 * @author http://wenku.baidu.com/view/401fbf92dd88d0d233d46afe.html
 */
public class HorizontalListView extends ListView {

    /**
     * 手势
     */
    private GestureDetector mGesture;
    /**
     * 列头
     */
    private LinearLayout mListHead;
    /**
     * 偏移坐标
     */
    private int mOffset = 0;
    /**
     * 屏幕宽度
     */
    private int screenWidth;

    /**
     * 构造函数
     */
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGesture = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                return false;
            }

            /** 滚动 */
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                synchronized (HorizontalListView.this) {
                    int moveX = (int) distanceX;
                    int curX = mListHead.getScrollX();
                    int scrollWidth = getWidth();
                    int dx = moveX;
                    // 控制越界问题
                    if (curX + moveX < 0)
                        dx = 0;
                    if (curX + moveX + getScreenWidth() > scrollWidth)
                        dx = scrollWidth - getScreenWidth() - curX;

                    mOffset += dx;
                    // 根据手势滚动Item视图
                    for (int i = 0, j = getChildCount(); i < j; i++) {
                        View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
                        if (child.getScrollX() != mOffset)
                            child.scrollTo(mOffset, 0);
                    }
                    mListHead.scrollBy(dx, 0);
                }
                requestLayout();
                return true;
            }
        });
    }

    /**
     * 分发触摸事件
     */
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mGesture.onTouchEvent(ev);
    }

    /**
     * 获取屏幕可见范围内最大屏幕
     *
     * @return screenWidth
     */
    private int getScreenWidth() {
        if (screenWidth == 0) {
            screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            if (getChildAt(0) != null) {
                screenWidth -= ((ViewGroup) getChildAt(0)).getChildAt(0)
                        .getMeasuredWidth();
            } else if (mListHead != null) {
                // 减去固定第一列
                screenWidth -= mListHead.getChildAt(0).getMeasuredWidth();
            }
        }
        return screenWidth;
    }

}