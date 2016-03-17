package com.yslc.bean;

/**
 * K线图类型Bean
 */
public class KTypeModel {

    private String name;
    private int type;
    private boolean isCheck;

    public KTypeModel(String name, int type, boolean isCheck) {
        this.name = name;
        this.type = type;
        this.isCheck = isCheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
