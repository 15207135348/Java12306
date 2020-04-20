package com.yy.service.util;

import com.yy.domain.Session;
import com.yy.service.api.API12306Service;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionPoolService {

    private static final Logger LOGGER = Logger.getLogger(SessionPoolService.class);
    //用户名与Session之间的映射
    private static Map<String, Session> sessions = new ConcurrentHashMap<>();
    @Autowired
    private API12306Service api12306Service;
    @Autowired
    private CookieService cookieService;

    //与登陆相关
    @Value("${12306.INIT_URL}")
    private String INIT_URL;
    //与登陆相关
    @Value("${use.chrome}")
    private boolean useChrome;

    public void remove(String username) {
        if (username == null || username.isEmpty()) {
            username = "default";
        }
        sessions.remove(username);
    }


    public Session getSession(String username) {
        if (username == null || username.isEmpty()) {
            username = "default";
        }
        Session session = sessions.get(username);
        if (session == null) {
            session = new Session();
            sessions.put(username, session);
            LOGGER.info(String.format("缓存中没有找到【%s】的session，添加新的session", username));
            if (useChrome) {
                session.addCookies(cookieService.getCookies(INIT_URL));
            } else {
                Map<String, String> head = new HashMap<>();
                head.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/81.0.4044.92 Safari/537.36 ");
                session.httpsGet(INIT_URL, head);
                Map<String, String> map = api12306Service.getDeviceID(session);
                if (map != null) {
                    add(session, map);
                }
            }
        } else {
            List<Cookie> cookies = session.getCookieStore().getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("RAIL_EXPIRATION")) {
                    long expired = Long.parseLong(cookie.getValue());
                    long current = System.currentTimeMillis();
                    long leftTime = expired - current;
                    LOGGER.info(String.format("找到【%s】的session，还有【%d】毫秒cookie过期", username, leftTime));
                    //如果还有12个小时就要过期了，就重新加载cookie
                    if (leftTime < 12 * 1000 * 3600) {
                        remove(username);
                        LOGGER.info("重新加载cookie");
                    }
                }
            }
        }
        return session;
    }

    private void add(Session session, Map<String, String> cookies) {
        Set<org.openqa.selenium.Cookie> set = new HashSet<>();
        for (String name : cookies.keySet()) {
            String value = cookies.get(name);
            long expired = 1000 * 3600 * 24 * 365L + System.currentTimeMillis();
            org.openqa.selenium.Cookie cookie = new org.openqa.selenium.Cookie(name, value, ".12306.cn", "/", new Date(expired));
            set.add(cookie);
        }
        session.addCookies(set);
    }

}
