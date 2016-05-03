package com.yslc.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.yslc.R;
import com.yslc.bean.ColnumBean;
import com.yslc.data.service.NewModelService;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.base.BaseFragment;
import com.yslc.view.ColumnHorizontalScrollView;
import com.yslc.view.ColumnHorizontalScrollView.OnSelecterCallback;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;

import java.util.ArrayList;

/**
 * 视频Fragment
 * 思路同咨讯Fragmetn,没有使用ViewPager装载子Fragment
 * 此Fragment跟NewFragmentActivity太过相识，没有注释的方法请参考NewFragmentActivity同类方法
 * @author HH
 */
public class VedioFragmentActivity extends ViewPagerFragment {


    @Override
    protected void getTitle() {
        NewModelService service = new NewModelService(getContext());
        service.getColumnData("2C430AF1-FD0E-433D-B40D-F51BDB99FEC0", new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                ArrayList<ColnumBean> listTitle = new ArrayList<ColnumBean>();
                listTitle = (ArrayList<ColnumBean>) data;
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
        return new VideoFragment();
    }
}
