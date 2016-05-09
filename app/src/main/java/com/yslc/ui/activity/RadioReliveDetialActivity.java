package com.yslc.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
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
        OnClickListener, LoadView.OnTryListener {
    private BaseListView listView;
    private LoadView loadView;
    private ArrayList<RadioBean> listData;//列表数据
    private QuickAdapter<RadioBean> adapter;
    private RadioBean detailBean = null;
//    private RadioModelService radioModelService;
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
        pageIndex = 1;
        //加载圈圈
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //listView
        listView = (BaseListView) findViewById(R.id.listview);
        listView.setFooterDividersEnabled(true);
        listView.setRefreshLength(14); // 除去最新的一条，14条数据即可刷新
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

        listData = new ArrayList<>();
//        radioModelService = new RadioModelService(this);//业务类
        if (loadView.setStatus(LoadView.LOADING)) {//开始下载数据
            getHostDetail();
        }
    }

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
                        if(pageIndex == 1){
                            listData.clear();
                        }
                        listData.addAll(list);
                        if (listData.size() > 0) {
                            setHeaderView(detailBean, listData.get(0));//设置第一项布局
                            setAdapterData();//设置其他项
                        }
                    }

                });


    }

    /**
     * 设置HeaderView
     * <p>包含电台的设置和最新一期的设置</p>
     * @param DetailBean 节目描述数据
     * @param newBean 节目列表数据的第一项数据（最新一期信息）
     */
    private void setHeaderView(RadioBean DetailBean, RadioBean newBean) {
        if (listView.getHeaderViewsCount() == 0) {//没有列表头
            // 将第一条最新一期的数据移除，因为往期列表不需要用到它
            listData.remove(0);

            View headerView = View.inflate(this,
                    R.layout.header_radio_relive_datial, null);

            if (listData.size() < 2) {//显示没有数据
                headerView.findViewById(R.id.listIsEmpty).setVisibility(View.VISIBLE);
            }

            //主持人头像
            ImageLoader.getInstance().displayImage(DetailBean.getRadioHostUrl(),
                    (ImageView) headerView.findViewById(R.id.radioHostImg),
                    ViewUtil.getCircleOptions());
            //主持人名字
            ((TextView) headerView.findViewById(R.id.radioHostName))
                    .setText(DetailBean.getRadioHost());
            //节目名字
            ((TextView) headerView.findViewById(R.id.radioNames))
                    .setText(DetailBean.getRadioName());
            //节目播出时间
            ((TextView) headerView.findViewById(R.id.radioDates))
                    .setText(getIntent().getStringExtra("Date") + "\n" + DetailBean.getRadioTime());

            //最新一期的item
            View view = headerView.findViewById(R.id.radioTo);
            //设置名字和播放时间
            ((TextView) view.findViewById(R.id.radioName)).setText(newBean.getRadioName());
            ((TextView) view.findViewById(R.id.radioDate)).setText(newBean.getRadioTime());
            RelativeLayout btn = (RelativeLayout) view;//播放按钮图片
            newBean.setRadioHost(DetailBean.getRadioHost());//注册人名字
            btn.setTag(newBean);//播放按钮设置标签，内容是最新一期的数据
            btn.setOnClickListener(this);
            listView.addHeaderView(headerView);
        }
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

        if (listData.size() < 2) {//第二条数据开始才是适配器的数据
            return;
        }

        adapter = new QuickAdapter<RadioBean>(this,
                R.layout.item_radio_relive_play_listview, listData) {
            @Override
            protected void convert(BaseAdapterHelper helper, RadioBean item) {
                helper.setText(R.id.radioName, item.getRadioName());
                helper.setText(R.id.radioDate, item.getRadioTime());
                helper.getView(R.id.radioTo).setTag(item);//播放图片标签数据
                helper.setOnClickListener(R.id.radioTo, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RadioReliveDetialActivity.this, RadioRelivePlayerActivity.class);
                        RadioBean bean = (RadioBean) v.getTag();
                        bean.setRadioHost(detailBean.getRadioHost());
                        intent.putExtra("RadioBean", bean);
                        RadioReliveDetialActivity.this.startActivity(intent);
                    }
                });
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {//最新一期
        switch (v.getId()) {
            case R.id.radioTo:
                Intent intent = new Intent(RadioReliveDetialActivity.this, RadioRelivePlayerActivity.class);
                intent.putExtra("RadioBean", ((RadioBean) v.getTag()));
                startActivity(intent);
                break;
        }
    }

}
