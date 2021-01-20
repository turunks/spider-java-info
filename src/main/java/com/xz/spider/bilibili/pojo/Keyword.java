package com.xz.spider.bilibili.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * keyword
 *
 * @author
 */
@Data
public class Keyword implements Serializable {
    private Integer id;

    /**
     * keyword关键字
     */
    private String keyword;

    private static final long serialVersionUID = 1L;
}