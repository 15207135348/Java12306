package com.yy.controller;

import com.yy.aop.interfaces.RecordLog;
import com.yy.api.rail.PersonalCenter;
import com.yy.dao.PassengerRepository;
import com.yy.dao.WxAccountRepository;
import com.yy.dao.entity.Passenger;
import com.yy.dao.entity.WxAccount;
import com.yy.domain.RespMessage;
import com.yy.api.APIWeChat;
import com.yy.api.rail.Login12306;
import com.yy.util.CookieUtil;
import org.apache.log4j.Logger;
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

    private static final Logger LOGGER = Logger.getLogger(AuthController.class);


    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    private WxAccountRepository wxAccountRepository;

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
            WxAccount newAccount = APIWeChat.code2Session(code);
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
            CookieUtil.addCookie(newAccount.getOpenId(), response);
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
        String openID = CookieUtil.getOpenIDFromRequest(request);
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
        String openID = CookieUtil.getOpenIDFromRequest(request);
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
            RespMessage loginRes = Login12306.login(username, password);
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
            List<Passenger> passengers = PersonalCenter.getPassengers(username);
            saveOrUpdatePassengers(passengers);
        }).start();
        respMessage.setSuccess(true);
        respMessage.setMessage("操作成功");
        return respMessage;
    }

    public void saveOrUpdatePassengers(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            Passenger passenger1 = passengerRepository.findByUsernameAndName(passenger.getUsername(), passenger.getName());
            if (passenger1 == null) {
                passengerRepository.save(passenger);
                LOGGER.info("添加乘客人");
            } else {
                passenger.setId(passenger1.getId());
                passengerRepository.save(passenger);
                LOGGER.info("更新乘客人");
            }
        }
    }
}
