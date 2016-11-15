package com.example.zsq.mynews.bean;

import java.io.Serializable;

/**
 * Created by Adamlambert on 2016/10/19.
 */
public class NewsClassify implements Serializable{//实现序列化接口
    //新闻id
    private int id;
    //新闻标题
    private String title;
    private String UrlId;

    public String getUrlId() {
        return UrlId;
    }

    public void setUrlId(String urlId) {
        UrlId = urlId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
