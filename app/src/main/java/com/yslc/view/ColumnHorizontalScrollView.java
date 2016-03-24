package com.yslc.view;

import java.util.ArrayList;

import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.util.CommonUtil;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 自定义栏目类型横向滚动条
 *
 * @author HH
 */
public class ColumnHorizontalScrollView extends HorizontalScrollView {
    // item的最小宽度
    private static final int ITEM_MIN_WIDTH = 100;
    // 向右移动按钮的宽度
    private int rightBtnWidth = 35;
    private Context mContext;
    private RadioGroup radioGroup;
    private int dataSize = 0;
    private int mItemWidth = 0;
    private int currentIndexTab = 0;
    private int mTitleWidth = 0;
    private ColorStateList textColor;

    public ColumnHorizontalScrollView(Context context) {
        super(context);
        init(context);
    }

    public ColumnHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColumnHorizontalScrollView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 设置右边栏目按钮的宽度
     */
    public void setRightBtnWidth(int rightBtnWidth) {
        this.rightBtnWidth = rightBtnWidth;
    }

    /**
     * 设置横向滚动条宽度
     *
     * @param titleWidth
     */
    public void setTitleWidth(int titleWidth) {
        this.mTitleWidth = titleWidth;
    }

    /**
     * 设置item字体颜色
     *
     * @param color
     */
    public void setTextColors(ColorStateList color) {
        textColor = color;
    }


    /**
     * 初始化操作
     */
    private void init(Context context) {
        mContext = context;
        mTitleWidth = CommonUtil.getScreenWidth(context);   //默认宽度为屏幕宽度
        textColor = ContextCompat.getColorStateList(context, R.color.column_textcolor_selector);
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * 计算栏目宽度
     */
    private void calcultorItemWidth(int dataSize) {
        // 若屏幕不能容纳所有item，则显示向右走的按钮
        mItemWidth = calcurtalItemWidth(mTitleWidth
                        - CommonUtil.dip2px(mContext, rightBtnWidth),
                CommonUtil.dip2px(mContext, ITEM_MIN_WIDTH), dataSize);
    }

    /**
     * 计算好item的合理宽度
     * <p>
     * item的最小宽度为100
     *
     * @param titalWidth   全部item可以占用的宽度
     * @param minItemWidth 每个item的最小宽度
     * @param dataSize     数据的长度
     */
    private int calcurtalItemWidth(int titalWidth, int minItemWidth,
                                   int dataSize) {
        if (titalWidth / minItemWidth >= dataSize) {
            // 如果屏幕宽度刚好可以容纳下所有item
            return titalWidth / dataSize;
        }

        // 若屏幕宽度不能容纳所有的item，则计算合理的item宽度
        int goodItemSize = (int) Math.ceil((float) titalWidth / minItemWidth);
        return (titalWidth / goodItemSize);
    }

    /**
     * 设置栏目数据
     * <p>设置副标题</p>
     */
    public void setColumnData(ArrayList<ColnumBean> listTitle) {
        dataSize = listTitle.size();
        calcultorItemWidth(listTitle.size());
        radioGroup = (RadioGroup) getChildAt(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                mItemWidth, LayoutParams.MATCH_PARENT);

        // 动态添加子栏目
        for (int i = 0, len = listTitle.size(); i < len; i++) {
            RadioButton columnTextView = (RadioButton) View.inflate(mContext,
                    R.layout.controls_radiobutton, null);
            columnTextView.setTextColor(textColor);
            columnTextView.setText(listTitle.get(i).getName());
            columnTextView.setId(i);
            if (currentIndexTab == i) {
                columnTextView.setChecked(true);
            }

            radioGroup.addView(columnTextView, i, params);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0, len = group.getChildCount(); i < len; i++) {
                    if (group.getChildAt(i).getId() == checkedId) {
                        onSelecterCallback.onSelecterCallback(i);
                        break;
                    }
                }
            }
        });

    }

    /**
     * 选择Column里面的栏目
     */
    public void selectTab(int tab_postion) {
        // 上一次点击的item
        currentIndexTab = tab_postion;

        // 移动
        smoothScrollTo((currentIndexTab - 1) * mItemWidth, 0);

        // 维护选中
        radioGroup.check(radioGroup.getChildAt(tab_postion).getId());
    }

    /**
     * 点击下一个栏目按钮
     */
    public void goRight() {
        // 向右走
        if (currentIndexTab < dataSize - 1) {
            currentIndexTab++;
        } else {
            currentIndexTab = 0;
        }
        onSelecterCallback.onSelecterCallback(currentIndexTab);
    }

    /**
     * 选择完栏目后进行回调
     */
    private OnSelecterCallback onSelecterCallback;

    public void setOnSelecterCallback(OnSelecterCallback onSelecterCallback) {
        this.onSelecterCallback = onSelecterCallback;
    }

    public interface OnSelecterCallback {
        void onSelecterCallback(int selectPage);
    }

}
