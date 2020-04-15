package com.yy.controller;

import com.yy.aop.interfaces.RecordLog;
import com.yy.dao.WxAccountRepository;
import com.yy.dao.entity.Passenger;
import com.yy.dao.entity.WxAccount;
import com.yy.domain.RespMessage;
import com.yy.service.api.APIWXService;
import com.yy.service.core.Login12306Service;
import com.yy.service.core.PassengerService;
import com.yy.service.util.CookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 登陆
 *
 * @author yy
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    APIWXService apiwxService;
    @Autowired
    PassengerService passengerService;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private Login12306Service login12306Service;
    @Autowired
    private CookieService cookieService;

    @RecordLog
    @GetMapping(value = "/login_wx")
    @ResponseBody
    public RespMessage loginWX(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String code = request.getParameter("code");
        if (code == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("code为空");
            return respMessage;
        }
        try {
            //拿到微信用户的openID和secret等信息
            WxAccount newAccount = apiwxService.code2Session(code);
            if (newAccount == null) {
                respMessage.setSuccess(false);
                respMessage.setMessage("code2Session出现错误");
                return respMessage;
            }
            WxAccount oldAccount = wxAccountRepository.findByOpenId(newAccount.getOpenId());
            if (oldAccount == null) {
                wxAccountRepository.save(newAccount);
            } else {
                oldAccount.setSessionKey(oldAccount.getSessionKey());
                oldAccount.setUnionId(oldAccount.getUnionId());
                wxAccountRepository.save(oldAccount);
            }
            //添加cookie
            cookieService.addCookie(newAccount.getOpenId(), response);
            //返回消息
            respMessage.setSuccess(true);
            respMessage.setMessage("登陆成功");
            return respMessage;
        } catch (Exception e) {
            respMessage.setSuccess(false);
            respMessage.setMessage(e.getMessage());
            return respMessage;
        }
    }

    /**
     * 获取12306的用户名
     *
     * @param request
     * @param response
     * @return
     */
    @RecordLog
    @GetMapping(value = "/get_12306_account")
    @ResponseBody
    public RespMessage get12306Account(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String openID = cookieService.getOpenIDFromRequest(request);
        if (openID == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先调用login_wx接口进行登陆");
            return respMessage;
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        if (wxAccount == null || wxAccount.getUsername() == null || wxAccount.getPassword() == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请求设置12306账户");
            return respMessage;
        }
        respMessage.setSuccess(true);
        respMessage.setMessage(wxAccount.getUsername());
        return respMessage;
    }

    /**
     * 获取12306的用户名
     *
     * @param request
     * @param response
     * @return
     */
    @RecordLog
    @GetMapping(value = "/set_12306_account")
    @ResponseBody
    public RespMessage set12306Account(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String openID = cookieService.getOpenIDFromRequest(request);
        if (openID == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先调用login_wx接口进行登陆");
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("用户名或者密码不能为空");
            return respMessage;
        }
        //验证12306账户密码是否正确
        try {
            RespMessage loginRes = login12306Service.login(username, password);
            if (!loginRes.isSuccess()) {
                return loginRes;
            }
        } catch (Exception e) {
            respMessage.setSuccess(false);
            respMessage.setMessage("服务器内部错误");
            return respMessage;
        }
        //设置用户名和密码
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        wxAccount.setUsername(username);
        wxAccount.setPassword(password);
        wxAccountRepository.save(wxAccount);
        //异步获取乘客人信息，并保存到数据库
        new Thread(() -> {
            List<Passenger> passengers = passengerService.getPassengers(username);
            passengerService.saveOrUpdatePassengers(passengers);
        }).start();
        respMessage.setSuccess(true);
        respMessage.setMessage("操作成功");
        return respMessage;
    }
}
