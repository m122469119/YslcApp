package com.yslc.bean;

/**
 * 大盘信息
 * <p>
 * Created by HH on 2016/2/29.
 */
public class StockInfo {
    private String name;
    private String now;
    private String proportion;
    private String differ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public String getDiffer() {
        return differ;
    }

    public void setDiffer(String differ) {
        this.differ = differ;
    }
}
