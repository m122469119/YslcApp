package com.yslc.view;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.yslc.R;
import com.yslc.bean.SingleStockInfo;
import com.yslc.data.service.StockService;
import com.yslc.util.KChartUtil;

import java.util.List;

/**
 * K线图
 *
 * @author HH
 */
public class StocyKView extends StocyBaseView {

    private List<SingleStockInfo> infos;//股票数据
    private float per; // 每一天数据所占的宽度，每天均线为一个点，所以点的坐标为正中间的位置
    private double highPrice, lowPrice, maxCount;// 最高价,最低价,最高的成交总手数
    private int[] perXPoint;//记录x坐标
    private PathEffect effects;
    private float per16, per26, perHalf, per46, per56;//数据宽度的1/6,2/6,一半等等
    private final Object mLock = new Object();
    private double heightScale;//单位价格的高度
    private float kChartTopTemp;//k线顶端（k线通常不超出这条线）

    /***
     * k线背景图
     * <p>5条打横虚线，打竖三条虚线</p>
     */
    @Override
    protected void drawKChatBackGround() {
        LineGrayPaint.setPathEffect(effects);
        Path path = new Path();

        // 画上面的虚线
        int y = kChartTop;
        path.moveTo(left, y);
        path.lineTo(right, y);
        String text = getPriceText(highPrice);//最高价格
        mCanvas.drawText(text, left, y + textHeight * 2, textGrayPaint);

        double max = highPrice - lowPrice;
        if (max > 10) {
            // 分成三等分,画中间的三根虚线
            int n = 4;
            double sper = (highPrice - lowPrice) / 4;// 每一等分的价格差
            for (int i = 1; i < n; i++) {
                y = i * ((KChartbottom - kChartTop) / n) + kChartTop;
                path.moveTo(left, y);
                path.lineTo(right, y);
                text = getPriceText(highPrice - i * sper);
                mCanvas.drawText(text, left, y + textHeight / 2, textGrayPaint);
            }
        } else {
            // 分成两等分,画中间的虚线
            y = (KChartbottom - kChartTop) / 2 + kChartTop;
            path.moveTo(left, y);
            path.lineTo(right, y);
            text = getPriceText(highPrice - (highPrice - lowPrice) / 2);
            mCanvas.drawText(text, left, y + textHeight / 2, textGrayPaint);
        }

        // 画下面的虚线
        y = KChartbottom;
        path.moveTo(left, y);
        path.lineTo(right, y);
        text = getPriceText(lowPrice);
        mCanvas.drawText(text, left, y - textHeight / 2, textGrayPaint);

        // 画左右等分的虚线和下面的日期
        int x;
        for (int i = NUM - 1; i > 0; i--) {
            x = left + perWidth * i;
            path.moveTo(x, kChartTop);
            path.lineTo(x, KChartbottom);
            perXPoint[i - 1] = x;
        }
        mCanvas.drawPath(path, LineGrayPaint);
    }

