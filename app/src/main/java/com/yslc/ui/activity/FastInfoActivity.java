package com.yslc.ui.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.R;
import com.yslc.bean.FastInfoBean;
import com.yslc.data.service.StarModelService;
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
 * TODO 下拉刷新，加载更多，点击事件
 */
public class FastInfoActivity extends BaseActivity implements LoadView.OnTryListener{
    private ImageLoader imageLoader;
    private BaseListView listView;
    private QuickAdapter<FastInfoBean> adapter;
    private ImageView starImg;
    private TextView starName;
    private int pagerIndex;
    private LoadView loadView;
    private ArrayList<FastInfoBean> dataList;//数据
    private StarModelService starModelService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_relive_detial;
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
        loadView = (LoadView)findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView.setOnItemClickListener(listener);
        listView.setOnLoadMoreListener(loadMoreListener);
        getData(pagerIndex=1);
    }

    /**
     * listView点击事件
     */
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToastUtil.showMessage(FastInfoActivity.this, "点击事件");
        }
    };
    //加载更多时间
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
        RequestParams params = new RequestParams();
        params.put("pageIndex",String.valueOf(pageIndex));
        //JsonHttpResponseHandler()
        HttpUtil.get( HttpUtil.GET_FAST_INFO,this,params,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        super.onSuccess(jsonObject);
                        loadView.setStatus(LoadView.SUCCESS);
                        if(pageIndex!=1){//加载更多
                            dataList.addAll(parseData(jsonObject));
                        }else {
                            dataList = parseData(jsonObject);
                        }
                        showView(dataList);
                    }

                    @Override
                    public void onFailure(Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(throwable, jsonObject);
                        loadView.setStatus(LoadView.ERROR);
                        listView.onFinishLoad();
                    }
                });
    }

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
