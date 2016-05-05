package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.bean.ColumnBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.view.LoadView;

/**
 * 资讯Fragment
 * 获取所有咨讯标题以及Id
 * 使用ViewPager装载，包括不同类型的Fragment
 * 传递不同类型的咨讯Id给Framgent，Fragment通过Id获取资讯内容
 *
 * @author HH
 */
public class NewFragmentActivity extends ViewPagerFragment {

    /**
     * 获取咨讯栏目数据
     */
    protected void getTitle() {
//        newModelService = new NewModelService(getContext());
        //请求参数
        RequestParams params = new RequestParams();
        params.put("btid", HttpUtil.PARAMS_INFO_BTID);
        HttpUtil.get(HttpUtil.GET_COLNUM, getActivity(), params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            // 获取失败
                            loadView.setStatus(LoadView.ERROR);
                        } else {
                            ArrayList<ColumnBean> list = ParseUtil.parseColumnBean(arg0);
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
