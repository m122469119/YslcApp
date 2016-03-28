package com.yslc.view;

import com.yslc.util.CommonUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 照片剪切
 * <p>自定义显示图片控件</p>
 * @author HH
 */
public class PhotoImageView extends ImageView {

    private Context mContext;
    private int mWidth;
    private int mHeight;

    public PhotoImageView(Context context) {
        super(context);
        init(context);
    }

    public PhotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 宽高
     */
    private void init(Context context) {
        this.mContext = context;
        int screenWidth = CommonUtil.getScreenWidth(mContext);
        mWidth = (screenWidth - CommonUtil.dip2px(mContext, 6)) / 3;
        mHeight = mWidth;
    }

    /**
     * 设置图片大小
     */
    public void setImageSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

}
