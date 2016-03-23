package com.yslc.ui.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.WebActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.NewBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 视频列表
 * 使用GridVie显示视频item
 * @author HH
 */
public class VideoFragment extends BaseFragment implements OnScrollListener,
        OnTryListener {
    private LoadView loadView;
    private GridView gridView;
    private QuickAdapter<NewBean> adapter;
    private SwipeRefreshLayout refreshableView;
    private ImageLoader imageLoader;
    private String colnumBeanId = null;
    private Context context;
    private ArrayList<NewBean> vedioLists;
    private int pageSize = 15;//页面大小
    private int pageIndex = 1;

    /**
     * 关联context
     * 获取副标题id
     * 实例化加载网络图片工具类
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getParentFragment().getActivity();
        colnumBeanId = getArguments().getString("id");
        imageLoader = ImageLoader.getInstance();
    }

    /**
     * 设置布局文件
     * 包含下拉刷新，正在加载圈圈，和GridView（视频列表）
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_video;
    }

    /**
     * 初始化布局
     * <p>实例化（视频）数据类</p>
     * <p>关联加载圈圈、视频内容GridView、下拉刷新并设置监听事件</p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);

        vedioLists = new ArrayList<>();//视频数据
        //加载圈圈
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //下拉刷新
        refreshableView = (SwipeRefreshLayout) views
                .findViewById(R.id.refreshable_view);
        // 设置卷内的颜色
        refreshableView.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //视频列表
        gridView = (GridView) views.findViewById(R.id.gridView);

        // 加载更多（设置监听器）
        listViewLoadmore();

        // 下拉刷新（监听器）
        listViewPush();
    }

    /**
     * 第一次进来加载
     * <p>获取数据</p>
     */
    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        if (loadView.setStatus(LoadView.LOADING)) {
            getData(1);
        }
    }

    /**
     * 获取数据
     * @param pageIndex 请求参数（当前页面）
     */
    private void getData(final int pageIndex) {
        RequestParams params = new RequestParams();
        params.put("sstid", colnumBeanId);
        params.put("pagesize", String.valueOf(pageSize));
        params.put("pageindex", String.valueOf(pageIndex));

        HttpUtil.get(HttpUtil.GET_VEDIO, context, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);

                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        loadView.setStatus(LoadView.SUCCESS);
                        if (pageIndex == 1) {//第一页判断是刷新
                            vedioLists.clear();
                        }
                        //解析数据
                        try {
                            JSONObject jo = new JSONObject(arg0);
                            parseJson(jo);//解析数据
                            if (jo.getJSONArray("NewsInfo").length() == 0
                                    && pageIndex == 1) {
                                // 没有数据的情况
                                loadView.setStatus(LoadView.EMPTY_DATA);
                                return;
                            }
                            //加载更多
                            if (jo.getJSONArray("NewsInfo").length() < pageSize
                                    && pageIndex >= 2) {
                                // 没有更多数据了，加载更多的情况
                                ToastUtil.showMessage(context, "没有更多了...");
                            }

                            // 设置数据
                            setData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        // 加载完成
                        refreshableView.setRefreshing(false);
                    }

                });
    }

    /**
     * 解析JSON数据
     * <p>保存在vediolists数据类里</p>
     */
    private void parseJson(JSONObject jo) {
        try {
            // 解析视频列表
            JSONArray infoJa = jo.getJSONArray("NewsInfo");
            JSONObject tempJo;
            NewBean infoItem;
            for (int i = 0, len = infoJa.length(); i < len; i++) {
                tempJo = infoJa.getJSONObject(i);
                infoItem = new NewBean();
                infoItem.setNild(tempJo.optString("NiId"));
                infoItem.setNiImg(tempJo.optString("NiImg"));
                infoItem.setNiTitle(tempJo.optString("NiTitle"));
                infoItem.setNiContent(tempJo.optString("NiContent"));
                infoItem.setNiTime(tempJo.optString("NiTime"));
                vedioLists.add(infoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置数据
     * <p>刷新adapter(没有则创建adapter并配置)</p>
     */
    private void setData() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
            return;
        }

        adapter = new QuickAdapter<NewBean>(context,
                R.layout.item_video_gridview, vedioLists) {
            @Override
            protected void convert(BaseAdapterHelper helper, NewBean item) {
                imageLoader.displayImage(item.getNiImg(),
                        (ImageView) helper.getView(R.id.img));
                helper.setText(R.id.title, item.getNiTitle());
                helper.setTag(R.id.playBtn, item.getNild());//播放图片View记住视频id
                helper.setOnClickListener(R.id.playBtn, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 进入播放视频WebView页面
                        Intent intent = new Intent(context, WebActivity.class);
                        intent.putExtra("nid", v.getTag().toString());
                        intent.putExtra("type", 1); // Web页面视频标识
                        context.startActivity(intent);
                    }
                });
            }
        };

        gridView.setAdapter(adapter);
    }

    /**
     * 设置下拉刷新事件
     */
    private void listViewPush() {
        refreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(1);
            }
        });
    }

    /**
     * 设置加载更多事件
     */
    private void listViewLoadmore() {
        gridView.setOnScrollListener(this);
    }

    //-------------滑动接口-----------------
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private boolean isRefresh = true;


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if ((isRefresh && firstVisibleItem + visibleItemCount == totalItemCount)
                && (totalItemCount > 1)) {
            if (gridView.getAdapter().getCount() >= pageSize) {
                isRefresh = false;

                // 加载更多
                getData(pageIndex);
            }
        }
    }
    //------------结束滑动接口-----------------
    @Override
    public void onTry() {
        // 重新获取数据
        if (loadView.setStatus(LoadView.LOADING)) {
            getData(1);
        }
    }

}
