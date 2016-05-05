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
import com.yslc.util.ParseUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 资讯Fragment（不含轮换图片和大盘信息的咨讯Fragment）
 * <p>资讯默认fragment</p>
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
    private int pageSize, pageIndex;
//    private NewModelService newModelService;

    /**
     * 获取context、副标题id、实例化图片框架类、实例化数据列表
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageIndex =1;
        context = getActivity();
        colnumBeanId = getArguments().getString("id");
        imageLoader = ImageLoader.getInstance();
        infoItemList = new ArrayList<>();
    }

    /**
     * 设置布局
     * <p>包含下拉刷新和加载更多控件</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_new;
    }

    /**
     * 第一次加载数据
     * <p>调用oneload开始下载</p>
     */
    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        if (loadView.setStatus(LoadView.LOADING)) {
            oneLoad();
        }
    }

    /**
     * 初始化布局
     * <p>初始化加载View、listView、下拉刷新</p>
     * <p>实例化业务处理类</p>
     * <p>设置监听事件</p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);
        //正在加载框
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //listView
        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        //下拉刷新
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1, R.color.refreshViewColor2, R.color.refreshViewColor3);
        //实例化业务处理类
//        newModelService = new NewModelService(context);
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
        RequestParams params = new RequestParams();
        params.put("sstid", colnumBeanId);
        params.put("pageSize", String.valueOf(15));
        params.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.GET_MAIN, context, params,
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
                            refreshableView.setRefreshing(false);
                            loadView.setStatus(LoadView.ERROR);
                        } else {
                            refreshableView.setRefreshing(false);
                            listView.onFinishLoad();
                            loadView.setStatus(LoadView.SUCCESS);
                            if(pageIndex == 1){
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
     * <p>没有适配器创建，有的话直接刷新</p>
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

    /**
     * 重新加载
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            oneLoad();
        }
    }

}
