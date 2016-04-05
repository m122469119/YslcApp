package com.yslc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yslc.R;
import com.yslc.bean.MinuteInfo;
import com.yslc.util.CommonUtil;
import com.yslc.util.KChartUtil;

/**
 * 走势图手势监听以及画长按画十字形
 * ●左右滑移动K线图
 * ●长按移动画十字形，并显示信息
 * ●多点缩放K线图，可放大或拉长K线图
 * <p>
 * Created by HH on 2015/12/31.
 */
public class CrossView extends View implements View.OnTouchListener, View.OnLongClickListener {
    public static final int VIEW_TYPE_H = 0X21; //分时图
    public static final int VIEW_TYPE_K = 0X22; //K线图
    private int currentViewType = VIEW_TYPE_H; //当前绘制图像的类型

    private static final int SPLID_PADDING = 60;  //两侧可以滑动的距离
    private static final int SCAN_LENTH = 20;  //多点缩放至少大于20个像素才可以触动缩放事件
    private static final int MOVE_LENTH = 20;  //移动大于20个像数才可以触动移动事件

    public static final int DRAW_TYPE_LONG = 0X11; //长按移动事件
    public static final int DRAW_TYPE_DRAW = 0X12; //短按移动事件
    public static final int DRAW_TYPE_SCAN = 0X13; //多点缩放事件
    public static final int DRAW_TYPE_NONE = 0X14; //无事件
    private int currentDrawType = DRAW_TYPE_NONE;  //当前的移动事件

    private boolean isLongClick = false;
    private float x = -2, y = -2;//十字控件的x,y 坐标
    private int sceenWidth;
    private Paint paint;
    private float beforeLenght;  //两个触控点的距离

    public CrossView(Context context) {
        super(context);
        init();
    }

