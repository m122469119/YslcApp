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
    protected int totalWidth;//总宽度
    protected static final int NUM = 4;//打竖四条虚线
    protected int perWidth;//每格宽度
    protected Paint LineGrayPaint;
    protected Paint textGrayPaint;
    protected float textHeight;
    public boolean isCreat = false;//surface创建的标志

    /**
     * 初始化
     * <p>surfaceHolder设置</p>
     * <p>初始化两个画笔</p>
     */
    public void init() {
        mSurfaceHolder = getHolder();//初始化SurfaceHolder
        mSurfaceHolder.addCallback(this);//添加回调

        //线条画笔
        LineGrayPaint = new Paint();
        LineGrayPaint.setColor(Color.GRAY);//灰色
        LineGrayPaint.setAntiAlias(true);//去齿
        LineGrayPaint.setStrokeWidth(1);//笔宽
        LineGrayPaint.setStyle(Style.STROKE);//空心画笔

        //文本画笔
        textGrayPaint = new Paint();
        textGrayPaint.setColor(Color.GRAY);
        textGrayPaint.setAntiAlias(true);
        textGrayPaint.setTextSize(TEXT_SIZE);
        //计算文本高
        Paint.FontMetrics fm = textGrayPaint.getFontMetrics();
        textHeight = (fm.bottom - fm.top) / 2;//文字2分之一的高度
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
        KChartbottom = bottom / 3 * 2 - 25;//k线图底部（三分之二-25）
        this.right = right;
        this.left = left;
        this.kChartTop = top;//k线图顶部
        pillarsChartTop = KChartbottom + 65;//柱状图顶部（k线图和柱状图隔了65来放数据）
        pillarsChartbottom = bottom;//柱状图底部
        totalWidth = right - left;//总宽度
        perWidth = totalWidth / NUM;
    }

    /**
     * 画背景
     * <p>根据长宽画白色背景图</p>
     * @param h 高
     * @param w 宽
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

    /**
     * runnable接口
     * <p>根据flag来重划分时图或k线图</p>
     */
    @Override
    public void run() {
        try {
            int flag = mFlag;
            drawBackgroud(getWidth(), getHeight());//画背景
            drawKChatBackGround(); // 背景图
            if (flag == DRAW_MIN_TYPE) {//分时图
                // 分时
                drawHoursChart();
            } else if (flag == DRAW_K_TYPE) {//k线图
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
     * <p>运行线程绘制</p>
     * @param flag：绘制图像的类型（分时，还是X线）
     */
    public synchronized void drawChartType(int flag) {
        this.mFlag = flag;
        new Thread(this).run();
    }

    //--------------三个构造函数------------------
    //xml创建调用且指定style
    public StocyBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();//初始化
    }
    //xml创建调用
    public StocyBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    //java代码创建调用
    public StocyBaseView(Context context) {
        this(context, null, 0);
    }

    //--------------一下是surface.callback回调方法-----------------
    /**
     * 更新改变时调用
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    /**
     * surface随window创建调用此方法
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isCreat = true;
    }

    /**
     * surface随window消亡调用此方法
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isCreat = false;
    }

}
