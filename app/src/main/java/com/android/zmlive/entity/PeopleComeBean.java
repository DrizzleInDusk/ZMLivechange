package com.android.zmlive.entity;

/**
 * Created by Kkan on 2017/2/11.
 */

public class PeopleComeBean {
    private String data;
    private String color;
    private String type;

    public PeopleComeBean(String data, String color, String type, String level) {
        this.data = data;
        this.color = color;
        this.type = type;
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String level;

    public PeopleComeBean() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