    /**
     * 画三条均线（五日均，十日均，二十日均）
     */
    @Override
    protected void drawMAChart() {
        // 画均线
        Path path5 = new Path();
        Path path10 = new Path();
        Path path20 = new Path();
        float maStart = left;
        float maStartY;
        //三条均线起点
        path5.moveTo(maStart, (float) (kChartTopTemp + (highPrice - infos.get(0)
                .getMaValue5()) * heightScale));
        path10.moveTo(maStart, (float) (kChartTopTemp + (highPrice - infos.get(0)
                .getMaValue10()) * heightScale));
        path20.moveTo(maStart, (float) (kChartTopTemp + (highPrice - infos.get(0)
                .getMaValue20()) * heightScale));

        // 每一天实际所占的数据是4/6，左右边距各1/6
        for (SingleStockInfo info : infos) {
            maStart += perHalf;//x等于数据宽度的中点
            //y等于实际数据位置
            maStartY = (float) (kChartTopTemp + (highPrice - info.getMaValue5()) * heightScale);
            //path5.quadTo(maStart - perHalf, (float) (kChartTop + (highPrice - info.getMaValue5()) * heightScale) + 10, maStart, maStartY);  贝塞尔曲线方式
            path5.lineTo(maStart, maStartY);

            maStartY = (float) (kChartTopTemp + (highPrice - info.getMaValue10()) * heightScale);
            //path10.quadTo(maStart - perHalf, (float) (kChartTop + (highPrice - info.getMaValue10()) * heightScale) + 10, maStart, maStartY);
            path10.lineTo(maStart, maStartY);

            maStartY = (float) (kChartTopTemp + (highPrice - info.getMaValue20()) * heightScale);
//            path20.quadTo(maStart - perHalf, (float) (kChartTop + (highPrice - info.getMaValue20()) * heightScale) + 10, maStart, maStartY);
            path20.lineTo(maStart, maStartY);

            maStart += perHalf;
        }
        //设置画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Style.STROKE);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.stocyM5));
        mCanvas.drawPath(path5, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.stocyM10));
        mCanvas.drawPath(path10, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.stocyM20));
        mCanvas.drawPath(path20, paint);
    }

    /**
     * 画阴阳线（即K线）
     * <p>
     */
    @Override
    protected void drawCrosshairsChart() {
        float cLeft = left;
        float startY;
        float stopY;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        int cTop = 0;
        float cRight;
        int cBottom = 0;
        int position = 0;
        int perPointX = perXPoint[position];// 记录第一条垂直虚线的x坐标
        String text;
        for (SingleStockInfo info : infos) {
            cLeft += per16;
            //画时间
            if (cLeft >= perPointX && position != perXPoint.length - 1) {
                // 恰好画到第一条垂直虚线的地方，需要画下面的日期
                text = info.getDate().substring(2);
                mCanvas.drawText(text, perPointX - textGrayPaint.measureText(text) / 2,
                        KChartbottom + textHeight + 5, textGrayPaint);
                perPointX = perXPoint[++position];
            }

            cRight = (cLeft + per46);//即六分之5处
            //通过数据颜色判断数据是上涨还是下跌
            if (info.getColor() == StockService.UP_COLOR) {
                // 股价涨
                cTop = (int) (kChartTopTemp + (highPrice - info.getOpen()) * heightScale);
                cBottom = (int) (kChartTopTemp + (highPrice - info.getClose()) * heightScale);
                paint.setStyle(Style.STROKE);
            } else if (info.getColor() == StockService.DOWN_COLOR) {
                // 股价跌
                cTop = (int) (kChartTopTemp + (highPrice - info.getClose()) * heightScale);
                cBottom = (int) (kChartTopTemp + (highPrice - info.getOpen()) * heightScale);
                paint.setStyle(Style.FILL);
            }
            startY = (int) (kChartTopTemp + (highPrice - info.getHigh()) * heightScale);
            stopY = (int) (kChartTopTemp + (highPrice - info.getLow()) * heightScale);
            paint.setColor(ContextCompat.getColor(getContext(), info.getColor()));
            mCanvas.drawRect(cLeft, cTop, cRight, cBottom, paint);
            if (startY < cBottom) {
                mCanvas.drawLine(cLeft + per26, startY, cLeft + per26, cBottom, paint);
            }
            mCanvas.drawLine(cLeft + per26, cTop, cLeft + per26, stopY, paint);
            cLeft += per56;//移到下一数据起点
        }
    }

    /**
     * 根据手指移动的xy坐标，计算十字形的XY焦点坐标
     *
     * @param x：触摸点的x坐标
     * @param y：触摸点的y坐标
     * @return ： 新的坐标 + 数据下标
     */
    public float[] getXY(float x, float y) {
        float cLeft = left;
        float x0;
        for (SingleStockInfo info : infos) {
            cLeft += per16;
            x0 = cLeft + per26;
            //x的范围在在3/12 到13/12 （x0在6/12)
            if ((x0 - (per16 + per26) / 2 < x && x < x0) || (x > x0 && x < x0
                    + ((per56 + per26) / 2))) {
                return new float[]{x0, (float) (kChartTopTemp +
                        (highPrice - info.getClose()) * heightScale), infos.indexOf(info)};
            }
            cLeft += per56;
        }
        return null;
    }

    /**
     * 柱形图画两条成交量均线（五日均，十日均）
     */
    @Override
    protected void drawTotalChart() {
        // 画均线
        Path path5 = new Path();
        Path path10 = new Path();
        double heightScale = (pillarsChartbottom - pillarsChartTop) / maxCount;//单位数据高度
        float maStart = left;
        float maStartY;
        //起点
        path5.moveTo(maStart, (float) (pillarsChartTop + (maxCount - infos.get(0)
                .getMaValue5()) * heightScale));
        path10.moveTo(maStart, (float) (pillarsChartTop + (maxCount - infos.get(0)
                .getMaValue10()) * heightScale));

        // 每一天实际所占的数据是4/6，左右边距各1/6
        for (SingleStockInfo info : infos) {
            maStart += perHalf;
            maStartY = (float) (pillarsChartTop + (maxCount - info.getTotalValue5())
                    * heightScale);
            path5.lineTo(maStart, maStartY);
            maStartY = (float) (pillarsChartTop + (maxCount - info.getTotalValue10())
                    * heightScale);
            path10.lineTo(maStart, maStartY);
            maStart += perHalf;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Style.STROKE);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.stocyM5));
        mCanvas.drawPath(path5, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.stocyM10));
        mCanvas.drawPath(path10, paint);
    }

    /**
     * 下面的柱形图
     */
    @Override
    protected void drawPillarsChart(int flag) {
        // 画两条条虚线
        Path path = new Path();
        path.moveTo(left, pillarsChartTop);
        path.lineTo(right, pillarsChartTop);
        path.moveTo(left, (pillarsChartTop + pillarsChartbottom) / 2);
        path.lineTo(right, (pillarsChartTop + pillarsChartbottom) / 2);
        mCanvas.drawPath(path, LineGrayPaint);

        // 上中的成交手数值
        mCanvas.drawText(getPriceText(maxCount / 10000) + "(万手)", left,
                pillarsChartTop + textHeight / 2, textGrayPaint);
        mCanvas.drawText(getPriceText(maxCount / 2 / 10000), left, (pillarsChartbottom
                + pillarsChartTop) / 2 + textHeight / 2, textGrayPaint);

        float pStart = left;
        float pStartY;
        double heightScale = (pillarsChartbottom - pillarsChartTop) / maxCount;//单位高度
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        for (SingleStockInfo info : infos) {
            pStart += per16;
            pStartY = (float) (pillarsChartTop + (maxCount - info.getTotalCount()) * heightScale);
            paint.setColor(ContextCompat.getColor(getContext(), info.getColor()));
            if (info.getColor() == R.color.stocyUp) {
                paint.setStyle(Style.STROKE);//红色设为空
            } else {
                paint.setStyle(Style.FILL);//绿色充满
            }
            mCanvas.drawRect(pStart, pStartY, pStart + per46, pillarsChartbottom, paint);
            pStart += per56;
        }
    }

    public String getPriceText(double price) {
        return String.format("%.2f", price);
    }

    /**
     * 开始画
     */
    public void startDraw(List<SingleStockInfo> infos) {
        this.infos = infos;

        new Thread() {
            @Override
            public void run() {
                synchronized (mLock) {
                    try {

                        while (!isCreat) {
                            //只有Surface创建结束后才能进行绘制
                        }

                        mCanvas = mSurfaceHolder.lockCanvas();
                        clearCanvas();
                        per = (float) totalWidth / infos.size();// 数据宽度
                        per16 = per * 0.166666667f;
                        per26 = per * 0.333333333f;
                        per46 = per * 0.666666667f;
                        per56 = per * 0.833333333f;
                        perHalf = per * 0.5f;
                        //初始化最高价格，最大交易手数
                        perWidth = totalWidth / NUM;// 每一格数据的额宽度
                        highPrice = infos.get(0).getHigh();// 最高价
                        lowPrice = infos.get(0).getLow();
                        maxCount = infos.get(0).getTotalCount();//交易手数
                        for (SingleStockInfo info : infos) {
                            highPrice = KChartUtil.getMax(highPrice, info.getHigh(),
                                    info.getMaValue5(), info.getMaValue10(), info.getMaValue20());
                            lowPrice = KChartUtil.getMin(lowPrice, info.getLow(),
                                    info.getMaValue5(), info.getMaValue10(), info.getMaValue20());
                            maxCount = KChartUtil.getMax(maxCount, info.getTotalCount(),
                                    info.getTotalValue5(), info.getTotalValue10());
                        }

                        //显示区域为十分之八，因此heightScale*0.8
                        heightScale = (KChartbottom - kChartTop) / (highPrice - lowPrice) * 0.8;
                        kChartTopTemp = kChartTop + (KChartbottom - kChartTop) / 10;//空出上面的百分之10
                        drawChartType(StocyBaseView.DRAW_K_TYPE);//开启线程绘图
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != mCanvas) {
                                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }

    /**
     * 清空画布
     */
    public void clearCanvas() {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
    }

    @Override
    protected void drawHoursChart() {

    }

    private void inits() {
        effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);//设置虚线
        perXPoint = new int[NUM];
    }

    public StocyKView(Context context) {
        super(context);
        inits();
    }

    public StocyKView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inits();
    }

    public StocyKView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inits();
    }

}
