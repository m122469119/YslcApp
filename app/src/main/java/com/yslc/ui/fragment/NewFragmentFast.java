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

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.NewModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.WebActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.NewBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ToastUtil;
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
    private NewModelService newModelService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView = (BaseListView) views.findViewById(R.id.listview);
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        //设置进度动画的颜色资源
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1,
                R.color.refreshViewColor2, R.color.refreshViewColor3);
        //设置点击事件
        listViewSetEvent();
        newModelService = new NewModelService(context);
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
     */
    private void oneLoad() {
        newModelService.fristLoadFastNewData(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                loadView.setStatus(LoadView.SUCCESS);
                refreshableView.setRefreshing(false);
                listView.onFinishLoad();
                infoItemList.clear();

                infoItemList.addAll((ArrayList<NewBean>) data);
                listRefersh();
            }

            @Override
            public <T> void failer(T data) {
                refreshableView.setRefreshing(false);
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 加载更多数据
     */
    private void loadMore() {
        newModelService.loadMoreNewData(false, null, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                ArrayList<NewBean> list = (ArrayList<NewBean>) data;

                listView.onFinishLoad();
                if (list.size() < newModelService.getPageSize()) {
                    //没有更多数据了，不可以再进行下拉刷新（FooterView显示没有更多了）
                    listView.noMoreData();
                }

                // 添加更多数据并刷新列表
                infoItemList.addAll((ArrayList<NewBean>) data);
                listRefersh();
            }

            @Override
            public <T> void failer(T data) {
                listView.onFinishLoad();
                ToastUtil.showMessage(context, HttpUtil.ERROR_INFO);
            }
        });
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