    public CrossView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CrossView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     * <p>初始化画笔，设置点击事件</p>
     */
    private void init() {
        sceenWidth = CommonUtil.getScreenWidth(getContext());
        setOnTouchListener(this);
        setOnLongClickListener(this);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    /**
     * 设置view的类型
     * <p>分时图和k线图表现不一样</p>
     * @param currentViewType 类型
     */
    public void setViewType(int currentViewType) {
        this.currentViewType = currentViewType;
    }

    @Override
    public boolean onLongClick(View v) {
        if (currentDrawType == DRAW_TYPE_NONE) {
            //长按事件（移动显示每条信息的数据）
            isLongClick = true;
            currentDrawType = DRAW_TYPE_LONG;
            onDrawCallback.onLongMove(x, y);
            vibration();
        }

        return false;
    }

    /**
     * 长按震动
     */
    private void vibration() {
        //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 100};
        vibrator.vibrate(pattern, -1);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isLongClick = false;
                x = (int) event.getX();//记下坐标
                y = (int) event.getY();
                return false;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    // 多点缩放事件
                    isLongClick = true;
                    currentDrawType = DRAW_TYPE_SCAN;
                    // 获取两点的距离
                    beforeLenght = getDistance(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if ((int) event.getX() == x && (int) event.getY() == y) {
                    return false;
                }
                if ((x < SPLID_PADDING || x > sceenWidth - SPLID_PADDING) && !isLongClick) {
                    //左右两侧为父ViewPager的滑动事件
                    return false;
                }

                // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                getParent().requestDisallowInterceptTouchEvent(true);
                if (currentDrawType == DRAW_TYPE_NONE) {
                    //如果原来不是长按或者多点缩放，则修改模式为普通滑动事件
                    currentDrawType = DRAW_TYPE_DRAW;
                }
                move(event);
                return isLongClick;

            case MotionEvent.ACTION_UP:
                //清除画布（主要作用是清除十字）
                x = -1;
                y = -1;
                if (isLongClick) {
                    invalidate();
                    isLongClick = false;
                    onDrawCallback.onUp();
                }

                //设置当前模式为无事件
                currentDrawType = DRAW_TYPE_NONE;
                break;
        }

        return isLongClick;
    }

    /**
     * 移动事件处理
     */
    private void move(MotionEvent event) {
        if (currentDrawType == DRAW_TYPE_LONG) {
            //长按移动事件
            onDrawCallback.onLongMove((int) event.getX(), (int) event.getY());
        } else if (currentDrawType == DRAW_TYPE_DRAW) {
            //短按移动事件
            int currentX = (int) event.getX();
            int currentY = (int) event.getY();
            if (Math.abs(currentX - x) > MOVE_LENTH) {
                //左右移动事件回调
                onDrawCallback.onMove((int) (currentX - x));
                x = currentX;
                y = currentY;
            }
        } else if (currentDrawType == DRAW_TYPE_SCAN) {
            //多点移动缩放事件
            float currentDistance = getDistance(event);
            if (Math.abs(currentDistance - beforeLenght) > SCAN_LENTH) {
                //多点缩放事件回调
                onDrawCallback.onScan((int) (currentDistance - beforeLenght));
                beforeLenght = currentDistance;
            }

        }
    }

    /**
     * 获取两个触控点的距离
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
     * 长按移动事件
     */
    private MinuteInfo info;

    public <T> void longClickMove(T bean, float x, float y) {
        if (currentViewType == VIEW_TYPE_H) {
            info = (MinuteInfo) bean;
        }

        //长按移动事件
        this.x = x;
        this.y = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (x == -1) {
            clearCanvas(canvas);
            return;
        }

        drawTen(canvas);

        if (currentViewType == VIEW_TYPE_H && null != info) {
            int panelWidth = CommonUtil.dip2px(getContext(), 90);
            //分时图绘制
            int tempx0;
            if (x > sceenWidth / 2) {
                tempx0 = 0;
            } else {
                tempx0 = sceenWidth - panelWidth;
            }
            //绘制矩形背景
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.greyTrans3));
            canvas.drawRect(tempx0, y, tempx0 + panelWidth, y + calHeight() * 5, paint);

            //绘制文本
            float tempy0 = y + calHeight() + calHeight() / 3;
            paint.setTextSize(CommonUtil.sp2px(getContext(), 13));
            paint.setColor(Color.WHITE);
            canvas.drawText(KChartUtil.getMinute(info.getMinute()), tempx0 + 10, tempy0, paint);
            paint.setColor(ContextCompat.getColor(getContext(), info.getColor()));
            tempy0 += calHeight();
            canvas.drawText(info.getNow() + "", tempx0 + 10, tempy0 + 5, paint);
            tempy0 += calHeight();
            canvas.drawText(info.getStocyGains() + "  " + info.getStocyAs(), tempx0 + 10, tempy0 + 5, paint);
            paint.setColor(Color.YELLOW);
            tempy0 += calHeight();
            canvas.drawText(KChartUtil.paranDouble(info.getVolume()), tempx0 + 10, tempy0 + 5, paint);
        }
    }

    /**
     * 计算文本高度(每个文本加多了10，作为padding)
     */
    private float calHeight() {
        //计算文本高
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (fm.bottom - fm.top) / 2 + 10;
    }

    /**
     * 手指长按移动画十字形
     */
    private void drawTen(Canvas canvas) {
        //画原点
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 3, paint);


        //横线和竖线
        paint.setColor(ContextCompat.getColor(getContext(), R.color.titleBg));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(0, y, getWidth(), y, paint);
        canvas.drawLine(x, 0, x, getHeight(), paint);
    }

    /**
     * 清空画布
     */
    public void clearCanvas(Canvas canvas) {
        if (null != canvas) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(null);
        }
    }

    private OnDrawCallback onDrawCallback;

    public void setOnDrawCallback(OnDrawCallback onDrawCallback) {
        this.onDrawCallback = onDrawCallback;
    }

    /**
     * 手势回调
     */
    public interface OnDrawCallback {
        /**
         * 长按移动事件
         */
        void onLongMove(float x, float y);

        /**
         * 短按移动事件
         *
         * @param direction ： 向左移动还是向右移动
         */
        void onMove(int direction);

        /**
         * 多点缩放事件
         *
         * @param direction ： 放大还是缩小
         */
        void onScan(int direction);

        /**
         * 抬起事件
         */
        void onUp();
    }

}
