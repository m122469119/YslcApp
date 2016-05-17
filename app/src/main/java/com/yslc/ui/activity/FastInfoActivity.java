package com.yslc.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.R;
import com.yslc.bean.FastInfoBean;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.ui.base.BaseActivity;
import com.yslc.util.HttpUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/31.
 *
 */
public class FastInfoActivity extends BaseActivity implements LoadView.OnTryListener{
    private BaseListView listView;
    private QuickAdapter<FastInfoBean> adapter;
    private int pagerIndex;
    private LoadView loadView;
    private ArrayList<FastInfoBean> dataList;//数据
    private static final int DATA_COUNT = 10;//每次下载10条数据用于加载更多
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.toolbar_listview_load;
    }

    @Override
    protected String getToolbarTitle() {
        return "闪电快讯";
    }

    /**
     * 初始化布局
     * <p>实例化业务处理类</p>
     * <p>开始下载数据</p>
     */
    @Override
    protected void initView() {
        dataList = new ArrayList<>();
        listView =(BaseListView)findViewById(R.id.listview);
        listView.setOnItemClickListener(listener);
        listView.setRefreshLength(DATA_COUNT);//每次有10条数据（不能删）
        listView.setOnLoadMoreListener(loadMoreListener);
        //加载圈圈
        loadView = (LoadView)findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //下拉刷新
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshable_view);
        refreshLayout.setColorSchemeResources(R.color.refreshViewColor1,
                R.color.refreshViewColor2,R.color.refreshViewColor3);
        refreshLayout.setOnRefreshListener(refreshListener);//下拉监听
        getData(pagerIndex = 1);
    }

    /**
     * 下拉刷新
     */
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout
            .OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData(pagerIndex=1);
        }
    };
    /**
     * listView点击事件
     */
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(FastInfoActivity.this, FastInfoDetailActivity.class);
            intent.putExtra("date", dataList.get(position).getDate());
            intent.putExtra("content", dataList.get(position).getContent());
            startActivity(intent);
        }
    };

    /**
     * 加载更多事件
     */
    BaseListView.OnLoadMoreListener loadMoreListener = new BaseListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            getData(++pagerIndex);
        }
    };
    /**
     * 下载数据
     */
    private void getData(int pageIndex) {
        loadView.setStatus(LoadView.LOADING);
        //检查是否有权限
        if( !isVip() ){
            showNotVip();
            loadView.setStatus(LoadView.EMPTY_DATA);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("pageIndex",String.valueOf(pageIndex));
        //JsonHttpResponseHandler()
        HttpUtil.get( HttpUtil.GET_FAST_INFO,this,params,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        super.onSuccess(jsonObject);
                        loadView.setStatus(LoadView.SUCCESS);
                        refreshLayout.setRefreshing(false);
                        listView.onFinishLoad();
                        if(pageIndex!=1){//加载更多
                            dataList.addAll(parseData(jsonObject));
                        }else {//第一次进来和下拉刷新
                            dataList.clear();
                            dataList.addAll(parseData(jsonObject));
                        }
                        showView(dataList);//十条数据
                    }

                    @Override
                    public void onFailure(Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(throwable, jsonObject);
                        loadView.setStatus(LoadView.ERROR);
                        refreshLayout.setRefreshing(false);
                        listView.onFinishLoad();
                    }
                });
    }

    /**
     * 显示不是vip才能观看的view
     */
    private void showNotVip() {
        //TODO 显示不是付费用户,跳转付费链接
        ToastUtil.showMessage(this, "vip客户才可以看");
    }

    /**
     * 判断是否是付费用户
     * @return
     */
    private boolean isVip() {
        //TODO 判断是否vip
        return false;
    }

    /**
     * 配置适配器并显示
     * @param dataList
     */
    private void showView(ArrayList<FastInfoBean> dataList) {
        if(null != adapter) {
            adapter.notifyDataSetChanged();
        }else{
            adapter = new QuickAdapter<FastInfoBean>(this,
                    R.layout.item_fast_info,dataList){
                @Override
                protected void convert(BaseAdapterHelper helper, FastInfoBean item) {
                    helper.setText(R.id.item_fast_info_time,item.getDate());
                    helper.setText(R.id.item_fast_info_content,item.getContent());
                }
            };
            listView.setAdapter(adapter);
        }
    }

    /**
     * 解析数据
     * @param jsonObject
     * @return
     */
    private ArrayList<FastInfoBean> parseData(JSONObject jsonObject) {
        JSONArray array = (JSONArray) jsonObject.opt("news");
        ArrayList<FastInfoBean> list = new ArrayList<FastInfoBean>();
        try{
            for(int i=0; i<array.length(); i++){
                FastInfoBean data = new FastInfoBean();
                data.setContent(array.getJSONObject(i).optString("content"));
                data.setDate(array.getJSONObject(i).optString("date"));
                list.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onTry() {
        if(loadView.setStatus(LoadView.LOADING)){
            getData(pagerIndex=1);
        }
    }
}
