package com.yslc.bean;

/**
 * Created by Administrator on 2016/3/31.
 */
public class FastInfoBean {
    private String date;//闪电情报日期
    private String content;//闪电情报内容

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "FastInfoBean{" +
                "date='" + date + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
