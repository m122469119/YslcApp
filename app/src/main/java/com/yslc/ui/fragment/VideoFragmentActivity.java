package com.yslc.ui.fragment;

import android.support.v4.app.Fragment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.bean.ColumnBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.view.LoadView;

import java.util.ArrayList;

/**
 * 视频Fragment
 * 思路同咨讯Fragmetn,没有使用ViewPager装载子Fragment
 * 此Fragment跟NewFragmentActivity太过相识，没有注释的方法请参考NewFragmentActivity同类方法
 * @author HH
 */
public class VideoFragmentActivity extends ViewPagerFragment {

    //参数不同
    @Override
    protected void getTitle() {
        //请求参数
        RequestParams params = new RequestParams();
        params.put("btid", HttpUtil.PARAMS_VEDIO_BTID);
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
        return new VideoFragment();
    }
}
