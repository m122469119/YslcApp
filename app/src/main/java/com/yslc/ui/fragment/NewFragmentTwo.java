package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
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
 * 咨讯Fragment（不含轮换图片和大盘信息的咨讯Fragment）
 *
 * @author HH
 */
public class NewFragmentTwo extends BaseFragment implements OnTryListener {
    private SwipeRefreshLayout refreshableView;
    private BaseListView listView;
    private LoadView loadView;
    private ImageLoader imageLoader;
    private QuickAdapter<NewBean> adapter;
    private Context context;

    private String colnumBeanId; // 本咨讯Title ID
    private ArrayList<NewBean> infoItemList;
    private NewModelService newModelService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        colnumBeanId = getArguments().getString("id");
        imageLoader = ImageLoader.getInstance();
        infoItemList = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.listview_new;
    }

    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        if (loadView.setStatus(LoadView.LOADING)) {
            oneLoad();
        }
    }

    @Override
    protected void findView(View views) {
        super.findView(views);

        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1, R.color.refreshViewColor2, R.color.refreshViewColor3);

        newModelService = new NewModelService(context);
        listViewOnEvent();
    }

    /**
     * 下拉刷新 & 加载更多 & ListView的点击事件
     */
    private void listViewOnEvent() {
        refreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新
                if (loadView.setStatus(LoadView.LOADING)) {
                    oneLoad();
                }
            }
        });

        /*refreshableView.setOnRefreshListener(() -> {
            if (loadView.setStatus(LoadView.LOADING)) {
                oneLoad();
            }
        });*/


        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多
                loadMore();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position < infoItemList.size()) {
                    // 进入新闻详情,这里需要去除FooterView的点击事件
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
        newModelService.loadMoreNewData(true, colnumBeanId, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                refreshableView.setRefreshing(false);
                listView.onFinishLoad();
                loadView.setStatus(LoadView.SUCCESS);

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
        newModelService.loadMoreNewData(false, colnumBeanId, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                ArrayList<NewBean> list = (ArrayList<NewBean>) data;

                listView.onFinishLoad();
                if (list.size() < newModelService.getPageSize()) {
                    //没有更多数据了，不可以再进行下拉刷新（FooterView显示没有更多了）
                    listView.noMoreData();
                }

                // 刷新列表
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
     */
    private void listRefersh() {
        if (null == adapter) {
            adapter = new QuickAdapter<NewBean>(context,
                    R.layout.item_main_listview, infoItemList) {
                @Override
                protected void convert(BaseAdapterHelper helper, NewBean item) {
                    imageLoader.displayImage(item.getNiImg(),
                            (ImageView) helper.getView(R.id.img));
                    helper.setText(R.id.title, item.getNiTitle());
                    helper.setText(R.id.content, item.getNiContent());
                    helper.setText(R.id.time, item.getNiTime());
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
