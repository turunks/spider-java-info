package com.xz.spider.bilibili.task;

import com.xz.spider.bilibili.controller.SpiderController;
import com.xz.spider.bilibili.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestTask {
    @Autowired
    NewsService newsService;

    @Autowired
    SpiderController spiderController;

    @Scheduled(cron = "0 0/30 * * * ?")// 每分钟执行一次
    private void saveNews() {
        System.out.println("开始爬数据" + new Date());
        newsService.getNews();
        System.out.println("开始导出excel" + new Date());
        spiderController.exportExcelByTmp();
    }

    @Scheduled(cron = "0 0/1 * * * ?")// 每分钟执行一次
    private void WeiBo() {
        System.out.println("开始爬取微博" + new Date());
        newsService.getWeiBo();
    }
}
