package com.yslc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.yslc.util.CommonUtil;

/**
 * *
 * 这里你要明白几个方法执行的流程： 首先ImageView是继承自View的子类.
 * onLayout方法：是一个回调方法.该方法会在在View中的layout方法中执行，在执行layout方法前面会首先执行setFrame方法.
 * layout方法：
 * setFrame方法：判断我们的View是否发生变化，如果发生变化，那么将最新的l，t，r，b传递给View，然后刷新进行动态更新UI.
 * 并且返回ture.没有变化返回false.
 * <p>
 * invalidate方法：用于刷新当前控件
 */
public class CropImageView extends ImageView {
    //双击时间间隔
    private static final int CLICK_DOUBLE_TIME = 300;
    private Context context;

    // 屏幕宽高与当前图片宽高
    private int screen_W, screen_H;
    private int bitmap_W, bitmap_H;

    // 可拉伸的最大与最小尺寸
    private int MAX_W, MIN_W;

    // 当前图片上下左右坐标
    private int current_Top, current_Right, current_Bottom, current_Left;

    //相当于图片本身的位置和相当于屏幕的位置
    private int start_x, start_y, current_x, current_y;

    // 两触点距离
    private float beforeLenght, afterLenght;

    //模式 NONE：无 DRAG：拖拽. ZOOM:缩放
    private enum MODE {
        NONE, DRAG, ZOOM
    }

    // 默认模式
    private MODE mode = MODE.NONE;

    //两点触摸的系统时间
    private long fristTime = 0;

    /**
     * 构造方法 *
     */
    public CropImageView(Context context) {
        super(context);
        this.context = context;
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 设置显示图片
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        /** 获取图片宽高 **/
        bitmap_W = bm.getWidth();
        bitmap_H = bm.getHeight();

        /** 获取屏幕宽高 **/
        screen_W = CommonUtil.getScreenWidth(context);
        screen_H = CommonUtil.getScreenHeight(context);

        /** 图片最大以及最小的尺寸 **/
        MAX_W = bitmap_W * 2 + screen_W;
        MIN_W = screen_W / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * touch 事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /** 处理单点、多点触摸 **/
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;

            // 多点触摸
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;

            case MotionEvent.ACTION_UP:
                mode = MODE.NONE;
                break;
        }

        return true;
    }

    /**
     * 按下 *
     */
    private void onTouchDown(MotionEvent event) {
        /** 用户双击图像 **/
        long nextTime = System.currentTimeMillis();
        if (nextTime - fristTime <= CLICK_DOUBLE_TIME) {
            //进行缩放
            if (this.getWidth() == 2 * bitmap_W + screen_W) {
                //拉伸到屏幕宽度
                this.setPosition(0, (screen_H - bitmap_H) / 2, bitmap_W, (screen_H - bitmap_H) / 2 + bitmap_H);
            } else {
                //拉伸到最大
                this.setPosition(-bitmap_W, (screen_H - bitmap_H) / 2 - bitmap_H, screen_W + bitmap_W, (screen_H - bitmap_H) / 2 + 2 * bitmap_H);
            }
        }

        fristTime = nextTime;
        mode = MODE.DRAG;
        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();
        start_x = (int) event.getX();
        start_y = current_y - this.getTop();
    }

    /**
     * 两个手指 只能放大缩小 *
     */
    private void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mode = MODE.ZOOM;        //模式修改为缩放
            beforeLenght = getDistance(event);        // 获取两点的距离
        }
    }

    /**
     * 移动的处理 *
     */
    private void onTouchMove(MotionEvent event) {
        /** 处理拖动 **/
        if (mode == MODE.DRAG) {

            /** 获取相应的l，t,r ,b **/
            int left = current_x - start_x;
            int right = current_x + this.getWidth() - start_x;
            int top = current_y - start_y;
            int bottom = current_y - start_y + this.getHeight();

            /** 水平进行判断 **/
            if (left >= screen_W / 2) {
                left = screen_W / 2;
                right = screen_W / 2 + this.getWidth();
            }
            if (right <= screen_W / 2) {
                left = screen_W / 2 - this.getWidth();
                right = screen_W / 2;
            }

            /** 垂直判断 **/
            if (top >= screen_H / 2) {
                top = screen_H / 2;
                bottom = screen_H / 2 + this.getHeight();
            }

            if (bottom <= screen_H / 2) {
                top = screen_H / 2 - this.getHeight();
                bottom = screen_H / 2;
            }

            /** 设置图片位置 **/
            this.setPosition(left, top, right, bottom);

            /** 维护当前触点坐标 **/
            current_x = (int) event.getRawX();
            current_y = (int) event.getRawY();
        }

        /** 处理缩放 **/
        else if (mode == MODE.ZOOM) {

            afterLenght = getDistance(event);// 获取两点的距离

            float gapLenght = afterLenght - beforeLenght;// 变化的长度

            if (Math.abs(gapLenght) > 5f) {
                this.setScale(afterLenght / beforeLenght); // 求的缩放的比例
                beforeLenght = afterLenght;
            }
        }
    }

    /**
     * 实现处理拖动 *
     */
    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }

    /**
     * 获取两点的距离 *
     */
    private float getDistance(MotionEvent event) {
        float x = 0, y = 0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 处理缩放 *
     */
    private void setScale(float scale) {
        /** 缩放的水平距离与垂直距离 **/
        int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;
        int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;

        /** 放大 **/
        if (scale > 1 && this.getWidth() <= MAX_W) {
            current_Left = this.getLeft() - disX;
            current_Top = this.getTop() - disY;
            current_Right = this.getRight() + disX;
            current_Bottom = this.getBottom() + disY;

            this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
        }

        /** 缩小 **/
        else if (scale < 1 && this.getWidth() >= MIN_W) {
            current_Left = this.getLeft() + disX;
            current_Top = this.getTop() + disY;
            current_Right = this.getRight() - disX;
            current_Bottom = this.getBottom() - disY;

            /***
             * 在这里要进行缩放处理
             */
            // 上边越界
            if (current_Top >= screen_H / 2) {
                current_Top = screen_H / 2;
                current_Bottom = this.getBottom() - 2 * disY;
            }
            // 下边越界
            if (current_Bottom <= screen_H / 2) {
                current_Bottom = screen_H / 2;
                current_Top = this.getTop() + 2 * disY;
            }
            // 左边越界
            if (current_Left >= screen_W / 2) {
                current_Left = screen_W / 2;
                current_Right = this.getRight() - 2 * disX;
            }
            // 右边越界
            if (current_Right <= screen_W / 2) {
                current_Right = screen_W / 2;
                current_Left = this.getLeft() + 2 * disX;
            }

            this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
        }
    }

}
