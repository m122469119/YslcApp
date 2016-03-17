package com.yslc.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.StarModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.StarMainActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.StarBean;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 明星Fragment
 *
 * @author HH
 */
public class StarFragment extends BaseFragment implements OnTryListener {
    private SwipeRefreshLayout refreshableView;
    private BaseListView listView;
    private LoadView loadView;
    private ImageLoader imageLoader;
    private QuickAdapter<StarBean> adapter;
    private Context context;
    private String colnumBeanId;
    private ArrayList<StarBean> infoItemList;
    private StarModelService starModelService;

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
    protected void findView(View views) {
        super.findView(views);

        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setVerticalScrollBarEnabled(false);
        listView.setFooterDividersEnabled(true);
        listView.setDivider(ContextCompat.getDrawable(context, R.drawable.aaa));
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1, R.color.refreshViewColor2, R.color.refreshViewColor3);

        starModelService = new StarModelService(context);
        listViewSetEvent();
    }

    /**
     * ListView的下拉刷新 & 加载更多 & 点击事件
     */
    private void listViewSetEvent() {
        refreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 进入明星详情页面,去除ListView FooterView的点击事件
                if (position < infoItemList.size()) {
                    Intent intent = new Intent(context, StarMainActivity.class);
                    intent.putExtra("sifId", infoItemList.get(position).getSif_Id());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

    /**
     * 第一次加载或者刷新
     */
    private void loadData() {
        starModelService.getStarListData(colnumBeanId, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                refreshableView.setRefreshing(false);
                listView.onFinishLoad();

                ArrayList<StarBean> list = (ArrayList<StarBean>) data;
                if (list.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    loadView.setStatus(LoadView.SUCCESS);
                    infoItemList.clear();
                    infoItemList.addAll(list);
                    listRefersh();
                }
            }

            @Override
            public <T> void failer(T data) {
                refreshableView.setRefreshing(false);
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        starModelService.getStarMoreListData(colnumBeanId, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                listView.onFinishLoad();

                ArrayList<StarBean> list = (ArrayList<StarBean>) data;
                if (list.size() < starModelService.getPageSize()) {
                    listView.noMoreData();
                }

                infoItemList.addAll(list);
                listRefersh();
            }

            @Override
            public <T> void failer(T data) {
                refreshableView.setRefreshing(false);
                ToastUtil.showMessage(context, LoadView.ERROR);
            }
        });
    }

    /**
     * 进行列表的加载或刷新
     */
    private void listRefersh() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        } else {
            // 配置圆形图片参数
            final DisplayImageOptions options = ViewUtil.getCircleOptions();
            adapter = new QuickAdapter<StarBean>(context, R.layout.item_star_listview, infoItemList) {
                @Override
                protected void convert(BaseAdapterHelper helper, StarBean item) {
                    imageLoader.displayImage(item.getSif_Img(), (ImageView) helper.getView(R.id.img), options);
                    helper.setText(R.id.name, item.getSif_Name());
                    helper.setText(R.id.content, item.getContent());
                    helper.setText(R.id.time, item.getSn_Time());
                }
            };
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

}
