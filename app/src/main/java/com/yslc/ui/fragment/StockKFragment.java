package com.yslc.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.bean.SingleStockInfo;
import com.yslc.ui.dialog.SelectKTypeWindow;
import com.yslc.inf.IGetStockDataCallBack;
import com.yslc.inf.OnItemClick;
import com.yslc.data.service.StockService;
import com.yslc.util.CommonUtil;
import com.yslc.util.KChartUtil;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;
import com.yslc.view.CrossView;
import com.yslc.view.StocyKView;

import java.util.List;

/**
 * K线图Fragment
 *
 * @author HH
 */
public class StockKFragment extends BaseFragment implements OnTryListener, IGetStockDataCallBack, CrossView.OnDrawCallback {
    private static final int DATA = 200;
    private String kType = "4";
    private static final int SHOW_MAX_LEN = 40;  //显示的最大的长度
    private static final int SHOW_SCAN_MAX_LEN = 80;  //缩放显示的最大的长度
    private static final int SHOW_SCAN_MIN_LEN = 20;  //缩放显示的最小的长度
    private static final int MOVE_LEN = 1;  //每次滑动新添加的数据
    private static final int SCAN_LEN = 1;  //每一次缩放添加或减少的数据

    //横竖屏当前显示的数据量（与上面的常量相对应，横屏显示量为竖屏的两倍）
    private int showMaxLen;
    private int showScanMaxLen;
    private int showScanMinLen;
    private int moveLen;
    private int scanLen;

    private int indexL;
    private int indexR;

    private SelectKTypeWindow selectKTypeWindow;
    private StocyKView stocyKView;
    private StockService stockService;
    private CrossView crossView;
    private LoadView loadView;
    private Context context;
    private List<SingleStockInfo> infos;
    private String code;

