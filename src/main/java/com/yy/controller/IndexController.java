package com.yy.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主要页面映射
 */
@Controller
public class IndexController {

    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        return "login";
    }
    // 注册页面不拦截
    @RequestMapping("/register")
    public String register(HttpServletRequest request) {
        return "register";
    }
    // 退出不拦截
    @RequestMapping("/quit")
    public String loginOut(HttpServletRequest request, HttpServletResponse response) {
        return "login";
    }
    @RequestMapping({"", "main"})
    public String main(HttpServletRequest request) {
        return "main";
    }
}
