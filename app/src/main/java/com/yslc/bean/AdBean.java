package com.yslc.bean;

/**
 * 滑动新闻图片Bean
 * ● 标题
 * ● 连接地址
 * ● 图片地址
 *
 * @author HH
 */
public class AdBean {
    private String title;
    private String linkUrl;
    private String imgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
