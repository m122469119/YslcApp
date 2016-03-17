package com.yslc.bean;

/**
 * 明星Bean
 * ●明星Id
 * ●明星姓名
 * ●明星文章标题
 * ●明星头像地址
 * ●明星文章内容
 * ●明星文章时间
 *
 * @author HH
 */
public class StarBean {
    private String Sif_Id;
    private String Sif_Name;
    private String Sif_Title;
    private String Sif_Img;
    private String content;
    private String Sn_Time;
    private String Sif_Relation;
    private String Sif_Degree;
    private String Sif_ComNumber;
    private String Sif_Praise;
    private boolean isPraise = true; // 是否点赞

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public String getSif_ComNumber() {
        return Sif_ComNumber;
    }

    public void setSif_ComNumber(String sif_ComNumber) {
        Sif_ComNumber = sif_ComNumber;
    }

    public String getSif_Praise() {
        return Sif_Praise;
    }

    public void setSif_Praise(String sif_Praise) {
        Sif_Praise = sif_Praise;
    }

    public String getSif_Id() {
        return Sif_Id;
    }

    public void setSif_Id(String sif_Id) {
        Sif_Id = sif_Id;
    }

    public String getSif_Name() {
        return Sif_Name;
    }

    public void setSif_Name(String sif_Name) {
        Sif_Name = sif_Name;
    }

    public String getSif_Title() {
        return Sif_Title;
    }

    public void setSif_Title(String sif_Title) {
        Sif_Title = sif_Title;
    }

    public String getSif_Img() {
        return Sif_Img;
    }

    public void setSif_Img(String sif_Img) {
        Sif_Img = sif_Img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSn_Time() {
        return Sn_Time;
    }

    public void setSn_Time(String sn_Time) {
        Sn_Time = sn_Time;
    }

    public String getSif_Relation() {
        return Sif_Relation;
    }

    public void setSif_Relation(String sif_Relation) {
        Sif_Relation = sif_Relation;
    }

    public String getSif_Degree() {
        return Sif_Degree;
    }

    public void setSif_Degree(String sif_Degree) {
        Sif_Degree = sif_Degree;
    }

}
