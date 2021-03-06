package com.yy.service.authority;

import com.yy.common.util.Base64Util;
import com.yy.common.util.OsUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class CookieManager {

    private static final String COOKIE_NAME = "TicketServer";
    private static final Logger LOGGER = Logger.getLogger(CookieManager.class);

    /**
     * cookie生成算法
     * @param openID 用户的openID
     */
    private static String genCookieValue(String openID)
    {
        return Base64Util.encode(openID);
    }

    /**
     * 设置浏览器cookie，方便以后可以无密码登陆
     */
    public static void addCookie(String openID, HttpServletResponse response)
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
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response)
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
    public static String getOpenIDFromRequest(HttpServletRequest request)
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
        return Base64Util.decode(encodedText);
    }

    private static final String macChromePath = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
    private static final String macChromeDriverPath = "res/lib/chromedriver";
    private static final String linuxChromePath = "/usr/bin/google-chrome";
    private static final String linuxChromeDriverPath = "/usr/bin/chromedriver";

    public static Set<org.openqa.selenium.Cookie> getCookies(String url)
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
