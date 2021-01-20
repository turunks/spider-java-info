package com.xz.spider.bilibili.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xz.spider.bilibili.Constants.Constants;
import com.xz.spider.bilibili.dao.KeywordDao;
import com.xz.spider.bilibili.dao.NewsDao;
import com.xz.spider.bilibili.pojo.News;
import com.xz.spider.bilibili.pojo.TouTiao;
import com.xz.spider.bilibili.service.NewsService;
import com.xz.spider.bilibili.util.ConnectUtil;
import com.xz.spider.bilibili.util.ExcelUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lyc
 * @date 2020-08-08 12:57:38
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsDao, News> implements NewsService {
    @Autowired
    NewsDao newsDao;

    @Autowired
    KeywordDao keywordDao;


    @Value("#{'${key.keyword}'.split(',')}")
    private List<String> keywordList;

    @Override
    public List<News> getNews() {
        System.out.println("获取配置文件关键字---+" + keywordList.toString());
        getDaChu();
        getBaiDu();
        getTouTiao();
        getWeiBo();
        weiboSerach();
        return null;
    }


    // 获取微博头条
    public List<News> getWeiBo() {
        // 据关键字筛选新闻
        List<String> keywordList = keywordDao.selKeyword();
        for (int i = 0; i < 10; i++) {
            String data = null;
            try {
                data = pullWBtoutiao(i + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //得到doc对象
            Document doc = Jsoup.parse(data);
            Elements select = doc.select("h3");
            for (Element element : select) {
                for (String keyword : keywordList) {
                    News news = new News();
                    String url = element.select("a").attr("href");
                    String title = element.text();
                    if (title.contains(keyword)) {
                        System.out.println("插入微博数据---" + title);
                        news.setTitle(title);
                        news.setUrl(url);
                        newsDao.insertByCondtion(news);
                    }

                }

            }
        }
        return null;
    }

    // 微博
    private String pullWBtoutiao(Integer page) throws IOException {
        String PAGE = page.toString();
        String URL = null;
        String datatime = String.valueOf(System.currentTimeMillis());
        URL = "https://weibo.com/a/aj/transform/loadingmoreunlogin?ajwvr=10&category=1760&page=" + PAGE + "&lefnav=0&cursor=&__rnd=" + datatime;
        CloseableHttpResponse response = ConnectUtil.doGet(URL);
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity);
        JSONObject jsonObject = JSONObject.parseObject(s);
        String data = (String) jsonObject.get("data");
        return data;
    }

    // 获取微博搜索内容
    public List<News> weiboSerach() {
        for (String weiboSerach : Constants.WEIBO_SERACH) {
            String serachUrl = "https://s.weibo.com/weibo?q=" + weiboSerach + "&Refer=STopic_history";
            Document baiduDocument = ConnectUtil.getDocument(serachUrl);
            Element body = baiduDocument.body();
            Elements eleArr = body.getElementsByAttributeValue("node-type", "feed_list_content");
            List<News> weiboList = eleArr.stream().map(ele -> {
                // 得到元素文本和其子孙文本的的结合
                String title = ele.text();
                News news = new News();
                news.setTitle(title);
                news.setUrl(serachUrl);
                return news;
            }).collect(Collectors.toList());
            // 插入数据title不重复
            weiboList.forEach(news -> newsDao.insertByCondtionTxt(news));
        }
        return null;
    }


    // 获取大楚新闻
    public List<News> getDaChu() {
        Document baiduDocument = ConnectUtil.getDocument("https://hb.qq.com/"); // 获取大楚新闻
        Element baiduEle = baiduDocument.body();
        List<String> keywordList = keywordDao.selKeyword();
        for (String keyword : keywordList) {
            Elements eleArr = baiduEle.getElementsContainingOwnText(keyword);
            List<News> daChuList = eleArr.stream().map(ele -> {
                String url = ele.attr("href");
                String title = ele.ownText();
                News news = new News();
                news.setTitle(title);
                news.setUrl(url);
                return news;
            }).collect(Collectors.toList());
            daChuList.forEach(news -> newsDao.insertByCondtion(news));
        }
        return null;
    }

    // 百度
    public List<News> getBaiDu() {
        CloseableHttpResponse response = ConnectUtil.doGet("https://news.baidu.com/widget?id=LocalNews&ajax=json&t=1608001482745");
        HttpEntity entity = response.getEntity();
        String s = null;
        JSONObject o = null;
        List<News> list = new ArrayList<>();
        // 关键则过滤后的news
        List<News> news1 = new ArrayList<>();
        try {
            s = EntityUtils.toString(entity);
            o = JSONObject.parseObject(s);
            JSONObject rows = o.getJSONObject("data").getJSONObject("LocalNews").getJSONObject("data").getJSONObject("rows");
            String first = rows.getString("first");
            String second = rows.getString("second");
            List<News> list1 = JSON.parseArray(first, News.class);
            List<News> list2 = JSON.parseArray(second, News.class);
            list.addAll(list1);
            list.addAll(list2);

            // 据关键字筛选新闻
            List<String> keywordList = keywordDao.selKeyword();
            for (String keyword : keywordList) {
                List<News> collect = list.stream().filter(news -> {
                    String title = news.getTitle();
                    boolean contains = title.contains(keyword);
                    return contains;
                }).collect(Collectors.toList());
                news1.addAll(collect);
            }
            news1.forEach(news -> newsDao.insertByCondtion(news));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 头条
    public List<News> getTouTiao() {
        CloseableHttpResponse response = ConnectUtil.doGet("https://www.toutiao.com/api/pc/realtime_news/");
        HttpEntity entity = response.getEntity();
        String s = null;
        JSONObject o = null;
        List<TouTiao> list = new ArrayList<>();
        List<News> newslist = new ArrayList<>();
        try {
            s = EntityUtils.toString(entity);
            o = JSONObject.parseObject(s);
            String data = o.getString("data");
            list = JSON.parseArray(o.getString("data"), TouTiao.class);
            newslist = list.stream().map(touTiao -> {
                News news = new News();
                news.setTitle(touTiao.getTitle());
                news.setUrl(Constants.TouTiaoPrefix + touTiao.getGroup_id());
                return news;
            }).collect(Collectors.toList());
            // 保存头条news
            newslist.forEach(news -> newsDao.insertByCondtion(news));
//            boolean b = newsService.saveOrUpdateBatch(newslist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newslist;
    }

    @PostConstruct
    public void init() {
        System.out.println("项目初始化拉取数据---");
        getNews();
        System.out.println("项目初始化导出excel---");
        List<News> list = newsDao.listByDay();
        ExcelUtils.exportExcelByTmp(list);
    }


}