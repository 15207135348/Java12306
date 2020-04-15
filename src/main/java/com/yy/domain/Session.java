package com.yy.domain;

import com.alibaba.fastjson.JSONObject;
import com.yy.service.util.ProxyService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Session implements Serializable {


    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
    /**
     * 返回成功状态码
     */
    private static final int SUCCESS_CODE = 200;

    private CookieStore cookieStore;

    public Session() {
        cookieStore = new BasicCookieStore();
    }


    public void addCookies(Set<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            String domain = cookie.getDomain();
            Date expiry = cookie.getExpiry();
            String path = cookie.getPath();
            BasicClientCookie clientCookie = new BasicClientCookie(name, value);
            clientCookie.setDomain(domain);
            clientCookie.setExpiryDate(expiry);
            clientCookie.setPath(path);
            cookieStore.addCookie(clientCookie);
        }
    }

    public void printCookie() {
        List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
        for (org.apache.http.cookie.Cookie cookie : cookies) {
            LOGGER.info(cookie.getName() + "=" + cookie.getValue());
        }
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public Object httpGet(String url, Map<String, String> header) {
        return get(url, header, false, true);
    }

    public Object httpsGet(String url, Map<String, String> header) {
        return get(url, header, true, true);
    }

    public Object httpGetWithoutProxy(String url, Map<String, String> header) {
        return get(url, header, false, false);
    }

    public Object httpsGetWithoutProxy(String url, Map<String, String> header) {
        return get(url, header, true, false);
    }

    public Object httpPost(String url, List<NameValuePair> nameValuePairList, Map<String, String> header) {
        return post(url, header, nameValuePairList, false, true);
    }

    public Object httpsPost(String url, List<NameValuePair> nameValuePairList, Map<String, String> header) {
        return post(url, header, nameValuePairList, true, true);
    }

    public Object httpPostWithoutProxy(String url, List<NameValuePair> nameValuePairList, Map<String, String> header) {
        return post(url, header, nameValuePairList, false, false);
    }

    public Object httpsPostWithoutProxy(String url, List<NameValuePair> nameValuePairList, Map<String, String> header) {
        return post(url, header, nameValuePairList, true, false);
    }

    private org.apache.http.cookie.Cookie findCookie(String name) {
        for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    private void addCookie(String cookieString) {

        cookieString = cookieString.replaceAll(" ", "");
        String[] strings = cookieString.split(";");
        BasicClientCookie clientCookie = null;
        for (int i = 0; i < strings.length; ++i) {
            int index = strings[i].indexOf("=");
            if (index < 0) {
                LOGGER.warn("addCookie:【" + cookieString + "】失败");
                continue;
            }
            String key = strings[i].substring(0, index);
            String value = strings[i].substring(index + 1);
            if (i == 0) {
                clientCookie = (BasicClientCookie) findCookie(key);
                if (clientCookie == null) {
                    clientCookie = new BasicClientCookie(key, value);
                } else {
                    clientCookie.setValue(value);
                }
            } else {
                if (clientCookie != null) {
                    clientCookie.setAttribute(key, value);
                }
            }
        }
        cookieStore.addCookie(clientCookie);
    }

    private static SSLConnectionSocketFactory getDefaultSSLConnectionSocketFactory() {
        SSLContext sslContext = null;
        try {
            //信任所有
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            return new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        return null;
    }

    private CloseableHttpClient getClient(boolean isHttps, boolean useProxy) {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setStaleConnectionCheckEnabled(true);
        RequestConfig defaultRequestConfig;
        if (!useProxy) {
            defaultRequestConfig = builder.build();
        } else {
            HttpHost httpHost;
            if (isHttps) {
                httpHost = ProxyService.getHttpsProxy();
            } else {
                httpHost = ProxyService.getHttpProxy();
            }
            if (httpHost == null) {
                defaultRequestConfig = builder.build();
            } else {
                defaultRequestConfig = builder.setProxy(httpHost).build();
            }
        }
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient client;
        if (isHttps) {
            client = httpClientBuilder.setDefaultCookieStore(cookieStore).setSSLSocketFactory(getDefaultSSLConnectionSocketFactory()).setDefaultRequestConfig(defaultRequestConfig).build();
        } else {
            client = httpClientBuilder.setDefaultCookieStore(cookieStore).setDefaultRequestConfig(defaultRequestConfig).build();
        }
        return client;
    }

    private Object get(String url, Map<String, String> headerMap, boolean isHttps, boolean useProxy) {
        JSONObject jsonObject;
        CloseableHttpResponse response = null;
        CloseableHttpClient client = getClient(isHttps, useProxy);
        try {
            //请求行
            URI uri = new URIBuilder(url).build();
            HttpGet httpGet = new HttpGet(uri);
            //请求头
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    httpGet.addHeader(key, headerMap.get(key));
                }
            }
            //添加默认的请求头
            httpGet.addHeader("Connection", "keep-alive");
            //执行请求
            response = client.execute(httpGet);
            //获取返回头的cookie
            Header[] headers = response.getHeaders("Set-Cookie");
            if (headers != null) {
                for (Header header : headers) {
                    String setCookie = header.getValue();
                    addCookie(setCookie);
                }
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                try {
                    jsonObject = JSONObject.parseObject(result);
                    return jsonObject;
                } catch (Exception e) {
                    return result;
                }
            } else {
                LOGGER.error("HttpClientService-line: {}, errorMsg{}", statusCode, "GET请求失败！");
            }
        } catch (Exception e) {
            LOGGER.error("HttpClientService-line: {}, Exception: {}", 100, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private Object post(String url, Map<String, String> headerMap, List<NameValuePair> nameValuePairList, boolean isHttps, boolean useProxy) {
        JSONObject jsonObject;
        CloseableHttpResponse response = null;
        CloseableHttpClient client = getClient(isHttps, useProxy);
        try {
            //请求行
            HttpPost httpPost = new HttpPost(url);
            //请求头
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    httpPost.addHeader(key, headerMap.get(key));
                }
            }
            //添加默认的请求头
            httpPost.addHeader("Connection", "keep-alive");
            //请求体
            if (nameValuePairList != null) {
                StringEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
                httpPost.setEntity(entity);
            }
            //返回结果
            response = client.execute(httpPost);
            //设置cookie
            Header[] headers = response.getHeaders("Set-Cookie");
            if (headers != null) {
                for (Header header : headers) {
                    String setCookie = header.getValue();
                    addCookie(setCookie);
                }
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                try {
                    jsonObject = JSONObject.parseObject(result);
                    return jsonObject;
                } catch (Exception e) {
                    return result;
                }
            } else {
                LOGGER.error("HttpClientService-line: {}, errorMsg{}", statusCode, "POST请求失败！");
            }
        } catch (Exception e) {
            LOGGER.error("HttpClientService-line: {}, Exception：{}", 149, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
