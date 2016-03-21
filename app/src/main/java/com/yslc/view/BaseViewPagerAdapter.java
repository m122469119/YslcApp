package com.yslc.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.bean.AdBean;

/**
 * 这是个viewPagerAdapter
 * <p>可以使用本地图片id设置图片</p>
 * <p>可使用url下载网络图片（imageLoader网络框架）</p>
 */
public class BaseViewPagerAdapter extends PagerAdapter {
    private List<ImageView> views;
    private ImageLoader imageLoader;
    private Context context;
    private int size;
    private boolean isForever;

    public BaseViewPagerAdapter(Context context) {
        this.context = context;
        views = new ArrayList<>();
        imageLoader = ImageLoader.getInstance();
    }

    /**
     * 设置网络资源图片
     *
     * @param netList
     * @param isForever 是否无限滑动
     */
    public void setNetImage(List<AdBean> netList, boolean isForever) {
        this.isForever = isForever;
        ImageView img;
        for (AdBean bean : netList) {
            img = new ImageView(context);
            img.setScaleType(ScaleType.CENTER_CROP);
            imageLoader.displayImage(bean.getImgUrl(), img);
            views.add(img);
        }
        size = views.size();
    }

    /**
     * 设置本地资源图片
     *
     * @param sdList 本地图片id
     * @param isForever 是否无限滑动
     */
    public void setSdImage(List<Integer> sdList, boolean isForever) {
        this.isForever = isForever;
        ImageView img;
        for (int resouce : sdList) {
            img = new ImageView(context);
            img.setScaleType(ScaleType.FIT_XY);
            img.setBackgroundResource(resouce);
            views.add(img);
        }
        size = views.size();
    }

    /**
     * 获取viewPager的实际view的长度
     *
     * @return
     */
    public int getRealLength() {
        return size;
    }

    @Override
    public int getCount() {
        return isForever ? Integer.MAX_VALUE : size;
    }

    /**
     * 判断出去的view是否等于进来的view 如果为true直接复用
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来就是position
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    /**
     * 创建一个view
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = views.get(position % size);
        if (null != view) {
            container.removeView(view);
            container.addView(view);
        }
        return view;
    }

}
