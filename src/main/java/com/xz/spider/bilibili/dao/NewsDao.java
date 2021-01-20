package com.xz.spider.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xz.spider.bilibili.pojo.News;

import java.util.List;


public interface NewsDao extends BaseMapper<News> {
    int insertByCondtion(News news);

    int insertByCondtionTxt(News news);

    List<News> listByDay();
}