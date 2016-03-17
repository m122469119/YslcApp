package com.yslc.bean;

/**
 * 评论Bean
 * ●评论Id
 * ●评论人昵称
 * ●评论人头像地址
 * ●评论内容
 * ●评论时间
 *
 * @author HH
 */
public class CommentBean {
    private String ncid;
    private String ncikName;
    private String uiImg;
    private String content;
    private String time;

    public String getNcid() {
        return ncid;
    }

    public void setNcid(String ncid) {
        this.ncid = ncid;
    }

    public String getNcikName() {
        return ncikName;
    }

    public void setNcikName(String ncikName) {
        this.ncikName = ncikName;
    }

    public String getUiImg() {
        return uiImg;
    }

    public void setUiImg(String uiImg) {
        this.uiImg = uiImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
