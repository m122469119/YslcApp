package com.yslc.ui.fragment;

import java.util.ArrayList;
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

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.bean.StockInfo;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.WebActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.AdBean;
import com.yslc.bean.NewBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.BaseIndicator;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseViewPagerAdapter;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 资讯Fragment(带轮换图片和大盘信息的咨讯Fragment)
 * <p>头版头条</p>
 * @author HH
 */
public class NewFragmentOne extends BaseFragment implements OnTryListener {
    private static final int TIME_IMG = 3000; // 图片轮播时间
    private static final int TIME_INFO = 10000; // 大盘信息更新时间
    private Context context;
    private TimerUtil imgTimer, infoTimer;//计时工具类
    private SwipeRefreshLayout refreshableView;
    private LoadView loadView;
    private BaseIndicator myIndicator;
    private BaseListView listView;
    private ViewPager viewPager;
    private TextView shTv1, shTv2, shTv3, szTv1, szTv2, szTv3, titleTv;
    private ImageLoader imageLoader;//图片下载框架

    private QuickAdapter<NewBean> adapter;
    private boolean isSlid = true;//轮播滑动标志
    private String colnumBeanId;
    private List<AdBean> titleImgList; // 图片列表
    private ArrayList<NewBean> infoItemList; // 咨讯内容列表
    private boolean isRefersh = false; // 是否刷新
    private int pageIndex, pageSize;

//    private NewModelService service;

    /**
     * 初始化Activity</br>
     * <p>获取上下文、副标题号、图片工具实例、实例业务逻辑类</p>
     * <p>设置两个计时器，一个图片轮播，一个用于更新大盘信息</p>
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        pageIndex = 1;
        //副标题号
        colnumBeanId = getArguments().getString("id");
        imageLoader = ImageLoader.getInstance();
//        service = new NewModelService(context);//业务逻辑类
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

    /**
     * 设置布局文件
     * <p>包含下拉刷新和加载更多的list</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_new;
    }

    /**
     * 初始化布局</br>
     * <p>设置监听事件</p>
     *
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);
        //listView上面的布局，轮播加股票指数
        View headView = View.inflate(getActivity(), R.layout.header_new_ad, null);
        //六个股票指数
        shTv1 = (TextView) headView.findViewById(R.id.Shanghai);
        shTv2 = (TextView) headView.findViewById(R.id.Now1);
        shTv3 = (TextView) headView.findViewById(R.id.Differ1);
        szTv1 = (TextView) headView.findViewById(R.id.shenzhen);
        szTv2 = (TextView) headView.findViewById(R.id.Now2);
        szTv3 = (TextView) headView.findViewById(R.id.Differ2);
        //下拉刷新
        refreshableView = (SwipeRefreshLayout) views.findViewById(R.id.refreshable_view);
        refreshableView.setColorSchemeResources(R.color.refreshViewColor1,
                R.color.refreshViewColor2, R.color.refreshViewColor3);
        //正在加载view
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //轮播
        viewPager = (ViewPager) headView.findViewById(R.id.viewPager);
        myIndicator = (BaseIndicator) headView.findViewById(R.id.myIndicator);
        titleTv = (TextView) headView.findViewById(R.id.titleTv);
        //主内容list
        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        //添加headView
        listView.addHeaderView(headView);
        titleImgList = new ArrayList<>();//初始化图片列表
        infoItemList = new ArrayList<>();//初始化内容数据列表
        setOnTouch();//viewPager事件
        listViewEvent();//设置其他监听事件
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
     * 加载数据
     */
    private void loadData() {
        HttpUtil.get(HttpUtil.GET_MAIN, context, null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        failure();
//                        callback.failer(null);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            failure();
                        } else {
                            //解析咨讯
                            ArrayList<NewBean> newList = ParseUtil.parseNewBean(arg0);
                            ArrayList<AdBean> imgList = ParseUtil.parseAdBean(arg0);

                            refreshableView.setRefreshing(false);
                            loadView.setStatus(LoadView.SUCCESS);
                            infoItemList.clear();//清除内容

                            //下拉刷新只刷新列表部分（大盘信息由定时器自动刷新）
                            if (!isRefersh) {//不是下拉刷新
                                // 显示广告,获取大盘信息
                                titleImgList.clear();
                                titleImgList.addAll(imgList);
                                getComposite();//大盘数据
                                showAd();
                            } else {
                                listView.onFinishLoad();
                            }

                            // 显示列表
                            infoItemList.addAll(newList);
                            showListView();//刷新列表
                            isRefersh = false;
                        }
                    }

