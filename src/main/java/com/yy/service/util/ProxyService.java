package com.yy.service.util;

import com.yy.dao.HttpProxyRepository;
import com.yy.dao.entity.HttpProxy;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.yy.util.SleepUtil.sleep;

@Service
public class ProxyService {

    private static final Logger LOGGER = Logger.getLogger(ProxyService.class);

    //获取代理的网站
    private static final String proxyWebSite = "https://www.xicidaili.com/nn/";
    private static final Random random = new Random();
    private static Map<String, HttpHost> httpHostMap = new ConcurrentHashMap<>();
    private static String key(HttpProxy httpProxy){
        return httpProxy.getIp()+":"+httpProxy.getPort();
    }

    @Autowired
    private HttpProxyRepository httpProxyRepository;

    //获取https类型的代理
    public static HttpHost getHttpsProxy() {
//        HttpHost httpHost = null;
//        if (!httpHostMap.isEmpty())
//        {
//            int pos = random.nextInt(httpHostMap.size());
//            for (String key : httpHostMap.keySet()){
//                if (pos-- == 0)
//                {
//                    httpHost = httpHostMap.get(key);
//                    LOGGER.info(String.format("getHttpsProxy:使用代理[%s]进行https请求", httpHost));
//                    break;
//                }
//            }
//        }
//        return httpHost;
        return null;
    }

    //获取http类型的代理
    public static HttpHost getHttpProxy() {
        return null;
    }

    /**
     * 定期添加新的代理
     */
//    @PostConstruct
    private void addProxies() {
        //添加一个小时以内的代理
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 3600000);
        List<HttpProxy> list = httpProxyRepository.findAllByCheckTimeGreaterThan(timestamp);
        for (HttpProxy httpProxy : list)
        {
            httpHostMap.put(key(httpProxy), new HttpHost(httpProxy.getIp(), httpProxy.getPort(), "http"));
        }
        new Thread(() -> {
            while (true){
                List<HttpProxy> proxies = getProxies();
                for (HttpProxy httpProxy : proxies)
                {
                    if (httpProxyRepository.findByIpAndPort(httpProxy.getIp(), httpProxy.getPort()) != null)
                    {
                        continue;
                    }
                    if (httpProxy.isHttps() && httpProxy.isAnonymous() && isUseful(httpProxy))
                    {
                        Timestamp checkTime = new Timestamp(System.currentTimeMillis());
                        httpProxy.setCheckTime(checkTime);
                        httpProxyRepository.save(httpProxy);
                        httpHostMap.put(key(httpProxy), new HttpHost(httpProxy.getIp(), httpProxy.getPort(), "http"));
                    }
                }
            }
        }).start();
    }

    /**
     * 定期删除失效的代理
     */
//    @PostConstruct
    private void delProxies(){
        new Thread(() -> {
            while (true) {
                //检查所有代理是否可用
                List<HttpProxy> list = httpProxyRepository.findAll();
                for (HttpProxy proxy : list) {
                    if (isUseful(proxy)) {
                        Timestamp checkTime = new Timestamp(System.currentTimeMillis());
                        proxy.setCheckTime(checkTime);
                        httpProxyRepository.save(proxy);
                        httpHostMap.put(key(proxy), new HttpHost(proxy.getIp(), proxy.getPort(), "http"));
                    }else {
                        LOGGER.info(String.format("代理【%s:%d】已失效，删除", proxy.getIp(), proxy.getPort()));
                        httpProxyRepository.deleteById(proxy.getId());
                        httpHostMap.remove(key(proxy));
                    }
                }
                sleep(5000);
            }
        }).start();
    }

    /**
     * 获取所有代理
     */
    private List<HttpProxy> getProxies() {
        List<HttpProxy> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(proxyWebSite)
                    .header("Sec-Fetch-Dest", "document")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("user-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .timeout(5000)
                    .get();
            Elements elements = doc.body().getElementsByTag("tr");
            for (int i = 1; i < elements.size(); ++i) {
                Element element = elements.get(i);
                Elements tds = element.getElementsByTag("td");
                String ip = tds.get(1).text();
                int port = Integer.parseInt(tds.get(2).text());
                boolean isAnonymous = "高匿" .equals(tds.get(4).text());
                boolean isHttps = "HTTPS" .equals(tds.get(5).text());
                String speed = tds.get(6).child(0).attr("title").replace("秒", "");
                String connTime = tds.get(7).child(0).attr("title").replace("秒", "");
                HttpProxy httpProxy = new HttpProxy();
                httpProxy.setSpeed(Double.parseDouble(speed));
                httpProxy.setConnTime(Double.parseDouble(connTime));
                httpProxy.setIp(ip);
                httpProxy.setPort(port);
                httpProxy.setAnonymous(isAnonymous);
                httpProxy.setHttps(isHttps);
                list.add(httpProxy);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return list;
    }


    private static boolean isUseful(HttpProxy httpProxy) {
        try {
            HttpHost httpHost = new HttpHost(httpProxy.getIp(), httpProxy.getPort(), "http");
            CloseableHttpClient httpClient = HttpClients.createDefault(); // 创建HttpClient实例
            HttpGet httpGet = new HttpGet("https://www.baidu.com"); // 创建Httpget实例
            //设置Http报文头信息
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setProxy(httpHost)
                    .build();
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response;
            response = httpClient.execute(httpGet);
            response.close();
            httpClient.close();
            return true;
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return false;
    }
}
