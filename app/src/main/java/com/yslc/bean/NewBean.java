package com.yslc.bean;

/**
 * 首页咨讯列表信息Bean
 * ●咨讯Id
 * ●咨讯标题
 * ●咨讯图片链接
 * ●咨讯内容
 * ●咨讯链接
 * ●咨讯作者
 * ●咨讯时间
 * ●评论数
 * ●字体颜色
 * ●阅读数
 * ●点赞数
 *
 * @author HH
 */
public class NewBean {
    private String Nild;
    private String NiTitle;
    private String NiImg;
    private String NiContent;
    private String NiSource;
    private String NiAuthor;
    private String NiTime;
    private String NiNumber;
    private String WhatColor;
    private String ReadNum;
    private String CommentNum;

    public String getNild() {
        return Nild;
    }

    public void setNild(String nild) {
        Nild = nild;
    }

    public String getReadNum() {
        return ReadNum;
    }

    public void setReadNum(String readNum) {
        ReadNum = readNum;
    }

    public String getCommentNum() {
        return CommentNum;
    }

    public void setCommentNum(String commentNum) {
        CommentNum = commentNum;
    }

    public String getWhatColor() {
        return WhatColor;
    }

    public void setWhatColor(String whatColor) {
        WhatColor = whatColor;
    }

    public String getNiTitle() {
        return NiTitle;
    }

    public void setNiTitle(String niTitle) {
        NiTitle = niTitle;
    }

    public String getNiImg() {
        return NiImg;
    }

    public void setNiImg(String niImg) {
        NiImg = niImg;
    }

    public String getNiContent() {
        return NiContent;
    }

    public void setNiContent(String niContent) {
        NiContent = niContent;
    }

    public String getNiSource() {
        return NiSource;
    }

    public void setNiSource(String niSource) {
        NiSource = niSource;
    }

    public String getNiAuthor() {
        return NiAuthor;
    }

    public void setNiAuthor(String niAuthor) {
        NiAuthor = niAuthor;
    }

    public String getNiTime() {
        return NiTime;
    }

    public void setNiTime(String niTime) {
        NiTime = niTime;
    }

    public String getNiNumber() {
        return NiNumber;
    }

    public void setNiNumber(String niNumber) {
        NiNumber = niNumber;
    }

}
