package com.yslc.bean;

public enum Type {
    HOUR("hour"), DAY("day"), WEEK("week"), MONTH("month");
    private String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}