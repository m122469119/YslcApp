package com.yslc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * 自定义股市行情View基类
 * <p>
 * 定义边界大小，定义绘图顺序以及类型
 *
 * @author HH
 */
public abstract class StocyBaseView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {
    public static final int TEXT_SIZE = 18;
    public static final int DRAW_MIN_TYPE = 0X01;  //画分时图
    public static final int DRAW_K_TYPE = 0X02;    //画K线图
    protected SurfaceHolder mSurfaceHolder;
    protected Canvas mCanvas;
    protected int left, right, kChartTop, KChartbottom;//走势图边界想
    protected int pillarsChartTop, pillarsChartbottom;//柱形图边界想
    protected int totalWidth;
    protected static final int NUM = 4;
    protected int perWidth;
    protected Paint LineGrayPaint;
    protected Paint textGrayPaint;
    protected float textHeight;
    public boolean isCreat = false;

    public void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        //线条画笔
        LineGrayPaint = new Paint();
        LineGrayPaint.setColor(Color.GRAY);
        LineGrayPaint.setAntiAlias(true);
        LineGrayPaint.setStrokeWidth(1);
        LineGrayPaint.setStyle(Style.STROKE);

        //文本画笔
        textGrayPaint = new Paint();
        textGrayPaint.setColor(Color.GRAY);
        textGrayPaint.setAntiAlias(true);
        textGrayPaint.setTextSize(TEXT_SIZE);

        //计算文本高
        Paint.FontMetrics fm = textGrayPaint.getFontMetrics();
        textHeight = (fm.bottom - fm.top) / 2;
    }

    /**
     * 初始化K线图（分时图）的边界大小
     *
     * @param left：K线图（分时图）左
     * @param top：K线图（分时图）上
     * @param right：K线图（分时图）右
     * @param bottom：K线图（分时图）下
     */
    public void initBorder(int left, int top, int right, int bottom) {
        KChartbottom = bottom / 3 * 2 - 25;
        this.right = right;
        this.left = left;
        this.kChartTop = top;
        pillarsChartTop = KChartbottom + 65;
        pillarsChartbottom = bottom;
        totalWidth = right - left;
        perWidth = totalWidth / NUM;
    }

    /**
     * 画背景
     */
    private void drawBackgroud(int w, int h) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL);
        mCanvas.drawRect(new Rect(0, 0, w, h), paint);
    }

    /**
     * 画k线背景图 <br/>
     * 日k，周k，月k，分时都适用
     */
    protected abstract void drawKChatBackGround();

    /**
     * 均线图
     */
    protected abstract void drawMAChart();

    /**
     * 十字线
     */
    protected abstract void drawCrosshairsChart();

    /**
     * 柱形图
     */
    protected abstract void drawPillarsChart(int flag);

    /**
     * 柱形图成交量均线
     */
    protected abstract void drawTotalChart();

    /**
     * 分时图
     */
    protected abstract void drawHoursChart();

    @Override
    public void run() {
        try {
            int flag = mFlag;
            drawBackgroud(getWidth(), getHeight());
            drawKChatBackGround(); // 背景图
            if (flag == DRAW_MIN_TYPE) {
                // 分时
                drawHoursChart();
            } else if (flag == DRAW_K_TYPE) {
                // 均线
                drawMAChart();
                // k线
                drawCrosshairsChart();
                // 成交量均线
                drawTotalChart();
            }

            drawPillarsChart(flag); // 柱形图
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    int mFlag = 0;

    /**
     * 开始绘制图像
     *
     * @param flag：绘制图像的类型（分时，还是X线）
     */
    public synchronized void drawChartType(int flag) {
        this.mFlag = flag;
        new Thread(this).run();
    }

    public StocyBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public StocyBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StocyBaseView(Context context) {
        this(context, null, 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isCreat = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isCreat = false;
    }

}
