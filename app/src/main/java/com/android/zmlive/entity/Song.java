package com.android.zmlive.entity;

/**
 * Created by Kkan on 2016/9/20.
 */

public class Song {
    private String id;
    private String musicpath;
    private String songid;
    private String author;
    private String title;
    private String pic_small;
    private int shichang;
    private long musicsize;
    public Song(){}
    public Song(String id,String title,String musicpath,int shichang,long musicsize){
        this.id=id;
        this.musicpath=musicpath;
        this.title=title;
        this.shichang=shichang;
        this.musicsize=musicsize;
    }

    public long getMusicsize() {
        return musicsize;
    }

    public void setMusicsize(long musicsize) {
        this.musicsize = musicsize;
    }

    public int getShichang() {
        return shichang;
    }

    public void setShichang(int shichang) {
        this.shichang = shichang;
    }

    public String getMusicpath() {
        return musicpath;
    }

    public void setMusicpath(String musicpath) {
        this.musicpath = musicpath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }
}
