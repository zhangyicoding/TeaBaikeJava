package com.estyle.teabaike.bean;

public class TempCollectionBean {

    private int position;
    private ContentDataBean collection;

    public TempCollectionBean(int position, ContentDataBean collection) {
        this.position = position;
        this.collection = collection;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ContentDataBean getCollection() {
        return collection;
    }

    public void setCollection(ContentDataBean collection) {
        this.collection = collection;
    }
}
