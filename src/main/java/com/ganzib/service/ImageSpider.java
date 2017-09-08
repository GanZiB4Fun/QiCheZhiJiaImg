package com.ganzib.service;

import com.ganzib.constant.WebConstant;
import com.ganzib.util.DownImg;
import com.ganzib.util.PhantomTools;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by GanZiBの智障 on 2017/9/8.
 */
@Service
public class ImageSpider {

    private static Site site = Site.me().
            setUserAgent(WebConstant.USER_AGENT).
            addHeader("accept", WebConstant.HEADER_ACCEPT).
            addHeader("Accept-Language", WebConstant.ACCEPT_LANGUAGE).addHeader("Content-type", "text/html")
            .setTimeOut(30000)
            .setSleepTime(3000).setCharset("UTF-8");

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public static Set<String> ARTICLE_URLS = new LinkedHashSet<>();

    @PostConstruct
    public void startSpider(){
        for (int i = 0; i <100 ; i++) {
            String listUrl = "https://m.autohome.com.cn/?from=z#pvareaid=2028690";
            String pageStr = "";
            try {
                pageStr  = PhantomTools.getPageText(listUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!"".equals(pageStr)) {
                Html html = new Html(pageStr);
                List<String> divs = html.xpath("//div[@id='feed-list']/div[@class='album-43-match']").all();
                for (String div : divs){
                    Html divHtml = new Html(div);
                    String title = divHtml.xpath("//h4/text()").get();
                    if (title.contains("媳妇")||title.contains("美女")||title.contains("模特")||title.contains("车模")||title.contains("老婆")||title.contains("腿")||title.contains("姐妹")||title.contains("辣妹")||title.contains("激情")||title.contains("诱惑")||title.contains("辣妈")||title.contains("MM")){
                        String articleUrl = divHtml.xpath("//a/@href").get();
                        System.out.println(articleUrl.substring(2,articleUrl.length()));
                        articleUrl = URLDecoder.decode(articleUrl);
                        String realUrl = "http://"+articleUrl.substring(articleUrl.indexOf("rdurl=//")+8,articleUrl.indexOf("#pvareaid"));
                        ARTICLE_URLS.add(realUrl);
                    }
                }
            }
        }

        for(String article_url : ARTICLE_URLS){
            String infoStr = "";

            try {
                infoStr = PhantomTools.getPageText(article_url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Html infoHtml = new Html(infoStr);
            String title = infoHtml.xpath("//header[@class='bbs-post-header']/h1/text()").get();
            List<String> imgUrls = infoHtml.xpath("//img[@name='LazyloadImg']/@data-original").all();

            for (int i = 0; i < imgUrls.size(); i++) {
                try {
                    System.out.println(sdf.format(new Date())+"正在下载汽车之家文章"+title+"别人老婆的图片");
                    DownImg.download("http:"+imgUrls.get(i),title+i+".jpg","D:\\QiCheImg\\"+title);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
