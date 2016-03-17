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
public class StockMinFragment extends BaseFragment implements OnTryListener, IGetStockDataCallBack, CrossView.OnDrawCallback, TimerUtil.OnTimerCallback {
    private static final int DATA = 360;
    private static final int GET_DATA_TIME = 5000;
    private StocyDetailDialog stocyDetailDialog;
    private StocksDetail detail;
    private StocyMinutesView stocyMinutesView;
    private CrossView crossView;
    private LoadView loadView;
    private View top1, top2, stocyDetail;
    private TextView stocyNow, stocyAs, stocyTime, stocyHand, stocySell1, stocyBug1; //个股信息
    private TextView now, stocyAs1, stocyAs2, priseNum, riseCount, balanceCount, downCount; //大盘信息
    private TextView current, stocyAs3, toDayPrice, nightDayPrice, heigPrice, lowPrice, totalNum, totalPrice;  //大盘信息横屏

    private List<MinuteInfo> list;
    private StockService stockService;
    private TimerUtil timerUtil;
    private Context context;
    private String code;
    private int screenW, screenH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        timerUtil = new TimerUtil(GET_DATA_TIME);
        timerUtil.setOnTimerCallback(this);
        code = getArguments().getString("code");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_stock_min;
    }

    @Override
    protected void findView(View views) {
        super.findView(views);
        screenW = CommonUtil.getScreenWidth(context);
        screenH = CommonUtil.getScreenHeight(context);
        stocyMinutesView = (StocyMinutesView) views.findViewById(R.id.stocyMinutesView);

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
        stocyMinutesView.initBorder(
                0, 0, screenW, (screenH - (int) context.getResources().getDimension(R.dimen.titlebarSize) -
                        (int) context.getResources().getDimension(R.dimen.stocyMinTopHeight) -
                        clonumHeight -
                        CommonUtil.getStatusHeightForFragment(context))
        );
        crossView = (CrossView) views.findViewById(R.id.crossView);
        crossView.setViewType(CrossView.VIEW_TYPE_H);
        crossView.setOnDrawCallback(this);
        stockService = new StockService(context);
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);

        top1 = views.findViewById(R.id.top1);
        top2 = views.findViewById(R.id.top2);
        stocyDetail = views.findViewById(R.id.stocyDetail);
        stocyNow = (TextView) views.findViewById(R.id.stocyNow);
        stocyAs = (TextView) views.findViewById(R.id.stocyAs);
        stocyTime = (TextView) views.findViewById(R.id.stocyTime);
        stocyHand = (TextView) views.findViewById(R.id.stocyHand);
        stocySell1 = (TextView) views.findViewById(R.id.stocySell1);
        stocyBug1 = (TextView) views.findViewById(R.id.stocyBug1);

        now = (TextView) views.findViewById(R.id.now);
        stocyAs1 = (TextView) views.findViewById(R.id.stocyAs1);
        stocyAs2 = (TextView) views.findViewById(R.id.stocyAs2);
        priseNum = (TextView) views.findViewById(R.id.priseNum);
        riseCount = (TextView) views.findViewById(R.id.riseCount);
        balanceCount = (TextView) views.findViewById(R.id.balanceCount);
        downCount = (TextView) views.findViewById(R.id.downCount);

        current = (TextView) views.findViewById(R.id.current);
        stocyAs3 = (TextView) views.findViewById(R.id.stocyAs3);
        toDayPrice = (TextView) views.findViewById(R.id.toDayPrice);
        nightDayPrice = (TextView) views.findViewById(R.id.nightDayPrice);
        heigPrice = (TextView) views.findViewById(R.id.heigPrice);
        lowPrice = (TextView) views.findViewById(R.id.lowPrice);
        totalNum = (TextView) views.findViewById(R.id.totalNum);
        totalPrice = (TextView) views.findViewById(R.id.totalPrice);

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
     * 加载界面列表
     */
    private void loadData() {
        if (loadView.setStatus(LoadView.LOADING)) {
            stockService.getMinuteKchart(false, code, String.valueOf(DATA), this);
        }
    }

    @Override
    public void onTimer() {
        stockService.getMinuteKchart(true, code, String.valueOf(DATA), this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.list != null && detail != null) {
            success(this.list, detail.getClose());
        }
    }

    @Override
    public void success(List<?> list, double closePrice) {
        loadView.setStatus(LoadView.SUCCESS);
        this.list = (List<MinuteInfo>) list;
        if (list == null || list.size() <= 0) {
            loadView.setStatus(LoadView.EMPTY_DATA);
            return;
        }

        stocyMinutesView.startDraw(this.list, closePrice);
        timerUtil.startTimer();
    }

    @Override
    public <T> void successDetail(T t) {
        detail = (StocksDetail) t;
        String type = ((StocksDetail) t).getKind();
        isShow(type);
        if (null != t && type.equals("0") && list.size() > 0) {
            //显示个股详情
            MinuteInfo bean = list.get(list.size() - 1);
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
        } else if (null != t && type.equals("1") && list.size() > 0) {
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

    private void setText(TextView tv, String value, int color) {
        if (tv != null) {
            tv.setText(value);
            if (color != 0) {
                tv.setTextColor(color);
            }
        }
    }

    @Override
    public void onLongMove(float x, float y) {
        float[] point = stocyMinutesView.getXY(x, y);
        if (null != point) {
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

    @Override
    public void before(Object o) {

    }

    @Override
    public void failer(Object o) {
        loadView.setStatus(LoadView.ERROR);
    }

    @Override
    public void onTry() {
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timerUtil.destroyTimer();
    }
}
