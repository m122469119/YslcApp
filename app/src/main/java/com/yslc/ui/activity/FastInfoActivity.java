package com.yslc.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.bean.FastInfoBean;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.ui.base.BaseActivity;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.SharedPreferencesUtil;
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
    private static final int LOGIN_REQUEST_CODE = 1;
    private BaseListView listView;
    private QuickAdapter<FastInfoBean> adapter;
    private int pagerIndex;
    private LoadView loadView;
    private ArrayList<FastInfoBean> dataList;//数据
    private static final int DATA_COUNT = 10;//每次下载10条数据用于加载更多
    private SwipeRefreshLayout refreshLayout;
//    private boolean isVip = false;

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
     * <p>判断是否vip()</p>
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
                R.color.refreshViewColor2, R.color.refreshViewColor3);
        refreshLayout.setOnRefreshListener(refreshListener);//下拉监听
//        getData(pagerIndex = 1);
        isVip();
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
            intent.putExtra("title", dataList.get(position).getTitle());
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
        RequestParams params = new RequestParams();
        params.put("pageIndex", String.valueOf(pageIndex));
        //JsonHttpResponseHandler()
        HttpUtil.get(HttpUtil.GET_FAST_INFO, this, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        super.onSuccess(jsonObject);
                        loadView.setStatus(LoadView.SUCCESS);
                        refreshLayout.setRefreshing(false);
                        listView.onFinishLoad();
                        if (pageIndex != 1) {//加载更多
                            dataList.addAll(ParseUtil.parseFastInfoBean(jsonObject));
                        } else {//第一次进来和下拉刷新
                            dataList.clear();
                            dataList.addAll(ParseUtil.parseFastInfoBean(jsonObject));
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
//        finish();
        Intent intent = new Intent(this, NeedVipActivity.class);
        intent.putExtra("activity", "FastInfoActivity");
        startActivityForResult(intent, PAY_REQUEST_CODE);
    }

    /**
     * 判断是否是付费用户
     * 检查使用登录，如果登录了，进一步异步检查是否是vip
     * <p>如果是vip，获取数据，如果不是vip,显示需要支付页面</p>
     * @return
     */
    private boolean isVip() {
        //判断用户是否登录
        if (!SharedPreferencesUtil.isLogin(this)) {
            ToastUtil.showMessage(this, "请先登录");
            this.startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
            return false;
        }
        //判读是否vip
        SharedPreferencesUtil share = new SharedPreferencesUtil(this, Constant.SPF_USER_INFO_NAME);
        RequestParams params = new RequestParams();
        params.put("phone", share.getString(Constant.SPF_USER_PHONE_KEY));
        params.put("belong", "YSLC");
        params.put("function", "YSLC0001");
        HttpUtil.get("/AppJson/pay/wx/verifyPower.ashx", this, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        try {
                            JSONObject json = new JSONObject(s);
                            switch (json.getInt("status")) {
                                case 0://不是vip
//                                    ToastUtil.showMessage(FastInfoActivity.this,
//                                            "不是VIP" + json.getString("msg"));
                                    showNotVip();
                                    loadView.setStatus(LoadView.EMPTY_DATA);
                                    break;
                                case 1://是vip
//                                    ToastUtil.showMessage(FastInfoActivity.this,
//                                            "你好，VIP" + json.getString("msg"));
                                    getData(pagerIndex = 1);
                                    break;
                                //检查是否有权限
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                    }
                });
        return false;
    }

    private static final int PAY_REQUEST_CODE = 000002;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK){
            isVip();
        }else if(requestCode == PAY_REQUEST_CODE && resultCode == RESULT_CANCELED){
            finish();
        }
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
                    helper.setText(R.id.item_fast_info_content,"【"+item.getTitle()+"】"
                            +item.getContent());
                }
            };
            listView.setAdapter(adapter);
        }
    }


    @Override
    public void onTry() {
        if(loadView.setStatus(LoadView.LOADING)){
            getData(pagerIndex=1);
        }
    }
}
