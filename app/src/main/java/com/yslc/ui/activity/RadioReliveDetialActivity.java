package com.yslc.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.RadioBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView;

import org.json.JSONObject;

/**
 * 股市重温节目详情界面
 *
 * @author HH
 */
public class RadioReliveDetialActivity extends BaseActivity implements
        LoadView.OnTryListener {
    private BaseListView listView;
    private LoadView loadView;
    private ArrayList<RadioBean> listData;//列表数据
    private QuickAdapter<RadioBean> adapter;
    private RadioBean detailBean = null;
    private int pageIndex, pageSize;

    /**
     * 设置布局
     * <p>包含标题，加载更多listView，加载圈圈</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_relive_detial;
    }

    /**
     * 设置标题
     * @return
     */
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.interactivesDetail);
    }

    /**
     * 初始化布局
     * <p>关联加载圈圈、listView并设置监听</p>
     * <p>实例化数据类，业务逻辑类</p>
     * <p>开始加载数据</p>
     */
    @Override
    protected void initView() {
        //加载圈圈
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //listView
        listView = (BaseListView) findViewById(R.id.listview);
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(pageSize < 15){
                    listView.noMoreData();
                    return;
                }
                pageIndex++;
                getListDate(detailBean.getRadioName());
            }
        });

        listView.setOnItemClickListener(itemListener);


        listData = new ArrayList<>();
        if (loadView.setStatus(LoadView.LOADING)) {//开始下载数据
            pageIndex = 1;
            getHostDetail();
        }
    }

    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(RadioReliveDetialActivity.this, RadioRelivePlayerActivity.class);
            RadioBean bean = listData.get(position);
            bean.setRadioHost(detailBean.getRadioHost());
            intent.putExtra("RadioBean", bean);
            RadioReliveDetialActivity.this.startActivity(intent);
        }
    };

    /**
     * 重写加载
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            pageIndex = 1;
            getHostDetail();
        }
    }

    /**
     * 获取节目详细信息
     * <p>即列表第一项显示主持人信息什么的</p>
     * <p>成功后再根据节目名称下载往期节目列表</p>
     */
    private void getHostDetail() {
        RequestParams params = new RequestParams();
        params.put("RadP_Id", getIntent().getStringExtra("RadP_Id"));
        HttpUtil.get(HttpUtil.PLAY_VEDIO_RELIVE_DETAILS, this, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(int arg0, JSONObject arg1) {
                        super.onSuccess(arg0, arg1);
                        if (arg1.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            loadView.setStatus(LoadView.ERROR);
                            return;
                        }
                        // 成功
                        // 解析详情
                        RadioBean detialBean = ParseUtil.parseHeadRadioBean(arg1);
                        detailBean = detialBean;
                        getListDate(detailBean.getRadioName());//下载往期列表
                        setHeaderView(detailBean);//设置第一项布局
                    }

                });

    }

    /**
     * 获取重温数据
     * <p>获取往期节目列表</p>
     * <p>成功后，把数据显示在列表上</p>
     * @param dbName 广播名称（作为网络请求参数）
     */
    private void getListDate(String dbName) {
        RequestParams params = new RequestParams();
        params.put("dbname", dbName);
        params.put("pagesize", "15");
        params.put("pageindex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.PLAY_VEDIO_RELIVE_DETAILS_LIST, this, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        listView.onFinishLoad();
                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(int arg0, JSONObject arg1) {
                        super.onSuccess(arg0, arg1);
                        //解析数据
                        ArrayList<RadioBean> list = ParseUtil.parseRadioBean(arg1);

                        listView.onFinishLoad();
                        loadView.setStatus(LoadView.SUCCESS);

                        pageSize = list.size();

                        //保存数据
                        if (pageIndex == 1) {
                            listData.clear();
                        }
                        listData.addAll(list);
                        setAdapterData();//设置其他项
                    }

                });


    }

    /**
     * 设置HeaderView
     * <p>包含电台的设置和最新一期的设置</p>
     * @param DetailBean 节目描述数据
     */
    private void setHeaderView(RadioBean DetailBean) {
            //主持人头像
            ImageLoader.getInstance().displayImage(DetailBean.getRadioHostUrl(),
                    (ImageView) findViewById(R.id.radioHostImg),
                    ViewUtil.getCircleOptions());
            //主持人名字
            ((TextView) findViewById(R.id.radioHostName))
                    .setText(DetailBean.getRadioHost());
            //节目名字
            ((TextView) findViewById(R.id.radioNames))
                    .setText(DetailBean.getRadioName());
            //节目播出时间
            ((TextView)findViewById(R.id.radioDates))
                    .setText(getIntent().getStringExtra("Date") + "\n" + DetailBean.getRadioTime());

    }

    /**
     * 设置adapter
     * <p>往期列表</p>
     */
    private void setAdapterData() {
        if (null != adapter) {//通知更新
            adapter.notifyDataSetChanged();
            return;
        }

        adapter = new QuickAdapter<RadioBean>(this,
                R.layout.item_radio_relive_play_listview, listData) {
            @Override
            protected void convert(BaseAdapterHelper helper, RadioBean item) {
                helper.setText(R.id.radioName, item.getRadioName());
                helper.setText(R.id.radioDate, item.getRadioTime());
                if(helper.getPosition() == 0){//显示往期
                    helper.setVisible(R.id.prv_head,true);
                    if(listData.size() == 1){//无往期内容
                        helper.setVisible(R.id.prv_head,true);
                        helper.setVisible(R.id.listIsEmpty, true);
                    }else {
                        helper.setVisible(R.id.listIsEmpty, false);
                    }
                }else{
                    helper.setVisible(R.id.prv_head, false);
                }
            }
        };
        listView.setAdapter(adapter);
    }

}
