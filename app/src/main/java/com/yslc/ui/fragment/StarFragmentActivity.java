package com.yslc.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.data.service.NewModelService;
import com.yslc.data.service.StarModelService;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.ui.base.BaseFragment;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;

import java.util.ArrayList;

/**
 * 明星FragmentActivity
 * <p>明星选卡</p>
 * <p>大多数方法雷同VedioFragmentActivity,所以不加注释了</p>
 * @author HH
 */
public class StarFragmentActivity extends ViewPagerFragment  {
    private StarModelService starModelService;

    @Override
    protected void getTitle() {
        starModelService = new StarModelService(getContext());
        starModelService.getStarColumnData(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                ArrayList<ColnumBean> listTitle = (ArrayList<ColnumBean>) data;
                if (listTitle.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    loadView.setStatus(LoadView.SUCCESS);
                    // 设置栏目数据
                    mColumnView.setColumnData(listTitle);
                    // 显示第一个类型的视频列表
                    initViewPager(listTitle);
                }
            }

            @Override
            public <T> void failer(T data) {
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }


    @Override
    protected Fragment createFragment(int position) {
        return new StarFragment();
    }
}
