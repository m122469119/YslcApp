package com.yslc.bean;

import java.io.Serializable;

/**
 * 股票代码列表Bean
 * <p>
 * Created by HH on 2016/1/6.
 */
public class StocyCodeBean implements Serializable {
    private String Stock_Code;
    private String Stock_Name;
    private String Stock_Abbreviation;

    public StocyCodeBean() {
    }

    public StocyCodeBean(String Stock_Code, String Stock_Name) {
        this.Stock_Code = Stock_Code;
        this.Stock_Name = Stock_Name;
    }

    public String getStock_Code() {
        return Stock_Code;
    }

    public void setStock_Code(String stock_Code) {
        Stock_Code = stock_Code;
    }

    public String getStock_Name() {
        return Stock_Name;
    }

    public void setStock_Name(String stock_Name) {
        Stock_Name = stock_Name;
    }

    public String getStock_Abbreviation() {
        return Stock_Abbreviation;
    }

    public void setStock_Abbreviation(String stock_Abbreviation) {
        Stock_Abbreviation = stock_Abbreviation;
    }
}
