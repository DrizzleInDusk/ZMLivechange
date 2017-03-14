package com.android.zmlive.entity;

/**
 * Created by Kkan on 2016/9/20.
 */

public class LwMess {
    private String mipmapid;
    private String title;
    private String money;
    private String pic;

    public LwMess() {

    }

    public LwMess(String mipmapid,String money,String title,String pic) {
        this.mipmapid = mipmapid;
        this.money = money;
        this.title = title;
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getMipmapid() {
        return mipmapid;
    }

    public void setMipmapid(String mipmapid) {
        this.mipmapid = mipmapid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
