package com.yslc.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.StarBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListViewForTitleBar;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListViewForTitleBar.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

import org.json.JSONObject;

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
    private int pageSize, pageIndex;

    /**
     * 包含加载更多listView，加载圈圈,标题栏
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_star_main;
    }

    /**
     * 设置标题
     * @return 标题
     */
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.starMain);
    }

    /**
     * 初始化布局
     * <p>实例化业务处理类</p>
     * <p>开始下载数据</p>
     */
    @Override
    protected void initView() {
        pageIndex = 1;
        headerView = View.inflate(this, R.layout.header_star_main, null);//明星简介
        imageLoader = ImageLoader.getInstance();
        dataList = new ArrayList<>();//数据类
        listView = (BaseListViewForTitleBar) findViewById(R.id.listview);
        titleBar = findViewById(R.id.toolbar);//标题栏
        listView.setTitleBar(titleBar);//关联标题栏
        // ListView添加HeaderView
        setStarInfo(null);//初始化headerView
        listViewSetEvent();//点击事件
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnTryListener(this);

        // 获取明星基本信息
        if (loadView.setStatus(LoadView.LOADING)) {
            getData();//下载数据
        }
    }

    /**
     * 加载更多已经进入博文详情
     * <p>listView点击事件</p>
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
                getMoreData();
            }
        });
    }

    private void getMoreData() {
        if(pageSize < 15){
            listView.noMoreData();
            return;
        }
        pageIndex++;
        getData();
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
     * <p>成功后显示明星信息和明星文章列表信息</p>
     */
    private void getData() {
        RequestParams params = new RequestParams();
        params.put("Sif_Id", getIntent().getStringExtra("sifId"));
        params.put("pageSize", "15");
        params.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.GET_STAR_CONTENT_LIST, this, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        loadView.setStatus(LoadView.ERROR);
                        listView.onFinishLoad();
                    }

                    @Override
                    public void onSuccess(JSONObject jo) {
                        super.onSuccess(jo);
                        if (jo.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            loadView.setStatus(LoadView.ERROR);
                            listView.onFinishLoad();
                        } else {//成功
                            StarBean mode = ParseUtil.parseSingleStarBean(jo);
                            // 解析文章列表
                            ArrayList<StarBean> list = ParseUtil.parseStarBean2(jo);

                            loadView.setStatus(LoadView.SUCCESS);
                            // 设置个人资料
                            setStarInfo(mode);

                            //如果该明星没有发表信息，则显示暂未发表文章
                            if (list.size() <= 0 && pageIndex == 1) {
                                // 暂无文章
                                showNoArtical();
                            }

                            listView.onFinishLoad();
                            pageSize = list.size();

                            if(pageIndex == 1){
                                dataList.clear();
                            }
                            // 设置文章列表
                            dataList.addAll(list);
                            setStarList();//设置文章
                        }
                    }

                });

    }

    private void showNoArtical() {
        View view = View.inflate(
                StarMainActivity.this,
                R.layout.include_nodata, null);
        ((TextView) view.findViewById(R.id.infoTv))
                .setText("暂未发表文章");
        listView.addHeaderView(view);
    }

    /**
     * 设置明星个人信息
     * <p>没有headView,则初始化headView布局，并添加</p>
     * <p>在headView显示明星信息</p>
     * @param mode 明星信息
     */
    private void setStarInfo(StarBean mode) {
        if (listView.getHeaderViewsCount() < 1) {//没有headView
            headerView.findViewById(R.id.starInfoLayout).setVisibility(
                    View.GONE);
            starImg = (ImageView) headerView.findViewById(R.id.starImg);
            starName = (TextView) headerView.findViewById(R.id.starName);
            listView.addHeaderView(headerView);
            listView.setAdapter(null);
            return;
        }

        //明星信息类不为空，显示明星信息控件不为空，则显示明星信息
        if (mode != null && null != headerView) {
            headerView.findViewById(R.id.starInfoLayout)//显示明星信息
                    .setVisibility(View.VISIBLE);
            //下载明星头像
            imageLoader.displayImage(mode.getSif_Img(), starImg, ViewUtil.getCircleOptions());
            starName.setText(mode.getSif_Name());//设置明星姓名
        }

    }

    /**
     * 设置明星文章内容列表
     * <p>点赞设置监听</p>
     */
    private void setStarList() {
        if (null != adapter) {//通知更新
            adapter.notifyDataSetChanged();
        } else {//配置适配器
            adapter = new QuickAdapter<StarBean>(this,
                    R.layout.item_star_article_listview, dataList) {
                @Override
                protected void convert(BaseAdapterHelper helper, StarBean item) {
                    helper.setText(R.id.title, item.getSif_Title());
                    helper.setText(R.id.time, item.getSn_Time());
                    helper.setText(R.id.content, item.getContent());
                    helper.setText(R.id.comment, "评论" + item.getSif_ComNumber());
                    TextView tv = helper.getView(R.id.prise);//点赞数量
                    tv.setTag(item);//点赞控件标签数据
                    tv.setText(item.getSif_Praise());//设置数量
                    if (item.isPraise()) {//是否可以点赞
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
     * 点赞事件
     */
    private void doPraise(final TextView tv) {
        RequestParams params = new RequestParams();
        params.put("Sn_Id", ((StarBean) tv.getTag()).getSif_Id());
        HttpUtil.get(HttpUtil.DO_PRAISE, this, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        tv.setEnabled(true);
                        ToastUtil.showMessage(StarMainActivity.this, "点赞失败");
//                        callback.failer("点赞失败");
                    }

                    @Override
                    public void onSuccess(JSONObject arg0) {
                        super.onSuccess(arg0);

                        if (!arg0.optString("Status").equals(
                                HttpUtil.ERROR_CODE)) {
                            tv.setEnabled(false);
                            ToastUtil.showMessage(StarMainActivity.this, arg0.optString("msg"));//点赞成功

                            ((StarBean) tv.getTag()).setPraise(false);//不可重复点赞
                            //点赞加一（转来转去有点煞笔）
                            String praise = String.valueOf(Integer.parseInt(((StarBean) tv.getTag())
                                    .getSif_Praise()) + 1);
                            ((StarBean) tv.getTag()).setSif_Praise(praise);
                            tv.setText(praise);
//                            callback.success(arg0.optString("msg"));//成功
                        } else {
                            tv.setEnabled(true);
                            ToastUtil.showMessage(StarMainActivity.this, arg0.optString("msg"));
//                            callback.failer(arg0.optString("msg"));
                        }
                    }
                });


    }

    /**
     * 重新加载事件
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getData();
        }
    }

}
