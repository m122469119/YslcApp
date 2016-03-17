package com.yslc.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.StarModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.StarBean;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListViewForTitleBar;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListViewForTitleBar.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 明星主页Activity 查看明星博文列表
 *
 * @author HH
 */
public class StarMainActivity extends BaseActivity implements OnClickListener,
        OnTryListener {
    private BaseListViewForTitleBar listView;
    private QuickAdapter<StarBean> adapter;
    private ImageLoader imageLoader;
    private View headerView;
    private ImageView starImg;
    private TextView starName;
    private View titleBar;
    private LoadView loadView;
    private ArrayList<StarBean> dataList;
    private StarModelService starModelService;

    @Override
    protected int getLayoutId() {
        return R.layout.listview_star_main;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.starMain);
    }

    @Override
    protected void initView() {
        headerView = View.inflate(this, R.layout.header_star_main, null);
        imageLoader = ImageLoader.getInstance();
        dataList = new ArrayList<>();
        listView = (BaseListViewForTitleBar) findViewById(R.id.listview);
        titleBar = findViewById(R.id.toolbar);
        listView.setTitleBar(titleBar);
        // ListView添加HeaderView
        setStarInfo(null);
        listViewSetEvent();
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnTryListener(this);

        // 获取明星基本信息
        starModelService = new StarModelService(this);
        if (loadView.setStatus(LoadView.LOADING)) {
            getData();
        }
    }

    /**
     * 加载更多已经进入博文详情
     */
    private void listViewSetEvent() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position > 0 && position < dataList.size() + 1) {
                    // 进入文章详情页
                    Intent intent = new Intent(StarMainActivity.this,
                            StarContentActivity.class);
                    intent.putExtra("snId", dataList.get(position - 1).getSif_Id());
                    StarMainActivity.this.startActivity(intent);
                }
            }
        });

        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多
                getData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prise:
                // 点赞
                doPraise((TextView) v);
                break;
        }
    }

    /**
     * 获取明星文章内容列表
     */
    private void getData() {
        starModelService.getStarArticelList(getIntent().getStringExtra("sifId"), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                loadView.setStatus(LoadView.SUCCESS);
                HashMap<StarBean, ArrayList<StarBean>> map = (HashMap<StarBean, ArrayList<StarBean>>) data;
                Iterator iter = map.keySet().iterator();
                StarBean mode = null;
                ArrayList<StarBean> list = null;
                if (iter.hasNext()) {
                    mode = (StarBean) iter.next();
                    list = map.get(mode);
                }
                // 设置个人资料
                setStarInfo(mode);

                if (list.size() <= 0 && starModelService.getPageIndex() == 2) {
                    // 暂无文章
                    View view = View.inflate(
                            StarMainActivity.this,
                            R.layout.include_nodata, null);
                    ((TextView) view.findViewById(R.id.infoTv))
                            .setText("暂未发表文章");
                    listView.addHeaderView(view);
                }

                listView.onFinishLoad();
                if (list.size() < starModelService.getPageSize() && starModelService.getPageIndex() > 2) {
                    listView.noMoreData();
                }

                // 设置文章列表
                dataList.addAll(list);
                setStarList();
            }

            @Override
            public <T> void failer(T data) {
                loadView.setStatus(LoadView.ERROR);
                listView.onFinishLoad();
            }
        });
    }

    /**
     * 设置明星个人信息
     */
    private void setStarInfo(StarBean mode) {
        if (listView.getHeaderViewsCount() < 1) {
            headerView.findViewById(R.id.starInfoLayout).setVisibility(
                    View.GONE);
            starImg = (ImageView) headerView.findViewById(R.id.starImg);
            starName = (TextView) headerView.findViewById(R.id.starName);
            listView.addHeaderView(headerView);
            listView.setAdapter(null);
            return;
        }

        if (mode != null && null != headerView) {
            headerView.findViewById(R.id.starInfoLayout)
                    .setVisibility(View.VISIBLE);
            imageLoader.displayImage(mode.getSif_Img(), starImg, ViewUtil.getCircleOptions());
            starName.setText(mode.getSif_Name());
        }

    }

    /**
     * 设置明星文章内容列表
     */
    private void setStarList() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new QuickAdapter<StarBean>(this,
                    R.layout.item_star_article_listview, dataList) {
                @Override
                protected void convert(BaseAdapterHelper helper, StarBean item) {
                    helper.setText(R.id.title, item.getSif_Title());
                    helper.setText(R.id.time, item.getSn_Time());
                    helper.setText(R.id.content, item.getContent());
                    helper.setText(R.id.comment, "评论" + item.getSif_ComNumber());
                    TextView tv = helper.getView(R.id.prise);
                    tv.setTag(item);
                    tv.setText(item.getSif_Praise());
                    if (item.isPraise()) {
                        tv.setEnabled(true);
                    } else {
                        tv.setEnabled(false);
                    }
                    helper.setOnClickListener(R.id.prise, StarMainActivity.this);
                }
            };
            listView.setAdapter(adapter);
        }
    }

    /**
     * 点赞接口
     */
    private void doPraise(final TextView tv) {
        starModelService.doPraiseForArtice(((StarBean) tv.getTag()).getSif_Id(), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                tv.setEnabled(false);
                ToastUtil.showMessage(StarMainActivity.this, data.toString());

                ((StarBean) tv.getTag()).setPraise(false);
                String praise = String.valueOf(Integer.parseInt(((StarBean) tv.getTag())
                        .getSif_Praise()) + 1);
                ((StarBean) tv.getTag()).setSif_Praise(praise);
                tv.setText(praise);
            }

            @Override
            public <T> void failer(T data) {
                tv.setEnabled(true);
                ToastUtil.showMessage(StarMainActivity.this, data.toString());
            }
        });
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getData();
        }
    }

}
