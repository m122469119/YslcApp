package com.yslc.view;

import java.util.List;

import com.yslc.R;
import com.yslc.bean.AdBean;
import com.yslc.util.CommonUtil;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义指示器搭载ViewPager,可以监听滑动到最后一页
 *
 * @author HH
 */
public class BaseIndicator extends LinearLayout implements OnPageChangeListener {
    private ViewPager viewPager;
    private int length;
    private ImageView[] indicatorArr;
    private List<AdBean> titleList;
    private TextView titleTv;

    public BaseIndicator(Context context) {
        super(context);
    }

    public BaseIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 没有标题类型的滑动图片ViewPager
     *
     * @param viewPager
     * @param context
     */
    public void setViewPager(ViewPager viewPager, Context context) {
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this);
        this.length = ((BaseViewPagerAdapter) viewPager.getAdapter())
                .getRealLength();

        // 创建点指示器
        addIndicator(context);
    }

    /**
     * 有标题类型的滑动图片ViewPager
     *
     * @param viewPager
     * @param context
     */
    public void setViewPager(ViewPager viewPager, Context context,
                             List<AdBean> titleList, TextView titleTv) {
        this.viewPager = viewPager;
        this.titleList = titleList;
        this.titleTv = titleTv;
        length = titleList.size();
        this.viewPager.addOnPageChangeListener(this);

        // 创建点指示器
        addIndicator(context);
    }

    /**
     * 创建点指示器
     * <p>
     * 根据ViewPager长度循环创建指示点
     */
    private void addIndicator(Context context) {
        indicatorArr = new ImageView[length];
        // 图片大小以及间距
        int imgW = CommonUtil.dip2px(context, 15);
        int padding = CommonUtil.dip2px(context, 2);
        for (int i = 0; i < length; i++) {
            ImageView image = new ImageView(context);
            image.setLayoutParams(new LayoutParams(imgW, imgW));
            image.setPadding(padding, 0, padding, 0);
            if (i == 0) {
                image.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.banner_dian_focus));
            } else {
                image.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.banner_dian_blur));
            }
            indicatorArr[i] = image;
            addView(image);
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }

    @Override
    public void onPageScrolled(int position, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < length; i++) {
            if (i == position % length) {
                // 滑动到当前点，更改指示点背景,更改title
                indicatorArr[i].setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.banner_dian_focus));
                if (null != titleList && null != titleTv) {
                    titleTv.setText(titleList.get(i).getTitle());
                }
            } else {
                indicatorArr[i].setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.banner_dian_blur));
            }
        }
    }

}
