package com.yslc.bean;

/**
 * K线图Bean
 *
 * @author HH
 */
public class SingleStockInfo {
    private int color;// 如果是跌，标记颜色为绿色  如果是涨，标记颜色为红色
    private double open;// 开盘价
    private double close; // 收盘价
    private double high;// 最高价
    private double low;// 最低价
    private String date;// 日期
    private String stocyGains; //涨幅
    private String stocyAs; //跌涨
    private double totalCount;// 一天成交的手数，总成交量
    private double totalPrice;// 总成交金额;
    private double maValue5;// 5日均线值
    private double maValue10;// 10日均线值
    private double maValue20;// 20日均线值
    private double totalValue5;// 成交量5日均线值
    private double totalValue10;// 成交量10日均线值


    public double getMaValue10() {
        return maValue10;
    }

    public void setMaValue10(double maValue10) {
        this.maValue10 = maValue10;
    }

    public double getMaValue20() {
        return maValue20;
    }

    public void setMaValue20(double maValue20) {
        this.maValue20 = maValue20;
    }

    public double getMaValue5() {
        return maValue5;
    }

    public void setMaValue5(double maValue) {
        this.maValue5 = maValue;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStocyGains() {
        return stocyGains;
    }

    public void setStocyGains(String stocyGains) {
        this.stocyGains = stocyGains;
    }

    public String getStocyAs() {
        return stocyAs;
    }

    public void setStocyAs(String stocyAs) {
        this.stocyAs = stocyAs;
    }

    public double getTotalValue10() {
        return totalValue10;
    }

    public void setTotalValue10(double totalValue10) {
        this.totalValue10 = totalValue10;
    }

    public double getTotalValue5() {
        return totalValue5;
    }

    public void setTotalValue5(double totalValue5) {
        this.totalValue5 = totalValue5;
    }
}
