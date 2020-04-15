package com.yy.service.util;

import com.yy.util.Base64s;
import com.yy.util.OsUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Service
public class CookieService {

    private static final String COOKIE_NAME = "TicketServer";
    private static final Logger LOGGER = Logger.getLogger(CookieService.class);

    /**
     * cookie生成算法
     * @param openID 用户的openID
     */
    private String genCookieValue(String openID)
    {
        return Base64s.encode(openID);
    }

    /**
     * 设置浏览器cookie，方便以后可以无密码登陆
     */
    public void addCookie(String openID, HttpServletResponse response)
    {
        String value = genCookieValue(openID);
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setMaxAge(3600*24*30);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 用户注销后，要清除cookie
     */
    public void removeCookie(HttpServletRequest request, HttpServletResponse response)
    {
        Cookie[] cookies =  request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(COOKIE_NAME)){
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * 从cookie中解析出用户名和加密的密码
     */
    public String getOpenIDFromRequest(HttpServletRequest request)
    {
        Cookie[] cookies =  request.getCookies();
        String encodedText = null;
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(COOKIE_NAME)){
                    encodedText = cookie.getValue();
                    break;
                }
            }
        }
        if (encodedText == null)
        {
            return null;
        }
        return Base64s.decode(encodedText);
    }


    @Value("${mac.chrome.path}")
    private String macChromePath;
    @Value("${mac.chromedriver.path}")
    private String macChromeDriverPath;
    @Value("${linux.chrome.path}")
    private String linuxChromePath;
    @Value("${linux.chromedriver.path}")
    private String linuxChromeDriverPath;

    Set<org.openqa.selenium.Cookie> getCookies(String url)
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");

        if (OsUtil.isLinux()){
            options.setBinary(linuxChromePath);
            System.setProperty("webdriver.chrome.driver", linuxChromeDriverPath);
            LOGGER.info(String.format("当前在linux系统下，加载chrome驱动路径:%s", linuxChromeDriverPath));
        }
        else if (OsUtil.isMacOS()){
            options.setBinary(macChromePath);
            System.setProperty("webdriver.chrome.driver", macChromeDriverPath);
            LOGGER.info(String.format("当前在【mac】系统下，加载chrome驱动路径:%s", macChromeDriverPath));
        }
        else {
            LOGGER.error("当前在【未知】系统下，不能正确加载chrome驱动");
            System.exit(1);
        }
        WebDriver driver = new ChromeDriver(options);
        driver.get(url);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<org.openqa.selenium.Cookie> cookies =driver.manage().getCookies();
        //关闭浏览器
        driver.close();
        driver.quit();
        return cookies;
    }
}