    private Button kLineTypeBtn;
    private int kLineTypeBtnWidth, paddingTop;
    private int screenW, screenH;
    private View totalPanel;
    private TextView stocyTime, stocyPrice, stocyGains, stocyOpen, stocyHeight, stocyLow, stocyM5, stocyM10, stocyM20, stocyTotal, stocyTotalMD5, stocyTotalMD10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        code = getArguments().getString("code");
        isScreenOrient();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_stock_k;
    }

    @Override
    protected void findView(View views) {
        super.findView(views);

        stocyKView = (StocyKView) views.findViewById(R.id.stocyKView);
        screenW = CommonUtil.getScreenWidth(context);
        screenH = CommonUtil.getScreenHeight(context);
        totalPanel = views.findViewById(R.id.totalPanel);

        //横竖屏时布局不同
        int orientation = getResources().getConfiguration().orientation;
        int clonumHeight = 0;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏，则切换为横屏
            clonumHeight = (int) context.getResources().getDimension(R.dimen.colnumbarSize);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏，则切换为竖屏
            clonumHeight = 0;
        }
        int topHeight = (int) context.getResources().getDimension(R.dimen.titlebarSize) +
                (int) context.getResources().getDimension(R.dimen.stocyTopHeight) +
                clonumHeight +
                CommonUtil.getStatusHeightForFragment(context);

        totalPanel.setPadding(8, (int) context.getResources().getDimension(R.dimen.stocyTopHeight) + (screenH - topHeight) / 3 * 2, 0, 0);
        stocyKView.initBorder(0, 0, screenW, screenH - topHeight);
        stockService = new StockService(context);
        crossView = (CrossView) views.findViewById(R.id.crossView);
        crossView.setViewType(CrossView.VIEW_TYPE_K);
        crossView.setOnDrawCallback(this);
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);

        setTypeBtn(views);
        selectKTypeWindow = new SelectKTypeWindow(context);
        selectKTypeWindow.setOnItemClick(new OnItemClick() {
            @Override
            public void onItemClick(String selectItem) {
                kLineTypeBtn.setText(selectItem.substring(0, selectItem.indexOf(" ")));
                kType = selectItem.substring(selectItem.indexOf(" ") + 1);
                if (loadView.setStatus(LoadView.LOADING)) {
                    stockService.getKchartInfo(true, code, kType, String.valueOf(DATA), StockKFragment.this);
                }
            }
        });

        stocyTime = (TextView) views.findViewById(R.id.stocyTime);
        stocyPrice = (TextView) views.findViewById(R.id.stocyPrice);
        stocyGains = (TextView) views.findViewById(R.id.stocyGains);
        stocyOpen = (TextView) views.findViewById(R.id.stocyOpen);
        stocyHeight = (TextView) views.findViewById(R.id.stocyHeight);
        stocyLow = (TextView) views.findViewById(R.id.stocyLow);
        stocyM5 = (TextView) views.findViewById(R.id.stocyM5);
        stocyM10 = (TextView) views.findViewById(R.id.stocyM10);
        stocyM20 = (TextView) views.findViewById(R.id.stocyM20);
        stocyTotal = (TextView) views.findViewById(R.id.stocyTotal);
        stocyTotalMD5 = (TextView) views.findViewById(R.id.stocyTotalMD5);
        stocyTotalMD10 = (TextView) views.findViewById(R.id.stocyTotalMD10);
    }


    /**
     * 选择K线图类型（日K，周K，月K）
     */
    private int x, y, x0, y0;
    private long fristTime = 0;

    private void setTypeBtn(View views) {
        kLineTypeBtn = (Button) views.findViewById(R.id.kLineTypeBtn);
        kLineTypeBtnWidth = (int) context.getResources().getDimension(R.dimen.stocyKTypeBtnRadus) / 2;
        paddingTop = (int) (context.getResources().getDimension(R.dimen.titlebarSize) +
                context.getResources().getDimension(R.dimen.colnumbarSize) + kLineTypeBtnWidth);
        kLineTypeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fristTime = System.currentTimeMillis();
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        x0 = (int) event.getRawX();
                        y0 = (int) event.getRawY();
                        if (x0 > kLineTypeBtnWidth && x0 < screenW - kLineTypeBtnWidth && y0 > paddingTop && y0 < screenH - kLineTypeBtnWidth) {
                            v.getParent()
                                    .requestDisallowInterceptTouchEvent(true);

                            //边界判断
                            kLineTypeBtn.layout(v.getLeft() + (x0 - x), v.getTop() + (y0 - y), v.getRight() + (x0 - x), v.getBottom() + (y0 - y));
                            x = x0;
                            y = y0;
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        long lastTime = System.currentTimeMillis();
                        if (lastTime - fristTime < 150) {
                            if (!selectKTypeWindow.isShowing()) {
                                selectKTypeWindow.showDialog();
                                fristTime = lastTime;
                            }
                        }
                        break;
                }

                return false;
            }
        });
    }

    /**
     * 判断横屏还是竖屏进行初始化
     */
    private void isScreenOrient() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            showMaxLen = SHOW_MAX_LEN;
            showScanMaxLen = SHOW_SCAN_MAX_LEN;
            showScanMinLen = SHOW_SCAN_MIN_LEN;
            moveLen = MOVE_LEN;
            scanLen = SCAN_LEN;
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            showMaxLen = SHOW_MAX_LEN * 2;
            showScanMaxLen = SHOW_SCAN_MAX_LEN * 2;
            showScanMinLen = SHOW_SCAN_MIN_LEN * 2;
            moveLen = MOVE_LEN * 2;
            scanLen = SCAN_LEN * 2;
        }
    }

    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();

        loadData();
    }

    /**
     * 加载界面列表
     */
    private void loadData() {
        if (loadView.setStatus(LoadView.LOADING)) {
            stockService.getKchartInfo(false, code, kType, String.valueOf(DATA), this);
        }
    }

    @Override
    public void before(Object o) {

    }

    @Override
    public void onLongMove(float x, float y) {
        float[] point = stocyKView.getXY(x, y);
        if (null != point) {
            crossView.longClickMove(null, point[0], point[1]);
            refreshData(infos.subList(indexL, indexR + 1).get((int) point[2]));
        }

    }

    @Override
    public void onUp() {
        refreshData(infos.get(infos.size() - 1));
    }

    /**
     * 刷新K线图数据
     */
    private void refreshData(SingleStockInfo bean) {
        if (bean == null) {
            return;
        }

        int color = ContextCompat.getColor(context, bean.getColor());
        stocyTime.setText(bean.getDate());
        stocyPrice.setTextColor(color);
        stocyPrice.setText(KChartUtil.paranDouble(bean.getClose()));
        stocyGains.setTextColor(color);
        stocyGains.setText(bean.getStocyAs() + " " + bean.getStocyGains());
        stocyOpen.setTextColor(color);
        stocyOpen.setText(KChartUtil.paranDouble(bean.getOpen()));
        stocyHeight.setTextColor(color);
        stocyHeight.setText(KChartUtil.paranDouble(bean.getHigh()));
        stocyLow.setTextColor(color);
        stocyLow.setText(KChartUtil.paranDouble(bean.getLow()));
        stocyM5.setText("MD5:  " + KChartUtil.paranDouble(bean.getMaValue5()));
        stocyM10.setText("MD10:" + KChartUtil.paranDouble(bean.getMaValue10()));
        stocyM20.setText("MD20:" + KChartUtil.paranDouble(bean.getMaValue20()));
        stocyTotal.setText("总量(万手):" + KChartUtil.paranDouble(bean.getTotalCount() / 10000));
        stocyTotalMD5.setText("MD5:" + KChartUtil.paranDouble(bean.getTotalValue5() / 10000));
        stocyTotalMD10.setText("MD10:" + KChartUtil.paranDouble(bean.getTotalValue10() / 10000));
    }

    @Override
    public void onMove(int direction) {
        if (null == infos) {
            return;
        }

        if (direction < 0) {
            if (indexR == infos.size() - 1) {
                return;
            }

            if (indexR + moveLen <= infos.size() - 1) {
                refreshData(indexL + moveLen, indexR + moveLen);
            } else {
                int tempR = indexR;
                refreshData(indexL + infos.size() - 1 - tempR, infos.size() - 1);
            }
        } else {
            if (indexL <= 0) {
                return;
            }

            if (indexL - moveLen >= 0) {
                refreshData(indexL - moveLen, indexR - moveLen);
            } else {
                int tempL = indexL;
                refreshData(0, indexR - tempL);
            }
        }
    }

    @Override
    public void onScan(int direction) {
        if (direction > 0) {
            //放大
            if (indexR - indexL <= showScanMinLen) {
                return;
            }

            refreshData(indexL + scanLen, indexR - scanLen);
        } else {
            //缩小
            if (indexR - indexL >= showScanMaxLen) {
                return;
            }

            //计算边界值
            int tempL, tempR;
            if (indexL - scanLen < 0) {
                tempL = 0;
            } else {
                tempL = indexL - scanLen;
            }
            if (indexR + scanLen > infos.size() - 1) {
                tempR = infos.size() - 1;
            } else {
                tempR = indexR + scanLen;
            }
            refreshData(tempL, tempR);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.infos != null) {
            success(this.infos, -1);
        }
    }

    @Override
    public void success(List<?> list, double close) {
        loadView.setStatus(LoadView.SUCCESS);

        infos = (List<SingleStockInfo>) list;

        if (infos == null || infos.size() <= 0) {
            loadView.setStatus(LoadView.EMPTY_DATA);
            return;
        }

        refreshData(infos.get(infos.size() - 1));
        refreshData(infos.size() >= showMaxLen ? infos.size() - showMaxLen : 0, infos.size() - 1);
    }

    private void refreshData(int l, int r) {
        if (infos.size() > 0) {
            setLR(l, r);
            stocyKView.startDraw(infos.subList(indexL, indexR + 1));
        }
    }

    private void setLR(int l, int r) {
        this.indexL = l;
        this.indexR = r;
    }

    @Override
    public <T> void successDetail(T t) {

    }

    @Override
    public void failer(Object o) {
        loadView.setStatus(LoadView.ERROR);
    }

    @Override
    public void onTry() {
        loadData();
    }
}
