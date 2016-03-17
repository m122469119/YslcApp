package com.yslc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 分时图信息Bean
 *
 * @author HH
 */
public class MinuteInfo implements Parcelable {

    //日期、分钟数、现价、均价、成交量、成交额
    private String minute;
    private double now;
    private double avgPrice;
    private double volumeTatil;
    private double turnoverTatil;
    private double volume;
    private double turnover;
    private String stocyAs;
    private String stocyGains;
    private int color;

    public String getStocyAs() {
        return stocyAs;
    }

    public void setStocyAs(String stocyAs) {
        this.stocyAs = stocyAs;
    }

    public String getStocyGains() {
        return stocyGains;
    }

    public void setStocyGains(String stocyGains) {
        this.stocyGains = stocyGains;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public MinuteInfo() {

    }

    public MinuteInfo(Parcel source) {
        this.minute = source.readString();
        this.now = source.readDouble();
        this.avgPrice = source.readDouble();
        this.volume = source.readDouble();
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public double getNow() {
        return now;
    }

    public void setNow(double now) {
        this.now = now;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(minute);
        dest.writeDouble(now);
        dest.writeDouble(avgPrice);
        dest.writeDouble(volume);
    }

    public static final Creator<MinuteInfo> CREATOR = new Creator<MinuteInfo>() {
        @Override
        public MinuteInfo createFromParcel(Parcel source) {
            return new MinuteInfo(source);
        }

        @Override
        public MinuteInfo[] newArray(int i) {
            return new MinuteInfo[i];
        }
    };

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getVolumeTatil() {
        return volumeTatil;
    }

    public void setVolumeTatil(double volumeTatil) {
        this.volumeTatil = volumeTatil;
    }

    public double getTurnoverTatil() {
        return turnoverTatil;
    }

    public void setTurnoverTatil(double turnoverTatil) {
        this.turnoverTatil = turnoverTatil;
    }
}
