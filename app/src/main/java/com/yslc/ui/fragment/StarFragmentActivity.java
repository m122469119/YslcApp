package com.yslc.ui.fragment;

import android.support.v4.app.Fragment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yslc.bean.ColumnBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.view.LoadView;


import java.util.ArrayList;

/**
 * 明星FragmentActivity
 * <p>明星选卡</p>
 * <p>大多数方法雷同VedioFragmentActivity,所以不加注释了</p>
 * @author HH
 */
public class StarFragmentActivity extends ViewPagerFragment  {

    @Override
    protected void getTitle() {
        HttpUtil.get(HttpUtil.GET_STAR_TYPE, getContext(), null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        // 获取栏目成功并解析
                        ArrayList<ColumnBean> listTitle = ParseUtil
                                .parseColumnBean(arg0,"St_Id","St_Name");

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

                });
    }



    @Override
    protected Fragment createFragment(int position) {
        return new StarFragment();
    }
}
