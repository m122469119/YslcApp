package com.yslc.bean;

import java.io.Serializable;

/**
 * 股市广播Bean
 * ●广播Id
 * ●广播节目名称
 * ●广播时间
 * ●广播节目主持人名称
 * ●广播节目主持人头像链接
 * ●广播音频URL
 *
 * @author HH
 */
public class RadioBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String radioId;
    private String radioName;
    private String radioDate;
    private String radioTime;
    private String radioHost;
    private String radioHostUrl;
    private String radioUrl;

    public String getRadioId() {
        return radioId;
    }

    public void setRadioId(String radioId) {
        this.radioId = radioId;
    }

    public String getRadioUrl() {
        return radioUrl;
    }

    public void setRadioUrl(String radioUrl) {
        this.radioUrl = radioUrl;
    }

    public String getRadioHostUrl() {
        return radioHostUrl;
    }

    public void setRadioHostUrl(String radioHostUrl) {
        this.radioHostUrl = radioHostUrl;
    }

    public String getRadioTime() {
        return radioTime;
    }

    public void setRadioTime(String radioTime) {
        this.radioTime = radioTime;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public String getRadioDate() {
        return radioDate;
    }

    public void setRadioDate(String radioDate) {
        this.radioDate = radioDate;
    }

    public String getRadioHost() {
        return radioHost;
    }

    public void setRadioHost(String radioHost) {
        this.radioHost = radioHost;
    }

}
