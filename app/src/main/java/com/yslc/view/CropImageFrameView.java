package com.yslc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.yslc.util.CommonUtil;

/**
 * 裁剪头像矩形框
 * <p>
 * Created by HH on 2015/12/9.
 */
public class CropImageFrameView extends View {
    private static final float LINE_WIDTH = 2f;

    private Context context;
    private int squeW;
    private int squeH;
    private int screenW;
    private int screenH;
    private Paint paint;

    public CropImageFrameView(Context context) {
        super(context);
        this.context = context;
        initPaint();
    }

    public CropImageFrameView(Context context, AttributeSet attri) {
        super(context, attri);
        this.context = context;
        initPaint();
    }

    /**
     * 画笔设置
     */
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        screenW = CommonUtil.getScreenWidth(context);
        screenH = CommonUtil.getScreenHeight(context);

        //计算裁剪框的宽高
        squeW = screenW - 20;
        squeH = squeW;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** 画四个半透明矩形 **/
        paint.setColor(Color.BLACK);
        paint.setAlpha(150);
        paint.setStyle(Paint.Style.FILL);
        //上方矩形
        canvas.drawRect(0, 0, screenW, (screenH - squeH) / 2, paint);
        //左方矩形
        canvas.drawRect(0, (screenH - squeH) / 2, (screenW - squeW) / 2, (screenH + squeH) / 2, paint);
        //右方矩形
        canvas.drawRect((screenW + squeW) / 2, (screenH - squeH) / 2, screenW, (screenH + squeH) / 2, paint);
        //下方矩形
        canvas.drawRect(0, (screenH + squeH) / 2, screenW, screenH, paint);

        /** 画矩形框 **/
        paint.setColor(Color.BLUE);
        paint.setAlpha(225);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_WIDTH);
        canvas.drawRect((screenW - squeW) / 2, (screenH - squeH) / 2, (screenW + squeW) / 2, (screenH + squeH) / 2, paint);
    }

    /**
     * 获取矩形框的四个点的坐标
     * 返回left, top, right, bottom
     */
    public float[] getRectPoint() {
        return new float[]{(screenW - squeW) / 2 + LINE_WIDTH, (screenH - squeH) / 2 + LINE_WIDTH, (screenW + squeW) / 2 - LINE_WIDTH, (screenH + squeH) / 2 - LINE_WIDTH};
    }

}
