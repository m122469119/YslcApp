package com.yslc.view;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.support.v4.content.ContextCompat;

import com.yslc.R;
import com.yslc.bean.MinuteInfo;
import com.yslc.util.KChartUtil;

import java.util.List;

/**
 * 分时图View
 *
 * @author HH
 */
public class StocyMinutesView extends StocyBaseView {
    // 每一天数据所占的宽度，每天均线为一个点，所以点的坐标为正中间的位置
    private static final float COLLTOR_PILLARSCHART = 0.3f;
    private float PER;
    private float per16, per26, per46, per56;
    private List<MinuteInfo> minuteInfos;
    private double highPrice;// 最高价
    private double lowPrice;// 最低价
    private double maxCount;// 最高的成交总手数
    private int[] perXPoint = new int[NUM];
    private PathEffect effects;
    private double heightScale;

    /**
     * 绘制k线图，分时图的基本布局
     */
    @Override
    protected void drawKChatBackGround() {
        LineGrayPaint.setPathEffect(effects);//线段类型（虚线，实现，散离等）
        // 画上面的虚线
        Path path = new Path();
        int y = kChartTop;
        path.moveTo(left, y);//左上角
        path.lineTo(right, y);//右上角
        String text = getPriceText(highPrice);//最高价
        mCanvas.drawText(text, left, y + textHeight * 2, textGrayPaint);//画最高价
        double max = highPrice - lowPrice;//最大差价
        if (max > 10) {
            // 分成四等分,画中间的三根虚线
            int n = 4;
            double sper = (highPrice - lowPrice) / 4;// 每一等分的价格宽度
            for (int i = 1; i < n; i++) {
                y = i * ((KChartbottom - kChartTop) / n) + kChartTop;
                path.moveTo(left, y);
                path.lineTo(right, y);
                text = getPriceText(highPrice - i * sper);
                mCanvas.drawText(text, left, y + textHeight / 2, textGrayPaint);
            }
        } else {
            // 分成两等分, 画中间的虚线
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
        // 画等分的虚线和下面的日期
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
     * 下面的柱形图
     */
    @Override
    protected void drawPillarsChart(int flag) {
        //画三条虚线
        Path path = new Path();
        path.moveTo(left, pillarsChartTop);
        path.lineTo(right, pillarsChartTop);
        path.moveTo(left, (pillarsChartTop + pillarsChartbottom) / 2);
        path.lineTo(right, (pillarsChartTop + pillarsChartbottom) / 2);
        mCanvas.drawPath(path, LineGrayPaint);

        // 上中下的成交手数值
        mCanvas.drawText(getPriceText(maxCount / 10000) + "(万手)", left, pillarsChartTop + textHeight / 2, textGrayPaint);
        mCanvas.drawText(getPriceText(maxCount / 2 / 10000), left, (pillarsChartbottom + pillarsChartTop) / 2 + textHeight / 2, textGrayPaint);

        float pStart = left;
        float pStartY;
        double heightScale = (pillarsChartbottom - pillarsChartTop) / maxCount;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        paint.setAlpha(150);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.titleBg));
        for (MinuteInfo info : minuteInfos) {
            pStart += per26 - COLLTOR_PILLARSCHART;
            pStartY = (float) (pillarsChartTop + (maxCount - info.getVolume()) * heightScale);
            mCanvas.drawRect(pStart, pStartY, pStart + per46 + COLLTOR_PILLARSCHART, pillarsChartbottom, paint);
            pStart += per46 + COLLTOR_PILLARSCHART;
        }
    }

