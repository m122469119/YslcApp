package com.yslc.bean;

import java.io.Serializable;

/**
 * 栏目Bean
 * ●栏目Id
 * ●栏目名称
 * ●栏目排序
 *
 * @author HH
 */
public class ColnumBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String stOrder;

    public ColnumBean() {

    }

    public ColnumBean(String id, String name, String stOrder) {
        this.id = id;
        this.name = name;
        this.stOrder = stOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStOrder() {
        return stOrder;
    }

    public void setStOrder(String stOrder) {
        this.stOrder = stOrder;
    }

}
