package com.xz.spider.bilibili.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xz.spider.bilibili.pojo.Keyword;

import java.util.List;

public interface KeywordDao extends BaseMapper<Keyword> {
    List<String> selKeyword();
}