package com.yslc.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.bean.StockInfo;
import com.yslc.data.service.NewModelService;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.WebActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.AdBean;
import com.yslc.bean.NewBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.BaseIndicator;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseViewPagerAdapter;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 咨讯Fragment(带轮换图片和大盘信息的咨讯Fragment)
 *
 * @author HH
 */
public class NewFragmentOne extends BaseFragment implements OnTryListener {
    private static final int TIME_IMG = 3000; // 图片轮播时间
    private static final int TIME_INFO = 10000; // 大盘信息更新时间
    private Context context;
    private TimerUtil imgTimer, infoTimer;
    private SwipeRefreshLayout refreshableView;
    private LoadView loadView;
    private BaseIndicator myIndicator;
    private BaseListView listView;
    private ViewPager viewPager;
    private TextView shTv1, shTv2, shTv3, szTv1, szTv2, szTv3, titleTv;
    private ImageLoader imageLoader;

    private QuickAdapter<NewBean> adapter;
    private boolean isSlid = true;
    private String colnumBeanId;
    private List<AdBean> titleImgList; // 图片列表
    private ArrayList<NewBean> infoItemList; // 咨讯内容列表
    private boolean isRefersh = false; // 是否刷新

    private NewModelService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        colnumBeanId = getArguments().getString("id");
        imageLoader = ImageLoader.getInstance();
        service = new NewModelService(context);
        imgTimer = new TimerUtil(TIME_IMG);
        imgTimer.setOnTimerCallback(new TimerUtil.OnTimerCallback() {
            @Override
            public void onTimer() {
                //图片轮转
                if (isSlid && null != viewPager) {
                    // 可以自动滑动
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
        infoTimer = new TimerUtil(TIME_INFO);
        infoTimer.setOnTimerCallback(new TimerUtil.OnTimerCallback() {
            @Override
            public void onTimer() {
                //大盘更新
                getComposite();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.listview_new;
    }

    @Override
    protected void findView(View views) {
        super.findView(views);

        View headView = View.inflate(getActivity(), R.layout.header_new_ad, null);
        shTv1 = (TextView) headView.findViewById(R.id.Shanghai);
        shTv2 = (TextView) headView.findViewById(R.id.Now1);
        shTv3 = (TextView) headView.findViewById(R.id.Differ1);
        szTv1 = (TextView) headView.findViewById(R.id.shenzhen);
        szTv2 = (TextView) headView.findViewById(R.id.Now2);
        szTv3 = (TextView) headView.findViewById(R.id.Differ2);
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1, R.color.refreshViewColor2, R.color.refreshViewColor3);
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        viewPager = (ViewPager) headView.findViewById(R.id.viewPager);
        myIndicator = (BaseIndicator) headView.findViewById(R.id.myIndicator);
        titleTv = (TextView) headView.findViewById(R.id.titleTv);
        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.addHeaderView(headView);
        titleImgList = new ArrayList<>();
        infoItemList = new ArrayList<>();
        setOnTouch();
        listViewEvent();
    }

    /**
     * 下拉刷新 & 加载更多 & Item点击事件
     */
    private void listViewEvent() {
        refreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefersh = true;
                loadData();
            }
        });

        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position > 0 && position < infoItemList.size() + 1) {
                    // 进入新闻详情
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("nid", infoItemList.get(position - 1).getNild());
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * 加载首页数据
     */
    private void loadData() {
        service.loadMoreNewData(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                refreshableView.setRefreshing(false);
                loadView.setStatus(LoadView.SUCCESS);
                infoItemList.clear();

                HashMap<ArrayList<AdBean>, ArrayList<NewBean>> map = (HashMap<ArrayList<AdBean>, ArrayList<NewBean>>) data;
                Iterator iterator = map.keySet().iterator();
                ArrayList<AdBean> adList = null;
                ArrayList<NewBean> newList = null;
                while (iterator.hasNext()) {
                    adList = (ArrayList<AdBean>) iterator.next();
                    newList = map.get(adList);
                }

                if (!isRefersh) {
                    // 显示广告,获取大盘信息
                    titleImgList.clear();
                    titleImgList.addAll(adList);
                    getComposite();
                    showAd();
                } else {
                    listView.onFinishLoad();
                }

                // 显示列表
                infoItemList.addAll(newList);
                showListView();
                isRefersh = false;
            }

            @Override
            public <T> void failer(T data) {
                isRefersh = false;
                refreshableView.setRefreshing(false);
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 加载更多数据
     */
    private void loadMore() {
        service.loadMoreNewData(false, colnumBeanId, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                listView.onFinishLoad();

                ArrayList<NewBean> list = (ArrayList<NewBean>) data;
                // 是否加载到了最后一页...
                if (list.size() < service.getPageSize()) {
                    listView.noMoreData();
                }
                infoItemList.addAll(list);
                showListView();
            }

            @Override
            public <T> void failer(T data) {
                listView.onFinishLoad();
                ToastUtil.showMessage(context, HttpUtil.ERROR_INFO);
            }
        });
    }

    /**
     * 显示标题广告
     */
    private void showAd() {
        BaseViewPagerAdapter adapters = new BaseViewPagerAdapter(getActivity());
        adapters.setNetImage(titleImgList, true);
        viewPager.setAdapter(adapters);
        // 创建指示器
        myIndicator.setViewPager(viewPager, getActivity(), titleImgList, titleTv);
        imgTimer.startTimer();

        //图片无限循环
        int n = Integer.MAX_VALUE / 2 % titleImgList.size();
        int itemPosition = Integer.MAX_VALUE / 2 - n;
        viewPager.setCurrentItem(itemPosition);
    }

    /**
     * 显示列表内容
     */
    private void showListView() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        } else {
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
        }
    }

    /**
     * 获取大盘信息
     */
    private void getComposite() {
        service.getStockInfo(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                ArrayList<StockInfo> list = (ArrayList<StockInfo>) data;
                shTv1.setText(list.get(0).getName());
                showColoe(shTv2, list.get(0).getNow(), list.get(0).getProportion(), shTv3);
                shTv3.setText(list.get(0).getDiffer() + "  " + list.get(0).getProportion() + "%");

                szTv1.setText(list.get(1).getName());
                showColoe(szTv2, list.get(1).getNow(), list.get(1).getProportion(), szTv3);
                szTv3.setText(list.get(1).getDiffer() + "  " + list.get(1).getProportion() + "%");

                // 开启大盘信息更新定时操作
                infoTimer.startTimer();
            }

            @Override
            public <T> void failer(T data) {
                ToastUtil.showMessage(context, "获取大盘信息失败");
            }
        });
    }

    /**
     * 根据大盘信息显示颜色图片
     */
    private void showColoe(TextView tv, String str, String radio, TextView tv2) {
        tv.setText(str);
        if (Float.parseFloat(radio) > 0) {
            // 上涨（使用红色文字，红色小图标）
            tv.setTextColor(ContextCompat.getColor(context, R.color.redInfo));
            tv2.setTextColor(ContextCompat.getColor(context, R.color.redInfo));
            tv.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.icon_red),
                    null, null, null);
        } else {
            // 下跌 （使用绿色文字，绿色小图标）
            tv.setTextColor(ContextCompat.getColor(context, R.color.greenInfo));
            tv2.setTextColor(ContextCompat.getColor(context, R.color.greenInfo));
            tv.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.icon_green),
                    null, null, null);
        }
    }

    /**
     * 设置ViewPager的onTouch事件，防止也自动滑动冲突
     */
    private boolean isDeal = false;
    private int y1 = 0, x1 = 0;
    private long fristClickTime = 0;

    private void setOnTouch() {
        viewPager.getParent().requestDisallowInterceptTouchEvent(true);
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fristClickTime = System.currentTimeMillis();
                        y1 = (int) event.getY();
                        x1 = (int) event.getX();
                        if (x1 < 60) {
                            isDeal = true;
                        } else {
                            isDeal = false;
                            if (viewPager.getChildCount() > 1) { // 有内容，多于1个时
                                // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                                viewPager.getParent()
                                        .requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:

                        // 暂停自动滑动
                        isSlid = false;
                        if ((int) event.getY() - y1 > 5 && Math.abs(x1 - (int) event.getX()) < 40) {
                            // 通知其父控件，将事件交换给父控件
                            viewPager.getParent()
                                    .requestDisallowInterceptTouchEvent(false);
                        }

                        return isDeal;

                    case MotionEvent.ACTION_UP:

                        if (System.currentTimeMillis() - fristClickTime <= 100 && (int) event.getY() == y1
                                && (int) event.getX() == x1) {
                            // 进入广告新闻详情
                            Intent intent = new Intent(context, WebActivity.class);
                            intent.putExtra("nid", "-1");
                            intent.putExtra("url", titleImgList.get(viewPager
                                    .getCurrentItem() % titleImgList.size())
                                    .getLinkUrl());
                            context.startActivity(intent);
                        }
                        isSlid = true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        // 第一次加载数据
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && null != imgTimer) {
            // Fragment不可见时销毁定时线程
            stopHandler();
        } else if (isVisibleToUser && null != imgTimer) {
            // 如果Fragment可见，开启图片轮播,开启大盘信息更新
            startHandler();
        }

    }

    /**
     * 开启定时任务
     */
    private void startHandler() {
        imgTimer.startTimer();
        infoTimer.startTimer();
    }

    /**
     * 暂停定时线程
     */
    private void stopHandler() {
        imgTimer.stopTimer();
        infoTimer.stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imgTimer.destroyTimer();
        infoTimer.destroyTimer();
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

}