    /**
     * 分时图
     * <p>
     * 分时图显示在区域的三分之二处
     */
    @Override
    public void drawHoursChart() {
        float cLeft = left;
        Path path = new Path();
        Path avgPath = new Path();
        path.moveTo(cLeft, KChartbottom - 1);
        avgPath.moveTo(cLeft + per26, (int) (kChartTop + (highPrice - minuteInfos.get(0).getAvgPrice()) * heightScale));
        int position = 0;
        int perPointX = perXPoint[position];// 记录第一条垂直虚线的x坐标
        float textWidth;
        for (MinuteInfo info : minuteInfos) {
            cLeft += per16;
            path.quadTo(cLeft, (int) (kChartTop + (highPrice - info.getNow()) * heightScale), cLeft + per26, (int) (kChartTop + (highPrice - info.getNow()) * heightScale));
            avgPath.quadTo(cLeft, (int) (kChartTop + (highPrice - info.getAvgPrice()) * heightScale), cLeft + per26, (int) (kChartTop + (highPrice - info.getAvgPrice()) * heightScale));
            //path.lineTo(cLeft + per26, (int) (kChartTop + (highPrice - info.getNow()) * heightScale));
            //avgPath.lineTo(cLeft + per26, (int) (kChartTop + (highPrice - info.getAvgPrice()) * heightScale));
            if (cLeft >= perPointX && position != perXPoint.length - 1) {
                String text = KChartUtil.getMinute(info.getMinute());
                textWidth = textGrayPaint.measureText(text);
                mCanvas.drawText(text, perPointX - textWidth / 2, KChartbottom + textHeight + 5, textGrayPaint);
                perPointX = perXPoint[++position];
            }
            cLeft += per56;
        }
        Paint LinePaint = new Paint();
        int color = minuteInfos.get(minuteInfos.size() - 1).getColor();
        LinePaint.setColor(ContextCompat.getColor(getContext(), color));
        LinePaint.setAntiAlias(true);
        LinePaint.setStrokeWidth(1);
        LinePaint.setStyle(Style.STROKE);
        mCanvas.drawPath(path, LinePaint);
        LinePaint.setAlpha(50);
        LinePaint.setStyle(Style.FILL);
        path.lineTo(cLeft, KChartbottom - 1);
        mCanvas.drawPath(path, LinePaint);

        LinePaint.setAlpha(255);
        LinePaint.setColor(ContextCompat.getColor(getContext(), R.color.stocyHAvg));
        LinePaint.setStyle(Style.STROKE);
        mCanvas.drawPath(avgPath, LinePaint);

        //最后一点画大圆
        LinePaint.setColor(ContextCompat.getColor(getContext(), color));
        LinePaint.setStyle(Style.FILL);
        mCanvas.drawCircle(cLeft - per56 + per26, (int) (kChartTop + (highPrice - minuteInfos.get(minuteInfos.size() - 1).getNow()) * heightScale), 3, LinePaint);
    }

    /**
     * 更加手指移动的xy坐标，计算十字形的XY焦点坐标
     *
     * @param x：触摸点的x坐标
     * @param y：触摸点的y坐标
     * @return ： 新的坐标+数据下标
     */
    public float[] getXY(float x, float y) {
        float lefts = left;
        float x0;
        for (MinuteInfo info : minuteInfos) {
            lefts += per16;
            x0 = lefts + per26;
            if ((x0 - (per16 + per26) / 2 <= x && x <= x0) || (x >= x0 && x <= x0 + ((per56 + per26) / 2))) {
                return new float[]{x0, (float) (kChartTop + (highPrice - info.getNow()) * heightScale), minuteInfos.indexOf(info)};
            }
            lefts += per56;
        }

        return null;
    }

    public String getPriceText(double price) {
        return String.format("%.2f", price);
    }

    /**
     * 开始画
     */
    public void startDraw(List<MinuteInfo> info, double closePrice) {
        // 分时
        minuteInfos = info;

        new Thread() {
            public void run() {
                try {
                    synchronized (mSurfaceHolder) {
                        while (!isCreat) {
                            //只有Surface创建结束后才能进行绘制
                        }

                        mCanvas = mSurfaceHolder.lockCanvas();
                        clearCanvas();
                        perWidth = totalWidth / NUM;// 每一个格的宽度
                        PER = (float) totalWidth / 240; //共开市240分钟
                        per16 = PER * 0.166666666f;
                        per26 = PER * 0.333333333f;
                        per46 = PER * 0.666666666f;
                        per56 = PER * 0.833333333f;

                        highPrice = minuteInfos.get(0).getNow();
                        lowPrice = minuteInfos.get(0).getNow();
                        maxCount = minuteInfos.get(0).getVolume();
                        for (MinuteInfo info : minuteInfos) {
                            highPrice = KChartUtil.getMax(highPrice, info.getNow(), info.getAvgPrice());
                            lowPrice = KChartUtil.getMin(lowPrice, info.getNow(), info.getAvgPrice());
                            maxCount = Math.max(maxCount, info.getVolume());
                        }

                        double max = Math.max(Math.abs(highPrice - closePrice), Math.abs(lowPrice - closePrice));

                        highPrice = closePrice + max;
                        lowPrice = closePrice - max;

                        heightScale = (KChartbottom - kChartTop) / (highPrice - lowPrice);
                        drawChartType(StocyBaseView.DRAW_MIN_TYPE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private void inits() {
        effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
    }

    public StocyMinutesView(Context context) {
        super(context);
        inits();
    }

    public StocyMinutesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inits();
    }

    public StocyMinutesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inits();
    }

    @Override
    protected void drawMAChart() {
    }

    @Override
    protected void drawCrosshairsChart() {
    }

    @Override
    protected void drawTotalChart() {
    }

}
