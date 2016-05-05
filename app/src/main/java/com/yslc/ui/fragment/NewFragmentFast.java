package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.WebActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.NewBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 快讯Fragment
 *
 * @author HH
 */
public class NewFragmentFast extends BaseFragment implements OnTryListener {
    private SwipeRefreshLayout refreshableView;//下拉刷新
    private BaseListView listView;//加载更多listView
    private LoadView loadView;
    private QuickAdapter<NewBean> adapter;//适配器
    private Context context;
    private ArrayList<NewBean> infoItemList;//list数据
//    private NewModelService newModelService;
    private int pageIndex, pageSize;//当前下载页面大少
    private String columnBeanId;

    /**
     * <p>获取上下文</p>
     * <p>实例化数据列表</p>
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        columnBeanId = getArguments().getString("id");
        pageIndex = 1;
        context = getActivity();
        infoItemList = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.listview_new;
    }

    /**
     * 第一次加载
     * <p>父类调用</p>
     */
    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        if (loadView.setStatus(LoadView.LOADING)) {
            //开始加载
            oneLoad();
        }
    }

    /**
     * 初始化View
     * <p>关联loadView并设置监听事件</p>
     * <p>关联listView</p>
     * <p>关联下拉刷新</p>
     * <p>设置点击事件</p>
     * <p>实例化业务逻辑类</p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);
//        loadView = super.loadView;
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView = (BaseListView) views.findViewById(R.id.listview);
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        //设置进度动画的颜色资源
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1,
                R.color.refreshViewColor2, R.color.refreshViewColor3);
        //设置点击事件
        listViewSetEvent();
//        newModelService = new NewModelService(context);
    }

    /**
     * 下拉刷新 & 加载更多 & ListView的Item点击事件
     */
    private void listViewSetEvent() {
        refreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (loadView.setStatus(LoadView.LOADING)) {
                    oneLoad();
                }
            }
        });

        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position < infoItemList.size()) {
                    // 进入新闻详情,添加了ListView的footer这里需要去除FooterView的点击事件
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("nid", infoItemList.get(position).getNild());
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * 第一次加载或者刷新
     * <p>成功后加载停止，把数据添加到数据列表</p>
     * 成功pagerIndex =2
     */
    private void oneLoad() {
        RequestParams params = new RequestParams();
        params.put("pageSize", String.valueOf(15));
        params.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.GET_MAIN_FAST, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        refreshableView.setRefreshing(false);
                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            // 加载失败
                            refreshableView.setRefreshing(false);
                            loadView.setStatus(LoadView.ERROR);
                        } else {
//                            pageIndex = 2;
                            loadView.setStatus(LoadView.SUCCESS);
                            refreshableView.setRefreshing(false);
                            listView.onFinishLoad();
                            if(pageIndex==1){//加载第一页清空
                                infoItemList.clear();
                            }
                            ArrayList<NewBean> list = ParseUtil.parseNewBean(arg0);
                            pageSize = list.size();
                            infoItemList.addAll(list);
                            listRefersh();

                        }
                    }
                });
    }

    /**
     * 加载更多数据
     * <p>如果成功获取更多数据，把数据追加到数据列表，并刷新list</p>
     */
    private void loadMore() {
        if(pageSize<15){
            listView.noMoreData();
        }else{
            pageIndex++;//加载下一页
            oneLoad();
        }
    }

    /**
     * 进行列表的刷新
     * <p>数据更新了，需要把新数据显示在listView上</p>
     * <p></p>
     * <p>如果适配器没有实例化，则先实例化适配器，并且设置适配数据</p>
     */
    private void listRefersh() {
        if (null == adapter) {
            adapter = new QuickAdapter<NewBean>(context, R.layout.item_alerts,
                    infoItemList) {
                @Override
                protected void convert(BaseAdapterHelper helper, NewBean item) {
                    if (item.getWhatColor().equals("0")) {
                        // 内容黑色显示
                        ((TextView) helper.getView(R.id.value)).setText(Html
                                .fromHtml("<font color=\"#3983f4\">"
                                        + item.getNiTime() + "</font>" + "  "
                                        + "<font color=\"#212121\">"
                                        + item.getNiContent() + "</font>"));
                    } else {
                        // 内容红色显示
                        ((TextView) helper.getView(R.id.value)).setText(Html
                                .fromHtml("<font color=\"#3983f4\">"
                                        + item.getNiTime() + "</font>" + "  "
                                        + "<font color=\"#ff0000\">"
                                        + item.getNiContent() + "</font>"));
                    }

                    // 阅读数和评论数
                    helper.setText(R.id.readNum, "阅读:" + item.getReadNum());
                }
            };

            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            oneLoad();
        }
    }

}
