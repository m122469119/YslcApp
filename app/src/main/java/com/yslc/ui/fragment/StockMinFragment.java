package com.yslc.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.bean.MinuteInfo;
import com.yslc.bean.StocksDetail;
import com.yslc.ui.dialog.StocyDetailDialog;
import com.yslc.inf.IGetStockDataCallBack;
import com.yslc.data.service.StockService;
import com.yslc.util.CommonUtil;
import com.yslc.util.KChartUtil;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;
import com.yslc.view.CrossView;
import com.yslc.view.StocyMinutesView;

import java.util.List;

/**
 * 分时图Fragment
 *
 * @author HH
 */
public class StockMinFragment extends BaseFragment implements OnTryListener, IGetStockDataCallBack,
        CrossView.OnDrawCallback, TimerUtil.OnTimerCallback {
    private static final int DATA = 360;//数据长度？？
    private static final int GET_DATA_TIME = 5000;
    private StocyDetailDialog stocyDetailDialog;
    private StocksDetail detail;//买卖盘数据详情
    private StocyMinutesView stocyMinutesView;//分时图
    private CrossView crossView;
    private LoadView loadView;
    private View top1, top2, stocyDetail;
    private TextView stocyNow, stocyAs, stocyTime, stocyHand, stocySell1, stocyBug1; //个股信息
    private TextView now, stocyAs1, stocyAs2, priseNum, riseCount, balanceCount, downCount; //大盘信息
    private TextView current, stocyAs3, toDayPrice, nightDayPrice, heigPrice, lowPrice, totalNum,
            totalPrice;  //大盘信息横屏

    private List<MinuteInfo> list;//分时曲线图数据
    private StockService stockService;
    private TimerUtil timerUtil;
    private Context context;
    private String code;//股票代码
    private int screenW, screenH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        timerUtil = new TimerUtil(GET_DATA_TIME);//5秒刷新一次
        timerUtil.setOnTimerCallback(this);
        code = getArguments().getString("code");//股票代码
    }

    /**
     * 设置布局
     * <p>包含两个分时图控件,加载圈圈，和汇总信息头</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_stock_min;
    }

    /**
     * 初始化控件
     * <p></p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);
        screenW = CommonUtil.getScreenWidth(context);//屏宽
        screenH = CommonUtil.getScreenHeight(context);//屏高
        stocyMinutesView = (StocyMinutesView) views.findViewById(R.id.stocyMinutesView);//分时图

        //横竖屏时布局不同
        int orientation = getResources().getConfiguration().orientation;
        int clonumHeight = 0;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏
            clonumHeight = (int) context.getResources().getDimension(R.dimen.colnumbarSize);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏
            clonumHeight = 0;
        }
        //设置分时图大小 高度是屏高-标题栏-汇总信息栏-副标题栏高-状态栏
        stocyMinutesView.initBorder(
                0, 0, screenW, (screenH - (int) context.getResources().getDimension(R.dimen.titlebarSize) -
                        (int) context.getResources().getDimension(R.dimen.stocyMinTopHeight) -
                        clonumHeight -
                        CommonUtil.getStatusHeightForFragment(context))
        );
        //十字控件
        crossView = (CrossView) views.findViewById(R.id.crossView);
        crossView.setViewType(CrossView.VIEW_TYPE_H);
        crossView.setOnDrawCallback(this);
        stockService = new StockService(context);//业务处理类
        //加载圈圈
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //股票汇总信息
        top1 = views.findViewById(R.id.top1);//股票
        top2 = views.findViewById(R.id.top2);//指数
        stocyDetail = views.findViewById(R.id.stocyDetail);//股票买卖盘
        stocyNow = (TextView) views.findViewById(R.id.stocyNow);//当前价
        stocyAs = (TextView) views.findViewById(R.id.stocyAs);//实时涨幅
        stocyTime = (TextView) views.findViewById(R.id.stocyTime);//时间
        stocyHand = (TextView) views.findViewById(R.id.stocyHand);//现手
        stocySell1 = (TextView) views.findViewById(R.id.stocySell1);//卖盘
        stocyBug1 = (TextView) views.findViewById(R.id.stocyBug1);//买盘
        //指数汇总信息
        now = (TextView) views.findViewById(R.id.now);//当前点数
        stocyAs1 = (TextView) views.findViewById(R.id.stocyAs1);//涨幅比率
        stocyAs2 = (TextView) views.findViewById(R.id.stocyAs2);//涨幅点数
        priseNum = (TextView) views.findViewById(R.id.priseNum);//成交额
        riseCount = (TextView) views.findViewById(R.id.riseCount);//上涨家数
        balanceCount = (TextView) views.findViewById(R.id.balanceCount);//平盘家数
        downCount = (TextView) views.findViewById(R.id.downCount);//下跌家数
        //横屏数据
        current = (TextView) views.findViewById(R.id.current);//股价指数
        stocyAs3 = (TextView) views.findViewById(R.id.stocyAs3);//涨跌
        toDayPrice = (TextView) views.findViewById(R.id.toDayPrice);//开盘价
        nightDayPrice = (TextView) views.findViewById(R.id.nightDayPrice);//收盘价
        heigPrice = (TextView) views.findViewById(R.id.heigPrice);//最高价
        lowPrice = (TextView) views.findViewById(R.id.lowPrice);//最低价
        totalNum = (TextView) views.findViewById(R.id.totalNum);//成交量
        totalPrice = (TextView) views.findViewById(R.id.totalPrice);//成交额
        //点击打开买卖盘详细信息
        stocyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list == null || list.size() < 1 || detail == null) {
                    ToastUtil.showMessage(context, "暂无详情");
                    return;
                }
                stocyDetailDialog = new StocyDetailDialog(context, list.get(list.size() - 1), detail);
                stocyDetailDialog.showDialog();
            }
        });

        isShow("0");
    }

    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        loadData();
    }

    /**
     * 根据分时类型显示隐藏详细布局
     * @param type 类型（0股票或1指数）
     */
    private void isShow(String type) {
        if (type.equals("0")) {
            //个股分时
            top1.setVisibility(View.VISIBLE);
            top2.setVisibility(View.GONE);
        } else if (type.equals("1")) {
            //大盘分时
            top1.setVisibility(View.GONE);
            top2.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 下载数据
     */
    private void loadData() {
        if (loadView.setStatus(LoadView.LOADING)) {
            //是否刷新、股票代码、数据长度、回调函数
            stockService.getMinuteKchart(false, code, String.valueOf(DATA), this);
        }
    }

    private void setText(TextView tv, String value, int color) {
        if (tv != null) {
            tv.setText(value);
            if (color != 0) {
                tv.setTextColor(color);
            }
        }
    }

    /**
     * 计时回调
     * <p>每隔n分钟获取一次数据</p>
     */
    @Override
    public void onTimer() {
        stockService.getMinuteKchart(true, code, String.valueOf(DATA), this);//true刷新数据
    }

    //----------获取股票数据回调----------------------

    /**
     * 分时线图
     * @param list
     * @param closePrice
     */
    @Override
    public void success(List<?> list, double closePrice) {
        loadView.setStatus(LoadView.SUCCESS);
        this.list = (List<MinuteInfo>) list;
        if (list == null || list.size() <= 0) {//空数据
            loadView.setStatus(LoadView.EMPTY_DATA);
            return;
        }

        stocyMinutesView.startDraw(this.list, closePrice);//画图
        timerUtil.startTimer();//启动计时器
    }

    /**
     * 其他数据设置
     * @param t
     * @param <T>
     */
    @Override
    public <T> void successDetail(T t) {
        detail = (StocksDetail) t;
        String type = ((StocksDetail) t).getKind();
        isShow(type);
        if (null != t && type.equals("0") && list.size() > 0) {//股票
            //显示个股详情
            MinuteInfo bean = list.get(list.size() - 1);//最新股票状态数据
            stocyNow.setTextColor(ContextCompat.getColor(context, bean.getColor()));
            stocyNow.setText(String.valueOf(bean.getNow()));
            stocyAs.setText(bean.getStocyGains() + " " + bean.getStocyAs());
            stocyAs.setTextColor(ContextCompat.getColor(context, bean.getColor()));
            stocyTime.setText(KChartUtil.getMinute(bean.getMinute()));
            stocyTime.setTextColor(ContextCompat.getColor(context, bean.getColor()));
            stocyHand.setText((int) bean.getVolume() / 1000 + " B");
            stocyHand.setTextColor(ContextCompat.getColor(context, bean.getColor()));
            stocySell1.setText(detail.getSell1() + "  " + (int) Double.parseDouble(detail.getsAmount1()));
            stocyBug1.setText(detail.getBug1() + "  " + (int) Double.parseDouble(detail.getbAmount1()));
        } else if (null != t && type.equals("1") && list.size() > 0) {//指数
            //显示大盘详情
            MinuteInfo bean = list.get(list.size() - 1);
            setText(now, KChartUtil.paranDouble(detail.getNow()), ContextCompat.getColor(context, bean.getColor()));
            setText(stocyAs1, bean.getStocyGains(), ContextCompat.getColor(context, bean.getColor()));
            setText(stocyAs2, bean.getStocyAs(), ContextCompat.getColor(context, bean.getColor()));
            setText(priseNum, KChartUtil.paranDouble(detail.getTotalTurnover()), 0);
            setText(riseCount, KChartUtil.paranDouble(detail.getRiseCount()), 0);
            setText(balanceCount, KChartUtil.paranDouble(detail.getBalanceCount()), 0);
            setText(downCount, KChartUtil.paranDouble(detail.getDownCount()), 0);
            setText(current, KChartUtil.paranDouble(detail.getNow()), ContextCompat.getColor(context, bean.getColor()));
            setText(stocyAs3, bean.getStocyGains() + "  " + bean.getStocyAs(), ContextCompat.getColor(context, bean.getColor()));
            setText(toDayPrice, KChartUtil.paranDouble(detail.getNow()), 0);
            setText(nightDayPrice, KChartUtil.paranDouble(detail.getClose()), 0);
            setText(heigPrice, KChartUtil.paranDouble(detail.getHighest()), 0);
            setText(lowPrice, KChartUtil.paranDouble(detail.getLowest()), 0);
            setText(totalNum, KChartUtil.paranDouble(bean.getVolume()), 0);
            setText(totalPrice, KChartUtil.paranDouble(bean.getTurnover()), 0);
        }
    }

    @Override
    public void before(Object o) {

    }

    @Override
    public void failer(Object o) {
        loadView.setStatus(LoadView.ERROR);
    }

    //------------获取股票数据回调结束-----------------
    //-----------手势接口回调----------------
    @Override
    public void onLongMove(float x, float y) {
        float[] point = stocyMinutesView.getXY(x, y);
        if (null != point) {//显示10子控件
            crossView.longClickMove(list.get((int) point[2]), point[0], point[1]);
        }
    }

    @Override
    public void onUp() {

    }

    @Override
    public void onMove(int direction) {

    }

    @Override
    public void onScan(int direction) {

    }
    //----------------手势接口回调结束-------------------

    /**
     * 重新加载回调
     */
    @Override
    public void onTry() {
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.list != null && detail != null) {//画线图
            success(this.list, detail.getClose());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timerUtil.destroyTimer();//删除计时器
    }
}
