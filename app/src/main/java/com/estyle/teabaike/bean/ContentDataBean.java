package com.estyle.teabaike.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ContentDataBean {

    @Id()
    private String id;
    private String title;
    private String source;
    private String create_time;
    private String author;
    private String weiboUrl;
    private String wap_content;
    private long currentTimeMillis;

    @Generated(hash = 1361673546)
    public ContentDataBean(String id, String title, String source,
                           String create_time, String author, String weiboUrl, String wap_content,
                           long currentTimeMillis) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.create_time = create_time;
        this.author = author;
        this.weiboUrl = weiboUrl;
        this.wap_content = wap_content;
        this.currentTimeMillis = currentTimeMillis;
    }

    @Generated(hash = 1694515371)
    public ContentDataBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getWeiboUrl() {
        return this.weiboUrl;
    }

    public void setWeiboUrl(String weiboUrl) {
        this.weiboUrl = weiboUrl;
    }

    public String getWap_content() {
        return this.wap_content;
    }

    public void setWap_content(String wap_content) {
        this.wap_content = wap_content;
    }

    public long getCurrentTimeMillis() {
        return this.currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

}
