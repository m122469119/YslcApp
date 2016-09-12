package com.yslc.data.service;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.bean.MinuteInfo;
import com.yslc.bean.SingleStockInfo;
import com.yslc.bean.StocksDetail;
import com.yslc.inf.IGetStockDataCallBack;
import com.yslc.util.HttpUtil;
import com.yslc.util.KChartUtil;
import com.yslc.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 股市行情数据请求服务
 * 分时图数据请求
 * K线图数据请求
 *
 * @author HH
 */
public class StockService {
    public static final int UP_COLOR = R.color.stocyUp;
    public static final int DOWN_COLOR = R.color.stocyDown;
    private Context context;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public StockService(Context context) {
        this.context = context;
        sharedPreferencesUtil = new SharedPreferencesUtil(context, Constant.CACHE_STOCK_DATA_NAME);
    }

    /**
     * 获取分时图数据
     *
     * @param isRefresh:是否计时刷新，及时刷新需要访问最新数据
     * @param symbol:股票代码
     * @param datalen:每次获取多少条数据
     * @param callBack 回调函数
     */
    public void getMinuteKchart(boolean isRefresh, String symbol, String datalen, IGetStockDataCallBack callBack) {
        //不是定时刷新则优先使用缓存数据
        if (!isRefresh) {//不是定时器的刷新
            //是否有分时缓存数据
            String cacheData = getCacheData(Constant.CACHE_STOCK_MIN_KEY);//缓存数据
            if (cacheData.length() > 0) {//缓存数据＞0
                StocksDetail d = parseDetail(cacheData);//解析数据
                callBack.success(parseHourJson(cacheData, d.getClose()), d.getClose());
                callBack.successDetail(d);
                return;
            }
        }


        RequestParams params = new RequestParams();
        params.put("symbol", symbol);//股票代码
        params.put("datalen", datalen);//数据长度
        params.put("p", HttpUtil.P);
        HttpUtil.post(HttpUtil.GET_STOCY_H_DATA, context, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                if (null != s && s.length() > 0) {
                    StocksDetail d = parseDetail(s);
                    //解析数据出错返回空
                    if(d == null){
                        callBack.failer(null);
                        return;
                    }
                    callBack.success(parseHourJson(s, d.getClose()), d.getClose());
                    callBack.successDetail(d);

                    //缓存分时数据
                    putCacheData(Constant.CACHE_STOCK_MIN_KEY, s);
                } else {
                    callBack.failer(null);
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                callBack.failer(null);
            }

        });
    }

    /**
     * 解析分时图数据
     */
    private ArrayList<MinuteInfo> parseHourJson(String result, double closePrice) {
        ArrayList<MinuteInfo> list = new ArrayList<>();
        try {
            JSONObject tatilJo = new JSONObject(result);
            JSONArray ja = tatilJo.getJSONArray("MinuteData");
            MinuteInfo info;
            JSONObject jo;
            for (int i = 0, len = ja.length(); i < len; i++) {
                jo = ja.getJSONObject(i);
                info = new MinuteInfo();
                info.setMinute(jo.optString("date"));
                info.setNow(Double.parseDouble(jo.optString("current")));
                info.setVolumeTatil(Double.parseDouble(jo.optString("volume")));
                info.setTurnoverTatil(Double.parseDouble(jo.optString("turnover")));
                info.setAvgPrice(info.getTurnover() / info.getVolume());
                list.add(info);
            }
            //计算成交量成交额，跌涨幅，计算分时均价
            KChartUtil.calcDealAvg(list);
            KChartUtil.calcGainsH(list, closePrice);
            KChartUtil.calcHAvg(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析分时详情
     * @param s json数据
     */
    private StocksDetail parseDetail(String s) {
        StocksDetail bean = new StocksDetail();
        try {
            JSONObject jo = new JSONObject(s).getJSONObject("quote");
            bean.setKind(new JSONObject(s).optString("type"));
            bean.setClose(Double.parseDouble(jo.optString("closing")));
            bean.setBug1(jo.optString("buy1"));
            bean.setSell1(jo.optString("sell1"));
            bean.setsAmount1(jo.optString("sAmount1"));
            bean.setbAmount1(jo.optString("bAmount1"));

            bean.setBug2(jo.optString("buy2"));
            bean.setSell2(jo.optString("sell2"));
            bean.setsAmount2(jo.optString("sAmount2"));
            bean.setbAmount2(jo.optString("bAmount2"));

            bean.setBug3(jo.optString("buy3"));
            bean.setSell3(jo.optString("sell3"));
            bean.setsAmount3(jo.optString("sAmount3"));
            bean.setbAmount3(jo.optString("bAmount3"));

            bean.setBug4(jo.optString("buy4"));
            bean.setSell4(jo.optString("sell1"));
            bean.setsAmount4(jo.optString("sAmount4"));
            bean.setbAmount4(jo.optString("bAmount4"));

            bean.setBug5(jo.optString("buy5"));
            bean.setSell5(jo.optString("sell5"));
            bean.setsAmount5(jo.optString("sAmount5"));
            bean.setbAmount5(jo.optString("bAmount5"));

            bean.setRiseCount(Double.parseDouble(jo.optString("riseCount")));
            bean.setDownCount(Double.parseDouble(jo.optString("downCount")));
            bean.setBalanceCount(Double.parseDouble(jo.optString("balanceCount")));
            bean.setTotalTurnover(Double.parseDouble(jo.optString("totalTurnover")));
            bean.setHighest(Double.parseDouble(jo.optString("highest")));
            bean.setLowest(Double.parseDouble(jo.optString("lowest")));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return bean;
    }

    /**
     * 获取K线图数据
     *
     * @param isRefresh:是否计时刷新，及时刷新需要访问最新数据
     * @param symbol:股票代码
     * @param klinetype:K线图类型(0:五分钟K线，1:15分钟K线， 2:30分钟K线， 3:60分钟K线，4:日K线，5:周线，6:月线)
     * @param datalen:每次获取多少条数据
     */
    public void getKchartInfo(boolean isRefresh, String symbol, String klinetype, String datalen,
                              IGetStockDataCallBack callBack) {
        //是否有缓存数据
        if (!isRefresh) {//不是定时器更新
            //获取对应的K线缓存数据（有缓存数据优先使用）
            String cacheData = getCacheData(Constant.CACHE_STOCK_K_KEY + klinetype);
            if (cacheData.length() > 0) {
                callBack.success(priseKchatJson(cacheData), -1);
                return;
            }
        }

        RequestParams params = new RequestParams();
        params.put("symbol", symbol);
        params.put("klinetype", klinetype);
        params.put("datalen", datalen);
        params.put("p", HttpUtil.P);
        HttpUtil.post(HttpUtil.GET_STOCY_K_DATA, context, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                callBack.success(priseKchatJson(s), -1);

                //缓存K线数据
                putCacheData(Constant.CACHE_STOCK_K_KEY + klinetype, s);
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
            }

        });
    }

    /**
     * 解析K线图数据
     */
    public ArrayList<SingleStockInfo> priseKchatJson(String result) {
        ArrayList<SingleStockInfo> list = new ArrayList<>();
        try {
            JSONArray ja = new JSONObject(result).getJSONArray("KLine");
            JSONObject jo;
            SingleStockInfo ssi;
            for (int i = 0, len = ja.length(); i < len; i++) {
                jo = ja.getJSONObject(i);
                ssi = new SingleStockInfo();
                ssi.setDate(jo.optString("date").substring(0, jo.optString("date").indexOf(" ")));
                ssi.setOpen(Double.parseDouble(jo.optString("opening")));
                ssi.setClose(Double.parseDouble(jo.optString("closing")));
                ssi.setHigh(Double.parseDouble(jo.optString("highest")));
                ssi.setLow(Double.parseDouble(jo.optString("lowest")));
                ssi.setColor(Double.parseDouble(jo.optString("opening")) < Double.parseDouble(jo.optString("closing")) ? UP_COLOR : DOWN_COLOR);
                ssi.setTotalCount(Double.parseDouble(jo.optString("volume")));
                ssi.setTotalPrice(Double.parseDouble(jo.optString("turnover")));
                list.add(ssi);
            }

            //计算跌涨幅
            KChartUtil.calcGains(list);

            //分别计算五日十日二十日均
            KChartUtil.calcMAF2T(list, 5);
            KChartUtil.calcMAF2T(list, 10);
            KChartUtil.calcMAF2T(list, 20);

            //分别计算截成交量五日十日均
            KChartUtil.calcMAF2TS(list, 5);
            KChartUtil.calcMAF2TS(list, 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 保存数据
     */
    private void putCacheData(String key, String data) {
        sharedPreferencesUtil.setString(key, data);
    }

    /**
     * 获取数据
     */
    private String getCacheData(String key) {
        return sharedPreferencesUtil.getString(key);
    }

}
