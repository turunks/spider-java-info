package com.xz.spider.bilibili.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xz.spider.bilibili.Constants.Constants;
import com.xz.spider.bilibili.dao.KeywordDao;
import com.xz.spider.bilibili.dao.NewsDao;
import com.xz.spider.bilibili.pojo.*;
import com.xz.spider.bilibili.service.NewsService;
import com.xz.spider.bilibili.util.ExcelUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import com.xz.spider.bilibili.util.ConnectUtil;

import javax.servlet.http.HttpServletResponse;

@RestController
@PropertySource(encoding = "UTF-8", value = "classpath:application.properties")
public class SpiderController {

//    @Value("#{'${key.keyword}'.split(',')}")
//    private List<String> keywordList;

    @Autowired
    NewsService newsService;

    @Autowired
    NewsDao newsDao;

    @Autowired
    KeywordDao keywordDao;


    @RequestMapping(value = "/test/1", method = RequestMethod.GET)
    public String getVideo(String url) {
//		Connection connection = Jsoup.connect();
//		connection.header("(Request-Line)", "POST /cgi-bin/login?lang=zh_CN HTTP/1.1");
//		connection.header("Accept", "application/json, text/javascript, */*; q=0.01");
//		connection.header("Accept-Encoding", "gzip, deflate");
//		connection.header("Accept-Language", "zh-cn");
//		connection.header("Cache-Control", "no-cache");
//		connection.header("Connection", "Keep-Alive");
//		connection.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//		connection.header("Host", "mp.weixin.qq.com");
//		connection.header("Referer", "https://news.qq.com/");
//		connection.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)");
//        Document document = ConnectUtil.getDocument("https://search.bilibili.com/all?keyword=%E5%A6%96%E7%8E%8B&page=1&order=totalrank");
        Document baiduDocument = ConnectUtil.getDocument("https://weibo.com/"); // 获取大楚新闻
        Element baiduEle = baiduDocument.body();
//        Elements byId = baiduEle.getElementsByClass("list_des");
//        Elements es = baiduEle.select("video-item matrix");
        Elements eleArr = baiduEle.getElementsContainingOwnText("武汉");
        List<DaChu> daChuList = eleArr.stream().map(ele -> {
            String Url = ele.attr("href");
            String title = ele.ownText();
            DaChu daChu = new DaChu();
            daChu.setTitle(title);
            daChu.setUrl(Url);
            return daChu;
        }).collect(Collectors.toList());


//        for (Element element : eleArr) {
//            String watchUrl = element.attr("href");
//            String title = element.ownText();
//            System.out.println(title + "---" + watchUrl);
//        }


//        for (int i = 0; i < es.size(); i++) {
//            //遍历获取列表页每一个元素
//            Elements eidArray = es.get(i).select(".search-watch-later.icon-later-off");
//            Element eid = eidArray.first();
//
//            //获取视频的播放时长
//            Elements etimeArray = es.get(i).select(".so-imgTag_rb");
//            Element etime = etimeArray.first();
//            String time = etime.text();
//            System.out.println(time);
//
//            //获取视频的aid
//            String aid = eid.attr("data-aid");
//            //根据视频aid获取视频的部分信息
//            Archive archive = ConnectUtil.spiderArchive(aid);
//
//            //抓取观看链接
//            Elements eurlArray = es.get(i).select("a[lnk-type=\"video\"]");
//            Element eurl = eurlArray.first();
//            //视频播放页链接
//            String watchUrl = eurl.attr("href");
//
//            //获取播放详情页的dom树
//            Document detail = ConnectUtil.getDetail("https:" + watchUrl);
//            //cid(弹幕id)
//            String cid = ConnectUtil.getCid(detail);
//            //mid(up主id)
//            String mid = ConnectUtil.getMid(detail);
//            UPer up = ConnectUtil.getUPer(mid);
//
//            System.out.println(cid + "----" + mid);
//            try {
//                //爬取弹幕xml,保存在本地
//                ConnectUtil.getBarrage(cid, aid, constant.getBarrage());
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//
//            //回复数
//            String reply = ConnectUtil.getReply(aid);
//        }
        return "1";
    }

    @RequestMapping(value = "/test/dachu", method = RequestMethod.GET)
    public List<News> getDaChu() {
        Document baiduDocument = ConnectUtil.getDocument("https://hb.qq.com/"); // 获取大楚新闻
        Element baiduEle = baiduDocument.body();
        // 据关键字筛选新闻
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

    // 获取微博首页新闻
    @RequestMapping(value = "/test/weibo", method = RequestMethod.GET)
    public List<News> getWeiBo() {
        Document baiduDocument = ConnectUtil.getDocument("https://weibo.com/");
        Element body = baiduDocument.body();
        Elements eleArr = body.getElementsByTag("script");
        List<Elements> daChuList = eleArr.stream().map(ele -> {
            Elements text = ele.getElementsContainingText("女孩");
            String s = ele.data();
            System.out.println("??---" + s);
            return text;
        }).collect(Collectors.toList());
        return null;
    }

    // 获取微博搜索关键词舆论
    @RequestMapping(value = "/test/weiboSerach", method = RequestMethod.GET)
    public List<News> weiboSerach() {
        for (String weiboSerach : Constants.WEIBO_SERACH) {
            String serachUrl = "https://s.weibo.com/weibo?q=" + weiboSerach + "&Refer=STopic_history";
            Document baiduDocument = ConnectUtil.getDocument(serachUrl);
            Element body = baiduDocument.body();
            Elements eleArr = body.getElementsByAttributeValue("node-type", "feed_list_content");
            List<News> weiboList = eleArr.stream().map(ele -> {
                String title = ele.ownText();
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

    /**
     * 获取百度新闻资源信息
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/test/baidu", method = RequestMethod.GET)
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
        return news1;
    }

    /**
     * 获取头条新闻资源信息
     *
     * @param url
     * @return
     */
    @RequestMapping(value = "/test/toutiao", method = RequestMethod.GET)
    public List<News> getTouTiao(String url) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newslist;
    }

    @RequestMapping(value = "/test/exportExcel", method = RequestMethod.GET)
    public void exportExcel(HttpServletResponse response, String fileName) {
        fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString();
        List<News> rows = newsService.list();
        ExcelUtils.exportExcel(response, fileName, rows);
    }

    @GetMapping(value = "/test/getNews")
    public void getNews() {
        List<News> rows = newsService.getNews();
    }

    @PostMapping(value = "/test/exportExcelByTmp")
    public void exportExcelByTmp() {
        List<News> list = newsDao.listByDay();
        ExcelUtils.exportExcelByTmp(list);
    }

}
