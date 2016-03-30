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
public class StockKFragment extends BaseFragment implements OnTryListener,
        IGetStockDataCallBack, CrossView.OnDrawCallback {
    private static final int DATA = 200;
    private String kType = "4";
    private static final int SHOW_MAX_LEN = 40;  //显示的最大的长度
    private static final int SHOW_SCAN_MAX_LEN = 80;  //缩放显示的最大的长度
    private static final int SHOW_SCAN_MIN_LEN = 20;  //缩放显示的最小的长度
    private static final int MOVE_LEN = 1;  //每次滑动新添加的数据
    private static final int SCAN_LEN = 1;  //每一次缩放添加或减少的数据

    //横竖屏当前显示的数据量（与上面的常量相对应，横屏显示量为竖屏的两倍）
    private int showMaxLen;
    private int showScanMaxLen;//最大缩放
    private int showScanMinLen;//最少缩放
    private int moveLen;//滑动步长
    private int scanLen;//缩放步长

    private int indexL;//当前的数据开头
    private int indexR;//当前的数据结束

    private SelectKTypeWindow selectKTypeWindow;
    private StocyKView stocyKView;
    private StockService stockService;
    private CrossView crossView;
    private LoadView loadView;
    private Context context;
    private List<SingleStockInfo> infos;
    private String code;//股票代码

    private Button kLineTypeBtn;
    private int kLineTypeBtnWidth, paddingTop;
    private int screenW, screenH;
    private View totalPanel;
    private TextView stocyTime, stocyPrice, stocyGains, stocyOpen, stocyHeight, stocyLow,
            stocyM5, stocyM10, stocyM20, stocyTotal, stocyTotalMD5, stocyTotalMD10;
    int clonumHeight = 0;//副标题高度
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        code = getArguments().getString("code");//股票代码
        isScreenOrient();
    }

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_stock_k;
    }

    /**
     * 初始化布局
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);

        stocyKView = (StocyKView) views.findViewById(R.id.stocyKView);//k线图
        screenW = CommonUtil.getScreenWidth(context);//屏宽
        screenH = CommonUtil.getScreenHeight(context);//屏高
        totalPanel = views.findViewById(R.id.totalPanel);//中间的的bar

        //横竖屏时布局不同（根据横竖屏设置副标题高度）
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏，则切换为横屏
            clonumHeight = (int) context.getResources().getDimension(R.dimen.colnumbarSize);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏，则切换为竖屏
            clonumHeight = 0;
        }

        //k线控件上方的高度标题栏+汇总栏+副标题栏+状态栏
        int topHeight = (int) context.getResources().getDimension(R.dimen.titlebarSize) +
                (int) context.getResources().getDimension(R.dimen.stocyTopHeight) +
                clonumHeight +
                CommonUtil.getStatusHeightForFragment(context);
        //k线控件的3分之2的位置
        totalPanel.setPadding(8, (int) context.getResources().getDimension(R.dimen.stocyTopHeight) + (screenH - topHeight) / 3 * 2, 0, 0);
        stocyKView.initBorder(0, 0, screenW, screenH - topHeight);//k线控件大小
        stockService = new StockService(context);//初始化业务类
        crossView = (CrossView) views.findViewById(R.id.crossView);//十字控件
        crossView.setViewType(CrossView.VIEW_TYPE_K);//k线模式
        crossView.setOnDrawCallback(this);//十字回调
        //加载圈圈
        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);

        setTypeBtn(views);//设置按钮

        //均线选择弹出框
        selectKTypeWindow = new SelectKTypeWindow(context);
        selectKTypeWindow.setOnItemClick(new OnItemClick() {
            @Override
            public void onItemClick(String selectItem) {
                kLineTypeBtn.setText(selectItem.substring(0, selectItem.indexOf(" ")));//按钮文字
                kType = selectItem.substring(selectItem.indexOf(" ") + 1);//k线类型
                if (loadView.setStatus(LoadView.LOADING)) {//后台获取数据
                    stockService.getKchartInfo(true, code, kType, String.valueOf(DATA),
                            StockKFragment.this);
                }
            }
        });

        //关联其他控件
        stocyTime = (TextView) views.findViewById(R.id.stocyTime);//股票时间
        stocyPrice = (TextView) views.findViewById(R.id.stocyPrice);//股票价钱
        stocyGains = (TextView) views.findViewById(R.id.stocyGains);//股票涨幅
        stocyOpen = (TextView) views.findViewById(R.id.stocyOpen);//开盘价
        stocyHeight = (TextView) views.findViewById(R.id.stocyHeight);//最高价
        stocyLow = (TextView) views.findViewById(R.id.stocyLow);//最低价
        stocyM5 = (TextView) views.findViewById(R.id.stocyM5);//5日均线
        stocyM10 = (TextView) views.findViewById(R.id.stocyM10);//10日均线
        stocyM20 = (TextView) views.findViewById(R.id.stocyM20);//20日均线
        stocyTotal = (TextView) views.findViewById(R.id.stocyTotal);//总手
        stocyTotalMD5 = (TextView) views.findViewById(R.id.stocyTotalMD5);//5日均线
        stocyTotalMD10 = (TextView) views.findViewById(R.id.stocyTotalMD10);//10日均线
    }


    /**
     * 选择K线图类型（日K，周K，月K）
     */
    private int x, y, x0, y0;
    private long fristTime = 0;

    /**
     * 圆形Button
     * <p>初始化和设置点击事件</p>
     * @param views
     */
    private void setTypeBtn(View views) {
        kLineTypeBtn = (Button) views.findViewById(R.id.kLineTypeBtn);//圆button
        kLineTypeBtnWidth = (int) context.getResources().getDimension(R.dimen.stocyKTypeBtnRadus) / 2;//按钮半径
        //标题栏+副标题栏+状态栏
        paddingTop = (int) (context.getResources().getDimension(R.dimen.titlebarSize) +
                clonumHeight + kLineTypeBtnWidth)
                + CommonUtil.getStatusHeightForFragment(context);
        //点击事件
        kLineTypeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下事件，记下时间和坐标
                        fristTime = System.currentTimeMillis();
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE://移动事件，记下移动后得坐标
                        x0 = (int) event.getRawX();
                        y0 = (int) event.getRawY();
                        //判断移动后的x坐标大于按钮半径，即没有出屏框(其他同理，即按钮不出边框的时候）
                        if (x0 > kLineTypeBtnWidth && x0 < screenW - kLineTypeBtnWidth && y0 >
                                paddingTop && y0 < screenH - kLineTypeBtnWidth) {
                            v.getParent()
                                    .requestDisallowInterceptTouchEvent(true);//拦截父控件点击事件

                            //移动按钮
                            kLineTypeBtn.layout(v.getLeft() + (x0 - x), v.getTop() + (y0 - y),
                                    v.getRight() + (x0 - x), v.getBottom() + (y0 - y));
                            x = x0;
                            y = y0;
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        long lastTime = System.currentTimeMillis();//记下松手时间
                        if (lastTime - fristTime < 150) {//事件过短判断为点击事件
                            if (!selectKTypeWindow.isShowing()) {
                                selectKTypeWindow.showDialog();//弹框
//                                fristTime = lastTime;
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
     * <p>横屏时控件大小数据*2</p>
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
    //-------------onDrawingCallback回调------------------
    //长按移动回调
    @Override
    public void onLongMove(float x, float y) {
        float[] point = stocyKView.getXY(x, y);//k线图计算坐标数据
        if (null != point) {
            crossView.longClickMove(null, point[0], point[1]);//通知重画十字
            //寻找当前的y点位置的股票数据，并更新当前数据到View
            refreshData(infos.subList(indexL, indexR + 1).get((int) point[2]));
        }

    }

    /**
     * 手指抬起事件，把最后的数据显示在View
     */
    @Override
    public void onUp() {
        refreshData(infos.get(infos.size() - 1));
    }

    /**
     * 移动事件
     * @param direction ： 向左移动还是向右移动
     */
    @Override
    public void onMove(int direction) {
        if (null == infos) {
            return;
        }

        if (direction < 0) {//左滑
            if (indexR == infos.size() - 1) {//当显示数据长度等于数据总长度说明不能移动
                return;
            }

            if (indexR + moveLen <= infos.size() - 1) {
                refreshData(indexL + moveLen, indexR + moveLen);
            } else {
                int tempR = indexR;
                //(infos.size() - 1 - tempR)移动最大长度
                refreshData(indexL + infos.size() - 1 - tempR, infos.size() - 1);
            }
        } else {//右滑
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

    /**
     * 缩放回调
     * @param direction ： 放大还是缩小
     */
    @Override
    public void onScan(int direction) {
        if (direction > 0) {
            //放大
            if (indexR - indexL <= showScanMinLen) {//不可无限放大
                return;
            }

            refreshData(indexL + scanLen, indexR - scanLen);
        } else {
            //缩小
            if (indexR - indexL >= showScanMaxLen) {//不可无限缩小
                return;
            }

            //计算边界值
            int tempL, tempR;
            if (indexL - scanLen < 0) {//出左边
                tempL = 0;
            } else {
                tempL = indexL - scanLen;
            }
            if (indexR + scanLen > infos.size() - 1) {//出右边
                tempR = infos.size() - 1;
            } else {
                tempR = indexR + scanLen;
            }
            refreshData(tempL, tempR);
        }

    }
    //--------------onDrawingCallback接口回调结束-----------------

    /**
     * 刷新数据
     * <p>显示k线图以外的数据</p>
     * @param bean 股票最新的一项数据
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

    //-------------获取k线数据回调-----------------

    /**
     * 获取数据成功回调
     * <p>成功后把数据显示并画k线</p>
     * @param list
     * @param close
     */
    @Override
    public void success(List<?> list, double close) {
        loadView.setStatus(LoadView.SUCCESS);

        infos = (List<SingleStockInfo>) list;

        if (infos == null || infos.size() <= 0) {//空数据
            loadView.setStatus(LoadView.EMPTY_DATA);
            return;
        }

        refreshData(infos.get(infos.size() - 1));//显示数据
        //画k线
        refreshData(infos.size() >= showMaxLen ? infos.size() - showMaxLen : 0, infos.size() - 1);
    }

    @Override
    public <T> void successDetail(T t) {

    }

    @Override
    public void before(Object o) {

    }

    @Override
    public void failer(Object o) {
        loadView.setStatus(LoadView.ERROR);
    }

    //------------获取k线数据回调结束--------------------

    /**
     * 画k线图
     * @param l 数据头
     * @param r 数据长度
     */
    private void refreshData(int l, int r) {
        if (infos.size() > 0) {
            setLR(l, r);
            stocyKView.startDraw(infos.subList(indexL, indexR + 1));//裁剪数据开始画k线
        }
    }
    /**
     * 设置当前数据的index
     * @param l 数据头
     * @param r 数据尾
     */
    private void setLR(int l, int r) {
        this.indexL = l;
        this.indexR = r;
    }

    @Override
    public void onTry() {
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.infos != null) {
            success(this.infos, -1);//数据不为空，就把数据显示出来
        }
    }
}
