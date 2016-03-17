package com.yslc.bean;

/**
 * 分时图(个股/大盘)详细信息
 * <p> 位于K线图上方显示的信息 </p>
 * <p>
 * Created by HH on 2016/1/12.
 */
public class StocksDetail {
    private String kind; //类型（个股还是分时）

    private double now;
    private double as;
    private double time;
    private double close;
    private String hands;
    private String sell1;
    private String sell2;
    private String sell3;
    private String sell4;
    private String sell5;
    private String sAmount1;
    private String sAmount2;
    private String sAmount3;
    private String sAmount4;
    private String sAmount5;
    private String bAmount1;
    private String bAmount2;
    private String bAmount3;
    private String bAmount4;
    private String bAmount5;
    private String bug1;
    private String bug2;
    private String bug3;
    private String bug4;
    private String bug5;

    //大盘详情Filed
    private double riseCount;
    private double downCount;
    private double balanceCount;
    private double totalTurnover;
    private double highest;
    private double lowest;

    public double getHighest() {
        return highest;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    public double getLowest() {
        return lowest;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getbAmount1() {
        return bAmount1;
    }

    public void setbAmount1(String bAmount1) {
        this.bAmount1 = bAmount1;
    }

    public String getbAmount2() {
        return bAmount2;
    }

    public void setbAmount2(String bAmount2) {
        this.bAmount2 = bAmount2;
    }

    public String getbAmount3() {
        return bAmount3;
    }

    public void setbAmount3(String bAmount3) {
        this.bAmount3 = bAmount3;
    }

    public String getbAmount4() {
        return bAmount4;
    }

    public void setbAmount4(String bAmount4) {
        this.bAmount4 = bAmount4;
    }

    public String getbAmount5() {
        return bAmount5;
    }

    public void setbAmount5(String bAmount5) {
        this.bAmount5 = bAmount5;
    }

    public double getAs() {
        return as;
    }

    public void setAs(double as) {
        this.as = as;
    }

    public String getBug1() {
        return bug1;
    }

    public void setBug1(String bug1) {
        this.bug1 = bug1;
    }

    public String getBug2() {
        return bug2;
    }

    public void setBug2(String bug2) {
        this.bug2 = bug2;
    }

    public String getBug3() {
        return bug3;
    }

    public void setBug3(String bug3) {
        this.bug3 = bug3;
    }

    public String getBug4() {
        return bug4;
    }

    public void setBug4(String bug4) {
        this.bug4 = bug4;
    }

    public String getBug5() {
        return bug5;
    }

    public void setBug5(String bug5) {
        this.bug5 = bug5;
    }

    public String getHands() {
        return hands;
    }

    public void setHands(String hands) {
        this.hands = hands;
    }

    public double getNow() {
        return now;
    }

    public void setNow(double now) {
        this.now = now;
    }

    public String getsAmount1() {
        return sAmount1;
    }

    public void setsAmount1(String sAmount1) {
        this.sAmount1 = sAmount1;
    }

    public String getsAmount2() {
        return sAmount2;
    }

    public void setsAmount2(String sAmount2) {
        this.sAmount2 = sAmount2;
    }

    public String getsAmount3() {
        return sAmount3;
    }

    public void setsAmount3(String sAmount3) {
        this.sAmount3 = sAmount3;
    }

    public String getsAmount4() {
        return sAmount4;
    }

    public void setsAmount4(String sAmount4) {
        this.sAmount4 = sAmount4;
    }

    public String getsAmount5() {
        return sAmount5;
    }

    public void setsAmount5(String sAmount5) {
        this.sAmount5 = sAmount5;
    }

    public String getSell1() {
        return sell1;
    }

    public void setSell1(String sell1) {
        this.sell1 = sell1;
    }

    public String getSell2() {
        return sell2;
    }

    public void setSell2(String sell2) {
        this.sell2 = sell2;
    }

    public String getSell3() {
        return sell3;
    }

    public void setSell3(String sell3) {
        this.sell3 = sell3;
    }

    public String getSell4() {
        return sell4;
    }

    public void setSell4(String sell4) {
        this.sell4 = sell4;
    }

    public String getSell5() {
        return sell5;
    }

    public void setSell5(String sell5) {
        this.sell5 = sell5;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getRiseCount() {
        return riseCount;
    }

    public void setRiseCount(double riseCount) {
        this.riseCount = riseCount;
    }

    public double getDownCount() {
        return downCount;
    }

    public void setDownCount(double downCount) {
        this.downCount = downCount;
    }

    public double getBalanceCount() {
        return balanceCount;
    }

    public void setBalanceCount(double balanceCount) {
        this.balanceCount = balanceCount;
    }

    public double getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(double totalTurnover) {
        this.totalTurnover = totalTurnover;
    }

}