                    private void failure() {
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
        if(pageSize < 15){
            listView.noMoreData();
            return;
        }
        pageIndex++;
        RequestParams params = new RequestParams();
        params.put("sstid", colnumBeanId);
        params.put("pageSize", String.valueOf(15));
        params.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.GET_MAIN, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        listView.onFinishLoad();
                        ToastUtil.showMessage(context, HttpUtil.ERROR_INFO);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        listView.onFinishLoad();
//
                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            listView.onFinishLoad();
                            ToastUtil.showMessage(context, HttpUtil.ERROR_INFO);
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
                            showListView();
                        }
                    }
                });

    }

    /**
     * 显示标题广告
     * <p>创建设置适配器</p>
     * <p>创建设置指示器</p>
     */
    private void showAd() {
        BaseViewPagerAdapter adapters = new BaseViewPagerAdapter(getActivity());
        adapters.setNetImage(titleImgList, true);//网络图片，无限滑动
        viewPager.setAdapter(adapters);
        // 创建指示器
        myIndicator.setViewPager(viewPager, getActivity(), titleImgList, titleTv);
        imgTimer.startTimer();

        //图片无限循环
        int n = Integer.MAX_VALUE / 2 % titleImgList.size();
        int itemPosition = Integer.MAX_VALUE / 2 - n;//计算出一个最接近MAX_VALUE/2而且余数为0的数
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
                    //两个参数url,和imageView
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
     * <p>获取去成功后刷新大盘</p>
     */
    private void getComposite() {
        HttpUtil.get(HttpUtil.GET_STOCK, context, null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        ToastUtil.showMessage(context, "获取大盘信息失败");
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            ToastUtil.showMessage(context, "获取大盘信息失败");
                            return;
                        }

                        ArrayList<StockInfo> list = ParseUtil.parseStockInfo(arg0);
                        showComposite(list);

                    }

                });

    }

    private void showComposite(ArrayList<StockInfo> list) {
        shTv1.setText(list.get(0).getName());//上海大盘
        showColor(shTv2, list.get(0).getNow(), list.get(0).getProportion(), shTv3);
        shTv3.setText(list.get(0).getDiffer() + "  " + list.get(0).getProportion() + "%");

        szTv1.setText(list.get(1).getName());//深圳大盘
        showColor(szTv2, list.get(1).getNow(), list.get(1).getProportion(), szTv3);
        szTv3.setText(list.get(1).getDiffer() + "  " + list.get(1).getProportion() + "%");

        // 开启大盘信息更新定时操作
        infoTimer.startTimer();
    }

    /**
     * 根据大盘信息显示颜色图片
     * <p>设置指数</p>
     * <p>根据上涨下跌，设置颜色</p>
     * @param tv 指数
     * @param str 指数名称
     * @param radio 上涨或下跌标志
     * @param tv2 上涨点数与涨幅
     */
    private void showColor(TextView tv, String str, String radio, TextView tv2) {
        tv.setText(str);
        if (Float.parseFloat(radio) > 0) {
            // 上涨（使用红色文字，红色小图标）
            tv.setTextColor(ContextCompat.getColor(context, R.color.redInfo));
            tv2.setTextColor(ContextCompat.getColor(context, R.color.redInfo));
            tv.setCompoundDrawablesWithIntrinsicBounds(//左边设置红色向上箭头
                    ContextCompat.getDrawable(context, R.drawable.icon_red),
                    null, null, null);
        } else {
            // 下跌 （使用绿色文字，绿色小图标）
            tv.setTextColor(ContextCompat.getColor(context, R.color.greenInfo));
            tv2.setTextColor(ContextCompat.getColor(context, R.color.greenInfo));
            tv.setCompoundDrawablesWithIntrinsicBounds(//左边设置绿色向下箭头
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

    /**
     * 创建fragment后第一次开始加载数据
     */
    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        // 第一次加载数据
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

    /**
     * 此方法系统调用
     * <p>可见是开启定时器</p>
     * <p>不可见时关闭定时器</p>
     * @param isVisibleToUser
     */
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

    /**
     * 销毁定时器
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        imgTimer.destroyTimer();
        infoTimer.destroyTimer();
    }

    /**
     * 重试刷新
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

}
