package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.NewModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.bean.ColnumBean;
import com.yslc.view.LoadView;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.LoadView.OnTryListener;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;

/**
 * 资讯Fragment
 * 获取所有咨讯标题以及Id
 * 使用ViewPager装载，包括不同类型的Fragment
 * 传递不同类型的咨讯Id给Framgent，Fragment通过Id获取资讯内容
 *
 * @author HH
 */
public class NewFragmentActivity extends ViewPagerFragment {
    private NewModelService newModelService;//业务类

    /**
     * 获取咨讯栏目数据
     */
    protected void getTitle() {
        newModelService = new NewModelService(getContext());
        newModelService.getColumnData("C879A54D-7605-4619-A0F8-3F2516D87C05", new GetDataCallback() {
            /**
             * 成功获取栏目数据
             * <p>设置副标题栏</p>
             * <p>根据数据初始化ViewPager</p>
             * @param data 栏目标题列表
             * @param <T>
             */
            @Override
            public <T> void success(T data) {
                ArrayList<ColnumBean> list = (ArrayList<ColnumBean>) data;
                if (list.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    loadView.setStatus(LoadView.SUCCESS);
                    // 设置栏目数据
                    mColumnView.setColumnData(list);
                    // 初始化Fragments
                    initViewPager(list);
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
        if (position == 0) {//快讯和头版头条布局不同一般，所以单独创建
            return  new NewFragmentFast();
        } else if (position == 1) {
            return new NewFragmentOne();
        } else {
            return  new NewFragmentTwo();
        }
    }

}
