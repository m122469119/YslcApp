package com.yslc.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.RadioModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.RadioBean;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView;

/**
 * 股市重温节目详情界面
 *
 * @author HH
 */
public class RadioReliveDetialActivity extends BaseActivity implements
        OnClickListener, LoadView.OnTryListener {
    private BaseListView listView;
    private LoadView loadView;
    private ArrayList<RadioBean> listData;
    private QuickAdapter<RadioBean> adapter;
    private RadioBean detailBean = null;
    private RadioModelService radioModelService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_radio_relive_detial;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.interactivesDetail);
    }

    @Override
    protected void initView() {
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView = (BaseListView) findViewById(R.id.listview);
        listView.setFooterDividersEnabled(true);
        listView.setRefreshLength(14); // 除去最新的一条，14条数据即可刷新
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getListDate(detailBean.getRadioName());
            }
        });

        listData = new ArrayList<>();
        radioModelService = new RadioModelService(this);
        if (loadView.setStatus(LoadView.LOADING)) {
            getHostDetail();
        }
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getHostDetail();
        }
    }

    /**
     * 获取节目详细信息
     */
    private void getHostDetail() {
        radioModelService.getRadioReliveDetail(getIntent().getStringExtra("RadP_Id"), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                detailBean = (RadioBean) data;
                getListDate(detailBean.getRadioName());
            }

            @Override
            public <T> void failer(T data) {
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 获取重温数据
     */
    private void getListDate(String dbName) {
        radioModelService.getReliveListForHost(dbName, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                listView.onFinishLoad();
                loadView.setStatus(LoadView.SUCCESS);

                ArrayList<RadioBean> list = (ArrayList<RadioBean>) data;
                if (list.size() < radioModelService.getPageSize() && radioModelService.getPageIndex() > 2) {
                    listView.noMoreData();
                }

                listData.addAll(list);
                if (listData.size() > 0) {
                    setHeaderView(detailBean, listData.get(0));
                    setAdapterData();
                }
            }

            @Override
            public <T> void failer(T data) {
                listView.onFinishLoad();
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 设置HeaderView
     */
    private void setHeaderView(RadioBean DetailBean, RadioBean newBean) {
        if (listView.getHeaderViewsCount() == 0) {
            // 将第一条最新一期的数据移除，因为往期列表不需要用到它
            listData.remove(0);

            View headerView = View.inflate(this,
                    R.layout.header_radio_relive_datial, null);

            if (listData.size() < 2) {
                headerView.findViewById(R.id.listIsEmpty).setVisibility(View.VISIBLE);
            }

            ImageLoader.getInstance().displayImage(DetailBean.getRadioHostUrl(),
                    (ImageView) headerView.findViewById(R.id.radioHostImg),
                    ViewUtil.getCircleOptions());
            ((TextView) headerView.findViewById(R.id.radioHostName))
                    .setText(DetailBean.getRadioHost());
            ((TextView) headerView.findViewById(R.id.radioNames))
                    .setText(DetailBean.getRadioName());
            ((TextView) headerView.findViewById(R.id.radioDates))
                    .setText(getIntent().getStringExtra("Date") + "\n" + DetailBean.getRadioTime());

            View view = headerView.findViewById(R.id.newRadio);
            ((TextView) view.findViewById(R.id.radioName)).setText(newBean.getRadioName());
            ((TextView) view.findViewById(R.id.radioDate)).setText(newBean.getRadioTime());
            ImageView btn = (ImageView) view.findViewById(R.id.radioTo);
            newBean.setRadioHost(DetailBean.getRadioHost());
            btn.setTag(newBean);
            btn.setOnClickListener(this);
            listView.addHeaderView(headerView);
        }
    }

    /**
     * 设置adapter
     */
    private void setAdapterData() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
            return;
        }

        if (listData.size() < 2) {
            return;
        }

        adapter = new QuickAdapter<RadioBean>(this,
                R.layout.item_radio_relive_play_listview, listData) {
            @Override
            protected void convert(BaseAdapterHelper helper, RadioBean item) {
                helper.setText(R.id.radioName, item.getRadioName());
                helper.setText(R.id.radioDate, item.getRadioTime());
                helper.getView(R.id.radioTo).setTag(item);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radioTo:
                Intent intent = new Intent(RadioReliveDetialActivity.this, RadioRelivePlayerActivity.class);
                intent.putExtra("RadioBean", ((RadioBean) v.getTag()));
                startActivity(intent);
                break;
        }
    }

}
