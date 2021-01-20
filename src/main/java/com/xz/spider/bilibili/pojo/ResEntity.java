package com.xz.spider.bilibili.pojo;

import java.util.List;

public class ResEntity {
    String message;
    List<Object> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
