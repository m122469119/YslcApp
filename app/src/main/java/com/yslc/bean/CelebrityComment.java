package com.yslc.bean;

/**
 * Created by Administrator on 2016/4/18.
 */
public class CelebrityComment {
    private String no;//板块
    private String title;//标题
    private String url;//地址

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CelebrityComment{" +
                "no='" + no + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
