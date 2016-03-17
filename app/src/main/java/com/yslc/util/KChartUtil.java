package com.yslc.util;

import com.yslc.bean.MinuteInfo;
import com.yslc.bean.SingleStockInfo;
import com.yslc.data.service.StockService;

import java.util.List;

/**
 * 股市行情数据处理工具类
 *
 * @author HH
 */
public class KChartUtil {

    /**
     * 获取时分
     *
     * @param minute：format(yyyy-mm hh:ss:mm)
     * @return : str
     */
    public static String getMinute(String minute) {
        return minute.substring(minute.indexOf(" ") + 1, minute.lastIndexOf(":"));
    }

    /**
     * 计算K线图均线
     *
     * @param kChartDayBean:K线实体
     * @param days：几日均
     * @return list
     */
    public static List<SingleStockInfo> calcMAF2T(
            List<SingleStockInfo> kChartDayBean, int days) {

        if (days < 2) {
            return null;
        }

        float sum = 0;
        float avg;
        for (int i = 0; i < kChartDayBean.size(); i++) {
            //获取成交值
            float close = (float) kChartDayBean.get(i).getClose();
            if (i < days) {
                //这里是不足N天的时候，直接除以天数得出均值
                sum = sum + close;
                avg = sum / (i + 1f);
            } else {
                //加上第n+1天的值再减去第一天的值得出n天的均值
                sum = sum
                        + close
                        - (float) kChartDayBean.get(i - days)
                        .getClose();
                avg = sum / days;
            }
            // kChartDayBean.get(i).setMaValue5(avg);
            if (days == 5) {
                //这里是五日均
                kChartDayBean.get(i).setMaValue5(avg);
            } else if (days == 10) {
                //这里是十日均
                kChartDayBean.get(i).setMaValue10(avg);
            } else if (days == 20) {
                //这里是20日均
                kChartDayBean.get(i).setMaValue20(avg);
            }
        }
        return kChartDayBean;
    }

    /**
     * 计算K线图成交量均线
     *
     * @param kChartDayBean:K线实体
     * @param days：几日均
     * @return list
     */
    public static List<SingleStockInfo> calcMAF2TS(
            List<SingleStockInfo> kChartDayBean, int days) {

        if (days < 2) {
            return null;
        }

        float sum = 0;
        float avg;
        for (int i = 0; i < kChartDayBean.size(); i++) {
            //获取成交值
            float totalCount = (float) kChartDayBean.get(i).getTotalCount();
            if (i < days) {
                //这里是不足N天的时候，直接除以天数得出均值
                sum = sum + totalCount;
                avg = sum / (i + 1f);
            } else {
                //加上第n+1天的值再减去第一天的值得出n天的均值
                sum = sum
                        + totalCount
                        - (float) kChartDayBean.get(i - days)
                        .getTotalCount();
                avg = sum / days;
            }
            // kChartDayBean.get(i).setMaValue5(avg);
            if (days == 5) {
                //这里是五日均
                kChartDayBean.get(i).setTotalValue5(avg);
            } else if (days == 10) {
                //这里是十日均
                kChartDayBean.get(i).setTotalValue10(avg);
            }
        }
        return kChartDayBean;
    }

    /**
     * 计算分时成交量和成交额（服务器端获取的是总的成交量和成交额）
     *
     * @param list：分时列表
     * @return list;
     */
    public static List<MinuteInfo> calcDealAvg(
            List<MinuteInfo> list) {
        MinuteInfo todayBean, lastdayBean;
        for (int i = 0, len = list.size(); i < len; i++) {
            todayBean = list.get(i);
            if (i == 0) {
                todayBean.setVolume(todayBean.getVolumeTatil());
                todayBean.setTurnover(todayBean.getTurnoverTatil());
            } else {
                lastdayBean = list.get(i - 1);
                todayBean.setVolume(todayBean.getVolumeTatil() - lastdayBean.getVolumeTatil());
                todayBean.setTurnover(todayBean.getTurnoverTatil() - lastdayBean.getTurnoverTatil());
            }
        }

        return list;
    }

    /**
     * 计算分时均价
     *
     * @param list：分时列表
     * @return list;
     */
    public static List<MinuteInfo> calcHAvg(
            List<MinuteInfo> list) {
        MinuteInfo todayBean;
        for (int i = 0, len = list.size(); i < len; i++) {
            todayBean = list.get(i);
            todayBean.setAvgPrice(todayBean.getTurnoverTatil()
                    / todayBean.getVolumeTatil() / 100);
        }

        return list;
    }

    /**
     * 计算分时线跌涨幅
     *
     * @param list：分时列表
     * @return list
     */
    public static List<MinuteInfo> calcGainsH(
            List<MinuteInfo> list, double close) {
        MinuteInfo todayBean;
        for (int i = 0, len = list.size(); i < len; i++) {
            todayBean = list.get(i);
            todayBean.setStocyAs(paranDouble(todayBean.getNow() - close));
            todayBean.setStocyGains(paranDouble((todayBean.getNow() - close) / close * 100) + "%");

            if (todayBean.getNow() - close >= 0) {
                todayBean.setColor(StockService.UP_COLOR);
            } else {
                todayBean.setColor(StockService.DOWN_COLOR);
            }
        }
        return list;
    }

    /**
     * 计算k线跌涨幅
     *
     * @param list：K线列表
     * @return list
     */
    public static List<SingleStockInfo> calcGains(
            List<SingleStockInfo> list) {
        SingleStockInfo todayBean, lastdayBean;
        double d;
        for (int i = 0, len = list.size(); i < len; i++) {
            todayBean = list.get(i);
            if (i == 0) {
                todayBean.setStocyAs("0");
                todayBean.setStocyGains("0");
            } else {
                lastdayBean = list.get(i - 1);
                d = todayBean.getClose() - lastdayBean.getClose();
                todayBean.setStocyAs(paranDouble(d));
                todayBean.setStocyGains(paranDouble(d / lastdayBean.getClose() * 100) + "%");
            }
        }
        return list;
    }

    /**
     * 计算出最大值
     */
    public static double getMax(double... num) {
        double max = num[0];
        for (double n : num) {
            max = Math.max(max, n);
        }
        return max;
    }

    /**
     * 计算出最小值
     */
    public static double getMin(double... num) {
        double min = num[0];
        for (double n : num) {
            min = Math.min(min, n);
        }
        return min;
    }

    /**
     * double保留两位小数
     */
    public static String paranDouble(double d) {
        return String.format("%.2f", d);
    }


}
