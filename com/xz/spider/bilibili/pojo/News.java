package com.xz.spider.bilibili.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * news
 * @author 
 */
@Data
public class News implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 创建时间
     */
    private Date creatime;

    private static final long serialVersionUID = 1L;
}