package com.xz.spider.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xz.spider.bilibili.pojo.News;

import java.util.List;

/**
* 
*
* @author lyc
* @date 2020-08-08 12:57:38
*/

public interface NewsService extends IService<News>{
    List<News> getNews();

    // 微博单独爬取
    List<News> getWeiBo();
}